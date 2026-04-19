package com.nearby.app.ui.store

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nearby.app.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import javax.inject.Inject

data class AddProductState(
    val name: String = "",
    val price: String = "",
    val stock: String = "",
    val description: String = "",
    val category: String = "general",
    val isFeatured: Boolean = false,
    val selectedImageUri: Uri? = null,
    val enhancedImageUri: Uri? = null,
    val isEnhancing: Boolean = false,
    val isEnhanced: Boolean = false,
    val isSubmitting: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class AddProductViewModel @Inject constructor(
    private val productRepo: ProductRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val _state = MutableStateFlow(AddProductState())
    val state: StateFlow<AddProductState> = _state.asStateFlow()

    fun onNameChange(v: String) { _state.value = _state.value.copy(name = v) }
    fun onPriceChange(v: String) { _state.value = _state.value.copy(price = v) }
    fun onStockChange(v: String) { _state.value = _state.value.copy(stock = v) }
    fun onDescriptionChange(v: String) { _state.value = _state.value.copy(description = v) }
    fun onCategoryChange(v: String) { _state.value = _state.value.copy(category = v) }
    fun onFeaturedChange(v: Boolean) { _state.value = _state.value.copy(isFeatured = v) }

    fun onImageSelected(uri: Uri) {
        _state.value = _state.value.copy(
            selectedImageUri = uri,
            enhancedImageUri = null,
            isEnhanced = false,
        )
    }

    /**
     * AI Enhance: Uploads the image to get a URL, then triggers the backend AI service.
     */
    fun enhanceImage(shopId: String) {
        val uri = _state.value.selectedImageUri ?: return
        
        viewModelScope.launch {
            _state.value = _state.value.copy(isEnhancing = true, error = null)
            
            try {
                // 1. Upload to get a public URL first
                val stream = context.contentResolver.openInputStream(uri)
                val bytes = stream?.readBytes()
                stream?.close()
                
                if (bytes == null) throw Exception("Could not read image bytes")
                
                val requestBody = bytes.toRequestBody("image/*".toMediaTypeOrNull())
                val part = MultipartBody.Part.createFormData("file", "upload.jpg", requestBody)
                
                var remoteUrl = ""
                productRepo.uploadImage(part).collect { res ->
                    if (res is com.nearby.app.data.network.NetworkResult.Success) {
                        remoteUrl = res.data.imageUrl
                    }
                }
                
                if (remoteUrl.isEmpty()) throw Exception("Image upload failed")
                
                // 2. Trigger AI task on backend
                productRepo.triggerAiEnhance(remoteUrl, shopId).collect { res ->
                    when (res) {
                        is com.nearby.app.data.network.NetworkResult.Success -> {
                            _state.value = _state.value.copy(
                                isEnhancing = false,
                                isEnhanced = true,
                                error = "AI Task started! Enhancement will be ready soon."
                            )
                        }
                        is com.nearby.app.data.network.NetworkResult.Error -> {
                            _state.value = _state.value.copy(isEnhancing = false, error = res.message)
                        }
                        else -> {}
                    }
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isEnhancing = false, error = "Enhance failed: ${e.message}")
            }
        }
    }

    fun addProduct(shopId: String) {
        val s = _state.value
        val price = s.price.toDoubleOrNull() ?: run {
            _state.value = _state.value.copy(error = "Invalid price")
            return
        }
        val stock = s.stock.toIntOrNull() ?: 0

        viewModelScope.launch {
            _state.value = _state.value.copy(isSubmitting = true, error = null)

            var finalImageUrl = ""

            // Upload image if Uri is selected
            if (s.selectedImageUri != null) {
                try {
                    val stream = context.contentResolver.openInputStream(s.selectedImageUri)
                    val bytes = stream?.readBytes()
                    stream?.close()
                    if (bytes != null) {
                        val requestBody = bytes.toRequestBody("image/*".toMediaTypeOrNull())
                        val part = MultipartBody.Part.createFormData("file", "upload.jpg", requestBody)
                        val uploadResult = productRepo.uploadImage(part)

                        uploadResult.collect { netRes ->
                            if (netRes is com.nearby.app.data.network.NetworkResult.Success) {
                                finalImageUrl = netRes.data.imageUrl
                            }
                        }
                    }
                } catch (e: Exception) {
                    _state.value = _state.value.copy(isSubmitting = false, error = "Image upload failed")
                    return@launch
                }
            }

            productRepo.addProduct(
                shopId = shopId,
                name = s.name.trim(),
                price = price,
                category = s.category,
                imageUrl = finalImageUrl
            ).collect { result ->
                when (result) {
                    is com.nearby.app.data.network.NetworkResult.Loading -> { } // Already loading
                    is com.nearby.app.data.network.NetworkResult.Success -> {
                        _state.value = _state.value.copy(isSubmitting = false, isSuccess = true)
                    }
                    is com.nearby.app.data.network.NetworkResult.Error -> {
                        _state.value = _state.value.copy(isSubmitting = false, error = result.message)
                    }
                }
            }
        }

    }
}

package com.nearby.app.ui.store

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nearby.app.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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
     * AI Enhance: In a production app this would call an image-processing API
     * (e.g. remove background, auto-colour-correct, add white background).
     * Here we simulate the async operation with a 2-second delay.
     * The enhanced URI is the same image, but you could replace it with
     * a real Cloudinary / Imgix transform URL.
     */
    fun enhanceImage() {
        val uri = _state.value.selectedImageUri ?: return
        viewModelScope.launch {
            _state.value = _state.value.copy(isEnhancing = true)
            delay(2200) // Simulated AI processing time
            _state.value = _state.value.copy(
                isEnhancing = false,
                isEnhanced = true,
                enhancedImageUri = uri, // In production: replace with enhanced image URL
            )
        }
    }

    fun addProduct(shopId: String) {
        val s = _state.value
        val price = s.price.toDoubleOrNull() ?: run {
            _state.value = _state.value.copy(error = "Invalid price")
            return
        }
        val stock = s.stock.toIntOrNull() ?: 0

        // Image URL: In production upload to Supabase Storage / Cloudinary first
        val imageUrl = s.selectedImageUri?.toString() ?: ""

        viewModelScope.launch {
            _state.value = _state.value.copy(isSubmitting = true, error = null)
            val result = productRepo.addProduct(
                shopId = shopId,
                name = s.name.trim(),
                price = price,
                description = s.description.trim(),
                stock = stock,
                imageUrl = imageUrl,
                category = s.category,
            )
            result.onSuccess {
                _state.value = _state.value.copy(isSubmitting = false, isSuccess = true)
            }.onFailure { e ->
                _state.value = _state.value.copy(isSubmitting = false, error = e.message ?: "Failed to add product")
            }
        }
    }
}

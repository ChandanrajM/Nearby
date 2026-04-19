package com.nearby.app.ui.store

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nearby.app.data.model.Product
import com.nearby.app.data.repository.ProductRepository
import com.nearby.app.data.repository.ShopRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StoreManageState(
    val shopId: String = "",
    val shopName: String = "",
    val products: List<Product> = emptyList(),
    val filteredProducts: List<Product> = emptyList(),
    val selectedCategory: String = "All",
    val categories: List<String> = listOf("All", "Just Dropped", "Vintage", "Streetwear"),
    val isLoading: Boolean = true,
)

@HiltViewModel
class StoreManageViewModel @Inject constructor(
    private val shopRepo: ShopRepository,
    private val productRepo: ProductRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(StoreManageState())
    val state: StateFlow<StoreManageState> = _state.asStateFlow()

    fun loadStore(shopId: String) {
        viewModelScope.launch {
            // Load Shop Details
            shopRepo.getShop(shopId).collect { result ->
                when (result) {
                    is com.nearby.app.data.network.NetworkResult.Loading -> {
                        _state.value = _state.value.copy(isLoading = true, shopId = shopId)
                    }
                    is com.nearby.app.data.network.NetworkResult.Success -> {
                        _state.value = _state.value.copy(
                            shopName = result.data.name,
                            isLoading = false
                        )
                    }
                    is com.nearby.app.data.network.NetworkResult.Error -> {
                        _state.value = _state.value.copy(isLoading = false)
                    }
                }
            }
        }
        
        viewModelScope.launch {
            // Load Products
            productRepo.getShopProducts(shopId).collect { result ->
                if (result is com.nearby.app.data.network.NetworkResult.Success) {
                    val products = result.data
                    _state.value = _state.value.copy(
                        products = products,
                        filteredProducts = products
                    )
                }
            }
        }
    }

    fun onCategoryChange(category: String) {
        _state.value = _state.value.copy(selectedCategory = category)
        val all = _state.value.products
        val filtered = if (category == "All") all
        else all.filter {
            it.category?.replace("_", " ")?.equals(category, ignoreCase = true) == true
        }
        _state.value = _state.value.copy(filteredProducts = filtered)
    }

    fun addProduct(name: String, price: String, category: String, imageUrl: String) {
        val priceVal = price.toDoubleOrNull() ?: return
        viewModelScope.launch {
            productRepo.addProduct(
                shopId = _state.value.shopId,
                name = name,
                price = priceVal,
                category = category,
                imageUrl = imageUrl
            ).collect { result ->
                if (result is com.nearby.app.data.network.NetworkResult.Success) {
                    loadStore(_state.value.shopId)
                }
            }
        }
    }

    fun updateProduct(
        productId: String,
        name: String? = null,
        price: String? = null,
        isAvailable: Boolean? = null
    ) {
        val priceVal = price?.toDoubleOrNull()
        viewModelScope.launch {
            productRepo.updateProduct(
                productId = productId,
                name = name,
                price = priceVal,
                isAvailable = isAvailable
            ).collect { result ->
                if (result is com.nearby.app.data.network.NetworkResult.Success) {
                    loadStore(_state.value.shopId)
                }
            }
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            productRepo.deleteProduct(productId).collect { result ->
                if (result is com.nearby.app.data.network.NetworkResult.Success) {
                    // Optimistic local remove after success
                    _state.value = _state.value.copy(
                        products = _state.value.products.filter { it.id != productId },
                        filteredProducts = _state.value.filteredProducts.filter { it.id != productId },
                    )
                }
            }
        }
    }
}


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
    val shopId: String = "shop-1",  // Replaced by actual auth user's shop ID
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
            _state.value = _state.value.copy(isLoading = true, shopId = shopId)
            val shopResult = shopRepo.getShop(shopId)
            val productsResult = shopRepo.getProductsByShop(shopId)

            shopResult.onSuccess { shop ->
                val products = productsResult.getOrDefault(emptyList())
                _state.value = _state.value.copy(
                    shopName = shop.name,
                    products = products,
                    filteredProducts = products,
                    isLoading = false,
                )
            }.onFailure {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    fun onCategoryChange(category: String) {
        _state.value = _state.value.copy(selectedCategory = category)
        val all = _state.value.products
        val filtered = if (category == "All") all
        else all.filter {
            it.category.replace("_", " ").equals(category, ignoreCase = true)
        }
        _state.value = _state.value.copy(filteredProducts = filtered)
    }

    fun addProduct(name: String, price: String, description: String) {
        val priceVal = price.toDoubleOrNull() ?: return
        viewModelScope.launch {
            productRepo.addProduct(
                shopId = _state.value.shopId,
                name = name,
                price = priceVal,
                description = description,
            )
            loadStore(_state.value.shopId)
        }
    }

    fun updateProduct(
        productId: String,
        name: String,
        price: String,
        description: String,
        stock: String,
    ) {
        val priceVal = price.toDoubleOrNull() ?: return
        val stockVal = stock.toIntOrNull() ?: 0
        viewModelScope.launch {
            val existing = _state.value.products.find { it.id == productId } ?: return@launch
            productRepo.updateProduct(
                productId = productId,
                name = name,
                price = priceVal,
                description = description,
                stock = stockVal,
                imageUrl = existing.image_url,
                category = existing.category,
            )
            loadStore(_state.value.shopId)
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            productRepo.deleteProduct(productId)
            // Optimistic local remove
            _state.value = _state.value.copy(
                products = _state.value.products.filter { it.id != productId },
                filteredProducts = _state.value.filteredProducts.filter { it.id != productId },
            )
        }
    }
}

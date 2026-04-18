package com.nearby.app.ui.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nearby.app.data.model.Product
import com.nearby.app.data.model.Shop
import com.nearby.app.data.repository.CartRepository
import com.nearby.app.data.repository.ShopRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ShopUiState(
    val shop: Shop? = null,
    val products: List<Product> = emptyList(),
    val filteredProducts: List<Product> = emptyList(),
    val selectedCategory: String = "All",
    val categories: List<String> = listOf("All", "Just Dropped", "Vintage", "Streetwear"),
    val isLoading: Boolean = true,
)

@HiltViewModel
class ShopViewModel @Inject constructor(
    private val shopRepo: ShopRepository,
    private val cartRepo: CartRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ShopUiState())
    val state: StateFlow<ShopUiState> = _state.asStateFlow()

    fun loadShop(shopId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val shopResult = shopRepo.getShop(shopId)
            val productsResult = shopRepo.getProductsByShop(shopId)

            shopResult.onSuccess { shop ->
                val products = productsResult.getOrDefault(emptyList())
                _state.value = _state.value.copy(
                    shop = shop,
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

    fun addToCart(product: Product) {
        cartRepo.addToCart(product)
    }

    fun getProduct(productId: String): Product? {
        return _state.value.products.find { it.id == productId }
    }
}

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
    private val productRepo: com.nearby.app.data.repository.ProductRepository,
    private val cartRepo: CartRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ShopUiState())
    val state: StateFlow<ShopUiState> = _state.asStateFlow()

    fun loadShop(shopId: String) {
        viewModelScope.launch {
            // Load Shop Details
            shopRepo.getShop(shopId).collect { result ->
                when (result) {
                    is com.nearby.app.data.network.NetworkResult.Loading -> {
                        _state.value = _state.value.copy(isLoading = true)
                    }
                    is com.nearby.app.data.network.NetworkResult.Success -> {
                        _state.value = _state.value.copy(
                            shop = result.data,
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

    fun addToCart(product: Product) {
        cartRepo.addToCart(product)
    }

    fun getProduct(productId: String): Product? {
        return _state.value.products.find { it.id == productId }
    }
}

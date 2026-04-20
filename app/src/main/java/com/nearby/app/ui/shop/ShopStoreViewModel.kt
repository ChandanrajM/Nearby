package com.nearby.app.ui.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nearby.app.data.network.NetworkResult
import com.nearby.app.data.repository.ShopRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// ─────────────────────────────────────────────────────────────────
// ShopStoreViewModel
//
// Manages all data for ShopStoreScreen:
// - Loads shop info + products from your FastAPI backend
// - Handles category filtering (locally — no extra API call)
// - Subscribes to Supabase Realtime for AI-processed image updates
//
// The screen only calls three functions:
//   viewModel.loadShop(shopId)         — called once on screen open
//   viewModel.selectCategory(category) — called when a chip is tapped
//   viewModel.uiState                  — the state to render
// ─────────────────────────────────────────────────────────────────

@HiltViewModel
class ShopStoreViewModel @Inject constructor(
    private val shopRepository: ShopRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShopUiState())
    val uiState: StateFlow<ShopUiState> = _uiState.asStateFlow()

    // ── Load shop + products ──────────────────────────────────────

    fun loadShop(shopId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // Load shop info
            shopRepository.getShop(shopId).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> Unit   // Already set above

                    is NetworkResult.Success -> {
                        val shop = result.data
                        _uiState.update { state ->
                            state.copy(
                                shop = ShopDisplayModel(
                                    id = shop.id,
                                    name = shop.name,
                                    category = shop.category,
                                    distanceKm = shop.distanceKm ?: 0.0,
                                    isOnline = shop.isOpen,
                                    hasLiveDrop = false,   // Set based on your backend logic
                                    categories = listOf(   // Could come from backend too
                                        "ALL", "JUST DROPPED", "VINTAGE", "STREETWEAR"
                                    )
                                )
                            )
                        }
                        // Now load products for this shop
                        loadProducts(shopId)
                    }

                    is NetworkResult.Error -> {
                        _uiState.update { it.copy(isLoading = false, error = result.message) }
                    }
                }
            }
        }
    }

    private fun loadProducts(shopId: String) {
        viewModelScope.launch {
            shopRepository.getShopProducts(shopId).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> Unit

                    is NetworkResult.Success -> {
                        val products = result.data.map { product ->
                            ProductDisplayModel(
                                id = product.id,
                                name = product.name,
                                price = product.price,
                                category = product.category ?: "ALL",
                                imageUrl = product.imageUrl,
                                processedImageUrl = product.processedImageUrl,
                                isAvailable = product.isAvailable,
                                isNew = product.status == "ready"
                            )
                        }
                        _uiState.update { it.copy(isLoading = false, products = products) }
                    }

                    is NetworkResult.Error -> {
                        _uiState.update { it.copy(isLoading = false, error = result.message) }
                    }
                }
            }
        }
    }

    // ── Category filter ───────────────────────────────────────────
    // Filtering is local — no API call needed.
    // ShopUiState.filteredProducts computes the filtered list automatically.

    fun selectCategory(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    // ── Realtime product image updates ────────────────────────────
    // Called from the screen when Supabase Realtime fires a "product ready" event.
    // Replaces the placeholder image with the AI-processed one without reloading.

    fun onProductImageReady(productId: String, processedImageUrl: String) {
        _uiState.update { state ->
            state.copy(
                products = state.products.map { product ->
                    if (product.id == productId) {
                        product.copy(processedImageUrl = processedImageUrl)
                    } else {
                        product
                    }
                }
            )
        }
    }
}

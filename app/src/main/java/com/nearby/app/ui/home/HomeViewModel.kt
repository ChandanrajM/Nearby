package com.nearby.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nearby.app.data.model.Shop
import com.nearby.app.data.repository.LocationRepository
import com.nearby.app.data.repository.ShopRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val shops: List<Shop> = emptyList(),
    val filteredShops: List<Shop> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String = "All",
    val isLoading: Boolean = true,
    val categories: List<String> = listOf("All", "Fashion", "Grocery", "Electronics", "Books", "General"),
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val shopRepo: ShopRepository,
    val locationRepo: LocationRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        loadLocation()
        loadShops()
    }

    private fun loadLocation() {
        viewModelScope.launch {
            locationRepo.fetchCurrentLocation()
        }
    }

    private fun loadShops() {
        viewModelScope.launch {
            val loc = locationRepo.location.value
            shopRepo.getNearbyShops(loc.latitude, loc.longitude).collect { result ->
                when (result) {
                    is com.nearby.app.data.network.NetworkResult.Loading -> {
                        _state.value = _state.value.copy(isLoading = true)
                    }
                    is com.nearby.app.data.network.NetworkResult.Success -> {
                        val shops = result.data.shops
                        _state.value = _state.value.copy(
                            shops = shops,
                            filteredShops = shops,
                            isLoading = false,
                        )
                    }
                    is com.nearby.app.data.network.NetworkResult.Error -> {
                        _state.value = _state.value.copy(isLoading = false)
                    }
                }
            }
        }
    }


    fun onSearchChange(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
        applyFilters()
    }

    fun onCategoryChange(category: String) {
        _state.value = _state.value.copy(selectedCategory = category)
        applyFilters()
    }

    private fun applyFilters() {
        val s = _state.value
        var filtered = s.shops
        if (s.selectedCategory != "All") {
            filtered = filtered.filter {
                it.category.equals(s.selectedCategory, ignoreCase = true)
            }
        }
        if (s.searchQuery.isNotBlank()) {
            filtered = filtered.filter {
                it.name.contains(s.searchQuery, ignoreCase = true) ||
                it.category.contains(s.searchQuery, ignoreCase = true)
            }
        }
        _state.value = s.copy(filteredShops = filtered)
    }
}

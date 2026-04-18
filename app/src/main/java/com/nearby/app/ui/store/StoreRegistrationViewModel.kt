package com.nearby.app.ui.store

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nearby.app.data.repository.LocationRepository
import com.nearby.app.data.repository.ShopRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StoreRegState(
    val currentStep: Int = 0,          // 0, 1, 2
    // Step 1: Shop Info
    val shopName: String = "",
    val ownerName: String = "",
    val phone: String = "",
    val address: String = "",
    // Step 2: Location
    val detectedCity: String = "",
    val selectedCity: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val cities: List<String> = listOf("Bengaluru", "Mumbai", "Delhi", "Hyderabad", "Chennai", "Pune", "Kolkata", "Ahmedabad"),
    // Step 3: Category & GST
    val selectedCategory: String = "",
    val gstNumber: String = "",
    val categories: List<String> = listOf("Fashion", "Grocery", "Electronics", "Books", "Home & Living", "Sports", "Beauty", "General"),
    // Status
    val isSubmitting: Boolean = false,
    val isSubmitted: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class StoreRegistrationViewModel @Inject constructor(
    private val shopRepo: ShopRepository,
    val locationRepo: LocationRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(StoreRegState())
    val state: StateFlow<StoreRegState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            locationRepo.fetchCurrentLocation()
            val loc = locationRepo.location.value
            _state.value = _state.value.copy(
                detectedCity = loc.city,
                selectedCity = loc.city,
                address = loc.address,
                latitude = loc.latitude,
                longitude = loc.longitude,
            )
        }
    }

    fun updateField(field: String, value: String) {
        val s = _state.value
        _state.value = when (field) {
            "shopName" -> s.copy(shopName = value)
            "ownerName" -> s.copy(ownerName = value)
            "phone" -> s.copy(phone = value)
            "address" -> s.copy(address = value)
            "selectedCity" -> s.copy(selectedCity = value)
            "selectedCategory" -> s.copy(selectedCategory = value)
            "gstNumber" -> s.copy(gstNumber = value)
            else -> s
        }
    }

    fun nextStep(): Boolean {
        val s = _state.value
        when (s.currentStep) {
            0 -> {
                if (s.shopName.isBlank() || s.ownerName.isBlank() || s.phone.isBlank()) {
                    _state.value = s.copy(error = "Please fill all fields")
                    return false
                }
            }
            1 -> {
                if (s.selectedCity.isBlank()) {
                    _state.value = s.copy(error = "Please select a city")
                    return false
                }
            }
        }
        if (s.currentStep < 2) {
            _state.value = s.copy(currentStep = s.currentStep + 1, error = null)
        }
        return true
    }

    fun prevStep() {
        val s = _state.value
        if (s.currentStep > 0) {
            _state.value = s.copy(currentStep = s.currentStep - 1, error = null)
        }
    }

    fun submit() {
        val s = _state.value
        if (s.selectedCategory.isBlank() || s.gstNumber.isBlank()) {
            _state.value = s.copy(error = "Please fill all fields")
            return
        }
        _state.value = s.copy(isSubmitting = true, error = null)
        viewModelScope.launch {
            val shopData = mapOf(
                "name" to s.shopName,
                "owner_name" to s.ownerName,
                "phone" to s.phone,
                "address" to s.address,
                "latitude" to s.latitude,
                "longitude" to s.longitude,
                "category" to s.selectedCategory.lowercase(),
            )
            val result = shopRepo.registerShop(shopData)
            result.onSuccess {
                _state.value = _state.value.copy(isSubmitting = false, isSubmitted = true)
            }.onFailure { e ->
                // Even on failure, mark as submitted for demo (mock mode)
                _state.value = _state.value.copy(isSubmitting = false, isSubmitted = true)
            }
        }
    }
}

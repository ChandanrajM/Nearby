package com.nearby.app.ui.store

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nearby.app.data.network.NetworkResult
import com.nearby.app.data.repository.ShopRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StoreQRState(
    val qrImageUrl: String? = null,
    val deepLink: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class StoreQRViewModel @Inject constructor(
    private val shopRepo: ShopRepository
) : ViewModel() {

    private val _state = MutableStateFlow(StoreQRState())
    val state: StateFlow<StoreQRState> = _state.asStateFlow()

    fun loadQrCode(shopId: String) {
        // The backend returns a raw PNG, so we don't need a Retrofit JSON call.
        // We can just construct the URL and let Coil fetch and cache it natively!
        val qrUrl = com.nearby.app.BuildConfig.BASE_URL + "shops/$shopId/qr"
        val link = "https://nearby.app/shop/$shopId"
        
        _state.value = _state.value.copy(
            isLoading = false,
            qrImageUrl = qrUrl,
            deepLink = link,
            error = null
        )
    }
}

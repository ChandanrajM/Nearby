package com.nearby.app.ui.scanner

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ScannerViewModel @Inject constructor() : ViewModel() {

    private val _scanResult = MutableStateFlow<String?>(null)
    val scanResult: StateFlow<String?> = _scanResult.asStateFlow()

    private val _isScanning = MutableStateFlow(true)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    fun onScanResult(shopId: String) {
        _scanResult.value = shopId
        _isScanning.value = false
    }

    fun resetScan() {
        _scanResult.value = null
        _isScanning.value = true
    }

    /**
     * Extract shop ID from QR code content.
     * Expected formats:
     *  - https://nearby.app/shop/{shopId}
     *  - shop-{uuid}
     *  - raw UUID
     */
    fun extractShopId(qrValue: String): String? {
        return when {
            qrValue.contains("/shop/") -> qrValue.substringAfterLast("/shop/").trim()
            qrValue.startsWith("shop-") -> qrValue.trim()
            qrValue.length in 4..100 -> qrValue.trim()
            else -> null
        }
    }
}

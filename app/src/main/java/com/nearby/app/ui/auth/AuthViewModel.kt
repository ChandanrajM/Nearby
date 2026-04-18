package com.nearby.app.ui.auth

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class AuthUiState(
    val phone: String = "",
    val otp: String = "",
    val isOtpSent: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    fun onPhoneChange(phone: String) {
        if (phone.length <= 10 && phone.all { it.isDigit() }) {
            _state.value = _state.value.copy(phone = phone, error = null)
        }
    }

    fun onOtpChange(otp: String) {
        if (otp.length <= 6 && otp.all { it.isDigit() }) {
            _state.value = _state.value.copy(otp = otp, error = null)
        }
    }

    fun sendOtp() {
        val phone = _state.value.phone
        if (phone.length != 10) {
            _state.value = _state.value.copy(error = "Enter a valid 10-digit number")
            return
        }
        // TODO: Call backend /auth/send-otp when available
        _state.value = _state.value.copy(isOtpSent = true, error = null)
    }

    fun verifyOtp(): Boolean {
        val otp = _state.value.otp
        if (otp.length != 6) {
            _state.value = _state.value.copy(error = "Enter the 6-digit OTP")
            return false
        }
        // TODO: Call backend /auth/verify-otp when available
        // For now, accept any 6-digit OTP
        return true
    }
}

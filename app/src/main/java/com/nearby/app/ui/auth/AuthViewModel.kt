package com.nearby.app.ui.auth

import androidx.lifecycle.viewModelScope
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.nearby.app.BuildConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val phone: String = "",
    val otp: String = "",
    val isOtpSent: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepo: com.nearby.app.data.repository.AuthRepository
) : androidx.lifecycle.ViewModel() {

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
        
        viewModelScope.launch {
            authRepo.sendOtp(phone).collect { result ->
                when (result) {
                    is com.nearby.app.data.network.NetworkResult.Loading -> {
                        _state.value = _state.value.copy(isLoading = true, error = null)
                    }
                    is com.nearby.app.data.network.NetworkResult.Success -> {
                        _state.value = _state.value.copy(isLoading = false, isOtpSent = true)
                    }
                    is com.nearby.app.data.network.NetworkResult.Error -> {
                        _state.value = _state.value.copy(isLoading = false, error = result.message)
                    }
                }
            }
        }
    }

    fun verifyOtp(onSuccess: () -> Unit) {
        val otp = _state.value.otp
        val phone = _state.value.phone
        if (otp.length != 6) {
            _state.value = _state.value.copy(error = "Enter the 6-digit OTP")
            return
        }

        viewModelScope.launch {
            authRepo.verifyOtp(phone, otp).collect { result ->
                when (result) {
                    is com.nearby.app.data.network.NetworkResult.Loading -> {
                        _state.value = _state.value.copy(isLoading = true, error = null)
                    }
                    is com.nearby.app.data.network.NetworkResult.Success -> {
                        _state.value = _state.value.copy(isLoading = false)
                        onSuccess()
                    }
                    is com.nearby.app.data.network.NetworkResult.Error -> {
                        _state.value = _state.value.copy(isLoading = false, error = result.message)
                    }
                }
            }
        }
    }

    fun loginWithGoogle(context: Context, onSuccess: () -> Unit) {
        val credentialManager = CredentialManager.create(context)
        
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
            .setAutoSelectEnabled(true)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true, error = null)
                val result = credentialManager.getCredential(context = context, request = request)
                
                val credential = result.credential
                
                val googleIdTokenCredential = when {
                    credential is com.google.android.libraries.identity.googleid.GoogleIdTokenCredential -> credential
                    credential is androidx.credentials.CustomCredential && 
                    credential.type == com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL -> {
                        com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.createFrom(credential.data)
                    }
                    else -> null
                }

                if (googleIdTokenCredential != null) {
                    val idToken = googleIdTokenCredential.idToken
                    
                    authRepo.loginWithGoogle(idToken).collect { networkResult ->
                        when (networkResult) {
                            is com.nearby.app.data.network.NetworkResult.Loading -> {
                                _state.value = _state.value.copy(isLoading = true)
                            }
                            is com.nearby.app.data.network.NetworkResult.Success -> {
                                _state.value = _state.value.copy(isLoading = false)
                                onSuccess()
                            }
                            is com.nearby.app.data.network.NetworkResult.Error -> {
                                _state.value = _state.value.copy(isLoading = false, error = networkResult.message)
                            }
                        }
                    }
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false, 
                        error = "Unexpected credential type: ${credential.type}"
                    )
                }

            } catch (e: GetCredentialException) {
                _state.value = _state.value.copy(isLoading = false, error = e.message ?: "Failed to sign in")
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = "An error occurred")
            }
        }
    }
}



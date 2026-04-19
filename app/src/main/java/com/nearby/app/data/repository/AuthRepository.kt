package com.nearby.app.data.repository

import com.nearby.app.data.model.User
import com.nearby.app.data.network.ApiService
import com.nearby.app.data.network.LoginRequest
import com.nearby.app.data.network.NetworkResult
import com.nearby.app.data.network.TokenManager
import com.nearby.app.data.network.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(tokenManager.isLoggedIn())
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    /** Send OTP to the given phone number */
    fun sendOtp(phone: String): Flow<NetworkResult<Unit>> = safeApiCall {
        apiService.sendOtp(mapOf("phone" to phone))
    }

    /** Verify the OTP code and login */
    fun verifyOtp(phone: String, otp: String): Flow<NetworkResult<com.nearby.app.data.network.AuthResponse>> = 
        safeApiCall {
            apiService.login(LoginRequest(phone, otp))
        }.onEach { result ->
            if (result is NetworkResult.Success) {
                handleLoginSuccess(result.data, phone)
            }
        }

    /** Login with Google ID Token */
    fun loginWithGoogle(idToken: String): Flow<NetworkResult<com.nearby.app.data.network.AuthResponse>> =
        safeApiCall {
            apiService.googleLogin(com.nearby.app.data.network.GoogleLoginRequest(idToken))
        }.onEach { result ->
            if (result is NetworkResult.Success) {
                handleLoginSuccess(result.data, "Google User")
            }
        }

    private fun handleLoginSuccess(response: com.nearby.app.data.network.AuthResponse, identifier: String) {
        tokenManager.saveTokens(response.accessToken, response.refreshToken, response.userId)
        _isLoggedIn.value = true
        fetchProfile()
    }

    /** Fetch the full user profile from the backend */
    fun fetchProfile() {
        if (!tokenManager.isLoggedIn()) return

        // This is a fire-and-forget for the state flow
        serviceScope.launch {
            safeApiCall { apiService.getUserProfile() }.collect { result ->
                if (result is NetworkResult.Success) {
                    _currentUser.value = result.data
                }
            }
        }
    }

    /** Update user profile data */
    fun updateProfile(name: String, email: String, avatarUrl: String? = null): Flow<NetworkResult<User>> = safeApiCall {
        apiService.updateUserProfile(UserUpdateProfileRequest(fullName = name, email = email, avatarUrl = avatarUrl))
    }.onEach { result ->
        if (result is NetworkResult.Success) {
            _currentUser.value = result.data
        }
    }


    fun signOut() {
        tokenManager.clearTokens()
        _currentUser.value = null
        _isLoggedIn.value = false
    }

    fun getUser(): User? = _currentUser.value
    
    fun getUserId(): String? = _currentUser.value?.id ?: tokenManager.getUserId()
}

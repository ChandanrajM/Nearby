package com.nearby.app.data.repository

import com.nearby.app.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    /**
     * Send OTP to the given phone number.
     * TODO: Connect to backend POST /auth/send-otp when available.
     */
    suspend fun sendOtp(phone: String): Result<Boolean> {
        // Mock: always succeeds
        return Result.success(true)
    }

    /**
     * Verify the OTP code.
     * TODO: Connect to backend POST /auth/verify-otp when available.
     */
    suspend fun verifyOtp(phone: String, otp: String): Result<User> {
        // Mock: accept any 6-digit OTP
        val user = User(
            id = "user-${phone.takeLast(4)}",
            phone = "+91$phone",
            name = "User",
        )
        _currentUser.value = user
        _isLoggedIn.value = true
        return Result.success(user)
    }

    /**
     * Google Sign-In.
     * TODO: Integrate with Supabase Auth + Google Credential Manager.
     * Store your Web OAuth Client ID in Supabase Dashboard → Authentication → Providers → Google.
     */
    suspend fun signInWithGoogle(idToken: String): Result<User> {
        val user = User(
            id = "google-user",
            name = "Google User",
            email = "user@gmail.com",
        )
        _currentUser.value = user
        _isLoggedIn.value = true
        return Result.success(user)
    }

    fun signOut() {
        _currentUser.value = null
        _isLoggedIn.value = false
    }

    fun getUser(): User? = _currentUser.value
}

package com.nearby.app.data.network

import android.util.Log
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AuthInterceptor — runs on EVERY outgoing HTTP request.
 *
 * Reads the saved JWT access token from TokenManager and adds:
 *   Authorization: Bearer <token>
 *
 * You never have to manually add auth headers in ApiService — this handles it automatically.
 */
@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // If there's no token (user not logged in), send request as-is
        val token = tokenManager.getAccessToken()
            ?: return chain.proceed(originalRequest)

        // Attach the token to the request header
        val authenticatedRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .header("Content-Type", "application/json")
            .build()

        return chain.proceed(authenticatedRequest)
    }
}

/**
 * TokenRefreshAuthenticator — runs ONLY when the server returns 401 (Unauthorized).
 *
 * This means the access token expired. It automatically:
 * 1. Calls the backend to get a new access token using the refresh token
 * 2. Saves the new tokens
 * 3. Retries the original request with the new token
 *
 * If refresh also fails (refresh token expired), the user is logged out.
 */
@Singleton
class TokenRefreshAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    private val apiService: dagger.Lazy<ApiService>  // Lazy to avoid circular dependency
) : Authenticator {

    // Tracks how many times we've tried to refresh — prevents infinite loops
    private var refreshAttempts = 0
    private val maxRefreshAttempts = 1

    override fun authenticate(route: Route?, response: Response): Request? {
        // If we already tried refreshing once and still got 401, give up
        if (refreshAttempts >= maxRefreshAttempts) {
            refreshAttempts = 0
            tokenManager.clearTokens()  // Force logout
            Log.w("AuthAuthenticator", "Token refresh failed — user logged out")
            return null
        }

        val refreshToken = tokenManager.getRefreshToken()
        if (refreshToken == null) {
            tokenManager.clearTokens()
            return null
        }

        return try {
            refreshAttempts++

            // Call the refresh endpoint synchronously (we're already on a background thread)
            val refreshResponse = runBlocking {
                apiService.get().refreshToken(RefreshTokenRequest(refreshToken))
            }

            if (refreshResponse.isSuccessful) {
                val newTokens = refreshResponse.body()!!
                tokenManager.saveTokens(newTokens.accessToken, newTokens.refreshToken)
                refreshAttempts = 0

                // Retry the original request with the new access token
                response.request.newBuilder()
                    .header("Authorization", "Bearer ${newTokens.accessToken}")
                    .build()
            } else {
                // Refresh failed — log out
                tokenManager.clearTokens()
                null
            }
        } catch (e: Exception) {
            Log.e("AuthAuthenticator", "Token refresh error: ${e.message}")
            tokenManager.clearTokens()
            null
        }
    }
}

package com.nearby.app.data.network

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

// ─────────────────────────────────────────
// NetworkResult — a wrapper for API call results
//
// Every API call can be in one of three states:
//   Loading  — request is in-flight
//   Success  — got data back
//   Error    — something went wrong (network, server, etc.)
//
// Use this in your ViewModel to drive the UI state.
// ─────────────────────────────────────────

sealed class NetworkResult<out T> {
    object Loading : NetworkResult<Nothing>()
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val message: String, val code: Int? = null) : NetworkResult<Nothing>()
}

/**
 * safeApiCall — wraps a Retrofit call and converts it to a NetworkResult Flow.
 *
 * Usage in a Repository:
 *   fun getShop(id: String) = safeApiCall { apiService.getShop(id) }
 *
 * Usage in a ViewModel:
 *   repo.getShop(id).collect { result ->
 *       when (result) {
 *           is NetworkResult.Loading -> showSpinner()
 *           is NetworkResult.Success -> showShop(result.data)
 *           is NetworkResult.Error   -> showError(result.message)
 *       }
 *   }
 */
fun <T> safeApiCall(apiCall: suspend () -> Response<T>): Flow<NetworkResult<T>> = flow {
    emit(NetworkResult.Loading)
    try {
        val response = apiCall()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                emit(NetworkResult.Success(body))
            } else {
                emit(NetworkResult.Error("Empty response from server", response.code()))
            }
        } else {
            val errorMsg = when (response.code()) {
                400 -> "Bad request — check your input"
                401 -> "Session expired — please log in again"
                403 -> "You don't have permission to do that"
                404 -> "Not found"
                429 -> "Too many requests — slow down"
                500 -> "Server error — try again later"
                else -> "Something went wrong (${response.code()})"
            }
            emit(NetworkResult.Error(errorMsg, response.code()))
        }
    } catch (e: java.net.UnknownHostException) {
        emit(NetworkResult.Error("No internet connection"))
    } catch (e: java.net.SocketTimeoutException) {
        emit(NetworkResult.Error("Connection timed out — check your internet"))
    } catch (e: javax.net.ssl.SSLHandshakeException) {
        // This fires when certificate pinning fails (possible MITM attack)
        emit(NetworkResult.Error("Secure connection failed"))
    } catch (e: Exception) {
        emit(NetworkResult.Error(e.message ?: "Unknown error"))
    }
}

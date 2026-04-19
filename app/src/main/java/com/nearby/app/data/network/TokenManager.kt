package com.nearby.app.data.network

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Stores and retrieves JWT tokens securely.
 * Uses EncryptedSharedPreferences — tokens are encrypted on disk.
 * NEVER use regular SharedPreferences for tokens.
 */
@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val PREFS_FILE = "nearby_secure_prefs"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_ID = "user_id"
    }

    // Creates or opens the encrypted preferences file
    private val prefs by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            context,
            PREFS_FILE,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    /** Save tokens and user ID after login */
    fun saveTokens(accessToken: String, refreshToken: String, userId: String? = null) {
        val editor = prefs.edit()
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .putString(KEY_REFRESH_TOKEN, refreshToken)
        
        if (userId != null) {
            editor.putString(KEY_USER_ID, userId)
        }
        editor.apply()
    }

    /** Returns the access token, or null if the user is not logged in */
    fun getAccessToken(): String? = prefs.getString(KEY_ACCESS_TOKEN, null)

    /** Returns the refresh token, or null if the user is not logged in */
    fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH_TOKEN, null)

    /** Returns the saved user ID */
    fun getUserId(): String? = prefs.getString(KEY_USER_ID, null)

    /** Call this on logout — wipes tokens and user ID */
    fun clearTokens() {
        prefs.edit()
            .remove(KEY_ACCESS_TOKEN)
            .remove(KEY_REFRESH_TOKEN)
            .remove(KEY_USER_ID)
            .apply()
    }

    /** True if the user has a saved access token */
    fun isLoggedIn(): Boolean = getAccessToken() != null
}

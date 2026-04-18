package com.nearby.app.ui.account

import androidx.lifecycle.ViewModel
import com.nearby.app.data.model.User
import com.nearby.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val authRepo: AuthRepository,
) : ViewModel() {

    val currentUser: StateFlow<User?> = authRepo.currentUser

    // Store status: none, pending, approved, rejected
    // TODO: Fetch from backend when shop registration endpoint is ready
    var storeStatus: String = "none"
        private set

    var shopId: String? = null
        private set

    fun hasStore(): Boolean = storeStatus == "approved"
    fun isUnderReview(): Boolean = storeStatus == "pending"

    fun signOut() {
        authRepo.signOut()
    }

    // Called after successful store registration
    fun onStoreRegistered() {
        storeStatus = "pending"
    }

    // Called when admin approves (from push notification or polling)
    fun onStoreApproved(shopId: String) {
        storeStatus = "approved"
        this.shopId = shopId
    }
}

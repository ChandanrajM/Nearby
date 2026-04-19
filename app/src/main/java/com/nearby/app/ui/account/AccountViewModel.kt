package com.nearby.app.ui.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nearby.app.data.model.User
import com.nearby.app.data.network.NetworkResult
import com.nearby.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AccountState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val authRepo: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AccountState())
    val state: StateFlow<AccountState> = _state.asStateFlow()

    init {
        _state.value = _state.value.copy(user = authRepo.currentUser.value)
        
        viewModelScope.launch {
            authRepo.currentUser.collect { user ->
                _state.value = _state.value.copy(user = user)
            }
        }
        
        // Fetch fresh profile on init
        authRepo.fetchProfile()
    }

    fun updateProfile(name: String, email: String) {
        _state.value = _state.value.copy(isSaving = true, error = null, successMessage = null)
        viewModelScope.launch {
            authRepo.updateProfile(name, email).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        _state.value = _state.value.copy(isSaving = true)
                    }
                    is NetworkResult.Success -> {
                        _state.value = _state.value.copy(
                            isSaving = false,
                            successMessage = "Profile updated successfully"
                        )
                    }
                    is NetworkResult.Error -> {
                        _state.value = _state.value.copy(
                            isSaving = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    fun clearMessages() {
        _state.value = _state.value.copy(error = null, successMessage = null)
    }
}

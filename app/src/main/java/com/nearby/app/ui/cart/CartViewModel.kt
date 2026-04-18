package com.nearby.app.ui.cart

import androidx.lifecycle.ViewModel
import com.nearby.app.data.model.CartItem
import com.nearby.app.data.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepo: CartRepository,
) : ViewModel() {

    val items: StateFlow<List<CartItem>> = cartRepo.items

    val totalPrice: Double get() = cartRepo.totalPrice
    val itemCount: Int get() = cartRepo.itemCount

    fun updateQuantity(productId: String, quantity: Int) {
        cartRepo.updateQuantity(productId, quantity)
    }

    fun removeItem(productId: String) {
        cartRepo.removeFromCart(productId)
    }

    fun clearCart() {
        cartRepo.clearCart()
    }
}

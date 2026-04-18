package com.nearby.app.data.repository

import com.nearby.app.data.model.CartItem
import com.nearby.app.data.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepository @Inject constructor() {

    private val _items = MutableStateFlow<List<CartItem>>(emptyList())
    val items: StateFlow<List<CartItem>> = _items.asStateFlow()

    val totalPrice: Double
        get() = _items.value.sumOf { it.totalPrice }

    val itemCount: Int
        get() = _items.value.sumOf { it.quantity }

    fun addToCart(product: Product) {
        _items.update { currentItems ->
            val existing = currentItems.find { it.product.id == product.id }
            if (existing != null) {
                currentItems.map {
                    if (it.product.id == product.id) it.copy(quantity = it.quantity + 1) else it
                }
            } else {
                currentItems + CartItem(product = product, quantity = 1)
            }
        }
    }

    fun removeFromCart(productId: String) {
        _items.update { it.filter { item -> item.product.id != productId } }
    }

    fun updateQuantity(productId: String, quantity: Int) {
        if (quantity <= 0) {
            removeFromCart(productId)
            return
        }
        _items.update { currentItems ->
            currentItems.map {
                if (it.product.id == productId) it.copy(quantity = quantity) else it
            }
        }
    }

    fun clearCart() {
        _items.update { emptyList() }
    }
}

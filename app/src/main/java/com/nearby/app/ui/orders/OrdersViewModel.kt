package com.nearby.app.ui.orders

import androidx.lifecycle.ViewModel
import com.nearby.app.data.model.Order
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor() : ViewModel() {

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadOrders()
    }

    private fun loadOrders() {
        // TODO: Fetch from backend when orders endpoint exists
        // Mock data for now
        _orders.value = listOf(
            Order(
                id = "ord-1",
                shop_name = "Acme Vintage",
                total = 365.0,
                status = "delivered",
                created_at = "2026-04-18",
            ),
            Order(
                id = "ord-2",
                shop_name = "Fresh Mart",
                total = 120.0,
                status = "placed",
                created_at = "2026-04-17",
            ),
            Order(
                id = "ord-3",
                shop_name = "TechZone",
                total = 2499.0,
                status = "confirmed",
                created_at = "2026-04-16",
            ),
        )
    }

    fun refreshOrders() {
        loadOrders()
    }
}

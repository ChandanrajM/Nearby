package com.nearby.app.data.model

data class Order(
    val id: String = "",
    val shop_id: String = "",
    val shop_name: String = "",
    val items: List<CartItem> = emptyList(),
    val total: Double = 0.0,
    val status: String = "placed",  // placed, confirmed, delivered, cancelled
    val created_at: String = "",
)

package com.nearby.app.data.model

data class Product(
    val id: String = "",
    val shop_id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val description: String = "",
    val stock: Int = 0,
    val image_url: String = "",
    val category: String = "",
    val is_featured: Boolean = false,
    val created_at: String = "",
)

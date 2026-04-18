package com.nearby.app.data.model

data class Shop(
    val id: String = "",
    val name: String = "",
    val owner_name: String = "",
    val phone: String = "",
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val category: String = "general",
    val status: String = "pending",       // pending, approved, rejected
    val gst_number: String = "",
    val city: String = "",
    val image_url: String = "",
    val created_at: String = "",
    val distance_km: Double? = null,
)

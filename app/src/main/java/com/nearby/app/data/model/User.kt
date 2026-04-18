package com.nearby.app.data.model

data class User(
    val id: String = "",
    val phone: String = "",
    val name: String = "",
    val email: String = "",
    val avatar_url: String = "",
    val shop_id: String? = null,   // non-null if user owns a store
)

package com.nearby.app.data.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id") val id: String = "",
    @SerializedName("phone") val phone: String = "",
    @SerializedName("full_name") val name: String = "",
    @SerializedName("email") val email: String = "",
    @SerializedName("avatar_url") val avatar_url: String = "",
    @SerializedName("shop_id") val shop_id: String? = null,   // non-null if user owns a store
)

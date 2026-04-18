package com.nearby.app.data.model

import com.google.gson.annotations.SerializedName

/**
 * Product model — matches backend schema
 */
data class Product(
    @SerializedName("id") val id: String,
    @SerializedName("shop_id") val shopId: String,
    @SerializedName("name") val name: String,
    @SerializedName("price") val price: Double,
    @SerializedName("category") val category: String?,
    @SerializedName("image_url") val imageUrl: String,
    @SerializedName("processed_image_url") val processedImageUrl: String? = null,
    @SerializedName("status") val status: String = "ready",        // "pending" | "ready" | "failed"
    @SerializedName("is_available") val isAvailable: Boolean = true
)

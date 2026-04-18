package com.nearby.app.data.model

import com.google.gson.annotations.SerializedName

/**
 * Shop model — matches backend schema
 */
data class Shop(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("category") val category: String,
    @SerializedName("owner_id") val ownerId: String,
    @SerializedName("address") val address: String,
    @SerializedName("lat") val lat: Double,
    @SerializedName("lng") val lng: Double,
    @SerializedName("distance_km") val distanceKm: Double? = null,
    @SerializedName("is_open") val isOpen: Boolean = true,
    @SerializedName("qr_code_url") val qrCodeUrl: String? = null,
    
    // Kept for backward compatibility with existing UI if needed
    @SerializedName("owner_name") val oldOwnerName: String? = null,
    @SerializedName("city") val city: String? = null
)

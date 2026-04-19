package com.nearby.app.data.repository

import com.nearby.app.data.model.Shop
import com.nearby.app.data.network.ApiService
import com.nearby.app.data.network.CreateShopRequest
import com.nearby.app.data.network.NetworkResult
import com.nearby.app.data.network.safeApiCall
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShopRepository @Inject constructor(
    private val api: ApiService,
) {
    /** Get a single shop by ID */
    fun getShop(shopId: String) = safeApiCall {
        api.getShop(shopId)
    }

    /** Get shops near the user's location */
    fun getNearbyShops(
        lat: Double,
        lng: Double,
        radiusKm: Double = 5.0,
        category: String? = null
    ) = safeApiCall {
        api.getNearbyShops(lat, lng, radiusKm, category)
    }

    /** Register a new shop */
    fun createShop(
        name: String,
        ownerName: String,
        phone: String,
        address: String,
        city: String,
        lat: Double,
        lng: Double,
        category: String,
        gstNumber: String
    ) = safeApiCall {
        api.createShop(
            CreateShopRequest(
                name = name,
                ownerName = ownerName,
                phone = phone,
                address = address,
                city = city,
                lat = lat,
                lng = lng,
                category = category,
                gstNumber = gstNumber
            )
        )
    }

    /** Get the QR code for a shop */
    fun getShopQrCode(shopId: String) = safeApiCall {
        api.getShopQrCode(shopId)
    }

    /** Get global trending products for discovery */
    fun getTrendingProducts(limit: Int = 10) = safeApiCall {
        api.getTrendingProducts(limit)
    }
}

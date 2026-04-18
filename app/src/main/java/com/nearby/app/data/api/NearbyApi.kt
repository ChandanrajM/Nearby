package com.nearby.app.data.api

import com.nearby.app.data.model.Product
import com.nearby.app.data.model.Shop
import okhttp3.ResponseBody
import retrofit2.http.*

interface NearbyApi {

    // ── Shops ──────────────────────────────────────────────────────────
    @POST("shops")
    suspend fun registerShop(@Body shop: Map<String, @JvmSuppressWildcards Any>): Map<String, Any>

    @GET("shops/{shopId}")
    suspend fun getShop(@Path("shopId") shopId: String): Shop

    @GET("shops/nearby")
    suspend fun getNearbyShops(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("radius") radius: Double = 10.0,
    ): List<Shop>

    // ── Products ───────────────────────────────────────────────────────
    @POST("products")
    suspend fun addProduct(@Body product: Map<String, @JvmSuppressWildcards Any>): Map<String, Any>

    @GET("products/shop/{shopId}")
    suspend fun getProductsByShop(@Path("shopId") shopId: String): List<Product>

    // ── QR ──────────────────────────────────────────────────────────────
    @GET("shops/{shopId}/qr")
    suspend fun getShopQr(@Path("shopId") shopId: String): ResponseBody

    // ── Health ──────────────────────────────────────────────────────────
    @GET("/")
    suspend fun healthCheck(): Map<String, String>
}

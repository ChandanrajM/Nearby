package com.nearby.app.data.api

import com.nearby.app.data.model.Product
import com.nearby.app.data.model.Shop
import okhttp3.ResponseBody
import retrofit2.http.*

/**
 * Retrofit service interface for Nearby backend API
 * Base URL: BuildConfig.API_URL (Railway deployment)
 */
interface ApiService {

    // ── Shops ──────────────────────────────────────────────────────────────

    /** POST /shops — Register a new shop */
    @POST("shops")
    suspend fun registerShop(
        @Body shop: Map<String, @JvmSuppressWildcards Any>
    ): Map<String, Any>

    /** GET /shops/{id} — Get a shop by ID */
    @GET("shops/{id}")
    suspend fun getShop(@Path("id") shopId: String): Shop

    /** GET /shops/nearby?lat=&lng=&radius= — Nearby shops by location */
    @GET("shops/nearby")
    suspend fun getNearbyShops(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("radius") radius: Double = 10.0,
    ): List<Shop>

    /** GET /shops/{id}/qr — Generate QR code PNG for shop */
    @GET("shops/{id}/qr")
    @Streaming
    suspend fun getShopQr(@Path("id") shopId: String): ResponseBody

    // ── Products ───────────────────────────────────────────────────────────

    /** POST /products — Add a product to a shop */
    @POST("products")
    suspend fun addProduct(
        @Body product: Map<String, @JvmSuppressWildcards Any>
    ): Map<String, Any>

    /** GET /products/shop/{shop_id} — Get all products for a shop */
    @GET("products/shop/{shop_id}")
    suspend fun getProductsByShop(@Path("shop_id") shopId: String): List<Product>

    /** PUT /products/{id} — Update a product */
    @PUT("products/{id}")
    suspend fun updateProduct(
        @Path("id") productId: String,
        @Body product: Map<String, @JvmSuppressWildcards Any>
    ): Map<String, Any>

    /** DELETE /products/{id} — Delete a product */
    @DELETE("products/{id}")
    suspend fun deleteProduct(@Path("id") productId: String): Map<String, Any>

    // ── Health ──────────────────────────────────────────────────────────────

    @GET("/")
    suspend fun healthCheck(): Map<String, String>
}

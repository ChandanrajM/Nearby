package com.nearby.app.data.network

import com.google.gson.annotations.SerializedName
import com.nearby.app.data.model.Product
import com.nearby.app.data.model.Shop
import retrofit2.Response
import retrofit2.http.*

// ─────────────────────────────────────────
// REQUEST / RESPONSE DATA CLASSES
// ─────────────────────────────────────────

data class LoginRequest(
    @SerializedName("phone") val phone: String,
    @SerializedName("otp") val otp: String
)

data class GoogleLoginRequest(
    @SerializedName("id_token") val idToken: String
)

data class AuthResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String,
    @SerializedName("user_id") val userId: String
)

data class RefreshTokenRequest(
    @SerializedName("refresh_token") val refreshToken: String
)

data class CreateShopRequest(
    @SerializedName("name") val name: String,
    @SerializedName("owner_name") val ownerName: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("address") val address: String,
    @SerializedName("city") val city: String,
    @SerializedName("lat") val lat: Double,
    @SerializedName("lng") val lng: Double,
    @SerializedName("category") val category: String,
    @SerializedName("gst_number") val gstNumber: String
)


data class UserUpdateProfileRequest(
    @SerializedName("full_name") val fullName: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("avatar_url") val avatarUrl: String? = null
)

data class CreateProductRequest(
    @SerializedName("shop_id") val shopId: String,
    @SerializedName("name") val name: String,
    @SerializedName("price") val price: Double,
    @SerializedName("category") val category: String?,
    @SerializedName("image_url") val imageUrl: String
)

data class UpdateProductRequest(
    @SerializedName("name") val name: String? = null,
    @SerializedName("price") val price: Double? = null,
    @SerializedName("is_available") val isAvailable: Boolean? = null
)

data class NearbyShopsResponse(
    @SerializedName("shops") val shops: List<Shop>,
    @SerializedName("total") val total: Int
)

data class UploadUrlResponse(
    @SerializedName("upload_url") val uploadUrl: String,
    @SerializedName("image_url") val imageUrl: String
)

data class UploadResponse(
    @SerializedName("image_url") val imageUrl: String
)

data class QrCodeResponse(
    @SerializedName("qr_image_url") val qrImageUrl: String,
    @SerializedName("deep_link") val deepLink: String
)

// ─────────────────────────────────────────
// API SERVICE INTERFACE
// ─────────────────────────────────────────

interface ApiService {

    // ── Auth ──────────────────────────────

    /** Send OTP to phone number */
    @POST("auth/send-otp")
    suspend fun sendOtp(@Body body: Map<String, String>): Response<Unit>

    /** Verify OTP — returns JWT tokens on success */
    @POST("auth/verify-otp")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    /** Exchange refresh token for new access token */
    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<AuthResponse>

    /** Google Login — returns JWT tokens on success */
    @POST("auth/google")
    suspend fun googleLogin(@Body request: GoogleLoginRequest): Response<AuthResponse>

    /** Get global trending products for discovery feed */
    @GET("products/trending")
    suspend fun getTrendingProducts(@Query("limit") limit: Int = 10): Response<List<Product>>


    // ── Orders ────────────────────────────

    /** Get orders for the current user */
    @GET("orders")
    suspend fun getOrders(@Query("user_id") userId: String): Response<List<com.nearby.app.data.model.Order>>

    /** Get details for a specific order */
    @GET("orders/{id}")
    suspend fun getOrderDetails(@Path("id") orderId: String): Response<com.nearby.app.data.model.Order>


    // ── User / Profile ────────────────────

    /** Get the current user's profile */
    @GET("auth/me")
    suspend fun getUserProfile(): Response<com.nearby.app.data.model.User>

    /** Update the user's profile */
    @PATCH("auth/me")
    suspend fun updateUserProfile(@Body request: UserUpdateProfileRequest): Response<com.nearby.app.data.model.User>


    // ── Shops ─────────────────────────────

    /** Register a new shop (seller only) */
    @POST("shops")
    suspend fun createShop(@Body request: CreateShopRequest): Response<Shop>

    /** Get a specific shop by ID */
    @GET("shops/{id}")
    suspend fun getShop(@Path("id") shopId: String): Response<Shop>

    /** Get shops near a location */
    @GET("shops/nearby")
    suspend fun getNearbyShops(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("radius") radiusKm: Double = 5.0,
        @Query("category") category: String? = null
    ): Response<NearbyShopsResponse>

    /** Get the QR code for a shop */
    @GET("shops/{id}/qr")
    suspend fun getShopQrCode(@Path("id") shopId: String): Response<QrCodeResponse>

    // ── Products ──────────────────────────

    /** Get all products for a shop */
    @GET("products/shop/{shop_id}")
    suspend fun getShopProducts(
        @Path("shop_id") shopId: String,
        @Query("category") category: String? = null
    ): Response<List<Product>>

    /** Add a new product to a shop */
    @POST("products")
    suspend fun createProduct(@Body request: CreateProductRequest): Response<Product>

    /** Update a product */
    @PATCH("products/{id}")
    suspend fun updateProduct(
        @Path("id") productId: String,
        @Body request: UpdateProductRequest
    ): Response<Product>

    /** Delete a product */
    @DELETE("products/{id}")
    suspend fun deleteProduct(@Path("id") productId: String): Response<Unit>

    /** Upload an image file for a product */
    @Multipart
    @POST("upload")
    suspend fun uploadImage(@Part file: okhttp3.MultipartBody.Part): Response<UploadResponse>

    /** Trigger AI enhancement preview */
    @POST("ai/enhance")
    suspend fun triggerAiEnhance(@Body body: Map<String, String>): Response<Map<String, Any>>
}

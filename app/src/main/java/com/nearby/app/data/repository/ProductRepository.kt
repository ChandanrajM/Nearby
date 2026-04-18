package com.nearby.app.data.repository

import com.nearby.app.data.api.ApiService
import com.nearby.app.data.model.Product
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val api: ApiService,
) {
    suspend fun getProductsByShop(shopId: String): Result<List<Product>> {
        return try {
            Result.success(api.getProductsByShop(shopId))
        } catch (e: Exception) {
            Result.success(emptyList())
        }
    }

    suspend fun addProduct(
        shopId: String,
        name: String,
        price: Double,
        description: String = "",
        stock: Int = 0,
        imageUrl: String = "",
        category: String = "general",
    ): Result<Map<String, Any>> {
        return try {
            val body = mapOf<String, Any>(
                "shop_id" to shopId,
                "name" to name,
                "price" to price,
                "description" to description,
                "stock" to stock,
                "image_url" to imageUrl,
                "category" to category,
            )
            Result.success(api.addProduct(body))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProduct(
        productId: String,
        name: String,
        price: Double,
        description: String,
        stock: Int,
        imageUrl: String,
        category: String,
    ): Result<Map<String, Any>> {
        return try {
            val body = mapOf<String, Any>(
                "name" to name,
                "price" to price,
                "description" to description,
                "stock" to stock,
                "image_url" to imageUrl,
                "category" to category,
            )
            Result.success(api.updateProduct(productId, body))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteProduct(productId: String): Result<Map<String, Any>> {
        return try {
            Result.success(api.deleteProduct(productId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

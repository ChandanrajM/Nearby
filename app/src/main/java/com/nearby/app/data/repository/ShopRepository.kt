package com.nearby.app.data.repository

import com.nearby.app.data.api.ApiService
import com.nearby.app.data.model.Product
import com.nearby.app.data.model.Shop
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShopRepository @Inject constructor(
    private val api: ApiService,
) {
    suspend fun getNearbyShops(lat: Double, lng: Double, radius: Double = 10.0): Result<List<Shop>> {
        return try {
            Result.success(api.getNearbyShops(lat, lng, radius))
        } catch (e: Exception) {
            // Return mock data when backend is unreachable
            Result.success(mockShops())
        }
    }

    suspend fun getShop(shopId: String): Result<Shop> {
        return try {
            Result.success(api.getShop(shopId))
        } catch (e: Exception) {
            val mock = mockShops().find { it.id == shopId }
            if (mock != null) Result.success(mock)
            else Result.failure(e)
        }
    }

    suspend fun registerShop(shop: Map<String, Any>): Result<Map<String, Any>> {
        return try {
            Result.success(api.registerShop(shop))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProductsByShop(shopId: String): Result<List<Product>> {
        return try {
            Result.success(api.getProductsByShop(shopId))
        } catch (e: Exception) {
            Result.success(mockProducts(shopId))
        }
    }

    suspend fun addProduct(product: Map<String, Any>): Result<Map<String, Any>> {
        return try {
            Result.success(api.addProduct(product))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Mock data for offline / first-run development ──────────────────────
    private fun mockShops(): List<Shop> = listOf(
        Shop(
            id = "shop-1", name = "Acme Vintage", owner_name = "Rahul",
            phone = "+919999999999", address = "MG Road, Bengaluru",
            latitude = 12.9716, longitude = 77.5946,
            category = "fashion", status = "approved", city = "Bengaluru",
        ),
        Shop(
            id = "shop-2", name = "Fresh Mart", owner_name = "Priya",
            phone = "+918888888888", address = "Koramangala, Bengaluru",
            latitude = 12.9352, longitude = 77.6245,
            category = "grocery", status = "approved", city = "Bengaluru",
        ),
        Shop(
            id = "shop-3", name = "TechZone", owner_name = "Amit",
            phone = "+917777777777", address = "Indiranagar, Bengaluru",
            latitude = 12.9784, longitude = 77.6408,
            category = "electronics", status = "approved", city = "Bengaluru",
        ),
        Shop(
            id = "shop-4", name = "BookNook", owner_name = "Sneha",
            phone = "+916666666666", address = "Jayanagar, Bengaluru",
            latitude = 12.9308, longitude = 77.5838,
            category = "books", status = "approved", city = "Bengaluru",
        ),
    )

    private fun mockProducts(shopId: String): List<Product> = listOf(
        Product(
            id = "p1", shop_id = shopId, name = "Nike Air Max 95 – Mint", price = 280.0,
            description = "Limited edition mint colorway, barely worn", stock = 5,
            category = "just_dropped", is_featured = true,
        ),
        Product(
            id = "p2", shop_id = shopId, name = "Vintage Levi's 501", price = 85.0,
            description = "Classic vintage denim, size 32×32", stock = 12,
            category = "vintage",
        ),
        Product(
            id = "p3", shop_id = shopId, name = "Supreme Box Logo Tee", price = 120.0,
            description = "Authentic Supreme streetwear, size L", stock = 3,
            category = "streetwear",
        ),
        Product(
            id = "p4", shop_id = shopId, name = "Carhartt WIP Jacket", price = 195.0,
            description = "Workwear classic, size M", stock = 8,
            category = "just_dropped",
        ),
        Product(
            id = "p5", shop_id = shopId, name = "Premium Dark Hoodie", price = 75.0,
            description = "100% cotton blend, oversized fit", stock = 20,
            category = "vintage",
        ),
        Product(
            id = "p6", shop_id = shopId, name = "Retro White Sneakers", price = 150.0,
            description = "Old-school profile, clean canvas upper", stock = 6,
            category = "vintage",
        ),
    )
}

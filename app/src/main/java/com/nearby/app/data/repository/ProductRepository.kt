package com.nearby.app.data.repository

import com.nearby.app.data.network.ApiService
import com.nearby.app.data.network.CreateProductRequest
import com.nearby.app.data.network.UpdateProductRequest
import com.nearby.app.data.network.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val api: ApiService,
) {
    /** Get all products for a shop */
    fun getShopProducts(shopId: String, category: String? = null) = safeApiCall {
        api.getShopProducts(shopId, category)
    }

    /** Add a new product to a shop */
    fun addProduct(
        shopId: String,
        name: String,
        price: Double,
        category: String?,
        imageUrl: String
    ) = safeApiCall {
        api.createProduct(CreateProductRequest(shopId, name, price, category, imageUrl))
    }

    /** Update a product (name, price, availability) */
    fun updateProduct(
        productId: String,
        name: String? = null,
        price: Double? = null,
        isAvailable: Boolean? = null
    ) = safeApiCall {
        api.updateProduct(productId, UpdateProductRequest(name, price, isAvailable))
    }

    /** Delete a product */
    fun deleteProduct(productId: String) = safeApiCall {
        api.deleteProduct(productId)
    }

    /** Get a pre-signed URL to upload an image */
    fun getImageUploadUrl(filename: String, shopId: String) = safeApiCall {
        api.getImageUploadUrl(mapOf("filename" to filename, "shop_id" to shopId))
    }
}

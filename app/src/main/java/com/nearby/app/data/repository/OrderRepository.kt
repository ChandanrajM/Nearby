package com.nearby.app.data.repository

import com.nearby.app.data.model.Order
import com.nearby.app.data.network.ApiService
import com.nearby.app.data.network.NetworkResult
import com.nearby.app.data.network.safeApiCall
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    private val api: ApiService,
    private val authRepo: AuthRepository
) {
    /** Get all orders for the current user */
    fun getOrders(): Flow<NetworkResult<List<Order>>> {
        val userId = authRepo.getUserId() ?: ""
        return safeApiCall {
            api.getOrders(userId)
        }
    }

    /** Get details for a specific order */
    fun getOrderDetails(orderId: String): Flow<NetworkResult<Order>> = safeApiCall {
        api.getOrderDetails(orderId)
    }
}

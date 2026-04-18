package com.nearby.app.ui.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nearby.app.data.model.Order
import com.nearby.app.ui.theme.*

@Composable
fun OrdersScreen() {
    // Mock orders for now — will be replaced when backend has orders endpoint
    val orders = remember {
        listOf(
            Order(id = "ord-1", shop_name = "Acme Vintage", total = 365.0, status = "delivered", created_at = "2026-04-18"),
            Order(id = "ord-2", shop_name = "Fresh Mart", total = 120.0, status = "placed", created_at = "2026-04-17"),
            Order(id = "ord-3", shop_name = "TechZone", total = 2499.0, status = "confirmed", created_at = "2026-04-16"),
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NearbyBackground),
    ) {
        // ── Header ─────────────────────────────────────────────────────
        Text(
            text = "My Orders",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            color = NearbyTextPrimary,
            modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 16.dp),
        )

        if (orders.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📦", style = MaterialTheme.typography.displayLarge)
                    Spacer(Modifier.height(12.dp))
                    Text("No orders yet", style = MaterialTheme.typography.bodyLarge, color = NearbyTextSecondary)
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(orders, key = { it.id }) { order ->
                    OrderCard(order)
                }
                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
private fun OrderCard(order: Order) {
    val statusColor = when (order.status) {
        "delivered" -> NearbyGreen
        "confirmed" -> NearbyCyan
        "placed" -> NearbyYellow
        "cancelled" -> NearbyError
        else -> NearbyTextSecondary
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NearbyCard),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = order.shop_name,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = NearbyTextPrimary,
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusColor.copy(alpha = 0.15f),
                ) {
                    Text(
                        text = order.status.replaceFirstChar { it.uppercase() },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = statusColor,
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = order.created_at,
                    style = MaterialTheme.typography.bodySmall,
                    color = NearbyTextTertiary,
                )
                Text(
                    text = "₹${order.total.toInt()}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = NearbyYellow,
                )
            }
        }
    }
}

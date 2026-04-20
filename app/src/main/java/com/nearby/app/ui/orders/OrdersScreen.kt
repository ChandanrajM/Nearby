package com.nearby.app.ui.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nearby.app.data.model.Order
import com.nearby.app.ui.theme.NearbyColors
import com.nearby.app.ui.theme.NearbyType

@Composable
fun OrdersScreen(
    onBack: () -> Unit,
    viewModel: OrdersViewModel = hiltViewModel(),
) {
    val orders by viewModel.orders.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NearbyColors.Background),
    ) {
        // ── Top Bar ──────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = NearbyColors.TextPrimary)
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = "My Orders",
                style = NearbyType.HeroProductName.copy(fontSize = 20.sp),
                color = NearbyColors.TextPrimary,
            )
        }

        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NearbyColors.PriceYellow)
            }
        } else if (orders.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingBag,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = NearbyColors.SurfaceVariant,
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "No orders yet",
                    style = MaterialTheme.typography.bodyLarge,
                    color = NearbyColors.TextSecondary,
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(orders) { order ->
                    OrderItemCard(order)
                }
            }
        }
    }
}

@Composable
fun OrderItemCard(order: Order) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = NearbyColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Order #${order.id.takeLast(6).uppercase()}",
                    style = NearbyType.CardTitle,
                    color = NearbyColors.TextPrimary,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${order.items.size} item(s) • ₹${order.total.toInt()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NearbyColors.TextSecondary,
                )
                Spacer(Modifier.height(8.dp))
                
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (order.status.lowercase()) {
                        "delivered" -> NearbyColors.OnlineDot.copy(alpha = 0.1f)
                        "cancelled" -> NearbyColors.OfflineDot.copy(alpha = 0.1f)
                        else -> NearbyColors.PriceYellow.copy(alpha = 0.1f)
                    }
                ) {
                    Text(
                        text = order.status.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = when (order.status.lowercase()) {
                            "delivered" -> NearbyColors.OnlineDot
                            "cancelled" -> NearbyColors.OfflineDot
                            else -> NearbyColors.PriceYellow
                        }
                    )
                }
            }
            
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = NearbyColors.TextTertiary,
            )
        }
    }
}

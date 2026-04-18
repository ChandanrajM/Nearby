package com.nearby.app.ui.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.nearby.app.data.model.CartItem
import com.nearby.app.ui.theme.*

@Composable
fun CartScreen(
    viewModel: CartViewModel = hiltViewModel(),
) {
    val items by viewModel.items.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NearbyBackground),
    ) {
        // ── Header ─────────────────────────────────────────────────────
        Text(
            text = "My Cart",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            color = NearbyTextPrimary,
            modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 16.dp),
        )

        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🛒", style = MaterialTheme.typography.displayLarge)
                    Spacer(Modifier.height(12.dp))
                    Text("Your cart is empty", style = MaterialTheme.typography.bodyLarge, color = NearbyTextSecondary)
                    Spacer(Modifier.height(4.dp))
                    Text("Browse shops and add products", style = MaterialTheme.typography.bodySmall, color = NearbyTextTertiary)
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f),
            ) {
                items(items, key = { it.product.id }) { cartItem ->
                    CartItemCard(
                        item = cartItem,
                        onIncrement = { viewModel.updateQuantity(cartItem.product.id, cartItem.quantity + 1) },
                        onDecrement = { viewModel.updateQuantity(cartItem.product.id, cartItem.quantity - 1) },
                        onRemove = { viewModel.removeItem(cartItem.product.id) },
                    )
                }
                item { Spacer(Modifier.height(8.dp)) }
            }

            // ── Checkout Footer ────────────────────────────────────────
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = NearbyCard,
                shadowElevation = 8.dp,
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text("Total", style = MaterialTheme.typography.titleLarge, color = NearbyTextSecondary)
                        Text(
                            text = "₹${viewModel.totalPrice.toInt()}",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = NearbyYellow,
                        )
                    }
                    Spacer(Modifier.height(14.dp))
                    Button(
                        onClick = { /* TODO: Checkout flow */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NearbyCyan,
                            contentColor = NearbyBlack,
                        ),
                    ) {
                        Text(
                            "Checkout  •  ${viewModel.itemCount} items",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                            ),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CartItemCard(
    item: CartItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRemove: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NearbyCard),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Thumbnail
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(NearbyCardLight),
                contentAlignment = Alignment.Center,
            ) {
                if (item.product.image_url.isNotEmpty()) {
                    AsyncImage(
                        model = item.product.image_url,
                        contentDescription = item.product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    Text(
                        item.product.name.take(2).uppercase(),
                        style = MaterialTheme.typography.titleLarge,
                        color = NearbyCyan.copy(alpha = 0.5f),
                    )
                }
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.product.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = NearbyTextPrimary,
                    maxLines = 2,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "₹${item.product.price.toInt()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NearbyTextSecondary,
                )
            }

            // Quantity controls
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onDecrement, modifier = Modifier.size(32.dp)) {
                        Text("−", style = MaterialTheme.typography.titleLarge, color = NearbyTextPrimary)
                    }
                    Text(
                        text = "${item.quantity}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = NearbyTextPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp),
                    )
                    IconButton(onClick = onIncrement, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Add, "Add", tint = NearbyCyan, modifier = Modifier.size(18.dp))
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "₹${item.totalPrice.toInt()}",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = NearbyYellow,
                )
            }
        }
    }
}

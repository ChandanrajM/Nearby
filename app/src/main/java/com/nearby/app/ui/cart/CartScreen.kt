package com.nearby.app.ui.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nearby.app.ui.theme.NearbyColors
import com.nearby.app.ui.theme.NearbyType

@Composable
fun CartScreen(
    onBack: () -> Unit,
    viewModel: CartViewModel = hiltViewModel(),
) {
    val items by viewModel.items.collectAsState()
    val total = viewModel.totalPrice

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
                text = "Shopping Cart",
                style = NearbyType.HeroProductName.copy(fontSize = 20.sp),
                color = NearbyColors.TextPrimary,
            )
        }

        if (items.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = NearbyColors.Surface,
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Your cart is empty",
                    style = MaterialTheme.typography.bodyLarge,
                    color = NearbyColors.TextSecondary,
                )
            }
        } else {
            Box(Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 120.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(items) { item ->
                        CartItemRow(
                            item = item,
                            onUpdateQuantity = { q -> viewModel.updateQuantity(item.product.id, q) }
                        )
                    }
                }

                // ── Bottom Summary ─────────────────────────────────────────
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = NearbyColors.Surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Total Price",
                                style = MaterialTheme.typography.bodyMedium,
                                color = NearbyColors.TextSecondary
                            )
                            Text(
                                text = "₹${total.toInt()}",
                                style = NearbyType.HeroPrice.copy(fontSize = 24.sp)
                            )
                        }
                        
                        Button(
                            onClick = { /* Checkout */ },
                            modifier = Modifier
                                .height(56.dp)
                                .padding(start = 16.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NearbyColors.PriceYellow,
                                contentColor = NearbyColors.Background
                            )
                        ) {
                            Text(
                                "CHECKOUT",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: com.nearby.app.data.model.CartItem,
    onUpdateQuantity: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(NearbyColors.Surface)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Product Image (placeholder)
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(NearbyColors.Background),
            contentAlignment = Alignment.Center
        ) {
            coil.compose.AsyncImage(
                model = item.product.imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }
        
        Spacer(Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.product.name,
                style = NearbyType.CardTitle,
                color = NearbyColors.TextPrimary,
            )
            Text(
                text = "₹${item.product.price.toInt()}",
                style = MaterialTheme.typography.bodyMedium,
                color = NearbyColors.PriceYellow,
            )
        }
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(NearbyColors.Background)
                .padding(horizontal = 4.dp, vertical = 4.dp)
        ) {
            IconButton(
                onClick = { onUpdateQuantity(item.quantity - 1) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(Icons.Default.Remove, null, tint = NearbyColors.TextPrimary, modifier = Modifier.size(16.dp))
            }
            Text(
                text = item.quantity.toString(),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = NearbyColors.TextPrimary,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            IconButton(
                onClick = { onUpdateQuantity(item.quantity + 1) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(Icons.Default.Add, null, tint = NearbyColors.PriceYellow, modifier = Modifier.size(16.dp))
            }
        }
    }
}

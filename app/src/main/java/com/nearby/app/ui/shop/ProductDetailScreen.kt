package com.nearby.app.ui.shop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
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
import com.nearby.app.ui.theme.*

@Composable
fun ProductDetailScreen(
    shopId: String,
    productId: String,
    onBack: () -> Unit,
    viewModel: ShopViewModel = hiltViewModel(),
) {
    LaunchedEffect(shopId) {
        viewModel.loadShop(shopId)
    }

    val state by viewModel.state.collectAsState()
    val product = state.products.find { it.id == productId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NearbyBackground)
            .verticalScroll(rememberScrollState()),
    ) {
        // ── Back button ────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(12.dp),
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, "Back", tint = NearbyTextPrimary)
            }
        }

        if (product == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NearbyCyan)
            }
            return
        }

        // ── Product Image ──────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(NearbyCardLight),
            contentAlignment = Alignment.Center,
        ) {
            if (product.image_url.isNotEmpty()) {
                AsyncImage(
                    model = product.image_url,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                Text(
                    text = product.name.take(3).uppercase(),
                    style = MaterialTheme.typography.displayLarge,
                    color = NearbyCyan.copy(alpha = 0.2f),
                )
            }
            if (product.is_featured) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = NearbyPink,
                ) {
                    Text(
                        text = "LIVE DROP",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = NearbyTextPrimary,
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── Product Info ───────────────────────────────────────────────
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = NearbyTextPrimary,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "₹${product.price.toInt()}",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = NearbyYellow,
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = product.description.ifEmpty { "No description available." },
                style = MaterialTheme.typography.bodyLarge,
                color = NearbyTextSecondary,
                lineHeight = 24.sp,
            )
            Spacer(Modifier.height(8.dp))
            Row {
                Text("Stock: ", style = MaterialTheme.typography.bodyMedium, color = NearbyTextTertiary)
                Text(
                    text = if (product.stock > 0) "${product.stock} available" else "Out of stock",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (product.stock > 0) NearbyGreen else NearbyError,
                )
            }

            Spacer(Modifier.height(32.dp))

            // ── Add to Cart Button ─────────────────────────────────────
            Button(
                onClick = { viewModel.addToCart(product) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NearbyCyan,
                    contentColor = NearbyBlack,
                ),
                enabled = product.stock > 0,
            ) {
                Icon(Icons.Default.ShoppingCart, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(
                    "Add to Cart",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                    ),
                )
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

package com.nearby.app.ui.shop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.nearby.app.ui.theme.NearbyColors
import com.nearby.app.ui.theme.NearbyType

@Composable
fun ProductDetailScreen(
    shopId: String,
    productId: String,
    onBack: () -> Unit,
    viewModel: ShopStoreViewModel = hiltViewModel(),
) {
    LaunchedEffect(shopId) {
        viewModel.loadShop(shopId)
    }

    val state by viewModel.uiState.collectAsState()
    val product = state.products.find { it.id == productId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NearbyColors.Background)
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
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = NearbyColors.TextPrimary
                )
            }
        }

        if (product == null) {
            Box(Modifier.fillMaxSize().height(400.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NearbyColors.PriceYellow)
            }
            return
        }

        // ── Product Image ──────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(360.dp)
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(NearbyColors.Surface),
            contentAlignment = Alignment.Center,
        ) {
            AsyncImage(
                model = product.displayImageUrl,
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }

        Spacer(Modifier.height(24.dp))

        // ── Product Info ───────────────────────────────────────────────
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Text(
                text = product.name,
                style = NearbyType.HeroProductName.copy(fontSize = 24.sp),
                color = NearbyColors.TextPrimary,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = product.formattedPrice,
                style = NearbyType.HeroPrice.copy(fontSize = 22.sp),
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Product in category: ${product.category}. \nExperience the best from your local shops expertly curated for you.",
                style = MaterialTheme.typography.bodyLarge,
                color = NearbyColors.TextSecondary,
                lineHeight = 24.sp,
            )
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(if (product.isAvailable) NearbyColors.OnlineDot else NearbyColors.OfflineDot)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (product.isAvailable) "Available" else "Out of stock",
                    style = NearbyType.Distance,
                    color = if (product.isAvailable) NearbyColors.TextSecondary else NearbyColors.TextTertiary,
                )
            }

            Spacer(Modifier.height(40.dp))

            // ── Add to Cart Button ─────────────────────────────────────
            Button(
                onClick = { viewModel.addToCart(product.id) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NearbyColors.PriceYellow,
                    contentColor = NearbyColors.Background,
                ),
                enabled = product.isAvailable,
            ) {
                Icon(Icons.Default.ShoppingCart, contentDescription = null)
                Spacer(Modifier.width(12.dp))
                Text(
                    "ADD TO CART",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        letterSpacing = 1.sp
                    ),
                )
            }
            Spacer(Modifier.height(48.dp))
        }
    }
}

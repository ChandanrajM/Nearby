package com.nearby.app.ui.shop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.nearby.app.ui.components.CategoryChips
import com.nearby.app.ui.components.ProductCard
import com.nearby.app.ui.theme.*

@Composable
fun ShopScreen(
    shopId: String,
    onBack: () -> Unit,
    onProductClick: (String) -> Unit,
    viewModel: ShopViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(shopId) {
        viewModel.loadShop(shopId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NearbyBackground),
    ) {
        // ── Top bar with shop name ─────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, "Back", tint = NearbyTextPrimary)
            }
            Text(
                text = state.shop?.name ?: "Loading...",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = NearbyTextPrimary,
            )
            if (state.shop?.status == "approved") {
                Spacer(Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(NearbyGreen)
                )
            }
        }

        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NearbyCyan)
            }
            return
        }

        // ── Category filter chips ──────────────────────────────────────
        CategoryChips(
            categories = state.categories,
            selectedCategory = state.selectedCategory,
            onCategorySelected = viewModel::onCategoryChange,
        )
        Spacer(Modifier.height(12.dp))

        // ── Product grid ───────────────────────────────────────────────
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            // Hero product (featured) — spans full width
            val featured = state.filteredProducts.find { it.is_featured }
            if (featured != null) {
                item(span = { GridItemSpan(2) }) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = NearbyCard),
                        onClick = { onProductClick(featured.id) },
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            if (featured.image_url.isNotEmpty()) {
                                AsyncImage(
                                    model = featured.image_url,
                                    contentDescription = featured.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize(),
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(NearbyCardLight),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        featured.name.take(2).uppercase(),
                                        style = MaterialTheme.typography.displayLarge,
                                        color = NearbyCyan.copy(alpha = 0.3f),
                                    )
                                }
                            }
                            // "LIVE DROP" badge
                            Surface(
                                modifier = Modifier
                                    .padding(14.dp)
                                    .align(Alignment.TopStart),
                                shape = RoundedCornerShape(8.dp),
                                color = NearbyPink,
                            ) {
                                Text(
                                    text = "LIVE DROP",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = NearbyTextPrimary,
                                )
                            }
                            // Name + Price overlay
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .fillMaxWidth()
                                    .background(NearbyOverlay)
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    text = featured.name,
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = NearbyTextPrimary,
                                    modifier = Modifier.weight(1f),
                                )
                                Text(
                                    text = "₹${featured.price.toInt()}",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = NearbyPink,
                                )
                            }
                        }
                    }
                }
            }

            // Regular product cards (2-col grid)
            val regularProducts = state.filteredProducts.filter { !it.is_featured }
            items(regularProducts, key = { it.id }) { product ->
                ProductCard(
                    product = product,
                    onClick = { onProductClick(product.id) },
                )
            }
        }
    }
}

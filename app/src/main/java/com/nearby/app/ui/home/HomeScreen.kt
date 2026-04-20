package com.nearby.app.ui.home

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nearby.app.ui.components.*
import com.nearby.app.ui.theme.NearbyColors
import com.nearby.app.ui.theme.NearbyType

@Composable
fun HomeScreen(
    onScanClick: () -> Unit,
    onShopClick: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val location by viewModel.locationRepo.location.collectAsState()

    // Ask for location permission on first load
    val permLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        if (perms[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            // Trigger location fetch again after permission granted
        }
    }

    LaunchedEffect(Unit) {
        if (!viewModel.locationRepo.hasLocationPermission()) {
            permLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                )
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NearbyColors.Background),
    ) {
        // ── Location Bar ───────────────────────────────────────────────
        LocationBar(
            address = location.address,
            area = location.area.ifEmpty { location.city },
        )

        // ── Search Bar ─────────────────────────────────────────────────
        NearbySearchBar(
            query = state.searchQuery,
            onQueryChange = viewModel::onSearchChange,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        Spacer(Modifier.height(24.dp))

        // ── Scan & Shop Banner ─────────────────────────────────────────
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            NearbyColors.PriceYellow,
                            NearbyColors.PriceYellow.copy(alpha = 0.8f)
                        ),
                    )
                )
                .clickable(onClick = onScanClick),
        ) {
            // Decorative elements
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .offset(x = 220.dp, y = -20.dp)
                    .clip(RoundedCornerShape(70.dp))
                    .background(Color.White.copy(alpha = 0.15f))
            )

            Column(
                modifier = Modifier.padding(24.dp),
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = NearbyColors.Background,
                ) {
                    Text(
                        text = "SCAN & SHOP",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = NearbyColors.PriceYellow,
                    )
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Shop Nearby\nStores Instantly",
                    style = NearbyType.HeroProductName.copy(
                        fontSize = 28.sp,
                        lineHeight = 34.sp
                    ),
                    color = NearbyColors.Background,
                )
                Spacer(Modifier.height(16.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NearbyColors.Background,
                    onClick = onScanClick,
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            tint = NearbyColors.PriceYellow,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Open Camera",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = NearbyColors.TextPrimary,
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(28.dp))
        
        // ── Trending Products ──────────────────────────────────────────
        if (state.trendingProducts.isNotEmpty()) {
            Text(
                text = "Trending Near You",
                style = NearbyType.HeroProductName.copy(fontSize = 18.sp),
                color = NearbyColors.TextPrimary,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(Modifier.height(16.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(state.trendingProducts) { product ->
                    Box(
                        modifier = Modifier
                            .size(260.dp, 160.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(NearbyColors.Surface)
                            .clickable { onShopClick(product.shopId) }
                    ) {
                        coil.compose.AsyncImage(
                            model = product.imageUrl,
                            contentDescription = product.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                        // Gradient Overlay for readability
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                                    )
                                )
                        )
                        Text(
                            text = product.name,
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp),
                            style = NearbyType.CardTitle.copy(color = Color.White),
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(28.dp))

        // ── Categories header ──────────────────────────────────────────
        Text(
            text = "Explore Categories",
            style = NearbyType.HeroProductName.copy(fontSize = 18.sp),
            color = NearbyColors.TextPrimary,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(Modifier.height(16.dp))

        CategoryChips(
            categories = state.categories,
            selectedCategory = state.selectedCategory,
            onCategorySelected = viewModel::onCategoryChange,
        )

        Spacer(Modifier.height(24.dp))

        // ── Nearby Shops header ────────────────────────────────────────
        Text(
            text = "Featured Shops",
            style = NearbyType.HeroProductName.copy(fontSize = 18.sp),
            color = NearbyColors.TextPrimary,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(Modifier.height(16.dp))

        // ── Shop Grid ──────────────────────────────────────────────────
        if (state.isLoading) {
            Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NearbyColors.PriceYellow)
            }
        } else if (state.filteredShops.isEmpty()) {
            Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                Text("No shops found nearby", color = NearbyColors.TextSecondary)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(state.filteredShops, key = { it.id }) { shop ->
                    ShopCard(
                        shop = shop,
                        onClick = { onShopClick(shop.id) },
                    )
                }
            }
        }
    }
}

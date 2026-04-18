package com.nearby.app.ui.home

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.nearby.app.data.repository.LocationData
import com.nearby.app.ui.components.*
import com.nearby.app.ui.theme.*

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
            .background(NearbyBackground),
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

        Spacer(Modifier.height(16.dp))

        // ── Scan & Shop Banner ─────────────────────────────────────────
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(NearbyGreenDark, NearbyGreen, NearbyGreenBanner),
                    )
                )
                .clickable(onClick = onScanClick),
        ) {
            // Decorative circles
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .offset(x = 200.dp, y = 10.dp)
                    .clip(RoundedCornerShape(60.dp))
                    .background(Color.White.copy(alpha = 0.08f))
            )
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .offset(x = 240.dp, y = 70.dp)
                    .clip(RoundedCornerShape(40.dp))
                    .background(Color.White.copy(alpha = 0.06f))
            )

            Column(
                modifier = Modifier.padding(20.dp),
            ) {
                // Yellow badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = NearbyYellow,
                ) {
                    Text(
                        text = "SCAN & SHOP",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = NearbyBlack,
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Shop Nearby\nStores Instantly",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        lineHeight = 32.sp,
                    ),
                    color = Color.White,
                )
                Spacer(Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White,
                    onClick = onScanClick,
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "📷",
                            fontSize = 16.sp,
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Scan QR",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = NearbyGreenDark,
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // ── Categories header ──────────────────────────────────────────
        Text(
            text = "Categories",
            style = MaterialTheme.typography.headlineSmall,
            color = NearbyTextPrimary,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(Modifier.height(12.dp))

        CategoryChips(
            categories = state.categories,
            selectedCategory = state.selectedCategory,
            onCategorySelected = viewModel::onCategoryChange,
        )

        Spacer(Modifier.height(16.dp))

        // ── Nearby Shops header ────────────────────────────────────────
        Text(
            text = "Nearby Shops",
            style = MaterialTheme.typography.headlineSmall,
            color = NearbyTextPrimary,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(Modifier.height(12.dp))

        // ── Shop Grid ──────────────────────────────────────────────────
        if (state.isLoading) {
            Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NearbyCyan)
            }
        } else if (state.filteredShops.isEmpty()) {
            Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                Text("No shops found", color = NearbyTextSecondary)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
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

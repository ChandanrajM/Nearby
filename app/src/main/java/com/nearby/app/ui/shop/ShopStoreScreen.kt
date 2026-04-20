package com.nearby.app.ui.shop

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.nearby.app.ui.theme.NearbyColors
import com.nearby.app.ui.theme.NearbyTheme
import com.nearby.app.ui.theme.NearbyType

// ─────────────────────────────────────────────────────────────────
// ShopStoreScreen — entry point
// This is what the Navigation component routes to when a QR is scanned
// or a shop is tapped in the nearby list.
// ─────────────────────────────────────────────────────────────────

@Composable
fun ShopStoreScreen(
    shopId: String,
    onBackClick: () -> Unit,
    onProductClick: (productId: String) -> Unit,
    viewModel: ShopStoreViewModel = hiltViewModel()
) {
    // Collect state from ViewModel
    val uiState by viewModel.uiState.collectAsState()

    // Load shop data when screen opens
    LaunchedEffect(shopId) {
        viewModel.loadShop(shopId)
    }

    ShopStoreContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onProductClick = onProductClick,
        onCategorySelected = viewModel::selectCategory
    )
}

// ─────────────────────────────────────────────────────────────────
// ShopStoreContent — pure UI, no ViewModel dependency
// Separated so it can be previewed in Android Studio with fake data
// ─────────────────────────────────────────────────────────────────

@Composable
fun ShopStoreContent(
    uiState: ShopUiState,
    onBackClick: () -> Unit,
    onProductClick: (productId: String) -> Unit,
    onCategorySelected: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NearbyColors.Background)
            .systemBarsPadding()   // Respect status bar and nav bar
    ) {
        when {
            // ── Loading skeleton ───────────────────────────────────────
            uiState.isLoading -> {
                ShopLoadingSkeleton()
            }

            // ── Error state ───────────────────────────────────────────
            uiState.error != null -> {
                ShopErrorState(
                    message = uiState.error,
                    onBack = onBackClick
                )
            }

            // ── Main content ──────────────────────────────────────────
            uiState.shop != null -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 32.dp),
                    horizontalArrangement = Arrangement.spacedBy(1.dp),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    // ── Header: back button + shop name + distance ──────
                    item(span = { GridItemSpan(2) }) {
                        ShopHeader(
                            shop = uiState.shop,
                            onBackClick = onBackClick
                        )
                    }

                    // ── Filter chips ────────────────────────────────────
                    item(span = { GridItemSpan(2) }) {
                        CategoryChips(
                            categories = uiState.shop.categories,
                            selectedCategory = uiState.selectedCategory,
                            onCategorySelected = onCategorySelected
                        )
                    }

                    // ── Hero product card (full width) ──────────────────
                    uiState.heroProduct?.let { hero ->
                        item(span = { GridItemSpan(2) }) {
                            HeroProductCard(
                                product = hero,
                                hasLiveDrop = uiState.shop.hasLiveDrop,
                                onClick = { onProductClick(hero.id) }
                            )
                        }
                    }

                    // ── 2-column product grid ───────────────────────────
                    items(
                        items = uiState.gridProducts,
                        key = { it.id }
                    ) { product ->
                        GridProductCard(
                            product = product,
                            onClick = { onProductClick(product.id) }
                        )
                    }

                    // ── Empty state when a category filter has no items ─
                    if (uiState.filteredProducts.isEmpty()) {
                        item(span = { GridItemSpan(2) }) {
                            EmptyCategoryState(category = uiState.selectedCategory)
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// ShopHeader — back arrow + shop name + distance + online dot
// ─────────────────────────────────────────────────────────────────

@Composable
private fun ShopHeader(
    shop: ShopDisplayModel,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back arrow
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Go back",
                tint = NearbyColors.TextPrimary,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(4.dp))

        // Shop name
        Text(
            text = shop.name,
            style = NearbyType.ShopName,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Online status dot
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(
                    if (shop.isOnline) NearbyColors.OnlineDot
                    else NearbyColors.OfflineDot
                )
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Distance
        Text(
            text = shop.formattedDistance,
            style = NearbyType.Distance
        )
    }
}

// ─────────────────────────────────────────────────────────────────
// CategoryChips — horizontally scrollable filter pill row
// ─────────────────────────────────────────────────────────────────

@Composable
private fun CategoryChips(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { category ->
            val isSelected = category == selectedCategory
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .then(
                        if (isSelected) {
                            Modifier.border(
                                width = 1.dp,
                                color = NearbyColors.TextPrimary,
                                shape = RoundedCornerShape(50)
                            )
                        } else {
                            Modifier.background(NearbyColors.SurfaceVariant)
                        }
                    )
                    .clickable { onCategorySelected(category) }
                    .padding(horizontal = 14.dp, vertical = 7.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category,
                    style = NearbyType.ChipLabel,
                    color = if (isSelected) NearbyColors.ChipSelected
                            else NearbyColors.ChipUnselected
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(4.dp))
}

// ─────────────────────────────────────────────────────────────────
// HeroProductCard — full-width featured product at the top
// Large image, product name on the left, price on the right
// Optional "LIVE DROP" badge in top-left corner
// ─────────────────────────────────────────────────────────────────

@Composable
fun HeroProductCard(
    product: ProductDisplayModel,
    hasLiveDrop: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(NearbyColors.Surface)
            .clickable(onClick = onClick)
    ) {
        Column {
            // ── Product image ────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                AsyncImage(
                    model = product.displayImageUrl,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Gradient overlay at the bottom — fades image into card bg
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    NearbyColors.Surface
                                )
                            )
                        )
                )

                // "LIVE DROP" badge — top left
                if (hasLiveDrop) {
                    LiveDropBadge(
                        modifier = Modifier
                            .padding(10.dp)
                            .align(Alignment.TopStart)
                    )
                }
            }

            // ── Name + price row ─────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = product.name,
                    style = NearbyType.HeroProductName,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = product.formattedPrice,
                    style = NearbyType.HeroPrice
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// GridProductCard — half-width card for the 2-column grid
// ─────────────────────────────────────────────────────────────────

@Composable
fun GridProductCard(
    product: ProductDisplayModel,
    onClick: () -> Unit
) {
    val cardAlpha = if (product.isAvailable) 1f else 0.45f   // Dim sold-out items

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(NearbyColors.Surface)
            .clickable(enabled = product.isAvailable, onClick = onClick)
    ) {
        Column {
            // ── Product image ────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)    // Square image
            ) {
                AsyncImage(
                    model = product.displayImageUrl,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .then(
                            if (!product.isAvailable) Modifier.background(
                                Color.Black.copy(alpha = 0.5f)
                            ) else Modifier
                        ),
                    alpha = cardAlpha
                )

                // "Sold out" overlay
                if (!product.isAvailable) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        SoldOutBadge()
                    }
                }
            }

            // ── Name + price ─────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = product.truncatedName,
                    style = NearbyType.GridProductName,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = if (product.isAvailable) NearbyColors.TextPrimary
                            else NearbyColors.TextTertiary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = product.formattedPrice,
                    style = NearbyType.GridPrice,
                    color = if (product.isAvailable) NearbyColors.PriceYellow
                            else NearbyColors.TextTertiary
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// Small reusable badge components
// ─────────────────────────────────────────────────────────────────

@Composable
private fun LiveDropBadge(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(NearbyColors.LiveRedDim)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = "LIVE DROP",
            style = NearbyType.LiveBadge
        )
    }
}

@Composable
private fun SoldOutBadge() {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color.Black.copy(alpha = 0.75f))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = "SOLD OUT",
            style = NearbyType.LiveBadge.copy(color = NearbyColors.TextSecondary)
        )
    }
}

// ─────────────────────────────────────────────────────────────────
// Loading skeleton — shown while data is fetching
// Animated shimmer placeholder cards
// ─────────────────────────────────────────────────────────────────

@Composable
private fun ShopLoadingSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header skeleton
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SkeletonBox(width = 36.dp, height = 36.dp, rounded = true)
            Spacer(modifier = Modifier.width(12.dp))
            SkeletonBox(modifier = Modifier.weight(0.6f), height = 20.dp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Chip row skeleton
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(4) { SkeletonBox(width = 72.dp, height = 30.dp, cornerRadius = 50) }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hero card skeleton
        SkeletonBox(modifier = Modifier.fillMaxWidth(), height = 260.dp, cornerRadius = 16)

        Spacer(modifier = Modifier.height(8.dp))

        // Grid skeleton
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(2) {
                SkeletonBox(
                    modifier = Modifier.weight(1f),
                    height = 180.dp,
                    cornerRadius = 12
                )
            }
        }
    }
}

@Composable
private fun SkeletonBox(
    modifier: Modifier = Modifier,
    width: androidx.compose.ui.unit.Dp? = null,
    height: androidx.compose.ui.unit.Dp,
    cornerRadius: Int = 8,
    rounded: Boolean = false
) {
    val shape = if (rounded) CircleShape else RoundedCornerShape(cornerRadius.dp)
    val sizeModifier = if (width != null) modifier.width(width) else modifier
    Box(
        modifier = sizeModifier
            .height(height)
            .clip(shape)
            .background(NearbyColors.Shimmer1)
    )
}

// ─────────────────────────────────────────────────────────────────
// Error state
// ─────────────────────────────────────────────────────────────────

@Composable
private fun ShopErrorState(message: String, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Couldn't load shop",
            style = NearbyType.ShopName,
            color = NearbyColors.TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = NearbyType.Distance,
            color = NearbyColors.TextSecondary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(containerColor = NearbyColors.SurfaceVariant)
        ) {
            Text("Go back", color = NearbyColors.TextPrimary)
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// Empty category state
// ─────────────────────────────────────────────────────────────────

@Composable
private fun EmptyCategoryState(category: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No items in $category",
            style = NearbyType.Distance,
            color = NearbyColors.TextTertiary
        )
    }
}

// ─────────────────────────────────────────────────────────────────
// Previews — visible in Android Studio Design tab
// ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun ShopStorePreview() {
    NearbyTheme {
        ShopStoreContent(
            uiState = ShopPreviewData.uiState,
            onBackClick = {},
            onProductClick = {},
            onCategorySelected = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun HeroCardPreview() {
    NearbyTheme {
        HeroProductCard(
            product = ShopPreviewData.products[0],
            hasLiveDrop = true,
            onClick = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212, widthDp = 180)
@Composable
private fun GridCardPreview() {
    NearbyTheme {
        GridProductCard(
            product = ShopPreviewData.products[1],
            onClick = {}
        )
    }
}

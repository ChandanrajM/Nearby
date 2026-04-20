package com.nearby.app.ui.shop

// ─────────────────────────────────────────────────────────────────
// UI State — everything the ShopStoreScreen needs to render
// ─────────────────────────────────────────────────────────────────

data class ShopUiState(
    val isLoading: Boolean = true,
    val shop: ShopDisplayModel? = null,
    val products: List<ProductDisplayModel> = emptyList(),
    val selectedCategory: String = "ALL",
    val error: String? = null
) {
    // Filtered product list — hero is always index 0 of the filtered result
    val filteredProducts: List<ProductDisplayModel>
        get() = if (selectedCategory == "ALL") products
                else products.filter { it.category == selectedCategory }

    val heroProduct: ProductDisplayModel?
        get() = filteredProducts.firstOrNull()

    val gridProducts: List<ProductDisplayModel>
        get() = if (filteredProducts.size > 1) filteredProducts.drop(1) else emptyList()
}

data class ShopDisplayModel(
    val id: String,
    val name: String,
    val category: String,
    val distanceKm: Double,         // e.g. 0.5 → shown as "0.5mi" or "0.5km"
    val isOnline: Boolean,          // Green dot when true
    val hasLiveDrop: Boolean,       // Shows "LIVE DROP" badge on hero card
    val categories: List<String>    // Filter chip options e.g. ["ALL", "JUST DROPPED", "VINTAGE"]
) {
    val formattedDistance: String
        get() = if (distanceKm < 1.0) "${(distanceKm * 1000).toInt()}m"
                else "${"%.1f".format(distanceKm)}km"
}

data class ProductDisplayModel(
    val id: String,
    val name: String,
    val price: Double,
    val category: String,
    val imageUrl: String,
    val processedImageUrl: String?,    // AI-processed image (preferred when available)
    val isAvailable: Boolean,
    val isNew: Boolean = false         // Shows "NEW" badge
) {
    val displayImageUrl: String
        get() = processedImageUrl ?: imageUrl   // Use AI image if ready, else raw

    val formattedPrice: String
        get() = "$${"%.0f".format(price)}"

    val truncatedName: String
        get() = if (name.length > 16) name.take(14) + "…" else name
}

// ─────────────────────────────────────────────────────────────────
// Preview / dummy data — used in @Preview composables only
// ─────────────────────────────────────────────────────────────────

object ShopPreviewData {
    val shop = ShopDisplayModel(
        id = "acme-vintage",
        name = "Acme Vintage",
        category = "Clothing",
        distanceKm = 0.5,
        isOnline = true,
        hasLiveDrop = true,
        categories = listOf("ALL", "JUST DROPPED", "VINTAGE", "STREETWEAR")
    )

    val products = listOf(
        ProductDisplayModel(
            id = "1",
            name = "Nike Air Max 95 - Mint",
            price = 280.0,
            category = "JUST DROPPED",
            imageUrl = "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=800",
            processedImageUrl = null,
            isAvailable = true,
            isNew = true
        ),
        ProductDisplayModel(
            id = "2",
            name = "Vintage Levis 501",
            price = 85.0,
            category = "VINTAGE",
            imageUrl = "https://images.unsplash.com/photo-1542272604-787c3835535d?w=400",
            processedImageUrl = null,
            isAvailable = true
        ),
        ProductDisplayModel(
            id = "3",
            name = "Supreme Box Logo Tee",
            price = 120.0,
            category = "STREETWEAR",
            imageUrl = "https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?w=400",
            processedImageUrl = null,
            isAvailable = true
        ),
        ProductDisplayModel(
            id = "4",
            name = "Carhartt Detroit Jacket",
            price = 150.0,
            category = "VINTAGE",
            imageUrl = "https://images.unsplash.com/photo-1591047139829-d91aecb6caea?w=400",
            processedImageUrl = null,
            isAvailable = true
        ),
        ProductDisplayModel(
            id = "5",
            name = "Arc'teryx Beta AR",
            price = 350.0,
            category = "JUST DROPPED",
            imageUrl = "https://images.unsplash.com/photo-1544966503-7cc5ac882d5e?w=400",
            processedImageUrl = null,
            isAvailable = true
        ),
        ProductDisplayModel(
            id = "6",
            name = "New Balance 550",
            price = 110.0,
            category = "JUST DROPPED",
            imageUrl = "https://images.unsplash.com/photo-1539185441755-769473a23570?w=400",
            processedImageUrl = null,
            isAvailable = false   // Out of stock
        ),
    )

    val uiState = ShopUiState(
        isLoading = false,
        shop = shop,
        products = products,
        selectedCategory = "ALL"
    )
}

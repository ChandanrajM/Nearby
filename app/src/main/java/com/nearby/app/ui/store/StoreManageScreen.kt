package com.nearby.app.ui.store

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nearby.app.data.model.Product
import com.nearby.app.ui.components.CategoryChips
import com.nearby.app.ui.components.ProductCard
import com.nearby.app.ui.theme.NearbyColors
import com.nearby.app.ui.theme.NearbyType

@Composable
fun StoreManageScreen(
    shopId: String,
    onBack: () -> Unit,
    onShowQR: () -> Unit,
    onAddProduct: (String) -> Unit = {},
    viewModel: StoreManageViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(shopId) {
        viewModel.loadStore(shopId)
    }

    var productToEdit by remember { mutableStateOf<Product?>(null) }
    var productToDelete by remember { mutableStateOf<Product?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NearbyColors.Background),
    ) {
        // ── Top Bar ────────────────────────────────────────────────────
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = state.shopName.ifEmpty { "My Store" },
                    style = NearbyType.HeroProductName.copy(fontSize = 20.sp),
                    color = NearbyColors.TextPrimary,
                )
                Text(
                    text = "Dashboard",
                    style = MaterialTheme.typography.bodySmall,
                    color = NearbyColors.TextSecondary,
                )
            }
            IconButton(onClick = onShowQR) {
                Icon(Icons.Default.QrCode, "QR Code", tint = NearbyColors.PriceYellow)
            }
        }

        // ── Quick Actions ──────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            QuickAction(
                icon = Icons.Default.Add,
                label = "Add Item",
                modifier = Modifier.weight(1f),
                onClick = { onAddProduct(shopId) },
            )
            QuickAction(
                icon = Icons.Default.QrCode,
                label = "Store QR",
                modifier = Modifier.weight(1f),
                onClick = onShowQR,
            )
            QuickAction(
                icon = Icons.Default.Refresh,
                label = "Refresh",
                modifier = Modifier.weight(1f),
                onClick = { viewModel.loadStore(shopId) },
            )
        }

        Spacer(Modifier.height(24.dp))

        // ── Inventory Section ──────────────────────────────────────────
        Text(
            text = "Inventory Management",
            style = NearbyType.CardTitle,
            color = NearbyColors.TextPrimary,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(Modifier.height(16.dp))

        CategoryChips(
            categories = state.categories,
            selectedCategory = state.selectedCategory,
            onCategorySelected = viewModel::onCategoryChange,
        )

        Spacer(Modifier.height(16.dp))

        if (state.isLoading) {
            Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NearbyColors.PriceYellow)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(state.filteredProducts, key = { it.id }) { product ->
                    Box {
                        ProductCard(
                            product = product,
                            onClick = { productToEdit = product },
                            onAddClick = { /* No-op in admin view */ }
                        )
                        // Admin Overlays
                        Row(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            IconButton(
                                onClick = { productToEdit = product },
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(Color.Black.copy(alpha = 0.6f), CircleShape),
                            ) {
                                Icon(Icons.Default.Edit, "Edit", tint = Color.White, modifier = Modifier.size(14.dp))
                            }
                            IconButton(
                                onClick = { productToDelete = product },
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(NearbyColors.OfflineDot.copy(alpha = 0.8f), CircleShape),
                            ) {
                                Icon(Icons.Default.Delete, "Delete", tint = Color.White, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    // ── Dialogs ──────────────────────────────────────────────────────
    productToEdit?.let { product ->
        EditProductDialog(
            product = product,
            onDismiss = { productToEdit = null },
            onUpdate = { name, price, isAvailable ->
                viewModel.updateProduct(product.id, name, price, isAvailable)
                productToEdit = null
            },
        )
    }

    productToDelete?.let { product ->
        AlertDialog(
            onDismissRequest = { productToDelete = null },
            containerColor = NearbyColors.Surface,
            title = { Text("Delete Product", color = NearbyColors.TextPrimary) },
            text = {
                Text(
                    "Are you sure you want to delete \"${product.name}\"? This action cannot be reversed.",
                    color = NearbyColors.TextSecondary,
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteProduct(product.id)
                        productToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NearbyColors.OfflineDot),
                ) { Text("Delete", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { productToDelete = null }) {
                    Text("Cancel", color = NearbyColors.TextTertiary)
                }
            },
        )
    }
}

@Composable
private fun QuickAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier.height(90.dp),
        shape = RoundedCornerShape(18.dp),
        color = NearbyColors.Surface,
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, label, tint = NearbyColors.PriceYellow, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(8.dp))
            Text(
                text = label, 
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold), 
                color = NearbyColors.TextSecondary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditProductDialog(
    product: Product,
    onDismiss: () -> Unit,
    onUpdate: (String, String, Boolean) -> Unit,
) {
    var name by remember(product.id) { mutableStateOf(product.name) }
    var price by remember(product.id) { mutableStateOf(product.price.toString()) }
    var isAvailable by remember(product.id) { mutableStateOf(product.isAvailable) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NearbyColors.Surface,
        title = {
            Text("Edit Product Details", color = NearbyColors.TextPrimary, style = NearbyType.CardTitle)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Product Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NearbyColors.PriceYellow,
                        unfocusedBorderColor = NearbyColors.Background,
                        focusedLabelColor = NearbyColors.PriceYellow,
                        cursorColor = NearbyColors.PriceYellow,
                        focusedTextColor = NearbyColors.TextPrimary,
                        unfocusedTextColor = NearbyColors.TextPrimary,
                    ),
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price (₹)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NearbyColors.PriceYellow,
                        unfocusedBorderColor = NearbyColors.Background,
                        focusedLabelColor = NearbyColors.PriceYellow,
                        cursorColor = NearbyColors.PriceYellow,
                        focusedTextColor = NearbyColors.TextPrimary,
                        unfocusedTextColor = NearbyColors.TextPrimary,
                    ),
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Switch(
                        checked = isAvailable,
                        onCheckedChange = { isAvailable = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = NearbyColors.OnlineDot,
                            checkedTrackColor = NearbyColors.OnlineDot.copy(alpha = 0.3f)
                        )
                    )
                    Spacer(Modifier.width(12.dp))
                    Text("Available in Store", color = NearbyColors.TextPrimary)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onUpdate(name, price, isAvailable) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = NearbyColors.PriceYellow,
                    contentColor = NearbyColors.Background
                ),
                shape = RoundedCornerShape(10.dp)
            ) { Text("Update", fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { 
                Text("Cancel", color = NearbyColors.TextTertiary) 
            }
        },
    )
}

package com.nearby.app.ui.store

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nearby.app.data.model.Product
import com.nearby.app.ui.components.CategoryChips
import com.nearby.app.ui.components.ProductCard
import com.nearby.app.ui.theme.*

@Composable
fun StoreManageScreen(
    onBack: () -> Unit,
    onViewQR: (String) -> Unit,
    onAddProduct: (String) -> Unit = {},
    viewModel: StoreManageViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val shopId = state.shopId

    LaunchedEffect(shopId) {
        viewModel.loadStore(shopId)
    }

    // Edit product dialog state
    var productToEdit by remember { mutableStateOf<Product?>(null) }
    // Delete confirmation dialog state
    var productToDelete by remember { mutableStateOf<Product?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NearbyBackground),
    ) {
        // ── Top bar ────────────────────────────────────────────────────
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = state.shopName.ifEmpty { "My Store" },
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = NearbyTextPrimary,
                )
                Text(
                    text = "Store Management",
                    style = MaterialTheme.typography.bodySmall,
                    color = NearbyTextSecondary,
                )
            }
            // QR Code button
            IconButton(onClick = { onViewQR(shopId) }) {
                Icon(Icons.Default.QrCode, "QR Code", tint = NearbyCyan)
            }
        }

        // ── Quick Actions ──────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            QuickAction(
                icon = Icons.Default.Add,
                label = "Add Product",
                modifier = Modifier.weight(1f),
                onClick = { onAddProduct(shopId) },
            )
            QuickAction(
                icon = Icons.Default.QrCode,
                label = "View QR",
                modifier = Modifier.weight(1f),
                onClick = { onViewQR(shopId) },
            )
            QuickAction(
                icon = Icons.Default.Refresh,
                label = "Refresh",
                modifier = Modifier.weight(1f),
                onClick = { viewModel.loadStore(shopId) },
            )
        }

        Spacer(Modifier.height(8.dp))

        // ── Category Chips ─────────────────────────────────────────────
        CategoryChips(
            categories = state.categories,
            selectedCategory = state.selectedCategory,
            onCategorySelected = viewModel::onCategoryChange,
        )

        Spacer(Modifier.height(12.dp))

        // ── Product count ──────────────────────────────────────────────
        Text(
            text = "${state.filteredProducts.size} products",
            style = MaterialTheme.typography.bodyMedium,
            color = NearbyTextSecondary,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        Spacer(Modifier.height(8.dp))

        // ── Product Grid (owner view with edit/delete overlays) ────────
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NearbyCyan)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(state.filteredProducts, key = { it.id }) { product ->
                    Box {
                        ProductCard(
                            product = product,
                            onClick = { productToEdit = product },
                        )
                        // Edit button
                        IconButton(
                            onClick = { productToEdit = product },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .size(32.dp)
                                .background(NearbyOverlay, CircleShape),
                        ) {
                            Icon(Icons.Default.Edit, "Edit", tint = NearbyCyan, modifier = Modifier.size(15.dp))
                        }
                        // Delete button
                        IconButton(
                            onClick = { productToDelete = product },
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(4.dp)
                                .size(32.dp)
                                .background(NearbyOverlay, CircleShape),
                        ) {
                            Icon(Icons.Default.Delete, "Delete", tint = NearbyError, modifier = Modifier.size(15.dp))
                        }
                    }
                }
            }
        }
    }

    // ── Edit Product Dialog ────────────────────────────────────────────
    productToEdit?.let { product ->
        EditProductDialog(
            product = product,
            onDismiss = { productToEdit = null },
            onUpdate = { name, price, desc, stock ->
                viewModel.updateProduct(product.id, name, price, desc, stock)
                productToEdit = null
            },
        )
    }

    // ── Delete Confirmation Dialog ─────────────────────────────────────
    productToDelete?.let { product ->
        AlertDialog(
            onDismissRequest = { productToDelete = null },
            containerColor = NearbySurface,
            title = { Text("Delete Product", color = NearbyTextPrimary) },
            text = {
                Text(
                    "Are you sure you want to delete \"${product.name}\"? This cannot be undone.",
                    color = NearbyTextSecondary,
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteProduct(product.id)
                        productToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NearbyError),
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { productToDelete = null }) {
                    Text("Cancel", color = NearbyTextSecondary)
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
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = NearbyCard),
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(icon, label, tint = NearbyCyan, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(6.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = NearbyTextSecondary)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditProductDialog(
    product: Product,
    onDismiss: () -> Unit,
    onUpdate: (String, String, String, String) -> Unit,
) {
    var name by remember(product.id) { mutableStateOf(product.name) }
    var price by remember(product.id) { mutableStateOf(product.price.toString()) }
    var description by remember(product.id) { mutableStateOf(product.description) }
    var stock by remember(product.id) { mutableStateOf(product.stock.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NearbySurface,
        title = {
            Text("Edit Product", color = NearbyTextPrimary, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf(
                    Triple("Name", name) { v: String -> name = v },
                    Triple("Price (₹)", price) { v: String -> price = v },
                    Triple("Stock", stock) { v: String -> stock = v },
                    Triple("Description", description) { v: String -> description = v },
                ).forEach { (label, value, onChange) ->
                    OutlinedTextField(
                        value = value,
                        onValueChange = onChange,
                        label = { Text(label) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NearbyCyan,
                            unfocusedBorderColor = NearbyDivider,
                            cursorColor = NearbyCyan,
                            focusedTextColor = NearbyTextPrimary,
                            unfocusedTextColor = NearbyTextPrimary,
                        ),
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onUpdate(name, price, description, stock) },
                colors = ButtonDefaults.buttonColors(containerColor = NearbyCyan, contentColor = NearbyBlack),
            ) { Text("Save Changes", fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = NearbyTextSecondary) }
        },
    )
}

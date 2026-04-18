package com.nearby.app.ui.store

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.nearby.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    shopId: String,
    onBack: () -> Unit,
    viewModel: AddProductViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Gallery image picker
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.onImageSelected(it) }
    }

    // Show success snackbar and navigate back
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            snackbarHostState.showSnackbar("Product added successfully!")
            onBack()
        }
    }

    // Show error snackbar
    LaunchedEffect(state.error) {
        state.error?.let { snackbarHostState.showSnackbar(it) }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = NearbyBackground,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            // ── Top Bar ────────────────────────────────────────────────────
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
                    text = "Add Product",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = NearbyTextPrimary,
                )
            }

            Spacer(Modifier.height(8.dp))

            // ── Image Upload Section ───────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(240.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (state.selectedImageUri == null)
                            Brush.verticalGradient(listOf(NearbyCard, NearbyCardLight))
                        else
                            Brush.verticalGradient(listOf(NearbyBackground, NearbyBackground))
                    )
                    .clickable { galleryLauncher.launch("image/*") },
                contentAlignment = Alignment.Center,
            ) {
                if (state.selectedImageUri != null) {
                    AsyncImage(
                        model = if (state.isEnhanced) state.enhancedImageUri else state.selectedImageUri,
                        contentDescription = "Product image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                    // Semi-transparent overlay for actions
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(NearbyOverlay),
                    )
                    // Action buttons overlay
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        // Change image
                        OutlinedButton(
                            onClick = { galleryLauncher.launch("image/*") },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = NearbyTextPrimary),
                            border = BorderStroke(1.dp, NearbyDivider),
                        ) {
                            Icon(Icons.Default.PhotoLibrary, null, Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Change", style = MaterialTheme.typography.labelMedium)
                        }
                        // AI Enhance button
                        AiEnhanceButton(
                            isEnhancing = state.isEnhancing,
                            isEnhanced = state.isEnhanced,
                            onClick = { viewModel.enhanceImage() },
                        )
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(NearbyCyan.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                Icons.Default.AddPhotoAlternate,
                                contentDescription = null,
                                tint = NearbyCyan,
                                modifier = Modifier.size(32.dp),
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "Tap to upload from gallery",
                            style = MaterialTheme.typography.bodyMedium,
                            color = NearbyTextSecondary,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "PNG, JPG up to 10MB",
                            style = MaterialTheme.typography.bodySmall,
                            color = NearbyTextTertiary,
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Form Fields ───────────────────────────────────────────────
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Product Name
                NearbyTextField(
                    value = state.name,
                    onValueChange = viewModel::onNameChange,
                    label = "Product Name",
                    placeholder = "e.g. Nike Air Max 95 - Mint",
                    leadingIcon = Icons.Default.Inventory2,
                )

                // Price
                NearbyTextField(
                    value = state.price,
                    onValueChange = viewModel::onPriceChange,
                    label = "Price (₹)",
                    placeholder = "e.g. 1299",
                    leadingIcon = Icons.Default.CurrencyRupee,
                    keyboardType = KeyboardType.Decimal,
                )

                // Stock
                NearbyTextField(
                    value = state.stock,
                    onValueChange = viewModel::onStockChange,
                    label = "Stock Quantity",
                    placeholder = "e.g. 10",
                    leadingIcon = Icons.Default.Layers,
                    keyboardType = KeyboardType.Number,
                )

                // Category
                CategoryDropdown(
                    selectedCategory = state.category,
                    onCategorySelected = viewModel::onCategoryChange,
                )

                // Description
                NearbyTextField(
                    value = state.description,
                    onValueChange = viewModel::onDescriptionChange,
                    label = "Description (optional)",
                    placeholder = "What makes this product special?",
                    leadingIcon = Icons.Default.Notes,
                    singleLine = false,
                    minLines = 3,
                )

                // Featured toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(NearbyCard)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Default.Star, "Featured", tint = NearbyYellow, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            "Feature this product",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = NearbyTextPrimary,
                        )
                        Text(
                            "Shows as hero card in your store",
                            style = MaterialTheme.typography.bodySmall,
                            color = NearbyTextSecondary,
                        )
                    }
                    Switch(
                        checked = state.isFeatured,
                        onCheckedChange = viewModel::onFeaturedChange,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = NearbyBackground,
                            checkedTrackColor = NearbyCyan,
                        ),
                    )
                }

                Spacer(Modifier.height(8.dp))

                // Submit button
                Button(
                    onClick = { viewModel.addProduct(shopId) },
                    enabled = !state.isSubmitting && state.name.isNotBlank() && state.price.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NearbyCyan,
                        contentColor = NearbyBlack,
                        disabledContainerColor = NearbyCard,
                        disabledContentColor = NearbyTextTertiary,
                    ),
                ) {
                    if (state.isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = NearbyBlack,
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Icon(Icons.Default.Add, null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Add to Catalogue",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun AiEnhanceButton(
    isEnhancing: Boolean,
    isEnhanced: Boolean,
    onClick: () -> Unit,
) {
    val scale by animateFloatAsState(
        targetValue = if (isEnhancing) 0.95f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "enhance_pulse",
    )

    Button(
        onClick = onClick,
        enabled = !isEnhancing && !isEnhanced,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isEnhanced) NearbyGreen else NearbyPink,
            contentColor = NearbyTextPrimary,
            disabledContainerColor = if (isEnhanced) NearbyGreen.copy(alpha = 0.7f) else NearbyCard,
            disabledContentColor = NearbyTextSecondary,
        ),
        modifier = Modifier.scale(scale),
    ) {
        when {
            isEnhancing -> {
                CircularProgressIndicator(Modifier.size(16.dp), color = NearbyTextPrimary, strokeWidth = 2.dp)
                Spacer(Modifier.width(4.dp))
                Text("Enhancing...", style = MaterialTheme.typography.labelMedium)
            }
            isEnhanced -> {
                Icon(Icons.Default.AutoAwesome, null, Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Enhanced ✓", style = MaterialTheme.typography.labelMedium)
            }
            else -> {
                Icon(Icons.Default.AutoAwesome, null, Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("AI Enhance", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryDropdown(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
) {
    val categories = listOf("general", "just_dropped", "vintage", "streetwear", "accessories", "footwear", "tops", "bottoms")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        OutlinedTextField(
            value = selectedCategory.replace("_", " ").replaceFirstChar { it.uppercase() },
            onValueChange = {},
            readOnly = true,
            label = { Text("Category") },
            leadingIcon = { Icon(Icons.Default.Category, null, tint = NearbyTextSecondary) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NearbyCyan,
                unfocusedBorderColor = NearbyDivider,
                focusedTextColor = NearbyTextPrimary,
                unfocusedTextColor = NearbyTextPrimary,
                focusedLabelColor = NearbyCyan,
                unfocusedLabelColor = NearbyTextSecondary,
                focusedContainerColor = NearbyCard,
                unfocusedContainerColor = NearbyCard,
            ),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            categories.forEach { cat ->
                DropdownMenuItem(
                    text = { Text(cat.replace("_", " ").replaceFirstChar { it.uppercase() }, color = NearbyTextPrimary) },
                    onClick = {
                        onCategorySelected(cat)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun NearbyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    minLines: Int = 1,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder, color = NearbyTextTertiary) },
        leadingIcon = { Icon(leadingIcon, null, tint = NearbyTextSecondary) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        singleLine = singleLine,
        minLines = minLines,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NearbyCyan,
            unfocusedBorderColor = NearbyDivider,
            cursorColor = NearbyCyan,
            focusedTextColor = NearbyTextPrimary,
            unfocusedTextColor = NearbyTextPrimary,
            focusedLabelColor = NearbyCyan,
            unfocusedLabelColor = NearbyTextSecondary,
            focusedContainerColor = NearbyCard,
            unfocusedContainerColor = NearbyCard,
        ),
    )
}

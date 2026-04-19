package com.nearby.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nearby.app.data.model.Shop
import com.nearby.app.ui.theme.*

@Composable
fun ShopCard(
    shop: Shop,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NearbyCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column {
            // Shop image / placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(NearbyCardLight),
                contentAlignment = Alignment.Center,
            ) {
                // No image_url in Shop model, just showing a placeholder
                if (false) {
                    // Placeholder for future banner feature
                    Text(
                        text = shop.name.take(2).uppercase(),
                        style = MaterialTheme.typography.displayMedium,
                        color = NearbyCyan,
                    )
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = shop.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = NearbyTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = shop.category.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodySmall,
                    color = NearbyTextSecondary,
                )
                if (shop.distanceKm != null) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "${shop.distanceKm} km away",
                        style = MaterialTheme.typography.labelSmall,
                        color = NearbyGreen,
                    )
                }
            }
        }
    }
}

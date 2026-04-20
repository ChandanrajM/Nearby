package com.nearby.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.nearby.app.data.model.Shop
import com.nearby.app.ui.theme.NearbyColors
import com.nearby.app.ui.theme.NearbyType

@Composable
fun ShopCard(
    shop: Shop,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = NearbyColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                AsyncImage(
                    model = "https://images.unsplash.com/photo-1441986300917-64674bd600d8?auto=format&fit=crop&q=80&w=400", // Default placeholder
                    contentDescription = shop.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // Distance badge
                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopEnd),
                    color = NearbyColors.Background.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${String.format("%.1f", shop.distanceKm ?: 1.2)} km",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = NearbyType.Distance,
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = shop.name,
                    style = NearbyType.CardTitle,
                    maxLines = 1,
                    color = NearbyColors.TextPrimary
                )
                
                Spacer(Modifier.height(4.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = NearbyColors.PriceYellow,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "4.8",
                        style = NearbyType.Distance,
                        color = NearbyColors.TextPrimary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "•",
                        color = NearbyColors.TextTertiary,
                        style = NearbyType.Distance
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = shop.category.replaceFirstChar { it.uppercase() },
                        style = NearbyType.Distance,
                        color = NearbyColors.TextSecondary
                    )
                }
                
                Spacer(Modifier.height(12.dp))
                
                // Status tag
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (shop.isOpen) NearbyColors.OnlineDot else NearbyColors.OfflineDot)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = if (shop.isOpen) "Open Now" else "Closed",
                        style = NearbyType.Distance.copy(fontSize = 11.sp),
                        color = if (shop.isOpen) NearbyColors.OnlineDot else NearbyColors.OfflineDot
                    )
                }
            }
        }
    }
}

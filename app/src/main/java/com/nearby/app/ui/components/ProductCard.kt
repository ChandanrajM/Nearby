package com.nearby.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.nearby.app.data.model.Product
import com.nearby.app.ui.theme.NearbyColors
import com.nearby.app.ui.theme.NearbyType

@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit,
    onAddClick: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(160.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = NearbyColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(NearbyColors.Background)
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Text(
                    text = product.name,
                    style = NearbyType.CardTitle.copy(fontSize = 14.sp),
                    maxLines = 1,
                    color = NearbyColors.TextPrimary
                )
                
                Spacer(Modifier.height(4.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "₹${product.price.toInt()}",
                        style = NearbyType.HeroPrice.copy(fontSize = 15.sp),
                        color = NearbyColors.PriceYellow
                    )
                    
                    IconButton(
                        onClick = { onAddClick(product) },
                        modifier = Modifier
                            .size(32.dp)
                            .background(NearbyColors.PriceYellow, RoundedCornerShape(8.dp))
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add to cart",
                            tint = NearbyColors.Background,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

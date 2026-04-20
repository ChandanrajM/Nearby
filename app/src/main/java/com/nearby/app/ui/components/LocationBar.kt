package com.nearby.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nearby.app.ui.theme.NearbyColors
import com.nearby.app.ui.theme.NearbyType

@Composable
fun LocationBar(
    address: String,
    area: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(NearbyColors.PriceYellow.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = NearbyColors.PriceYellow,
                modifier = Modifier.size(20.dp)
            )
        }
        
        Spacer(Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (area.isNotEmpty()) area else "Select Location",
                    style = NearbyType.CardTitle,
                    color = NearbyColors.TextPrimary
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = NearbyColors.TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = if (address.isNotEmpty()) address else "Locating you...",
                style = MaterialTheme.typography.bodySmall,
                color = NearbyColors.TextSecondary,
                maxLines = 1
            )
        }
    }
}

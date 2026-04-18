package com.nearby.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nearby.app.ui.theme.*

@Composable
fun LocationBar(
    address: String,
    area: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "Location",
            tint = NearbyGreen,
            modifier = Modifier.size(28.dp),
        )
        Spacer(Modifier.width(10.dp))
        Column {
            Text(
                text = area.ifEmpty { "Your Location" },
                style = MaterialTheme.typography.titleLarge,
                color = NearbyTextPrimary,
            )
            Text(
                text = if (address.length > 40) address.take(40) + "..." else address,
                style = MaterialTheme.typography.bodySmall,
                color = NearbyTextSecondary,
            )
        }
    }
}

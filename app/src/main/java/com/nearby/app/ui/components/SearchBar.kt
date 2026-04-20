package com.nearby.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nearby.app.ui.theme.NearbyColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbySearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp)),
        placeholder = {
            Text(
                "Search nearby shops...",
                color = NearbyColors.TextTertiary,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = NearbyColors.PriceYellow
            )
        },
        trailingIcon = {
            Icon(
                Icons.Default.Mic,
                contentDescription = null,
                tint = NearbyColors.TextSecondary
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = NearbyColors.Surface,
            unfocusedContainerColor = NearbyColors.Surface,
            disabledContainerColor = NearbyColors.Surface,
            cursorColor = NearbyColors.PriceYellow,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedTextColor = NearbyColors.TextPrimary,
            unfocusedTextColor = NearbyColors.TextPrimary
        ),
        singleLine = true,
        shape = RoundedCornerShape(16.dp)
    )
}

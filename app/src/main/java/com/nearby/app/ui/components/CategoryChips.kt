package com.nearby.app.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nearby.app.ui.theme.*

@Composable
fun CategoryChips(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Spacer(Modifier.width(8.dp))
        categories.forEach { category ->
            val selected = category == selectedCategory
            FilterChip(
                selected = selected,
                onClick = { onCategorySelected(category) },
                label = {
                    Text(
                        text = category.uppercase(),
                        style = MaterialTheme.typography.labelLarge,
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = NearbyCard,
                    labelColor = NearbyTextSecondary,
                    selectedContainerColor = NearbyCyan,
                    selectedLabelColor = NearbyBlack,
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = NearbyDivider,
                    selectedBorderColor = NearbyCyan,
                    enabled = true,
                    selected = selected,
                ),
            )
        }
        Spacer(Modifier.width(8.dp))
    }
}

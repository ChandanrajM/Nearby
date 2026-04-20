package com.nearby.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nearby.app.ui.theme.NearbyColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryChips(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            val selected = category == selectedCategory
            FilterChip(
                selected = selected,
                onClick = { onCategorySelected(category) },
                label = {
                    Text(
                        text = category,
                        style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp),
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = NearbyColors.Surface,
                    labelColor = NearbyColors.TextSecondary,
                    selectedContainerColor = NearbyColors.PriceYellow,
                    selectedLabelColor = NearbyColors.Background,
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = NearbyColors.Surface,
                    selectedBorderColor = NearbyColors.PriceYellow,
                    enabled = true,
                    selected = selected,
                    borderWidth = 1.dp
                )
            )
        }
    }
}

package com.nearby.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.nearby.app.ui.navigation.Routes
import com.nearby.app.ui.theme.NearbyColors

data class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

val baseBottomNavItems = listOf(
    BottomNavItem(Routes.HOME, "Home", Icons.Filled.Home, Icons.Outlined.Home),
    BottomNavItem(Routes.ORDERS, "Orders", Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart),
    BottomNavItem(Routes.CART, "Cart", Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart),
    BottomNavItem(Routes.ACCOUNT, "Account", Icons.Filled.Person, Icons.Outlined.Person),
)

@Composable
fun NearbyBottomBar(
    currentRoute: String?,
    user: com.nearby.app.data.model.User? = null,
    onNavigate: (String) -> Unit,
) {
    val items = remember(user) {
        val list = baseBottomNavItems.toMutableList()
        if (user?.shopStatus == "approved") {
            // Insert Store item before Account
            val shopId = user.shop_id ?: "unknown"
            list.add(3, BottomNavItem(Routes.storeManage(shopId), "Store", Icons.Filled.Storefront, Icons.Outlined.Storefront))
        }
        list
    }

    NavigationBar(
        containerColor = NearbyColors.Background,
        contentColor = NearbyColors.TextPrimary,
        tonalElevation = 0.dp,
    ) {
        items.forEach { item ->
            // Match exactly or start with for deeper routes if needed, 
            // but for bottom bar usually exact match on the base route.
            val selected = currentRoute == item.route
            
            val iconColor by animateColorAsState(
                if (selected) NearbyColors.PriceYellow else NearbyColors.TextTertiary,
                label = "navIconColor"
            )
            
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label,
                        tint = iconColor,
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = iconColor,
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = NearbyColors.PriceYellow.copy(alpha = 0.1f),
                ),
            )
        }
    }
}

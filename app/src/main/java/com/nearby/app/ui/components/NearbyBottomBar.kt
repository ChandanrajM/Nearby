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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.nearby.app.navigation.Routes
import com.nearby.app.ui.theme.*

data class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

val baseBottomNavItems = listOf(
    BottomNavItem(Routes.Home.route, "Home", Icons.Filled.Home, Icons.Outlined.Home),
    BottomNavItem(Routes.Orders.route, "Orders", Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart),
    BottomNavItem(Routes.Cart.route, "Cart", Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart),
    BottomNavItem(Routes.Account.route, "Account", Icons.Filled.Person, Icons.Outlined.Person),
)

@Composable
fun NearbyBottomBar(
    currentRoute: String,
    user: com.nearby.app.data.model.User? = null,
    onNavigate: (String) -> Unit,
) {
    val items = remember(user) {
        val list = baseBottomNavItems.toMutableList()
        if (user?.shopStatus == "approved") {
            // Insert Store item before Account
            list.add(3, BottomNavItem(Routes.StoreManage.route, "Store", Icons.Filled.Storefront, Icons.Outlined.Storefront))
        }
        list
    }

    NavigationBar(
        containerColor = NearbyBlack,
        contentColor = NearbyTextPrimary,
        tonalElevation = 0.dp,
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route
            val iconColor by animateColorAsState(
                if (selected) NearbyCyan else NearbyTextTertiary,
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
                    indicatorColor = NearbyCyan.copy(alpha = 0.12f),
                ),
            )
        }
    }
}

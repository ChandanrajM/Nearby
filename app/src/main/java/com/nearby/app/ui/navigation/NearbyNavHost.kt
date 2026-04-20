package com.nearby.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.nearby.app.ui.shop.ShopStoreScreen

// ─────────────────────────────────────────────────────────────────
// Navigation routes
// ─────────────────────────────────────────────────────────────────

object Routes {
    const val NEARBY_LIST   = "nearby"
    const val SHOP_STORE    = "shop/{shopId}"
    const val QR_SCANNER    = "scanner"

    fun shopStore(shopId: String) = "shop/$shopId"
}

// ─────────────────────────────────────────────────────────────────
// NearbyNavHost — the root navigation graph
//
// Place this in your MainActivity's setContent block:
//
//   setContent {
//       NearbyTheme {
//           NearbyNavHost()
//       }
//   }
// ─────────────────────────────────────────────────────────────────

@Composable
fun NearbyNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.NEARBY_LIST
    ) {

        // ── Nearby shop list (home screen) ─────────────────────────
        composable(Routes.NEARBY_LIST) {
            // NearbyListScreen(
            //     onShopClick = { shopId ->
            //         navController.navigate(Routes.shopStore(shopId))
            //     }
            // )
        }

        // ── QR Scanner ─────────────────────────────────────────────
        composable(Routes.QR_SCANNER) {
            // QrScannerScreen(
            //     onShopScanned = { shopId ->
            //         navController.navigate(Routes.shopStore(shopId)) {
            //             popUpTo(Routes.QR_SCANNER) { inclusive = true }
            //         }
            //     }
            // )
        }

        // ── Shop store screen ──────────────────────────────────────
        // Handles TWO entry points:
        //   1. Normal navigation from the nearby list (navController.navigate)
        //   2. Deep link from QR scan: https://nearby.app/s/{shopId}
        //      Android intercepts this URL and opens the app directly here.
        composable(
            route = Routes.SHOP_STORE,
            arguments = listOf(
                navArgument("shopId") { type = NavType.StringType }
            ),
            deepLinks = listOf(
                // QR code URLs look like: https://nearby.app/s/abc123
                // Android App Links intercept this and open ShopStoreScreen directly
                navDeepLink { uriPattern = "https://nearby.app/s/{shopId}" },
                // Also handle the custom scheme (backup for older Android)
                navDeepLink { uriPattern = "nearby://shop/{shopId}" }
            )
        ) { backStackEntry ->
            val shopId = backStackEntry.arguments?.getString("shopId") ?: return@composable

            ShopStoreScreen(
                shopId = shopId,
                onBackClick = { navController.popBackStack() },
                onProductClick = { productId ->
                    // Navigate to product detail when you build that screen
                    // navController.navigate("product/$productId")
                }
            )
        }
    }
}

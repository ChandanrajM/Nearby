package com.nearby.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.nearby.app.ui.account.AccountScreen
import com.nearby.app.ui.auth.LoginScreen
import com.nearby.app.ui.auth.RegistrationScreen
import com.nearby.app.ui.auth.VerifyOtpScreen
import com.nearby.app.ui.home.HomeScreen
import com.nearby.app.ui.orders.OrdersScreen
import com.nearby.app.ui.cart.CartScreen
import com.nearby.app.ui.scanner.ScannerScreen
import com.nearby.app.ui.shop.ShopStoreScreen
import com.nearby.app.ui.shop.ProductDetailScreen
import com.nearby.app.ui.store.StoreManageScreen
import com.nearby.app.ui.store.StoreQRScreen
import com.nearby.app.ui.store.StoreRegistrationScreen

// ─────────────────────────────────────────────────────────────────
// Navigation routes
// ─────────────────────────────────────────────────────────────────

object Routes {
    const val SPLASH            = "splash"
    const val LOGIN             = "login"
    const val REGISTER          = "register"
    const val VERIFY_OTP        = "verify_otp/{email}"
    
    const val HOME              = "home"
    const val CART              = "cart"
    const val ORDERS            = "orders"
    const val ACCOUNT           = "account"
    
    const val SCANNER           = "scanner"
    const val SHOP_STORE        = "shop/{shopId}"
    const val PRODUCT_DETAIL    = "shop/{shopId}/product/{productId}"
    
    const val STORE_REGISTRATION = "store_registration"
    const val STORE_MANAGE       = "store_manage/{shopId}"
    const val STORE_QR           = "store_qr/{shopId}"

    fun verifyOtp(email: String) = "verify_otp/$email"
    fun shopStore(shopId: String) = "shop/$shopId"
    fun productDetail(shopId: String, productId: String) = "shop/$shopId/product/$productId"
    fun storeManage(shopId: String) = "store_manage/$shopId"
    fun storeQr(shopId: String) = "store_qr/$shopId"
}

@Composable
fun NearbyNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier
    ) {
        // ── Auth ───────────────────────────────────────────────────
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = { navController.navigate(Routes.HOME) { popUpTo(Routes.LOGIN) { inclusive = true } } },
                onRegisterClick = { navController.navigate(Routes.REGISTER) }
            )
        }
        composable(Routes.REGISTER) {
            RegistrationScreen(
                onBack = { navController.popBackStack() },
                onRegisterSuccess = { email -> navController.navigate(Routes.verifyOtp(email)) }
            )
        }
        composable(
            route = Routes.VERIFY_OTP,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            VerifyOtpScreen(
                email = email,
                onVerifySuccess = { navController.navigate(Routes.HOME) { popUpTo(Routes.LOGIN) { inclusive = true } } }
            )
        }

        // ── Main UI ────────────────────────────────────────────────
        composable(Routes.HOME) {
            HomeScreen(
                onScanClick = { navController.navigate(Routes.SCANNER) },
                onShopClick = { shopId -> navController.navigate(Routes.shopStore(shopId)) }
            )
        }
        composable(Routes.CART) {
            CartScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.ORDERS) {
            OrdersScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.ACCOUNT) {
            AccountScreen(
                onLogout = { navController.navigate(Routes.LOGIN) { popUpTo(0) } },
                onManageStore = { shopId -> navController.navigate(Routes.storeManage(shopId)) },
                onRegisterStore = { navController.navigate(Routes.STORE_REGISTRATION) }
            )
        }

        // ── Shopping ───────────────────────────────────────────────
        composable(Routes.SCANNER) {
            ScannerScreen(
                onShopScanned = { shopId ->
                    navController.navigate(Routes.shopStore(shopId)) {
                        popUpTo(Routes.SCANNER) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Routes.SHOP_STORE,
            arguments = listOf(navArgument("shopId") { type = NavType.StringType }),
            deepLinks = listOf(
                navDeepLink { uriPattern = "https://nearby.app/s/{shopId}" },
                navDeepLink { uriPattern = "nearby://shop/{shopId}" }
            )
        ) { backStackEntry ->
            val shopId = backStackEntry.arguments?.getString("shopId") ?: return@composable
            ShopStoreScreen(
                shopId = shopId,
                onBackClick = { navController.popBackStack() },
                onProductClick = { productId ->
                    navController.navigate(Routes.productDetail(shopId, productId))
                }
            )
        }
        composable(
            route = Routes.PRODUCT_DETAIL,
            arguments = listOf(
                navArgument("shopId") { type = NavType.StringType },
                navArgument("productId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val shopId = backStackEntry.arguments?.getString("shopId") ?: ""
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            ProductDetailScreen(
                shopId = shopId,
                productId = productId,
                onBack = { navController.popBackStack() }
            )
        }

        // ── Store Management ───────────────────────────────────────
        composable(Routes.STORE_REGISTRATION) {
            StoreRegistrationScreen(
                onBack = { navController.popBackStack() },
                onSuccess = { shopId -> navController.navigate(Routes.storeManage(shopId)) }
            )
        }
        composable(
            route = Routes.STORE_MANAGE,
            arguments = listOf(navArgument("shopId") { type = NavType.StringType })
        ) { backStackEntry ->
            val shopId = backStackEntry.arguments?.getString("shopId") ?: ""
            StoreManageScreen(
                shopId = shopId,
                onBack = { navController.popBackStack() },
                onShowQR = { navController.navigate(Routes.storeQr(shopId)) }
            )
        }
        composable(
            route = Routes.STORE_QR,
            arguments = listOf(navArgument("shopId") { type = NavType.StringType })
        ) { backStackEntry ->
            val shopId = backStackEntry.arguments?.getString("shopId") ?: ""
            StoreQRScreen(
                shopId = shopId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

package com.nearby.app.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.nearby.app.ui.account.AccountScreen
import com.nearby.app.ui.account.AboutScreen
import com.nearby.app.ui.account.EditProfileScreen
import com.nearby.app.ui.account.HelpSupportScreen
import com.nearby.app.ui.account.NotificationsScreen
import com.nearby.app.ui.account.SavedAddressesScreen
import com.nearby.app.ui.auth.LoginScreen
import com.nearby.app.ui.cart.CartScreen
import com.nearby.app.ui.components.NearbyBottomBar
import com.nearby.app.ui.home.HomeScreen
import com.nearby.app.ui.orders.OrdersScreen
import com.nearby.app.ui.scanner.ScannerScreen
import com.nearby.app.ui.shop.ProductDetailScreen
import com.nearby.app.ui.shop.ShopScreen
import com.nearby.app.ui.splash.SplashScreen
import com.nearby.app.ui.store.AddProductScreen
import com.nearby.app.ui.store.StoreManageScreen
import com.nearby.app.ui.store.StoreQRScreen
import com.nearby.app.ui.store.StoreRegistrationScreen

@Composable
fun NearbyNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.Splash.route,
        enterTransition = { fadeIn(tween(300)) },
        exitTransition  = { fadeOut(tween(300)) },
    ) {
        // ── Splash ─────────────────────────────────────────────────────
        composable(Routes.Splash.route) {
            SplashScreen(
                onFinished = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(Routes.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Login ──────────────────────────────────────────────────────
        composable(Routes.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.MainGraph.route) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Main (with bottom nav) ────────────────────────────────────
        composable(Routes.MainGraph.route) {
            MainScreen(rootNavController = navController)
        }

        // ── Scanner (fullscreen, no bottom bar) ───────────────────────
        composable(Routes.Scanner.route) {
            ScannerScreen(
                onScanResult = { shopId ->
                    navController.navigate(Routes.ShopDetail.createRoute(shopId)) {
                        popUpTo(Routes.Scanner.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        // ── Shop Detail ────────────────────────────────────────────────
        composable(
            route = Routes.ShopDetail.route,
            arguments = listOf(navArgument("shopId") { type = NavType.StringType })
        ) { backStack ->
            val shopId = backStack.arguments?.getString("shopId") ?: ""
            ShopScreen(
                shopId = shopId,
                onBack = { navController.popBackStack() },
                onProductClick = { productId ->
                    navController.navigate(Routes.ProductDetail.createRoute(shopId, productId))
                }
            )
        }

        // ── Product Detail ─────────────────────────────────────────────
        composable(
            route = Routes.ProductDetail.route,
            arguments = listOf(
                navArgument("shopId") { type = NavType.StringType },
                navArgument("productId") { type = NavType.StringType },
            )
        ) { backStack ->
            val shopId = backStack.arguments?.getString("shopId") ?: ""
            val productId = backStack.arguments?.getString("productId") ?: ""
            ProductDetailScreen(
                shopId = shopId,
                productId = productId,
                onBack = { navController.popBackStack() }
            )
        }

        // ── Store Registration ─────────────────────────────────────────
        composable(Routes.StoreRegistration.route) {
            StoreRegistrationScreen(
                onBack    = { navController.popBackStack() },
                onSubmitted = { navController.popBackStack() }
            )
        }

        // ── Store Manage (owner view) ──────────────────────────────────
        composable(Routes.StoreManage.route) {
            StoreManageScreen(
                onBack = { navController.popBackStack() },
                onViewQR = { shopId ->
                    navController.navigate(Routes.StoreQR.createRoute(shopId))
                },
                onAddProduct = { shopId ->
                    navController.navigate(Routes.AddProduct.createRoute(shopId))
                }
            )
        }

        // ── Add Product ────────────────────────────────────────────────
        composable(
            route = Routes.AddProduct.route,
            arguments = listOf(navArgument("shopId") { type = NavType.StringType })
        ) { backStack ->
            val shopId = backStack.arguments?.getString("shopId") ?: ""
            AddProductScreen(
                shopId = shopId,
                onBack = { navController.popBackStack() }
            )
        }

        // ── Store QR ───────────────────────────────────────────────────
        composable(
            route = Routes.StoreQR.route,
            arguments = listOf(navArgument("shopId") { type = NavType.StringType })
        ) { backStack ->
            val shopId = backStack.arguments?.getString("shopId") ?: ""
            StoreQRScreen(shopId = shopId, onBack = { navController.popBackStack() })
        }

        // ── Account Sub-screens ────────────────────────────────────────
        composable(Routes.EditProfile.route) {
            EditProfileScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.Notifications.route) {
            NotificationsScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.SavedAddresses.route) {
            SavedAddressesScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.HelpSupport.route) {
            HelpSupportScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.About.route) {
            AboutScreen(onBack = { navController.popBackStack() })
        }
    }
}

@Composable
fun MainScreen(rootNavController: NavHostController) {
    val bottomNavController = rememberNavController()
    val currentRoute by bottomNavController.currentBackStackEntryAsState()
    val currentDestination = currentRoute?.destination?.route

    Scaffold(
        bottomBar = {
            NearbyBottomBar(
                currentRoute = currentDestination ?: Routes.Home.route,
                onNavigate = { route ->
                    bottomNavController.navigate(route) {
                        popUpTo(Routes.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        },
        containerColor = androidx.compose.ui.graphics.Color.Transparent
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = Routes.Home.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Routes.Home.route) {
                HomeScreen(
                    onScanClick = { rootNavController.navigate(Routes.Scanner.route) },
                    onShopClick = { shopId ->
                        rootNavController.navigate(Routes.ShopDetail.createRoute(shopId))
                    }
                )
            }
            composable(Routes.Orders.route) { OrdersScreen() }
            composable(Routes.Cart.route) { CartScreen() }
            composable(Routes.Account.route) {
                AccountScreen(
                    onOnlineStore      = { rootNavController.navigate(Routes.StoreRegistration.route) },
                    onManageStore      = { rootNavController.navigate(Routes.StoreManage.route) },
                    onSignOut          = {
                        rootNavController.navigate(Routes.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onEditProfile      = { rootNavController.navigate(Routes.EditProfile.route) },
                    onNotifications    = { rootNavController.navigate(Routes.Notifications.route) },
                    onSavedAddresses   = { rootNavController.navigate(Routes.SavedAddresses.route) },
                    onHelpSupport      = { rootNavController.navigate(Routes.HelpSupport.route) },
                    onAbout            = { rootNavController.navigate(Routes.About.route) },
                )
            }
        }
    }
}

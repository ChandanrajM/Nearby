package com.nearby.app.navigation

sealed class Routes(val route: String) {
    object Splash : Routes("splash")
    object Login : Routes("login")
    object MainGraph : Routes("main_graph")
    object Home : Routes("home")
    object Orders : Routes("orders")
    object Cart : Routes("cart")
    object Account : Routes("account")
    object Scanner : Routes("scanner")

    // Shop / product
    object ShopDetail : Routes("shop/{shopId}") {
        fun createRoute(shopId: String) = "shop/$shopId"
    }
    object ProductDetail : Routes("product/{shopId}/{productId}") {
        fun createRoute(shopId: String, productId: String) = "product/$shopId/$productId"
    }

    // Shopkeeper
    object StoreRegistration : Routes("store_registration")
    object StoreManage : Routes("store_manage")
    object StoreQR : Routes("store_qr/{shopId}") {
        fun createRoute(shopId: String) = "store_qr/$shopId"
    }
    object AddProduct : Routes("add_product/{shopId}") {
        fun createRoute(shopId: String) = "add_product/$shopId"
    }

    // Account sub-screens
    object EditProfile : Routes("edit_profile")
    object Notifications : Routes("notifications")
    object SavedAddresses : Routes("saved_addresses")
    object HelpSupport : Routes("help_support")
    object About : Routes("about")
}

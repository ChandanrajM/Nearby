package com.nearby.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nearby.app.navigation.NearbyNavGraph
import com.nearby.app.navigation.Routes
import com.nearby.app.ui.theme.NearbyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NearbyTheme {
                val nc = rememberNavController()
                navController = nc
                NearbyNavGraph(navController = nc)

                // Handle incoming deep link on cold start
                intent?.let { handleDeepLink(it, nc) }
            }
        }
    }

    /** Called when app is already running and a new deep link arrives (launchMode=singleTop) */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        navController?.let { handleDeepLink(intent, it) }
    }

    /**
     * Parses "nearby.app/shop/{shopId}" and "nearby.app/s/{shopId}" URLs and
     * navigates to the corresponding ShopDetail screen.
     */
    private fun handleDeepLink(intent: Intent, nc: NavController) {
        val data = intent.data ?: return
        if (data.host != "nearby.app") return

        val segments = data.pathSegments   // e.g. ["shop", "abc123"] or ["s", "abc123"]
        val shopId = when {
            segments.size >= 2 && (segments[0] == "shop" || segments[0] == "s") -> segments[1]
            else -> return
        }
        // Navigate after the nav graph is ready
        nc.navigate(Routes.ShopDetail.createRoute(shopId))
    }
}
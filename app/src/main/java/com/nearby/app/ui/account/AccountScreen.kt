package com.nearby.app.ui.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nearby.app.ui.theme.NearbyColors
import com.nearby.app.ui.theme.NearbyType

@Composable
fun AccountScreen(
    onLogout: () -> Unit,
    onManageStore: (String) -> Unit,
    onRegisterStore: () -> Unit,
    onEditProfile: () -> Unit = {},
    onNotifications: () -> Unit = {},
    onSavedAddresses: () -> Unit = {},
    onHelpSupport: () -> Unit = {},
    onAbout: () -> Unit = {},
    viewModel: AccountViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val user = state.user

    val hasStore = user?.shop_id != null
    val storeApproved = user?.shopStatus == "approved"
    val storeUnderReview = user?.shopStatus == "pending"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NearbyColors.Background)
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(Modifier.height(40.dp))

        // ── Profile Section ────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(NearbyColors.Surface),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = user?.name?.take(1)?.uppercase() ?: "U",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = NearbyColors.PriceYellow
                    ),
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = user?.name?.takeIf { it.isNotBlank() } ?: "User",
                style = NearbyType.HeroProductName.copy(fontSize = 22.sp),
                color = NearbyColors.TextPrimary,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = user?.phone ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = NearbyColors.TextTertiary,
            )
        }

        Spacer(Modifier.height(40.dp))

        // ── Store Section ──────────────────────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = NearbyColors.Surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Storefront, 
                        "Store", 
                        tint = NearbyColors.PriceYellow, 
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "Your Business",
                        style = NearbyType.CardTitle,
                        color = NearbyColors.TextPrimary,
                    )
                }
                Spacer(Modifier.height(16.dp))

                when {
                    storeApproved -> {
                        Text(
                            text = "Your store is live! Reach customers nearby and manage your inventory easily.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = NearbyColors.TextSecondary,
                            lineHeight = 20.sp
                        )
                        Spacer(Modifier.height(20.dp))
                        Button(
                            onClick = { user.shop_id?.let { onManageStore(it) } },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NearbyColors.OnlineDot.copy(alpha = 0.15f),
                                contentColor = NearbyColors.OnlineDot
                            ),
                        ) {
                            Text("Manage Store", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                    storeUnderReview -> {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = NearbyColors.PriceYellow.copy(alpha = 0.08f),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(Icons.Default.Pending, "Under Review", tint = NearbyColors.PriceYellow)
                                Spacer(Modifier.width(16.dp))
                                Column {
                                    Text(
                                        "Verification Pending",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = NearbyColors.PriceYellow,
                                    )
                                    Text(
                                        "Our team is reviewing your store application.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = NearbyColors.TextSecondary,
                                    )
                                }
                            }
                        }
                    }
                    else -> {
                        Text(
                            text = "Sell your products to thousands of neighbors. Register your local shop now.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = NearbyColors.TextSecondary,
                            lineHeight = 20.sp
                        )
                        Spacer(Modifier.height(20.dp))
                        Button(
                            onClick = onRegisterStore,
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NearbyColors.PriceYellow,
                                contentColor = NearbyColors.Background
                            ),
                        ) {
                            Text("Start Selling Online", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        // ── Menu Items ─────────────────────────────────────────────────────
        SectionLabel("ACCOUNT SETTINGS")
        AccountMenuItem(Icons.Default.Person, "Edit Profile", onClick = onEditProfile)
        AccountMenuItem(Icons.Default.Notifications, "Notifications", onClick = onNotifications)
        AccountMenuItem(Icons.Default.LocationOn, "Saved Addresses", onClick = onSavedAddresses)

        Spacer(Modifier.height(16.dp))

        SectionLabel("SUPPORT & LEGAL")
        AccountMenuItem(Icons.AutoMirrored.Filled.Help, "Help & Support", onClick = onHelpSupport)
        AccountMenuItem(Icons.Default.Info, "About Nearby", onClick = onAbout)

        Spacer(Modifier.height(24.dp))

        // ── Sign Out ───────────────────────────────────────────────────────
        AccountMenuItem(
            icon = Icons.AutoMirrored.Filled.ExitToApp,
            label = "Log Out",
            tint = NearbyColors.OfflineDot,
            onClick = onLogout,
            showChevron = false
        )

        Spacer(Modifier.height(48.dp))
    }
}

@Composable
private fun SectionLabel(label: String) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp),
        color = NearbyColors.TextTertiary,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
    )
}

@Composable
private fun AccountMenuItem(
    icon: ImageVector,
    label: String,
    tint: Color = NearbyColors.TextSecondary,
    showChevron: Boolean = true,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, label, tint = tint.copy(alpha = 0.8f), modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(16.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = if (tint == NearbyColors.OfflineDot) tint else NearbyColors.TextPrimary,
            modifier = Modifier.weight(1f),
        )
        if (showChevron) {
            Icon(
                Icons.Default.ChevronRight,
                "Navigate",
                tint = NearbyColors.TextTertiary,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

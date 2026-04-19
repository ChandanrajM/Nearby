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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nearby.app.ui.theme.*

@Composable
fun AccountScreen(
    onOnlineStore: () -> Unit,
    onManageStore: () -> Unit,
    onSignOut: () -> Unit,
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
            .background(NearbyBackground)
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(Modifier.height(20.dp))

        // ── Profile Section ────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(NearbyCardLight),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "U",
                    style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                    color = NearbyCyan,
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = user?.name?.takeIf { it.isNotBlank() } ?: "User",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = NearbyTextPrimary,
            )
            Text(
                text = user?.phone ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = NearbyTextSecondary,
            )
        }

        Spacer(Modifier.height(32.dp))

        // ── Online Store Section ───────────────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = NearbyCard),
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Storefront, "Store", tint = NearbyCyan, modifier = Modifier.size(28.dp))
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "Online Store",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = NearbyTextPrimary,
                    )
                }
                Spacer(Modifier.height(12.dp))

                when {
                    storeApproved -> {
                        Text(
                            text = "Your store is live! Manage your products and view your QR code.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = NearbyTextSecondary,
                        )
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = onManageStore,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NearbyGreen),
                        ) {
                            Text("Manage Store", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                    storeUnderReview -> {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = NearbyYellow.copy(alpha = 0.12f),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(Icons.Default.Lock, "Under Review", tint = NearbyYellow)
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(
                                        "Under Review",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = NearbyYellow,
                                    )
                                    Text(
                                        "Your store registration is being verified by admin.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = NearbyTextSecondary,
                                    )
                                }
                            }
                        }
                    }
                    else -> {
                        Text(
                            text = "Start selling online! Register your shop and reach customers nearby.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = NearbyTextSecondary,
                        )
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = onOnlineStore,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NearbyCyan, contentColor = NearbyBlack),
                        ) {
                            Text("Register Your Store", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // ── Section: Account ───────────────────────────────────────────────
        SectionLabel("Account")
        AccountMenuItem(Icons.Default.Person, "Edit Profile", onClick = onEditProfile)
        AccountMenuItem(Icons.Default.Notifications, "Notifications", onClick = onNotifications)
        AccountMenuItem(Icons.Default.LocationOn, "Saved Addresses", onClick = onSavedAddresses)

        Spacer(Modifier.height(8.dp))

        // ── Section: More ──────────────────────────────────────────────────
        SectionLabel("More")
        AccountMenuItem(Icons.Default.Help, "Help & Support", onClick = onHelpSupport)
        AccountMenuItem(Icons.Default.Info, "About", onClick = onAbout)

        Spacer(Modifier.height(16.dp))

        // ── Sign Out ───────────────────────────────────────────────────────
        AccountMenuItem(
            icon = Icons.AutoMirrored.Filled.ExitToApp,
            label = "Sign Out",
            tint = NearbyError,
            onClick = onSignOut,
        )

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun SectionLabel(label: String) {
    Text(
        text = label.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = NearbyTextTertiary,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
    )
}

@Composable
private fun AccountMenuItem(
    icon: ImageVector,
    label: String,
    tint: androidx.compose.ui.graphics.Color = NearbyTextSecondary,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, label, tint = tint, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(16.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = tint,
            modifier = Modifier.weight(1f),
        )
        Icon(
            Icons.Default.ChevronRight,
            "Navigate",
            tint = NearbyTextTertiary,
            modifier = Modifier.size(20.dp),
        )
    }
}

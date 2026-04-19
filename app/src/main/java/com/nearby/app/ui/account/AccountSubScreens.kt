package com.nearby.app.ui.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import com.nearby.app.ui.theme.*
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

// ═══════════════════════════════════════════════════════════════════════════
//  EDIT PROFILE SCREEN
// ═══════════════════════════════════════════════════════════════════════════

@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    viewModel: AccountViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    
    var name by remember(state.user) { mutableStateOf(state.user?.name ?: "") }
    var email by remember(state.user) { mutableStateOf(state.user?.email ?: "") }
    var phone by remember(state.user) { mutableStateOf(state.user?.phone ?: "") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NearbyBackground)
            .verticalScroll(rememberScrollState()),
    ) {
        AccountSubTopBar(title = "Edit Profile", onBack = onBack)
        Spacer(Modifier.height(20.dp))

        // Avatar
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(90.dp)
                .clip(CircleShape)
                .background(NearbyCard),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = name.firstOrNull()?.uppercase() ?: "U",
                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                color = NearbyCyan,
            )
        }
        TextButton(
            onClick = {},
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            Icon(Icons.Default.Edit, null, Modifier.size(16.dp), tint = NearbyCyan)
            Spacer(Modifier.width(4.dp))
            Text("Change Photo", color = NearbyCyan, style = MaterialTheme.typography.labelMedium)
        }

        Spacer(Modifier.height(24.dp))

        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            AccountFormField(value = name, onValueChange = { name = it }, label = "Full Name", icon = Icons.Default.Person)
            AccountFormField(value = phone, onValueChange = { phone = it }, label = "Phone Number", icon = Icons.Default.Phone)
            AccountFormField(value = email, onValueChange = { email = it }, label = "Email (optional)", icon = Icons.Default.Email)

            Spacer(Modifier.height(8.dp))
            
            if (state.error != null) {
                Text(state.error!!, color = NearbyError, style = MaterialTheme.typography.bodySmall)
            }
            if (state.successMessage != null) {
                Text(state.successMessage!!, color = NearbyGreen, style = MaterialTheme.typography.bodySmall)
            }

            Button(
                onClick = { 
                    viewModel.updateProfile(name, email)
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NearbyCyan, contentColor = NearbyBlack),
                enabled = !state.isSaving
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(Modifier.size(18.dp), color = NearbyBlack, strokeWidth = 2.dp)
                } else {
                    Text("Save Changes", fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(Modifier.height(32.dp))
    }
}

// ═══════════════════════════════════════════════════════════════════════════
//  NOTIFICATIONS SCREEN
// ═══════════════════════════════════════════════════════════════════════════

@Composable
fun NotificationsScreen(onBack: () -> Unit) {
    data class NotifSetting(val label: String, val subtitle: String, var enabled: Boolean)

    val settings = remember {
        mutableStateListOf(
            NotifSetting("Order Updates", "Get notified about your order status", true),
            NotifSetting("Nearby Shops", "New shops that open in your area", true),
            NotifSetting("Deals & Offers", "Exclusive deals from saved shops", false),
            NotifSetting("QR Scans", "When someone scans your shop QR", true),
            NotifSetting("Store Approval", "Status updates on your store registration", true),
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NearbyBackground),
    ) {
        AccountSubTopBar(title = "Notifications", onBack = onBack)
        Spacer(Modifier.height(8.dp))

        settings.forEachIndexed { index, setting ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(NearbyCard)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(setting.label, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold), color = NearbyTextPrimary)
                    Text(setting.subtitle, style = MaterialTheme.typography.bodySmall, color = NearbyTextSecondary)
                }
                Switch(
                    checked = setting.enabled,
                    onCheckedChange = { settings[index] = setting.copy(enabled = it) },
                    colors = SwitchDefaults.colors(checkedThumbColor = NearbyBackground, checkedTrackColor = NearbyCyan),
                )
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
//  SAVED ADDRESSES SCREEN
// ═══════════════════════════════════════════════════════════════════════════

@Composable
fun SavedAddressesScreen(onBack: () -> Unit) {
    data class Address(val label: String, val full: String, val isDefault: Boolean = false)

    val addresses = remember {
        mutableStateListOf(
            Address("Home", "42, 3rd Cross, Indiranagar, Bengaluru – 560038", isDefault = true),
            Address("Work", "WeWork Galaxy, Residency Road, Bengaluru – 560025"),
        )
    }
    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NearbyBackground),
    ) {
        AccountSubTopBar(title = "Saved Addresses", onBack = onBack) {
            IconButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, "Add Address", tint = NearbyCyan)
            }
        }
        Spacer(Modifier.height(8.dp))

        if (addresses.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.LocationOff, null, tint = NearbyTextTertiary, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(12.dp))
                    Text("No saved addresses", color = NearbyTextSecondary)
                }
            }
        } else {
            addresses.forEachIndexed { index, addr ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = NearbyCard),
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Icon(
                            if (addr.label == "Home") Icons.Default.Home else Icons.Default.Work,
                            null, tint = NearbyCyan, modifier = Modifier.size(22.dp),
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(addr.label, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = NearbyTextPrimary)
                                if (addr.isDefault) {
                                    Spacer(Modifier.width(8.dp))
                                    Surface(shape = RoundedCornerShape(4.dp), color = NearbyCyan.copy(alpha = 0.18f)) {
                                        Text("Default", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = NearbyCyan)
                                    }
                                }
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(addr.full, style = MaterialTheme.typography.bodySmall, color = NearbyTextSecondary)
                        }
                        IconButton(onClick = { addresses.removeAt(index) }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Delete, "Delete", tint = NearbyError, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(32.dp))
    }

    if (showAddDialog) {
        var newAddr by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = NearbySurface,
            title = { Text("Add Address", color = NearbyTextPrimary) },
            text = {
                OutlinedTextField(
                    value = newAddr,
                    onValueChange = { newAddr = it },
                    label = { Text("Full Address") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NearbyCyan, unfocusedBorderColor = NearbyDivider, focusedTextColor = NearbyTextPrimary, unfocusedTextColor = NearbyTextPrimary),
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (newAddr.isNotBlank()) {
                        addresses.add(Address("Other", newAddr))
                        showAddDialog = false
                    }
                }, colors = ButtonDefaults.buttonColors(containerColor = NearbyCyan, contentColor = NearbyBlack)) { Text("Add") }
            },
            dismissButton = { TextButton(onClick = { showAddDialog = false }) { Text("Cancel", color = NearbyTextSecondary) } },
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════
//  HELP & SUPPORT SCREEN
// ═══════════════════════════════════════════════════════════════════════════

@Composable
fun HelpSupportScreen(onBack: () -> Unit) {
    data class FaqItem(val question: String, val answer: String)
    val faqs = listOf(
        FaqItem("How does the QR code work?", "Once your shop is approved, you'll receive a unique QR code. Customers scan it to instantly access your store page and browse your products."),
        FaqItem("How long does shop approval take?", "Shop registrations are typically reviewed within 24-48 hours. You'll receive a notification once approved."),
        FaqItem("How do I update my product images?", "In 'Manage Store', tap the edit icon on any product. You can also use our AI Enhance feature to auto-improve your product photos."),
        FaqItem("Can customers order directly?", "Currently Nearby supports catalogue browsing and WhatsApp enquiries. In-app ordering is coming soon."),
        FaqItem("How do I cancel my registration?", "Contact our support team via the form below and we'll process your request within 24 hours."),
    )
    var expandedIndex by remember { mutableStateOf(-1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NearbyBackground)
            .verticalScroll(rememberScrollState()),
    ) {
        AccountSubTopBar(title = "Help & Support", onBack = onBack)
        Spacer(Modifier.height(12.dp))

        Text(
            "Frequently Asked Questions",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = NearbyTextPrimary,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(Modifier.height(8.dp))

        faqs.forEachIndexed { index, faq ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = NearbyCard),
                onClick = { expandedIndex = if (expandedIndex == index) -1 else index },
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(faq.question, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = NearbyTextPrimary, modifier = Modifier.weight(1f))
                        Icon(
                            if (expandedIndex == index) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            null, tint = NearbyTextSecondary,
                        )
                    }
                    if (expandedIndex == index) {
                        Spacer(Modifier.height(8.dp))
                        Text(faq.answer, style = MaterialTheme.typography.bodySmall, color = NearbyTextSecondary)
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Contact support card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = NearbyCyan.copy(alpha = 0.12f)),
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Still need help?", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = NearbyTextPrimary)
                Spacer(Modifier.height(4.dp))
                Text("Our support team is available Mon–Sat, 9am–6pm IST", style = MaterialTheme.typography.bodySmall, color = NearbyTextSecondary)
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NearbyCyan, contentColor = NearbyBlack),
                ) {
                    Icon(Icons.AutoMirrored.Filled.Chat, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Chat with Support", fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Icon(Icons.Default.Email, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Email Us")
                }
            }
        }
        Spacer(Modifier.height(32.dp))
    }
}

// ═══════════════════════════════════════════════════════════════════════════
//  ABOUT SCREEN
// ═══════════════════════════════════════════════════════════════════════════

@Composable
fun AboutScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NearbyBackground)
            .verticalScroll(rememberScrollState()),
    ) {
        AccountSubTopBar(title = "About", onBack = onBack)
        Spacer(Modifier.height(24.dp))

        // Logo area
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(NearbyCyan.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.LocationOn, null, tint = NearbyCyan, modifier = Modifier.size(44.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text("Nearby", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = NearbyTextPrimary)
            Text("Version 1.0.0", style = MaterialTheme.typography.bodySmall, color = NearbyTextSecondary)
        }

        Spacer(Modifier.height(32.dp))

        listOf(
            Triple(Icons.Default.Info, "Version", "1.0.0 (Build 1)"),
            Triple(Icons.Default.Code, "Platform", "Android (Jetpack Compose)"),
            Triple(Icons.Default.Storage, "Backend", "FastAPI + Supabase"),
            Triple(Icons.Default.Security, "Privacy Policy", "nearby.app/privacy"),
            Triple(Icons.Default.Gavel, "Terms of Service", "nearby.app/terms"),
            Triple(Icons.Default.ContactMail, "Contact", "hello@nearby.app"),
        ).forEach { (icon, label, value) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(NearbyCard)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(icon, null, tint = NearbyCyan, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(14.dp))
                Text(label, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = NearbyTextPrimary, modifier = Modifier.weight(1f))
                Text(value, style = MaterialTheme.typography.bodySmall, color = NearbyTextSecondary)
            }
        }

        Spacer(Modifier.height(24.dp))
        Text(
            "© 2025 Nearby. All rights reserved.",
            style = MaterialTheme.typography.bodySmall,
            color = NearbyTextTertiary,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Spacer(Modifier.height(32.dp))
    }
}

// ── Shared helpers ─────────────────────────────────────────────────────────

@Composable
private fun AccountSubTopBar(
    title: String,
    onBack: () -> Unit,
    actions: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = NearbyTextPrimary)
        }
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = NearbyTextPrimary,
            modifier = Modifier.weight(1f),
        )
        actions?.invoke()
    }
}

@Composable
private fun AccountFormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, null, tint = NearbyTextSecondary) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NearbyCyan,
            unfocusedBorderColor = NearbyDivider,
            cursorColor = NearbyCyan,
            focusedTextColor = NearbyTextPrimary,
            unfocusedTextColor = NearbyTextPrimary,
            focusedLabelColor = NearbyCyan,
            unfocusedLabelColor = NearbyTextSecondary,
            focusedContainerColor = NearbyCard,
            unfocusedContainerColor = NearbyCard,
        ),
    )
}

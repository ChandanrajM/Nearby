package com.nearby.app.ui.store

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nearby.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun StoreRegistrationScreen(
    onBack: () -> Unit,
    onSubmitted: () -> Unit,
    viewModel: StoreRegistrationViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val location by viewModel.locationRepo.location.collectAsState()

    // If submitted, show success
    if (state.isSubmitted) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(NearbyBackground),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp),
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    "Success",
                    tint = NearbyGreen,
                    modifier = Modifier.size(80.dp),
                )
                Spacer(Modifier.height(24.dp))
                Text(
                    "Registration Submitted!",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = NearbyTextPrimary,
                )
                Spacer(Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NearbyYellow.copy(alpha = 0.12f),
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Default.Lock, "Lock", tint = NearbyYellow)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Under Review", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = NearbyYellow)
                            Text("Your store will go live after admin verification.", style = MaterialTheme.typography.bodySmall, color = NearbyTextSecondary)
                        }
                    }
                }
                Spacer(Modifier.height(32.dp))
                Button(
                    onClick = onSubmitted,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NearbyCyan, contentColor = NearbyBlack),
                ) {
                    Text("Back to Home", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                }
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NearbyBackground),
    ) {
        // ── Top bar ────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = { if (state.currentStep == 0) onBack() else viewModel.prevStep() }) {
                Icon(Icons.Default.ArrowBack, "Back", tint = NearbyTextPrimary)
            }
            Text(
                "Register Your Store",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = NearbyTextPrimary,
            )
        }

        // ── Step Indicator ─────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            repeat(3) { step ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (step <= state.currentStep) NearbyCyan else NearbyDivider),
                )
            }
        }
        Text(
            text = when (state.currentStep) {
                0 -> "Step 1 of 3: Shop Details"
                1 -> "Step 2 of 3: Location"
                else -> "Step 3 of 3: Category & GST"
            },
            style = MaterialTheme.typography.labelMedium,
            color = NearbyTextSecondary,
            modifier = Modifier.padding(horizontal = 24.dp),
        )

        Spacer(Modifier.height(20.dp))

        // ── Form Content ───────────────────────────────────────────────
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
        ) {
            AnimatedContent(
                targetState = state.currentStep,
                transitionSpec = {
                    slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut()
                },
                label = "stepTransition",
            ) { step ->
                when (step) {
                    0 -> StepShopDetails(state, viewModel)
                    1 -> StepLocation(state, viewModel, location.address)
                    2 -> StepCategoryGst(state, viewModel)
                }
            }
        }

        // ── Error ──────────────────────────────────────────────────────
        if (state.error != null) {
            Text(
                text = state.error!!,
                color = NearbyError,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            )
        }

        // ── Bottom Button ──────────────────────────────────────────────
        Button(
            onClick = {
                if (state.currentStep < 2) {
                    viewModel.nextStep()
                } else {
                    viewModel.submit()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .height(54.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NearbyCyan, contentColor = NearbyBlack),
            enabled = !state.isSubmitting,
        ) {
            if (state.isSubmitting) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = NearbyBlack, strokeWidth = 2.dp)
            } else {
                Text(
                    text = if (state.currentStep < 2) "Next" else "Submit for Review",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp),
                )
            }
        }
    }
}

@Composable
private fun StepShopDetails(state: StoreRegState, viewModel: StoreRegistrationViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        RegTextField("Shop Name", state.shopName) { viewModel.updateField("shopName", it) }
        RegTextField("Owner Name", state.ownerName) { viewModel.updateField("ownerName", it) }
        RegTextField("Phone Number", state.phone, isPhone = true) { viewModel.updateField("phone", it) }
        RegTextField("Address", state.address) { viewModel.updateField("address", it) }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StepLocation(state: StoreRegState, viewModel: StoreRegistrationViewModel, detectedAddress: String) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Auto-detected location
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = NearbyCard),
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.MyLocation, "Detected", tint = NearbyGreen)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Auto-detected Location", style = MaterialTheme.typography.labelMedium, color = NearbyGreen)
                    Text(detectedAddress.take(60), style = MaterialTheme.typography.bodySmall, color = NearbyTextSecondary)
                }
            }
        }

        Text("Select your city:", style = MaterialTheme.typography.titleMedium, color = NearbyTextPrimary)

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            state.cities.forEach { city ->
                val selected = city == state.selectedCity
                FilterChip(
                    selected = selected,
                    onClick = { viewModel.updateField("selectedCity", city) },
                    label = { Text(city) },
                    shape = RoundedCornerShape(10.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = NearbyCard,
                        labelColor = NearbyTextSecondary,
                        selectedContainerColor = NearbyCyan,
                        selectedLabelColor = NearbyBlack,
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = NearbyDivider,
                        selectedBorderColor = NearbyCyan,
                        enabled = true,
                        selected = selected,
                    ),
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StepCategoryGst(state: StoreRegState, viewModel: StoreRegistrationViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Select your category:", style = MaterialTheme.typography.titleMedium, color = NearbyTextPrimary)
        Text("⚠️ Category cannot be changed after approval", style = MaterialTheme.typography.bodySmall, color = NearbyYellow)

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            state.categories.forEach { cat ->
                val selected = cat == state.selectedCategory
                FilterChip(
                    selected = selected,
                    onClick = { viewModel.updateField("selectedCategory", cat) },
                    label = { Text(cat) },
                    shape = RoundedCornerShape(10.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = NearbyCard,
                        labelColor = NearbyTextSecondary,
                        selectedContainerColor = NearbyCyan,
                        selectedLabelColor = NearbyBlack,
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = NearbyDivider,
                        selectedBorderColor = NearbyCyan,
                        enabled = true,
                        selected = selected,
                    ),
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        RegTextField("GST Number", state.gstNumber) { viewModel.updateField("gstNumber", it) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegTextField(
    label: String,
    value: String,
    isPhone: Boolean = false,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NearbyCyan,
            unfocusedBorderColor = NearbyDivider,
            cursorColor = NearbyCyan,
            focusedLabelColor = NearbyCyan,
            unfocusedLabelColor = NearbyTextTertiary,
            focusedTextColor = NearbyTextPrimary,
            unfocusedTextColor = NearbyTextPrimary,
        ),
    )
}

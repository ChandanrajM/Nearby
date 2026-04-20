package com.nearby.app.ui.store

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nearby.app.ui.theme.NearbyColors
import com.nearby.app.ui.theme.NearbyType
import com.nearby.app.ui.theme.Typography as NearbyTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreRegistrationScreen(
    onBack: () -> Unit,
    onSuccess: (String) -> Unit,
    viewModel: StoreRegistrationViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    // Assuming we need shopId for success, but state only has isSubmitted.
    // If shopId is needed but not in state, we might need to get it from profile or update ViewModel.
    // For now, if submitted, we'll go back or to success.
    LaunchedEffect(state.isSubmitted) {
        if (state.isSubmitted) {
            // Need a shopId. In ViewModel, it calls fetchProfile. 
            // We might want to wait a bit or just go back.
            onBack() 
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NearbyColors.Background)
    ) {
        // ── Top Bar ──────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = NearbyColors.TextPrimary)
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Register Your Store",
                style = NearbyType.HeroProductName.copy(fontSize = 20.sp),
                color = NearbyColors.TextPrimary,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(Modifier.height(12.dp))
            
            Text(
                text = "Business Information",
                style = MaterialTheme.typography.bodyLarge,
                color = NearbyColors.TextTertiary
            )

            // ── Form Fields ───────────────────────────────────────────
            OutlinedTextField(
                value = state.shopName,
                onValueChange = { viewModel.updateField("shopName", it) },
                label = { Text("Shop Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NearbyColors.PriceYellow,
                    unfocusedBorderColor = NearbyColors.Surface,
                    focusedLabelColor = NearbyColors.PriceYellow,
                    cursorColor = NearbyColors.PriceYellow,
                    focusedTextColor = NearbyColors.TextPrimary,
                    unfocusedTextColor = NearbyColors.TextPrimary,
                    unfocusedContainerColor = NearbyColors.Surface,
                    focusedContainerColor = NearbyColors.Surface,
                )
            )

            OutlinedTextField(
                value = state.ownerName,
                onValueChange = { viewModel.updateField("ownerName", it) },
                label = { Text("Owner Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NearbyColors.PriceYellow,
                    unfocusedBorderColor = NearbyColors.Surface,
                    focusedLabelColor = NearbyColors.PriceYellow,
                    cursorColor = NearbyColors.PriceYellow,
                    focusedTextColor = NearbyColors.TextPrimary,
                    unfocusedTextColor = NearbyColors.TextPrimary,
                    unfocusedContainerColor = NearbyColors.Surface,
                    focusedContainerColor = NearbyColors.Surface,
                )
            )

            OutlinedTextField(
                value = state.address,
                onValueChange = { viewModel.updateField("address", it) },
                label = { Text("Store Address") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                minLines = 2,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NearbyColors.PriceYellow,
                    unfocusedBorderColor = NearbyColors.Surface,
                    focusedLabelColor = NearbyColors.PriceYellow,
                    cursorColor = NearbyColors.PriceYellow,
                    focusedTextColor = NearbyColors.TextPrimary,
                    unfocusedTextColor = NearbyColors.TextPrimary,
                    unfocusedContainerColor = NearbyColors.Surface,
                    focusedContainerColor = NearbyColors.Surface,
                )
            )

            OutlinedTextField(
                value = state.selectedCategory,
                onValueChange = { viewModel.updateField("selectedCategory", it) },
                label = { Text("Store Category") },
                placeholder = { Text("e.g. Fashion, Grocery") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NearbyColors.PriceYellow,
                    unfocusedBorderColor = NearbyColors.Surface,
                    focusedLabelColor = NearbyColors.PriceYellow,
                    cursorColor = NearbyColors.PriceYellow,
                    focusedTextColor = NearbyColors.TextPrimary,
                    unfocusedTextColor = NearbyColors.TextPrimary,
                    unfocusedContainerColor = NearbyColors.Surface,
                    focusedContainerColor = NearbyColors.Surface,
                )
            )

            OutlinedTextField(
                value = state.gstNumber,
                onValueChange = { viewModel.updateField("gstNumber", it) },
                label = { Text("GST Number (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NearbyColors.PriceYellow,
                    unfocusedBorderColor = NearbyColors.Surface,
                    focusedLabelColor = NearbyColors.PriceYellow,
                    cursorColor = NearbyColors.PriceYellow,
                    focusedTextColor = NearbyColors.TextPrimary,
                    unfocusedTextColor = NearbyColors.TextPrimary,
                    unfocusedContainerColor = NearbyColors.Surface,
                    focusedContainerColor = NearbyColors.Surface,
                )
            )

            // ── Error Message ─────────────────────────────────────────
            if (state.error != null) {
                Text(
                    text = state.error!!,
                    color = NearbyColors.OfflineDot,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(12.dp))

            // ── Submit Button ──────────────────────────────────────────
            Button(
                onClick = { viewModel.submit() },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NearbyColors.PriceYellow,
                    contentColor = NearbyColors.Background
                )
            ) {
                StoreRegistrationButtonContent(state.isSubmitting)
            }
            
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun StoreRegistrationButtonContent(isSubmitting: Boolean) {
    if (isSubmitting) {
        CircularProgressIndicator(
            color = NearbyColors.Background, 
            modifier = Modifier.size(24.dp),
            strokeWidth = 2.dp
        )
    } else {
        // Use static Typography reference to avoid @Composable property issues inside Button Content
        val labelStyle = NearbyTypography.labelLarge
        Text(
            text = "SUBMIT REGISTRATION",
            style = labelStyle.copy(
                fontWeight = FontWeight.Bold, 
                letterSpacing = 1.sp
            )
        )
    }
}

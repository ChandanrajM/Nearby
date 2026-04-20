package com.nearby.app.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nearby.app.ui.theme.NearbyColors
import com.nearby.app.ui.theme.NearbyType

@Composable
fun RegistrationScreen(
    onBack: () -> Unit,
    onRegisterSuccess: (String) -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    // If OTP was sent from this screen, navigate to VerifyOtp
    LaunchedEffect(state.isOtpSent) {
        if (state.isOtpSent) {
            onRegisterSuccess(state.phone)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NearbyColors.Background)
            .padding(horizontal = 32.dp),
    ) {
        Spacer(Modifier.height(40.dp))
        
        IconButton(onClick = onBack, modifier = Modifier.offset(x = (-12).dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = NearbyColors.TextPrimary)
        }

        Spacer(Modifier.height(40.dp))

        Text(
            text = "Create Account",
            style = NearbyType.HeroProductName.copy(fontSize = 32.sp),
            color = NearbyColors.TextPrimary,
        )
        
        Spacer(Modifier.height(12.dp))
        
        Text(
            text = "Join Nearby today to discover awesome local deals",
            style = MaterialTheme.typography.bodyLarge,
            color = NearbyColors.TextSecondary,
        )

        Spacer(Modifier.height(60.dp))

        // Phone Input
        OutlinedTextField(
            value = state.phone,
            onValueChange = viewModel::onPhoneChange,
            label = { Text("Phone Number", color = NearbyColors.TextTertiary) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NearbyColors.PriceYellow,
                unfocusedBorderColor = NearbyColors.Surface,
                focusedTextColor = NearbyColors.TextPrimary,
                unfocusedTextColor = NearbyColors.TextPrimary,
                unfocusedContainerColor = NearbyColors.Surface,
                focusedContainerColor = NearbyColors.Surface,
            ),
        )

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = viewModel::sendOtp,
            modifier = Modifier.fillMaxWidth().height(60.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NearbyColors.PriceYellow, contentColor = NearbyColors.Background),
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(color = NearbyColors.Background, modifier = Modifier.size(24.dp))
            } else {
                Text("Continue", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp))
            }
        }

        if (state.error != null) {
            Spacer(Modifier.height(16.dp))
            Text(state.error!!, color = NearbyColors.OfflineDot, style = MaterialTheme.typography.bodySmall)
        }
    }
}

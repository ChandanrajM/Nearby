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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nearby.app.ui.theme.NearbyColors
import com.nearby.app.ui.theme.NearbyType

@Composable
fun VerifyOtpScreen(
    email: String, // Kept to match route but using phone from state
    onVerifySuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NearbyColors.Background)
            .padding(horizontal = 32.dp),
    ) {
        Spacer(Modifier.height(40.dp))
        
        IconButton(onClick = { /* Handle back if needed */ }, modifier = Modifier.offset(x = (-12).dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = NearbyColors.TextPrimary)
        }

        Spacer(Modifier.height(40.dp))

        Text(
            text = "Verify Code",
            style = NearbyType.HeroProductName.copy(fontSize = 32.sp),
            color = NearbyColors.TextPrimary,
        )
        
        Spacer(Modifier.height(12.dp))
        
        Text(
            text = "Enter the 6-digit code sent to your phone number",
            style = MaterialTheme.typography.bodyLarge,
            color = NearbyColors.TextSecondary,
        )

        Spacer(Modifier.height(60.dp))

        // OTP Input
        OutlinedTextField(
            value = state.otp,
            onValueChange = viewModel::onOtpChange,
            label = { Text("6-Digit Code", color = NearbyColors.TextTertiary) },
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
            textStyle = MaterialTheme.typography.headlineMedium.copy(
                textAlign = TextAlign.Center,
                letterSpacing = 8.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = { viewModel.verifyOtp(onSuccess = onVerifySuccess) },
            modifier = Modifier.fillMaxWidth().height(60.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NearbyColors.PriceYellow, contentColor = NearbyColors.Background),
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(color = NearbyColors.Background, modifier = Modifier.size(24.dp))
            } else {
                Text("Verify & Continue", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp))
            }
        }

        if (state.error != null) {
            Spacer(Modifier.height(16.dp))
            Text(state.error!!, color = NearbyColors.OfflineDot, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        }
    }
}

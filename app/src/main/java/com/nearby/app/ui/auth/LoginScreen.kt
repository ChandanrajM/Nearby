package com.nearby.app.ui.auth

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nearby.app.ui.theme.NearbyColors
import com.nearby.app.ui.theme.NearbyType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NearbyColors.Background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(100.dp))

            // ── Logo / Brand ───────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(NearbyColors.PriceYellow),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "N",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = NearbyColors.Background
                    ),
                )
            }
            
            Spacer(Modifier.height(24.dp))
            
            Text(
                text = "NEARBY",
                style = NearbyType.HeroProductName.copy(
                    letterSpacing = 8.sp,
                    fontSize = 24.sp
                ),
                color = NearbyColors.TextPrimary,
            )
            
            Spacer(Modifier.height(12.dp))
            
            Text(
                text = "Discover local shops instantly",
                style = MaterialTheme.typography.bodyLarge,
                color = NearbyColors.TextTertiary,
            )

            Spacer(Modifier.height(60.dp))

            // ── Phone Input ────────────────────────────────────────────
            OutlinedTextField(
                value = state.phone,
                onValueChange = viewModel::onPhoneChange,
                label = { Text("Phone Number", color = NearbyColors.TextTertiary) },
                placeholder = { Text("Enter 10-digit number", color = NearbyColors.TextTertiary.copy(alpha = 0.5f)) },
                prefix = {
                    Text(
                        text = "+91  ",
                        color = NearbyColors.TextPrimary,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NearbyColors.PriceYellow,
                    unfocusedBorderColor = NearbyColors.Surface,
                    cursorColor = NearbyColors.PriceYellow,
                    focusedLabelColor = NearbyColors.PriceYellow,
                    unfocusedLabelColor = NearbyColors.TextTertiary,
                    focusedTextColor = NearbyColors.TextPrimary,
                    unfocusedTextColor = NearbyColors.TextPrimary,
                    unfocusedContainerColor = NearbyColors.Surface,
                    focusedContainerColor = NearbyColors.Surface,
                ),
            )

            Spacer(Modifier.height(16.dp))

            // ── OTP Input (shown after send) ───────────────────────────
            AnimatedVisibility(
                visible = state.isOtpSent,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    OutlinedTextField(
                        value = state.otp,
                        onValueChange = viewModel::onOtpChange,
                        label = { Text("OTP Code", color = NearbyColors.TextTertiary) },
                        placeholder = { Text("Enter 6-digit OTP", color = NearbyColors.TextTertiary.copy(alpha = 0.5f)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NearbyColors.PriceYellow,
                            unfocusedBorderColor = NearbyColors.Surface,
                            cursorColor = NearbyColors.PriceYellow,
                            focusedLabelColor = NearbyColors.PriceYellow,
                            unfocusedLabelColor = NearbyColors.TextTertiary,
                            focusedTextColor = NearbyColors.TextPrimary,
                            unfocusedTextColor = NearbyColors.TextPrimary,
                            unfocusedContainerColor = NearbyColors.Surface,
                            focusedContainerColor = NearbyColors.Surface,
                        ),
                    )
                    Spacer(Modifier.height(20.dp))
                }
            }

            // ── Error ──────────────────────────────────────────────────
            if (state.error != null) {
                Text(
                    text = state.error!!,
                    color = NearbyColors.OfflineDot,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            // ── Primary Button ─────────────────────────────────────────
            Button(
                onClick = {
                    if (!state.isOtpSent) {
                        viewModel.sendOtp()
                    } else {
                        viewModel.verifyOtp(onSuccess = onLoginSuccess)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NearbyColors.PriceYellow,
                    contentColor = NearbyColors.Background,
                ),
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(color = NearbyColors.Background, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = if (!state.isOtpSent) "Send OTP" else "Verify & Login",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            letterSpacing = 1.sp
                        ),
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // ── Social Login ───────────────────────────────────────────
            val context = androidx.compose.ui.platform.LocalContext.current
            OutlinedButton(
                onClick = {
                    viewModel.loginWithGoogle(context, onSuccess = onLoginSuccess)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, NearbyColors.Surface),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = NearbyColors.TextPrimary,
                ),
            ) {
                Text(
                    text = "Continue with Google",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = NearbyColors.TextPrimary,
                )
            }

            Spacer(Modifier.height(24.dp))
            
            TextButton(onClick = onRegisterClick) {
                Text(
                    text = "Don't have an account? Sign Up",
                    color = NearbyColors.PriceYellow,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(Modifier.weight(1f))

            // ── Footer ─────────────────────────────────────────────────
            Text(
                text = "By continuing, you agree to our Terms & Privacy Policy",
                style = MaterialTheme.typography.bodySmall,
                color = NearbyColors.TextTertiary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp),
            )
        }
    }
}

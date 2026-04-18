package com.nearby.app.ui.auth

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nearby.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NearbyBackground),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(100.dp))

            // ── Logo / Brand ───────────────────────────────────────────
            Text(
                text = "N",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 48.sp,
                ),
                color = NearbyCyan,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "NEARBY",
                style = MaterialTheme.typography.headlineMedium.copy(
                    letterSpacing = 6.sp,
                    fontWeight = FontWeight.Bold,
                ),
                color = NearbyTextPrimary,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Sign in to continue",
                style = MaterialTheme.typography.bodyMedium,
                color = NearbyTextSecondary,
            )

            Spacer(Modifier.height(48.dp))

            // ── Phone Input ────────────────────────────────────────────
            OutlinedTextField(
                value = state.phone,
                onValueChange = viewModel::onPhoneChange,
                label = { Text("Phone Number") },
                placeholder = { Text("Enter 10-digit number") },
                prefix = {
                    Text(
                        text = "+91  ",
                        color = NearbyTextSecondary,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
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

            Spacer(Modifier.height(16.dp))

            // ── OTP Input (shown after send) ───────────────────────────
            AnimatedVisibility(visible = state.isOtpSent) {
                Column {
                    OutlinedTextField(
                        value = state.otp,
                        onValueChange = viewModel::onOtpChange,
                        label = { Text("OTP Code") },
                        placeholder = { Text("Enter 6-digit OTP") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                    Spacer(Modifier.height(16.dp))
                }
            }

            // ── Error ──────────────────────────────────────────────────
            if (state.error != null) {
                Text(
                    text = state.error!!,
                    color = NearbyError,
                    style = MaterialTheme.typography.bodySmall,
                )
                Spacer(Modifier.height(8.dp))
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
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NearbyCyan,
                    contentColor = NearbyBlack,
                ),
            ) {
                Text(
                    text = if (!state.isOtpSent) "Send OTP" else "Verify & Login",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                    ),
                )
            }

            Spacer(Modifier.height(32.dp))

            // ── Divider ────────────────────────────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Divider(modifier = Modifier.weight(1f), color = NearbyDivider)
                Text(
                    text = "  or  ",
                    style = MaterialTheme.typography.bodySmall,
                    color = NearbyTextTertiary,
                )
                Divider(modifier = Modifier.weight(1f), color = NearbyDivider)
            }

            Spacer(Modifier.height(24.dp))

            val context = androidx.compose.ui.platform.LocalContext.current
            OutlinedButton(
                onClick = {
                    viewModel.loginWithGoogle(context, onSuccess = onLoginSuccess)
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = Brush.linearGradient(listOf(NearbyDivider, NearbyDivider))
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = NearbyTextPrimary,
                ),
            ) {
                Text(
                    text = "G",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFFDB4437),
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "Continue with Google",
                    style = MaterialTheme.typography.labelLarge,
                    color = NearbyTextPrimary,
                )
            }

            Spacer(Modifier.weight(1f))

            // ── Footer ─────────────────────────────────────────────────
            Text(
                text = "By continuing, you agree to our Terms & Privacy Policy",
                style = MaterialTheme.typography.bodySmall,
                color = NearbyTextTertiary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp),
            )
        }
    }
}

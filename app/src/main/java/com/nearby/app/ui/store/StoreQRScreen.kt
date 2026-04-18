package com.nearby.app.ui.store

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nearby.app.ui.theme.*

@Composable
fun StoreQRScreen(
    shopId: String,
    onBack: () -> Unit,
) {
    // The QR code image would be loaded from the backend endpoint:
    // GET /shops/{shopId}/qr  -> returns a PNG image
    // For now we show a placeholder with the QR URL text

    val qrUrl = "https://nearby.app/shop/$shopId"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NearbyBackground),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // ── Top bar ────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, "Back", tint = NearbyTextPrimary)
                }
                Text(
                    "Your QR Code",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = NearbyTextPrimary,
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = { /* TODO: Share QR */ }) {
                    Icon(Icons.Default.Share, "Share", tint = NearbyCyan)
                }
            }

            Spacer(Modifier.height(40.dp))

            // ── QR Code Card ───────────────────────────────────────────
            Card(
                modifier = Modifier
                    .size(300.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = NearbyTextPrimary),
                elevation = CardDefaults.cardElevation(8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    // Placeholder QR pattern
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // In production, this would be AsyncImage loading from /shops/{shopId}/qr
                        Box(
                            modifier = Modifier
                                .size(200.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    width = 3.dp,
                                    brush = Brush.linearGradient(listOf(NearbyCyanDark, NearbyGreen)),
                                    shape = RoundedCornerShape(12.dp),
                                )
                                .background(NearbyCardLight),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "QR",
                                    style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Bold),
                                    color = NearbyCyan,
                                )
                                Text(
                                    text = shopId.take(8),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NearbyTextTertiary,
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "NEARBY",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 4.sp,
                            ),
                            color = NearbyBlack,
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            Text(
                text = "Share this QR code with your customers",
                style = MaterialTheme.typography.bodyLarge,
                color = NearbyTextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp),
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "They can scan it to browse your store instantly",
                style = MaterialTheme.typography.bodyMedium,
                color = NearbyTextTertiary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp),
            )

            Spacer(Modifier.height(40.dp))

            // ── Action buttons ─────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick = { /* TODO: Download QR */ },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.linearGradient(listOf(NearbyDivider, NearbyDivider))
                    ),
                ) {
                    Text("Download", color = NearbyTextPrimary)
                }
                Button(
                    onClick = { /* TODO: Share QR */ },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NearbyCyan, contentColor = NearbyBlack),
                ) {
                    Text("Share", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                }
            }
        }
    }
}

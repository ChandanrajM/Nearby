package com.nearby.app.ui.store

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.graphics.Bitmap
import com.nearby.app.ui.theme.NearbyColors
import com.nearby.app.ui.theme.NearbyType
import com.nearby.app.ui.theme.Typography as NearbyTypography
import com.nearby.app.util.QRCodeGenerator

@Composable
fun StoreQRScreen(
    shopId: String,
    onBack: () -> Unit,
) {
    val qrBitmap = QRCodeGenerator.generate("https://nearby.app/s/$shopId")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NearbyColors.Background),
        horizontalAlignment = Alignment.CenterHorizontally,
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
                text = "Store QR Code",
                style = NearbyType.HeroProductName.copy(fontSize = 20.sp),
                color = NearbyColors.TextPrimary,
            )
        }

        Spacer(Modifier.height(40.dp))

        // ── QR Container ──────────────────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = NearbyColors.Surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Scan to Shop",
                    style = NearbyType.HeroProductName.copy(fontSize = 24.sp),
                    color = NearbyColors.TextPrimary
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Customers can scan this to open your store instantly",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NearbyColors.TextTertiary,
                    textAlign = TextAlign.Center
                )
                
                Spacer(Modifier.height(32.dp))

                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (qrBitmap != null) {
                        Image(
                            bitmap = qrBitmap.asImageBitmap(),
                            contentDescription = "Shop QR Code",
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        CircularProgressIndicator(color = NearbyColors.PriceYellow)
                    }
                }
                
                Spacer(Modifier.height(32.dp))
                
                Text(
                    text = "ID: $shopId",
                    style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.sp),
                    color = NearbyColors.TextTertiary
                )
            }
        }

        Spacer(Modifier.weight(1f))

        // ── Actions ──────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = { /* TODO */ },
                modifier = Modifier.weight(1f).height(56.dp),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, NearbyColors.Surface),
            ) {
                Icon(Icons.Default.Download, null, tint = NearbyColors.TextPrimary)
                Spacer(Modifier.width(8.dp))
                Text("Save", color = NearbyColors.TextPrimary)
            }
            
            Button(
                onClick = { /* TODO */ },
                modifier = Modifier.weight(1f).height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NearbyColors.PriceYellow,
                    contentColor = NearbyColors.Background
                )
            ) {
                Icon(Icons.Default.Share, null)
                Spacer(Modifier.width(8.dp))
                // Use static Typography reference
                Text(
                    text = "Share", 
                    style = NearbyTypography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
        
        Spacer(Modifier.height(40.dp))
    }
}

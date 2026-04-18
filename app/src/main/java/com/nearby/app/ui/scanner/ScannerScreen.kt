package com.nearby.app.ui.scanner

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.nearby.app.ui.theme.*

@OptIn(ExperimentalGetImage::class)
@Composable
fun ScannerScreen(
    onScanResult: (String) -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasCamPermission by remember { mutableStateOf(false) }
    var scanned by remember { mutableStateOf(false) }

    val permLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasCamPermission = granted }

    LaunchedEffect(Unit) {
        val ok = ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        if (ok) hasCamPermission = true
        else permLauncher.launch(Manifest.permission.CAMERA)
    }

    // Scan-line animation (top to bottom)
    val infiniteTransition = rememberInfiniteTransition(label = "scanLine")
    val scanLineProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "scanLineY",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NearbyBlack),
    ) {
        if (hasCamPermission) {
            // ── Camera Preview ──────────────────────────────────────────
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        val provider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.surfaceProvider = previewView.surfaceProvider
                        }
                        val analyzer = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                            .also { analysis ->
                                analysis.setAnalyzer(ContextCompat.getMainExecutor(ctx)) { imageProxy ->
                                    val mediaImage = imageProxy.image
                                    if (mediaImage != null && !scanned) {
                                        val inputImage = InputImage.fromMediaImage(
                                            mediaImage, imageProxy.imageInfo.rotationDegrees
                                        )
                                        val scanner = BarcodeScanning.getClient()
                                        scanner.process(inputImage)
                                            .addOnSuccessListener { barcodes ->
                                                for (barcode in barcodes) {
                                                    val value = barcode.rawValue ?: continue
                                                    // Extract shop ID from QR value
                                                    val shopId = extractShopId(value)
                                                    if (shopId != null && !scanned) {
                                                        scanned = true
                                                        onScanResult(shopId)
                                                    }
                                                }
                                            }
                                            .addOnCompleteListener {
                                                imageProxy.close()
                                            }
                                    } else {
                                        imageProxy.close()
                                    }
                                }
                            }
                        try {
                            provider.unbindAll()
                            provider.bindToLifecycle(
                                lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA,
                                preview, analyzer
                            )
                        } catch (e: Exception) {
                            Log.e("ScannerScreen", "Camera bind failed", e)
                        }
                    }, ContextCompat.getMainExecutor(ctx))
                    previewView
                },
                modifier = Modifier.fillMaxSize(),
            )

            // ── Overlay with cut-out ────────────────────────────────────
            Canvas(modifier = Modifier.fillMaxSize()) {
                val scanAreaSize = size.width * 0.65f
                val left = (size.width - scanAreaSize) / 2
                val top = (size.height - scanAreaSize) / 2

                // Dark overlay
                drawRect(color = Color.Black.copy(alpha = 0.6f))

                // Clear the scan area
                drawRoundRect(
                    color = Color.Transparent,
                    topLeft = Offset(left, top),
                    size = Size(scanAreaSize, scanAreaSize),
                    cornerRadius = CornerRadius(20f),
                    blendMode = BlendMode.Clear,
                )

                // Scan frame border
                drawRoundRect(
                    color = NearbyCyan,
                    topLeft = Offset(left, top),
                    size = Size(scanAreaSize, scanAreaSize),
                    cornerRadius = CornerRadius(20f),
                    style = Stroke(width = 3.dp.toPx()),
                )

                // Animated scan line
                val lineY = top + scanAreaSize * scanLineProgress
                drawLine(
                    color = NearbyCyan.copy(alpha = 0.8f),
                    start = Offset(left + 16, lineY),
                    end = Offset(left + scanAreaSize - 16, lineY),
                    strokeWidth = 2.dp.toPx(),
                )
            }
        } else {
            // Permission not granted
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Camera permission is required to scan QR codes", color = NearbyTextSecondary)
            }
        }

        // ── Top bar ─────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(42.dp)
                    .background(NearbyOverlay, CircleShape),
            ) {
                Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
            }
            Spacer(Modifier.width(16.dp))
            Text(
                text = "Scan QR Code",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
            )
        }

        // ── Bottom hint ─────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp)
                .background(NearbyOverlay, RoundedCornerShape(12.dp))
                .padding(horizontal = 24.dp, vertical = 12.dp),
        ) {
            Text(
                text = "Point your camera at a shop's QR code",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
            )
        }
    }
}

private fun extractShopId(qrValue: String): String? {
    // Expected format: https://nearby.app/shop/{shopId}  or just the shopId
    return when {
        qrValue.contains("/shop/") -> qrValue.substringAfterLast("/shop/").trim()
        qrValue.startsWith("shop-") -> qrValue.trim()
        qrValue.length in 4..100 -> qrValue.trim()  // Accept raw IDs
        else -> null
    }
}

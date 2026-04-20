package com.nearby.app.ui.scanner

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.nearby.app.ui.theme.NearbyColors
import com.nearby.app.ui.theme.NearbyType

@Composable
fun ScannerScreen(
    onShopScanned: (String) -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { hasCameraPermission = it }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    val scanLineProgress by rememberInfiniteTransition(label = "").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (hasCameraPermission) {
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
                                    @androidx.camera.core.ExperimentalGetImage
                                    val mediaImage = imageProxy.image
                                    if (mediaImage != null) {
                                        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                                        BarcodeScanning.getClient().process(image)
                                            .addOnSuccessListener { barcodes ->
                                                for (barcode in barcodes) {
                                                    val value = barcode.rawValue ?: continue
                                                    val shopId = extractShopId(value)
                                                    if (shopId != null) {
                                                        onShopScanned(shopId)
                                                    }
                                                }
                                            }
                                            .addOnCompleteListener { imageProxy.close() }
                                    } else {
                                        imageProxy.close()
                                    }
                                }
                            }

                        try {
                            provider.unbindAll()
                            provider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_BACK_CAMERA,
                                preview,
                                analyzer
                            )
                        } catch (e: Exception) {
                            Log.e("Scanner", "Bind failed", e)
                        }
                    }, ContextCompat.getMainExecutor(ctx))
                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )

            // ── Overlay ──────────────────────────────────────────────
            Canvas(modifier = Modifier.fillMaxSize()) {
                val scanAreaSize = size.width * 0.7f
                val left = (size.width - scanAreaSize) / 2
                val top = (size.height - scanAreaSize) / 2

                // Shadow overlay
                drawRect(color = Color.Black.copy(alpha = 0.7f))

                // Cut-out
                drawRoundRect(
                    color = Color.Transparent,
                    topLeft = Offset(left, top),
                    size = Size(scanAreaSize, scanAreaSize),
                    cornerRadius = CornerRadius(24.dp.toPx()),
                    blendMode = BlendMode.Clear
                )

                // Frame
                drawRoundRect(
                    color = NearbyColors.PriceYellow,
                    topLeft = Offset(left, top),
                    size = Size(scanAreaSize, scanAreaSize),
                    cornerRadius = CornerRadius(24.dp.toPx()),
                    style = Stroke(width = 2.dp.toPx())
                )

                // Scan line
                val lineY = top + (scanAreaSize * scanLineProgress)
                drawLine(
                    color = NearbyColors.PriceYellow.copy(alpha = 0.6f),
                    start = Offset(left + 20.dp.toPx(), lineY),
                    end = Offset(left + scanAreaSize - 20.dp.toPx(), lineY),
                    strokeWidth = 2.dp.toPx()
                )
            }
        }

        // ── Controls ───────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Spacer(Modifier.width(16.dp))
                Text(
                    "Scan Shop QR",
                    style = NearbyType.HeroProductName.copy(fontSize = 20.sp),
                    color = Color.White
                )
            }

            Spacer(Modifier.weight(1f))

            Surface(
                modifier = Modifier.padding(bottom = 80.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color.Black.copy(alpha = 0.6f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.QrCodeScanner, null, tint = NearbyColors.PriceYellow)
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "Align the QR code within the frame",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }
            }
        }
    }
}

private fun extractShopId(qrValue: String): String? {
    return when {
        qrValue.contains("/shop/") -> qrValue.substringAfterLast("/shop/").trim()
        qrValue.startsWith("shop-") -> qrValue.trim()
        qrValue.length in 4..50 -> qrValue.trim()
        else -> null
    }
}

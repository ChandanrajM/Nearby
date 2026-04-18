package com.nearby.app.ui.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nearby.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    // Auto-navigate after 2.5 seconds
    LaunchedEffect(Unit) {
        delay(2500L)
        onFinished()
    }

    // Glow pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "splash")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "glow",
    )

    // Dot animation
    val dotOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Restart,
        ),
        label = "dots",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NearbyBlack),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // ── Logo: Pin shape with "N" inside ────────────────────────
            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.Center,
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val cx = w / 2
                    val cy = h * 0.38f
                    val r = w * 0.32f

                    // Outer glow
                    drawCircle(
                        color = NearbyCyan.copy(alpha = glowAlpha),
                        radius = r * 1.8f,
                        center = Offset(cx, cy),
                    )

                    // Pin body (teardrop path)
                    val pinPath = Path().apply {
                        // Top arc (the round part)
                        addArc(
                            oval = androidx.compose.ui.geometry.Rect(
                                left = cx - r,
                                top = cy - r,
                                right = cx + r,
                                bottom = cy + r,
                            ),
                            startAngleDegrees = -210f,
                            sweepAngleDegrees = 240f,
                        )
                        // Bottom point
                        lineTo(cx, h * 0.82f)
                        close()
                    }
                    drawPath(
                        path = pinPath,
                        color = NearbyCyan,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round),
                    )

                    // Shopping bag icon inside the pin
                    val bagW = r * 0.7f
                    val bagH = r * 0.65f
                    val bagLeft = cx - bagW / 2
                    val bagTop = cy - bagH / 2 + r * 0.05f

                    // Bag body
                    drawRoundRect(
                        color = NearbyCyan,
                        topLeft = Offset(bagLeft, bagTop),
                        size = Size(bagW, bagH),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx()),
                        style = Stroke(width = 2.dp.toPx()),
                    )

                    // Bag handle
                    val handlePath = Path().apply {
                        val handleW = bagW * 0.45f
                        moveTo(cx - handleW / 2, bagTop)
                        cubicTo(
                            cx - handleW / 2, bagTop - bagH * 0.45f,
                            cx + handleW / 2, bagTop - bagH * 0.45f,
                            cx + handleW / 2, bagTop,
                        )
                    }
                    drawPath(
                        path = handlePath,
                        color = NearbyCyan,
                        style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round),
                    )
                }

                // "N" letter centered
                Text(
                    text = "N",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                    ),
                    color = NearbyCyan,
                    modifier = Modifier.offset(y = (-6).dp),
                )
            }

            Spacer(Modifier.height(28.dp))

            // ── App name ───────────────────────────────────────────────
            Text(
                text = "N E A R B Y",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 8.sp,
                    fontSize = 28.sp,
                ),
                color = NearbyTextPrimary,
            )

            Spacer(Modifier.height(12.dp))

            // ── Tagline ────────────────────────────────────────────────
            Text(
                text = "Scan.  Discover.  Shop.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    letterSpacing = 2.sp,
                ),
                color = NearbyTextSecondary,
            )

            Spacer(Modifier.height(80.dp))

            // ── Animated loading dots ──────────────────────────────────
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(3) { index ->
                    val active = dotOffset.toInt() == index
                    Canvas(modifier = Modifier.size(8.dp)) {
                        drawCircle(
                            color = if (active) NearbyCyan else NearbyCyan.copy(alpha = 0.3f),
                            radius = size.minDimension / 2,
                        )
                    }
                }
            }
        }
    }
}

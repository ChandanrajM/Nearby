package com.nearby.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ─────────────────────────────────────────────────────────────────
// Nearby color palette — all hardcoded for the dark aesthetic
// These match the screenshots exactly
// ─────────────────────────────────────────────────────────────────

object NearbyColors {
    val Background       = Color(0xFF121212)   // Main screen background
    val Surface          = Color(0xFF1E1E1E)   // Card backgrounds
    val SurfaceVariant   = Color(0xFF2A2A2A)   // Chip backgrounds, dividers
    val SurfaceElevated  = Color(0xFF242424)   // Slightly lifted surfaces

    val PriceYellow      = Color(0xFFFFD60A)   // Price tag yellow — the signature color
    val PriceYellowDim   = Color(0xFF4A3D00)   // Yellow chip background (dark)

    val LiveRed          = Color(0xFFFF3B30)   // "LIVE DROP" badge
    val LiveRedDim       = Color(0xFF4A0F0C)   // "LIVE DROP" badge background

    val TextPrimary      = Color(0xFFFFFFFF)   // Main text
    val TextSecondary    = Color(0xFFAAAAAA)   // Distance, subtitles
    val TextTertiary     = Color(0xFF666666)   // Hints, placeholders

    val ChipSelected     = Color(0xFFFFFFFF)   // Selected filter chip text
    val ChipUnselected   = Color(0xFF888888)   // Unselected filter chip text
    val ChipSelectedBg   = Color(0xFF2A2A2A)   // Selected chip background (outlined)

    val OnlineDot        = Color(0xFF34C759)   // Green "shop is online" dot
    val OfflineDot       = Color(0xFF666666)   // Grey "shop is offline" dot

    val Shimmer1         = Color(0xFF1E1E1E)   // Loading skeleton base
    val Shimmer2         = Color(0xFF2C2C2C)   // Loading skeleton highlight
}

// ─────────────────────────────────────────────────────────────────
// Text styles
// ─────────────────────────────────────────────────────────────────

object NearbyType {
    val ShopName = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        color = NearbyColors.TextPrimary,
        letterSpacing = (-0.3).sp
    )
    val Distance = TextStyle(
        fontSize = 13.sp,
        fontWeight = FontWeight.Normal,
        color = NearbyColors.TextSecondary
    )
    val HeroProductName = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        color = NearbyColors.TextPrimary,
        letterSpacing = (-0.2).sp
    )
    val HeroPrice = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = NearbyColors.PriceYellow
    )
    val GridProductName = TextStyle(
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        color = NearbyColors.TextPrimary,
        letterSpacing = 0.sp
    )
    val GridPrice = TextStyle(
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = NearbyColors.PriceYellow
    )
    val ChipLabel = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.3.sp
    )
    val LiveBadge = TextStyle(
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = NearbyColors.LiveRed,
        letterSpacing = 0.8.sp
    )
}

// ─────────────────────────────────────────────────────────────────
// MaterialTheme wrapper — apply this at your app root (MainActivity)
// ─────────────────────────────────────────────────────────────────

private val NearbyDarkColorScheme = darkColorScheme(
    background = NearbyColors.Background,
    surface = NearbyColors.Surface,
    primary = NearbyColors.PriceYellow,
    onBackground = NearbyColors.TextPrimary,
    onSurface = NearbyColors.TextPrimary,
)

@Composable
fun NearbyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = NearbyDarkColorScheme,
        content = content
    )
}
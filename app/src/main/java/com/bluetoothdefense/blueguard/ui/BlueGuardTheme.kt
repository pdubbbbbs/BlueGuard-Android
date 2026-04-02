package com.bluetoothdefense.blueguard.ui

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val BluePrimary = Color(0xFF0078FF)
val BlueLight = Color(0xFF4DA6FF)
val DarkBg = Color(0xFF0A1628)
val DarkSurface = Color(0xFF111B2E)
val DarkCard = Color(0xFF1A2740)
val GreenSafe = Color(0xFF00FF88)
val YellowWarn = Color(0xFFFFAA00)
val OrangeSuspicious = Color(0xFFFF6600)
val RedDanger = Color(0xFFFF3366)

private val DarkColorScheme = darkColorScheme(
  primary = BluePrimary,
  onPrimary = Color.White,
  secondary = BlueLight,
  background = DarkBg,
  surface = DarkSurface,
  surfaceVariant = DarkCard,
  onBackground = Color(0xFFE0E0E0),
  onSurface = Color(0xFFE0E0E0),
  onSurfaceVariant = Color(0xFF9CA3AF),
)

@Composable
fun BlueGuardTheme(content: @Composable () -> Unit) {
  MaterialTheme(
    colorScheme = DarkColorScheme,
    content = content
  )
}

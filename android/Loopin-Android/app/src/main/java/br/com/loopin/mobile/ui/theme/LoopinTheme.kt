package br.com.loopin.mobile.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val LoopinForest = Color(0xFF245B45)
val LoopinForestDark = Color(0xFF183F30)
val LoopinLeaf = Color(0xFFA8C66C)
val LoopinMint = Color(0xFFE4EDDA)
val LoopinCream = Color(0xFFF5F7EF)
val LoopinInk = Color(0xFF24352C)
val LoopinMuted = Color(0xFF6E7B71)
val LoopinBorder = Color(0xFFE2E7DD)
val LoopinWhite = Color(0xFFFFFFFF)
val LoopinError = Color(0xFFB53C3C)
val LoopinAmber = Color(0xFF9A650C)

private val LoopinColorScheme = lightColorScheme(
    primary = LoopinForest,
    onPrimary = Color.White,
    primaryContainer = LoopinMint,
    onPrimaryContainer = LoopinForestDark,
    secondary = LoopinLeaf,
    onSecondary = LoopinForestDark,
    background = LoopinCream,
    onBackground = LoopinInk,
    surface = LoopinWhite,
    onSurface = LoopinInk,
    surfaceVariant = Color(0xFFEEF1E8),
    onSurfaceVariant = LoopinMuted,
    outline = LoopinBorder,
    error = LoopinError,
    onError = Color.White
)

@Composable
fun LoopinTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LoopinColorScheme,
        content = content
    )
}

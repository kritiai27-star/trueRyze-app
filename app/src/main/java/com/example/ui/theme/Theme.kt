package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val TrueRyzeDarkColorScheme = darkColorScheme(
    primary = CobaltPrimary,
    onPrimary = InkPrimary,
    primaryContainer = CobaltContainer,
    onPrimaryContainer = CobaltIce,
    secondary = NeonMint,
    onSecondary = GraphiteBg,
    secondaryContainer = MintContainer,
    onSecondaryContainer = TrustGreen,
    tertiary = ChampagneGold,
    onTertiary = GraphiteBg,
    background = GraphiteBg,
    onBackground = InkPrimary,
    surface = GraphiteSurface,
    onSurface = InkPrimary,
    surfaceVariant = GraphiteCard,
    onSurfaceVariant = InkSilver,
    surfaceContainerLow = GraphiteSurface,
    surfaceContainer = GraphiteCard,
    surfaceContainerHigh = GraphiteElevated,
    outline = SilverBorder,
    outlineVariant = GlassBorder,
    error = StatusError,
    onError = GraphiteBg,
    errorContainer = ErrorContainer,
    onErrorContainer = StatusError
)

@Composable
fun TrueRyzeTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = TrueRyzeDarkColorScheme,
        typography = TrueRyzeTypography,
        shapes = TrueRyzeShapes,
        content = content
    )
}

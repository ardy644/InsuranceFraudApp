package com.example.insurancefraudapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val VigilanceLightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    inversePrimary = InversePrimary,
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    tertiary = Tertiary,
    onTertiary = OnTertiary,
    tertiaryContainer = TertiaryContainer,
    onTertiaryContainer = OnTertiaryContainer,
    background = Background,
    onBackground = OnSurface,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    surfaceTint = SurfaceTint,
    inverseSurface = InverseSurface,
    inverseOnSurface = InverseOnSurface,
    error = Error,
    onError = OnError,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer,
    outline = Outline,
    outlineVariant = OutlineVariant,
    surfaceBright = SurfaceBright,
    surfaceContainer = SurfaceContainer,
    surfaceContainerHigh = SurfaceContainerHigh,
    surfaceContainerHighest = SurfaceContainerHighest,
    surfaceContainerLow = SurfaceContainerLow,
    surfaceContainerLowest = SurfaceContainerLowest,
    surfaceDim = SurfaceDim
)

private val VigilanceDarkColorScheme = darkColorScheme(
    primary = PrimaryContainer, // Invert somewhat for dark mode demo
    onPrimary = OnPrimaryContainer,
    primaryContainer = Primary,
    onPrimaryContainer = OnPrimary,
    background = SurfaceDim,
    onBackground = SurfaceBright,
    surface = SurfaceContainerLowest,
    onSurface = SurfaceBright,
    surfaceVariant = SurfaceContainerHigh,
    onSurfaceVariant = InverseOnSurface,
    error = ErrorContainer,
    onError = OnErrorContainer
)

@Composable
fun InsuranceFraudAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        VigilanceDarkColorScheme
    } else {
        VigilanceLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = VigilanceTypography,
        content = content
    )
}
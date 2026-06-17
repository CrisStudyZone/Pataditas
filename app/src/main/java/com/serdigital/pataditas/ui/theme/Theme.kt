package com.serdigital.pataditas.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ─── Light Color Scheme ──────────────────────────────────────────────────────

private val LightColorScheme = lightColorScheme(
    primary = CieloProfundo,
    onPrimary = Color.White,
    primaryContainer = CieloSuave,
    onPrimaryContainer = TextoPrincipal,
    secondary = LavandaMedio,
    onSecondary = Color.White,
    secondaryContainer = LavandaSuave,
    onSecondaryContainer = TextoPrincipal,
    tertiary = RosadoMedio,
    onTertiary = Color.White,
    tertiaryContainer = RosadoSuave,
    onTertiaryContainer = TextoPrincipal,
    background = BlancoRoto,
    onBackground = TextoPrincipal,
    surface = SuperficieCard,
    onSurface = TextoPrincipal,
    surfaceVariant = CremaSuave,
    onSurfaceVariant = TextoSecundario,
    outline = GrisClaro,
    outlineVariant = GrisClaro.copy(alpha = 0.5f),
    error = Rojo,
    onError = Color.White,
)

// ─── Tema principal ──────────────────────────────────────────────────────────

@Composable
fun PataditasTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme, // Solo modo claro por ahora
        typography = PataditasTypography,
        content = content
    )
}
/*
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun PataditasTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}*/
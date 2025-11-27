package com.dispensadoragua.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.compose.foundation.shape.RoundedCornerShape

/**
 * Tema minimalista y moderno para la aplicación
 */

// ⭐ Tipografía minimalista y moderna
private val MinimalTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    )
)

// ⭐ Bordes suaves y modernos
private val MinimalShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(26.dp)
)

// Paleta clara minimalista
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0A84FF),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE5F0FF),
    onPrimaryContainer = Color(0xFF00254D),

    secondary = Color(0xFF8392A0),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEDEFF1),
    onSecondaryContainer = Color(0xFF1E242A),

    background = Color(0xFFF9FAFB),
    onBackground = Color(0xFF1C1E21),

    surface = Color.White,
    onSurface = Color(0xFF1C1E21),

    surfaceVariant = Color(0xFFE6E8EB),
    onSurfaceVariant = Color(0xFF44474D),

    error = Color(0xFFB00020),
    onError = Color.White
)

// Paleta oscura minimalista
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4D9EFF),
    onPrimary = Color(0xFF002544),
    primaryContainer = Color(0xFF003964),
    onPrimaryContainer = Color(0xFFCCE6FF),

    secondary = Color(0xFF9BA7B4),
    onSecondary = Color(0xFF1C2329),
    secondaryContainer = Color(0xFF2F3740),
    onSecondaryContainer = Color(0xFFD7DEE3),

    background = Color(0xFF121316),
    onBackground = Color(0xFFE3E5E8),

    surface = Color(0xFF181A1C),
    onSurface = Color(0xFFE3E5E8),

    surfaceVariant = Color(0xFF2B3136),
    onSurfaceVariant = Color(0xFFBDC3C8),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

@Composable
fun DispensadorAguaAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Cambiar color de la barra de estado acorde al tema
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MinimalTypography,
        shapes = MinimalShapes,
        content = content
    )
}

// Theme.kt
package com.example.maghalam.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.core.view.WindowCompat
import com.example.maghalam.R

// تعریف فونت‌ها
val VazirFontFamily = FontFamily(
    Font(R.font.vazir, FontWeight.Normal),
    Font(R.font.vazir_bold, FontWeight.Bold),
    Font(R.font.vazir_medium, FontWeight.Medium)
)

val BNazaninFontFamily = FontFamily(
    Font(R.font.b_nazanin, FontWeight.Normal),
    Font(R.font.b_nazanin_bold, FontWeight.Bold)
)

val RobotoFontFamily = FontFamily(
    Font(R.font.roboto, FontWeight.Normal),
    Font(R.font.roboto, FontWeight.Bold),
    Font(R.font.roboto_italic, FontWeight.Medium)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF8FC7E8),
    onPrimary = Color(0xFF073247),
    primaryContainer = Color(0xFF164963),
    onPrimaryContainer = Color(0xFFD8F0FF),
    secondary = Color(0xFFA7D6C8),
    onSecondary = Color(0xFF10382F),
    secondaryContainer = Color(0xFF254E45),
    onSecondaryContainer = Color(0xFFD6F4EB),
    tertiary = Color(0xFFE5C07B),
    onTertiary = Color(0xFF3D2A00),
    background = Color(0xFF111417),
    onBackground = Color(0xFFE6EAEE),
    surface = Color(0xFF191D21),
    onSurface = Color(0xFFE6EAEE),
    surfaceVariant = Color(0xFF2B3137),
    onSurfaceVariant = Color(0xFFC4CDD5),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    outline = Color(0xFF8C969F)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF246A8D),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD3ECF8),
    onPrimaryContainer = Color(0xFF062D40),
    secondary = Color(0xFF4F776B),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD4EDE5),
    onSecondaryContainer = Color(0xFF0B3129),
    tertiary = Color(0xFF8A6A21),
    onTertiary = Color.White,
    background = Color(0xFFF7FAFC),
    onBackground = Color(0xFF1A1D20),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1A1D20),
    surfaceVariant = Color(0xFFE7EEF3),
    onSurfaceVariant = Color(0xFF4E5963),
    outline = Color(0xFF7A858E)
)

@Composable
fun MaghalamTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    selectedFont: String = "Vazir",
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

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    // انتخاب فونت بر اساس تنظیمات
    val fontFamily = when (selectedFont) {
        "Vazir" -> VazirFontFamily
        "B Nazanin" -> BNazaninFontFamily
        "Roboto" -> RobotoFontFamily
        else -> VazirFontFamily
    }

    val typography = Typography.copy(
        displayLarge = Typography.displayLarge.copy(fontFamily = fontFamily),
        displayMedium = Typography.displayMedium.copy(fontFamily = fontFamily),
        displaySmall = Typography.displaySmall.copy(fontFamily = fontFamily),
        headlineLarge = Typography.headlineLarge.copy(fontFamily = fontFamily),
        headlineMedium = Typography.headlineMedium.copy(fontFamily = fontFamily),
        headlineSmall = Typography.headlineSmall.copy(fontFamily = fontFamily),
        titleLarge = Typography.titleLarge.copy(fontFamily = fontFamily),
        titleMedium = Typography.titleMedium.copy(fontFamily = fontFamily),
        titleSmall = Typography.titleSmall.copy(fontFamily = fontFamily),
        bodyLarge = Typography.bodyLarge.copy(fontFamily = fontFamily),
        bodyMedium = Typography.bodyMedium.copy(fontFamily = fontFamily),
        bodySmall = Typography.bodySmall.copy(fontFamily = fontFamily),
        labelLarge = Typography.labelLarge.copy(fontFamily = fontFamily),
        labelMedium = Typography.labelMedium.copy(fontFamily = fontFamily),
        labelSmall = Typography.labelSmall.copy(fontFamily = fontFamily)
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        shapes = AppShapes,
        content = content
    )
}

package com.ccstudio.mpaz_pro_6.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.ccstudio.mpaz_pro_6.R

/**
 * Familia tipográfica de la app.
 *
 * Geist se incluye como fuente local (pesos 400, 500, 600 y 700) para que se
 * vea igual en todos los dispositivos. Los archivos están en `res/font`.
 */
val GeistFontFamily = FontFamily(
    Font(R.font.geist_regular, FontWeight.Normal),
    Font(R.font.geist_medium, FontWeight.Medium),
    Font(R.font.geist_semibold, FontWeight.SemiBold),
    Font(R.font.geist_bold, FontWeight.Bold),
    Font(R.font.geist_black, FontWeight.Black)
)

private val DefaultTypography = Typography()

private fun TextStyle.withGeist() = copy(fontFamily = GeistFontFamily)

/**
 * Tipografía de Material 3 con Geist en todos los estilos.
 *
 * Se conserva el ajuste original de `bodyLarge` (16/24 sp y tracking 0.5).
 */
val Typography = Typography(
    displayLarge = DefaultTypography.displayLarge.withGeist(),
    displayMedium = DefaultTypography.displayMedium.withGeist(),
    displaySmall = DefaultTypography.displaySmall.withGeist(),
    headlineLarge = DefaultTypography.headlineLarge.withGeist(),
    headlineMedium = DefaultTypography.headlineMedium.withGeist(),
    headlineSmall = DefaultTypography.headlineSmall.withGeist(),
    titleLarge = DefaultTypography.titleLarge.withGeist(),
    titleMedium = DefaultTypography.titleMedium.withGeist(),
    titleSmall = DefaultTypography.titleSmall.withGeist(),
    bodyLarge = DefaultTypography.bodyLarge.copy(
        fontFamily = GeistFontFamily,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = DefaultTypography.bodyMedium.withGeist(),
    bodySmall = DefaultTypography.bodySmall.withGeist(),
    labelLarge = DefaultTypography.labelLarge.withGeist(),
    labelMedium = DefaultTypography.labelMedium.withGeist(),
    labelSmall = DefaultTypography.labelSmall.withGeist()
)

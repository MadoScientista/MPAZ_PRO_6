package com.ccstudio.mpaz_pro_6.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Esquema claro de MPAZ.
 *
 * Lienzo gris (`#F0F0F0`) con superficies y cards blancas: la separación de las
 * cards viene del tono, no de sombras (la app no usa elevación). Los containers
 * de estado usan una escala de azul para que los chips sigan siendo legibles:
 * `primaryContainer` (publicado), `secondaryContainer` (autorizado y selección)
 * y `tertiaryContainer` (en revisión).
 */
private val LightColors = lightColorScheme(
    primary = BlueLight,
    onPrimary = White,
    primaryContainer = BlueLight,
    onPrimaryContainer = White,
    secondary = BlueLight,
    onSecondary = White,
    secondaryContainer = BlueMid,
    onSecondaryContainer = BlueStrong,
    tertiary = BlueLight,
    onTertiary = White,
    tertiaryContainer = BlueSoft,
    onTertiaryContainer = BlueStrong,
    background = CanvasLight,
    onBackground = InkLight,
    surface = White,
    onSurface = InkLight,
    surfaceVariant = NeutralLight,
    onSurfaceVariant = InkMutedLight,
    surfaceTint = Color.Transparent,
    inverseSurface = Black,
    inverseOnSurface = White,
    inversePrimary = BlueLight,
    error = ErrorLight,
    onError = White,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnErrorContainerLight,
    outline = BorderLight,
    outlineVariant = BorderSoftLight,
    scrim = Black,
    surfaceBright = White,
    surfaceDim = CanvasLight,
    surfaceContainerLowest = White,
    surfaceContainerLow = White,
    surfaceContainer = White,
    surfaceContainerHigh = White,
    surfaceContainerHighest = White
)

/**
 * Esquema oscuro de MPAZ.
 *
 * El equivalente de las cards blancas es `#1C1C1E`: `#111111` sobre negro queda
 * prácticamente invisible. La escala de azul de los containers se mantiene con
 * tintes oscuros y texto claro: `primaryContainer` es el azul profundo de
 * publicado, mientras que `primary` sigue siendo el acento brillante de botones,
 * spinner y bordes de selección.
 */
private val DarkColors = darkColorScheme(
    primary = BlueDark,
    onPrimary = Black,
    primaryContainer = BlueContainerDark,
    onPrimaryContainer = BlueOn,
    secondary = BlueDark,
    onSecondary = Black,
    secondaryContainer = BlueStrong,
    onSecondaryContainer = BlueOn,
    tertiary = BlueDark,
    onTertiary = Black,
    tertiaryContainer = BlueSoftDark,
    onTertiaryContainer = BlueOn,
    background = CanvasDark,
    onBackground = InkDark,
    surface = SurfaceDark,
    onSurface = InkDark,
    surfaceVariant = NeutralDark,
    onSurfaceVariant = InkMutedDark,
    surfaceTint = Color.Transparent,
    inverseSurface = White,
    inverseOnSurface = Black,
    inversePrimary = BlueDark,
    error = ErrorDark,
    onError = Black,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark,
    outline = BorderDark,
    outlineVariant = BorderSoftDark,
    scrim = Black,
    surfaceBright = NeutralDark,
    surfaceDim = CanvasDark,
    surfaceContainerLowest = CanvasDark,
    surfaceContainerLow = SurfaceLowDark,
    surfaceContainer = SurfaceDark,
    surfaceContainerHigh = SurfaceHighDark,
    surfaceContainerHighest = SurfaceDark
)

/**
 * Colores de la barra superior.
 *
 * No son roles de Material: la app los usa para invertir la marca en modo claro
 * (fondo azul y contenido blanco), manteniendo el modo oscuro con una superficie
 * neutra. Viven en un `CompositionLocal` porque `AppTopBar` no recibe el modo
 * como parámetro.
 */
@Immutable
data class TopBarColors(
    val container: Color,
    val content: Color,
    val subtitleContent: Color,
    val avatarContainer: Color,
    val avatarContent: Color
)

/** Colores de la barra superior; el tema los provee según el modo. */
val LocalTopBarColors = staticCompositionLocalOf {
    TopBarColors(
        container = Color.Unspecified,
        content = Color.Unspecified,
        subtitleContent = Color.Unspecified,
        avatarContainer = Color.Unspecified,
        avatarContent = Color.Unspecified
    )
}

/**
 * Tema de MPAZ.
 *
 * No aplica color dinámico: la paleta de `Color.kt` es la única fuente y se ve
 * igual en todos los dispositivos. El modo claro/oscuro lo resuelve
 * `ThemePreferenceStore` desde `MainActivity`.
 */
@Composable
fun MPAZ_PRO_6Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // `enableEdgeToEdge()` fija el color de los iconos de las barras una sola vez.
    // Al alternar el tema en caliente hay que volver a aplicarlo: si no, los iconos
    // quedan oscuros sobre el fondo oscuro y desaparecen.
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography
    ) {
        CompositionLocalProvider(
            LocalTopBarColors provides if (darkTheme) {
                // Modo oscuro: la barra sigue siendo una superficie neutra.
                TopBarColors(
                    container = colorScheme.surface,
                    content = colorScheme.onSurface,
                    subtitleContent = colorScheme.onSurfaceVariant,
                    avatarContainer = colorScheme.primaryContainer,
                    avatarContent = colorScheme.onPrimaryContainer
                )
            } else {
                // Modo claro: la barra se invierte a la marca.
                TopBarColors(
                    container = BlueLight,
                    content = White,
                    subtitleContent = White,
                    avatarContainer = White,
                    avatarContent = BlueLight
                )
            },
            LocalAppColors provides if (darkTheme) DarkAppColors else LightAppColors
        ) {
            content()
        }
    }
}

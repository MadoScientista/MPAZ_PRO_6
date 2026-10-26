package com.ccstudio.mpaz_pro_6.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Par de colores de un estado: contenedor y contenido.
 *
 * No son roles de Material: la app los usa para dar semántica a las etiquetas
 * (publicado, en revisión, completado, dificultad…) sin depender de los cuatro
 * roles de container del esquema.
 */
@Immutable
data class SemanticColor(
    val container: Color,
    val content: Color
)

/** Paleta semántica de estados de la app. */
@Immutable
data class AppColors(
    val success: SemanticColor,
    val warning: SemanticColor,
    val danger: SemanticColor,
    val info: SemanticColor,
    val accent: SemanticColor,
    val neutral: SemanticColor,
    val subjectAccents: List<Color>
)

/** Paleta semántica del modo claro. */
val LightAppColors = AppColors(
    success = SemanticColor(
        container = SuccessContainerLight,
        content = OnSuccessContainerLight
    ),
    warning = SemanticColor(
        container = WarningContainerLight,
        content = OnWarningContainerLight
    ),
    danger = SemanticColor(
        container = ErrorContainerLight,
        content = OnErrorContainerLight
    ),
    info = SemanticColor(
        container = BlueMid,
        content = BlueStrong
    ),
    accent = SemanticColor(
        container = AccentContainerLight,
        content = OnAccentContainerLight
    ),
    neutral = SemanticColor(
        container = NeutralLight,
        content = InkMutedLight
    ),
    subjectAccents = listOf(
        SubjectRedLight,
        SubjectBlueLight,
        SubjectGreenLight,
        SubjectAmberLight,
        SubjectPurpleLight,
        SubjectPinkLight,
        SubjectTealLight,
        SubjectIndigoLight
    )
)

/** Paleta semántica del modo oscuro. */
val DarkAppColors = AppColors(
    success = SemanticColor(
        container = SuccessContainerDark,
        content = OnSuccessContainerDark
    ),
    warning = SemanticColor(
        container = WarningContainerDark,
        content = OnWarningContainerDark
    ),
    danger = SemanticColor(
        container = ErrorContainerDark,
        content = OnErrorContainerDark
    ),
    info = SemanticColor(
        container = BlueStrong,
        content = BlueOn
    ),
    accent = SemanticColor(
        container = AccentContainerDark,
        content = OnAccentContainerDark
    ),
    neutral = SemanticColor(
        container = NeutralDark,
        content = InkMutedDark
    ),
    subjectAccents = listOf(
        SubjectRedDark,
        SubjectBlueDark,
        SubjectGreenDark,
        SubjectAmberDark,
        SubjectPurpleDark,
        SubjectPinkDark,
        SubjectTealDark,
        SubjectIndigoDark
    )
)

/**
 * Paleta semántica vigente.
 *
 * `MPAZ_PRO_6Theme` la provee según el modo. El valor por defecto es la paleta
 * clara, para que los previews y pruebas fuera del tema no queden sin colores.
 */
val LocalAppColors = staticCompositionLocalOf { LightAppColors }

package com.ccstudio.mpaz_pro_6.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Paleta de marca de MPAZ.
 *
 * Es la única fuente de colores de la app: las pantallas y los componentes solo
 * consumen `MaterialTheme.colorScheme` o la paleta semántica de `AppColors`. El
 * esquema se define en `Theme.kt` y no usa color dinámico, para que la marca se
 * vea igual en todos los dispositivos.
 *
 * Identificadores en inglés (regla del repositorio); comentarios en español.
 */

// Neutros del modo claro.
val Black = Color(0xFF000000)
val White = Color(0xFFFFFFFF)
val CanvasLight = Color(0xFFF0F0F0)
val InkLight = Color(0xFF0B0B0C)
val InkMutedLight = Color(0xFF4D4D4F)
val NeutralLight = Color(0xFFE9E9EB)
val BorderLight = Color(0xFF6B6B70)
val BorderSoftLight = Color(0xFFC7C7CC)

// Neutros del modo oscuro.
val CanvasDark = Color(0xFF000000)
val SurfaceDark = Color(0xFF1C1C1E)
val SurfaceLowDark = Color(0xFF141416)
val SurfaceHighDark = Color(0xFF242426)
val NeutralDark = Color(0xFF2C2C2E)
val InkDark = Color(0xFFF2F2F2)
val InkMutedDark = Color(0xFFA1A1A6)
val BorderDark = Color(0xFF8E8E93)
val BorderSoftDark = Color(0xFF3A3A3C)

// Azul de marca: el claro es más oscuro para cumplir contraste como texto.
val BlueLight = Color(0xFF0062CC)
val BlueDark = Color(0xFF0A84FF)
val BlueContainerDark = Color(0xFF0A4E9E)
val BlueStrong = Color(0xFF003A66)
val BlueMid = Color(0xFFB3D7FF)
val BlueSoft = Color(0xFFD6E9FF)
val BlueOn = Color(0xFFD6E9FF)
val BlueSoftDark = Color(0xFF0B2A4A)

// Error: tonos propios para no mezclar con el rojo del baseline de Material.
val ErrorLight = Color(0xFFB3261E)
val ErrorContainerLight = Color(0xFFFFDAD6)
val OnErrorContainerLight = Color(0xFF410E0B)
val ErrorDark = Color(0xFFFF8A80)
val ErrorContainerDark = Color(0xFF5C0F0B)
val OnErrorContainerDark = Color(0xFFFFDAD6)

// Estados semánticos del modo claro (contenedor pastel + texto oscuro).
val SuccessContainerLight = Color(0xFFD7F3E3)
val OnSuccessContainerLight = Color(0xFF0B3D2E)
val WarningContainerLight = Color(0xFFFFF1CC)
val OnWarningContainerLight = Color(0xFF5A4300)
val AccentContainerLight = Color(0xFFEADDFF)
val OnAccentContainerLight = Color(0xFF21005D)

// Estados semánticos del modo oscuro (contenedor profundo + texto claro).
val SuccessContainerDark = Color(0xFF12432F)
val OnSuccessContainerDark = Color(0xFFB8F0D1)
val WarningContainerDark = Color(0xFF4A3A00)
val OnWarningContainerDark = Color(0xFFFFE09B)
val AccentContainerDark = Color(0xFF3A2A5C)
val OnAccentContainerDark = Color(0xFFEADDFF)

// Colores representativos de las materias (solo para el punto del nombre).
// El orden es el de `subjectColorIndex`: rojo, azul, verde, ámbar, violeta,
// rosa, teal e índigo.
val SubjectRedLight = Color(0xFFC62828)
val SubjectBlueLight = BlueLight
val SubjectGreenLight = Color(0xFF2E7D32)
val SubjectAmberLight = Color(0xFFB26A00)
val SubjectPurpleLight = Color(0xFF6A3FB5)
val SubjectPinkLight = Color(0xFFAD1457)
val SubjectTealLight = Color(0xFF00695C)
val SubjectIndigoLight = Color(0xFF3949AB)

val SubjectRedDark = Color(0xFFFF8A80)
val SubjectBlueDark = BlueDark
val SubjectGreenDark = Color(0xFF81C784)
val SubjectAmberDark = Color(0xFFFFD54F)
val SubjectPurpleDark = Color(0xFFB39DDB)
val SubjectPinkDark = Color(0xFFF48FB1)
val SubjectTealDark = Color(0xFF4DB6AC)
val SubjectIndigoDark = Color(0xFF9FA8DA)

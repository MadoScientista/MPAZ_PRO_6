package com.ccstudio.mpaz_pro_6.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import java.text.Normalizer

/**
 * Color representativo de una materia.
 *
 * El backend no guarda colores: el tono se deriva del nombre. Las materias
 * habituales tienen un color fijo por palabra clave y el resto recibe uno
 * estable por hash, así ninguna queda sin color.
 */

/** Cantidad de colores disponibles en [AppColors.subjectAccents]. */
private const val SUBJECT_COLOR_COUNT = 8

/** Índice reservado para "sin materia": el componente no dibuja el punto. */
private const val NO_SUBJECT = -1

private val ACCENT_MARKS = Regex("\\p{Mn}+")

/**
 * Palabras clave por color, en orden de prioridad.
 *
 * El orden importa: "Educación Física" debe resolverse antes que "física" y
 * "Ciencias Sociales" antes que "ciencias".
 */
private val SUBJECT_KEYWORDS: List<Pair<Set<String>, Int>> = listOf(
    setOf("educacion fisica", "ed fisica", "deporte", "deportes") to 6,
    setOf("historia", "geografia", "sociales", "ciudadania") to 3,
    setOf("ciencias", "naturales", "biologia", "fisica", "quimica") to 2,
    setOf("lengua", "lenguaje", "literatura", "comunicacion") to 0,
    setOf("matematica", "matematicas", "algebra", "geometria") to 1,
    setOf("ingles", "frances", "idioma", "idiomas") to 4,
    setOf("arte", "artes", "musica", "plastica", "visuales") to 5,
    setOf("tecnologia", "computacion", "informatica", "programacion") to 7
)

/**
 * Índice del color de la materia, o [NO_SUBJECT] si no corresponde pintarla.
 *
 * Es una función pura para poder probarla en JVM sin Android.
 */
internal fun subjectColorIndex(subjectName: String): Int {
    val normalized = normalize(subjectName)
    if (normalized.isEmpty()) return NO_SUBJECT
    if (normalized.startsWith("sin asignatura")) return NO_SUBJECT
    if (normalized.startsWith("unidad a revisar")) return NO_SUBJECT

    SUBJECT_KEYWORDS.forEach { (keywords, index) ->
        if (keywords.any { normalized.contains(it) }) return index
    }

    return (normalized.hashCode() and Int.MAX_VALUE) % SUBJECT_COLOR_COUNT
}

/** Color del punto de la materia, o `null` si no hay materia que representar. */
@Composable
fun subjectAccent(subjectName: String): Color? {
    val index = subjectColorIndex(subjectName)
    return if (index == NO_SUBJECT) null else LocalAppColors.current.subjectAccents[index]
}

/** Minúsculas y sin tildes, para comparar nombres escritos de cualquier forma. */
private fun normalize(value: String): String =
    Normalizer.normalize(value.lowercase(), Normalizer.Form.NFD)
        .replace(ACCENT_MARKS, "")
        .trim()

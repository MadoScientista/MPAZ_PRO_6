package com.ccstudio.mpaz_pro_6.ui.theme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pruebas del color representativo de las materias.
 *
 * El backend no guarda colores: el índice se deriva del nombre, así que debe ser
 * insensible a mayúsculas y tildes, estable entre llamadas y cubrir las materias
 * del seed.
 */
class SubjectColorsTest {

    @Test
    fun `las materias del seed tienen su color`() {
        assertEquals(0, subjectColorIndex("Lengua y Comunicacion"))
        assertEquals(1, subjectColorIndex("Matematica"))
        assertEquals(2, subjectColorIndex("Ciencias Naturales"))
        assertEquals(
            3,
            subjectColorIndex("Historia, Geografia y Ciencias Sociales")
        )
    }

    @Test
    fun `ignora mayusculas y tildes`() {
        assertEquals(0, subjectColorIndex("LENGUA Y COMUNICACIÓN"))
        assertEquals(1, subjectColorIndex("Matemática"))
        assertEquals(4, subjectColorIndex("Inglés"))
        assertEquals(6, subjectColorIndex("Educación Física"))
    }

    @Test
    fun `las materias fuera del mapa tienen un color estable`() {
        val first = subjectColorIndex("Robótica")
        val second = subjectColorIndex("Robotica")

        assertEquals(first, second)
        assertTrue(first in 0..7)
    }

    @Test
    fun `sin materia no se pinta`() {
        assertEquals(-1, subjectColorIndex("Sin asignatura"))
        assertEquals(-1, subjectColorIndex("Unidad a revisar"))
        assertEquals(-1, subjectColorIndex("  "))
    }
}

package com.ccstudio.mpaz_pro_6.data.enums

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

/**
 * Pruebas de las etiquetas visibles de los enums.
 *
 * El `wireValue` sigue siendo el contrato con el backend y con Room; `label` es
 * solo para la UI. Estas pruebas evitan que se vuelva a usar el valor wire para
 * mostrar texto al usuario.
 */
class EnumLabelsTest {

    @Test
    fun `las etiquetas visibles usan formato de presentacion`() {
        assertEquals("Borrador", ContentStatus.DRAFT.label)
        assertEquals("En revisión", ContentStatus.IN_REVIEW.label)
        assertEquals("Actividad", ActivityType.ACTIVITY.label)
        assertEquals("Desafío", ActivityType.CHALLENGE.label)
        assertEquals("Prueba final", ActivityType.FINAL_TEST.label)
        assertEquals("Público", ContentOrigin.PUBLIC.label)
        assertEquals("Imagen", ContentResourceType.IMAGE.label)
        assertEquals("Media", Difficulty.MEDIUM.label)
        assertEquals("Fortaleza", ObjectiveLevel.STRENGTH.label)
        assertEquals("Pendiente de validación", ReportStatus.PENDING_VALIDATION.label)
        assertEquals("Jefe de UTP", UserRole.UTP.label)
        assertEquals("Docente", UserRole.DOCENTE.label)
    }

    @Test
    fun `las etiquetas no son valores wire`() {
        val labels = listOf(
            ContentStatus.DRAFT.label,
            ContentStatus.IN_REVIEW.label,
            ActivityType.CHALLENGE.label,
            ActivityType.FINAL_TEST.label,
            ContentOrigin.PUBLIC.label,
            ContentResourceType.IMAGE.label,
            Difficulty.MEDIUM.label,
            ObjectiveLevel.STRENGTH.label,
            ReportStatus.PENDING_VALIDATION.label,
            UserRole.ESTUDIANTE.label
        )

        labels.forEach { label ->
            assertFalse("La etiqueta no debe contener guiones bajos: $label", label.contains("_"))
        }
    }

    @Test
    fun `fromWire conserva los valores del contrato`() {
        assertEquals(ContentStatus.DRAFT, ContentStatus.fromWire("BORRADOR"))
        assertEquals(ActivityType.FINAL_TEST, ActivityType.fromWire("PRUEBA_FINAL"))
        assertEquals(ContentOrigin.PUBLIC, ContentOrigin.fromWire("PUBLICO"))
        assertEquals(ContentResourceType.AUDIO, ContentResourceType.fromWire("AUDIO"))
        assertEquals(Difficulty.HIGH, Difficulty.fromWire("ALTA"))
        assertEquals(ObjectiveLevel.SUPPORT, ObjectiveLevel.fromWire("REFUERZO"))
        assertEquals(
            ReportStatus.PENDING_VALIDATION,
            ReportStatus.fromWire("PENDIENTE_VALIDACION")
        )
        assertEquals(UserRole.DOCENTE, UserRole.fromWire("DOCENTE"))
    }
}

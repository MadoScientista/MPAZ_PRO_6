package com.ccstudio.mpaz_pro_6.data.local

import com.ccstudio.mpaz_pro_6.data.enums.ActivityType
import com.ccstudio.mpaz_pro_6.data.enums.ContentOrigin
import com.ccstudio.mpaz_pro_6.data.enums.ContentResourceType
import com.ccstudio.mpaz_pro_6.data.enums.ContentStatus
import com.ccstudio.mpaz_pro_6.data.enums.Difficulty
import com.ccstudio.mpaz_pro_6.data.enums.ObjectiveLevel
import com.ccstudio.mpaz_pro_6.data.enums.ReportStatus
import com.ccstudio.mpaz_pro_6.data.enums.UserRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.Instant

/** Pruebas de los conversores de Room. */
class ConvertersTest {

    private val converters = Converters()

    @Test
    fun instantSeGuardaComoEpochEnMilisegundos() {
        val instant = Instant.parse("2026-08-15T12:34:56Z")

        val epoch = converters.instantToEpoch(instant)

        assertEquals(instant.toEpochMilli(), epoch)
        assertEquals(instant, converters.epochToInstant(epoch))
    }

    @Test
    fun instantNuloSeConserva() {
        assertNull(converters.instantToEpoch(null))
        assertNull(converters.epochToInstant(null))
    }

    @Test
    fun enumsHacenRoundTripConSuValorWire() {
        listOf(
            ContentStatus.DRAFT to "BORRADOR",
            ActivityType.CHALLENGE to "DESAFIO",
            ContentOrigin.OWN to "PROPIO",
            ContentResourceType.IMAGE to "IMAGEN",
            Difficulty.MEDIUM to "MEDIA",
            ObjectiveLevel.STRENGTH to "FORTALEZA",
            ReportStatus.PENDING_VALIDATION to "PENDIENTE_VALIDACION",
            UserRole.ESTUDIANTE to "ESTUDIANTE"
        ).forEach { (enumValue, wireValue) ->
            assertEquals(wireValue, enumToWire(enumValue))
            assertEquals(enumValue, wireToEnum(wireValue))
        }
    }

    private fun enumToWire(value: Any): String = when (value) {
        is ContentStatus -> converters.contentStatusToString(value)
        is ActivityType -> converters.activityTypeToString(value)
        is ContentOrigin -> converters.contentOriginToString(value)
        is ContentResourceType -> converters.contentResourceTypeToString(value)
        is Difficulty -> converters.difficultyToString(value)
        is ObjectiveLevel -> converters.objectiveLevelToString(value)
        is ReportStatus -> converters.reportStatusToString(value)
        is UserRole -> converters.userRoleToString(value)
        else -> error("Enum no soportado: $value")
    }

    private fun wireToEnum(value: String): Any = when (value) {
        "BORRADOR" -> converters.stringToContentStatus(value)
        "DESAFIO" -> converters.stringToActivityType(value)
        "PROPIO" -> converters.stringToContentOrigin(value)
        "IMAGEN" -> converters.stringToContentResourceType(value)
        "MEDIA" -> converters.stringToDifficulty(value)
        "FORTALEZA" -> converters.stringToObjectiveLevel(value)
        "PENDIENTE_VALIDACION" -> converters.stringToReportStatus(value)
        "ESTUDIANTE" -> converters.stringToUserRole(value)
        else -> error("Valor wire no soportado: $value")
    }
}

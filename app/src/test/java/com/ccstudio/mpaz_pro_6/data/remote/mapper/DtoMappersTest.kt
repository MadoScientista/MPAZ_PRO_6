package com.ccstudio.mpaz_pro_6.data.remote.mapper

import com.ccstudio.mpaz_pro_6.data.enums.ActivityType
import com.ccstudio.mpaz_pro_6.data.enums.ContentOrigin
import com.ccstudio.mpaz_pro_6.data.enums.ContentStatus
import com.ccstudio.mpaz_pro_6.data.model.ReportObjectiveDetail
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateActivityRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateAssignmentRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateContentRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateActivityRequest
import com.ccstudio.mpaz_pro_6.data.local.mapper.toEntity
import com.ccstudio.mpaz_pro_6.data.remote.dto.ActivityDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.AssignmentDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.ContentDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.LearningUnitDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.ReportObjectiveDetailDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test
import java.time.Instant

/** Pruebas de los mapeadores entre DTO wire y dominio. */
class DtoMappersTest {

    @Test
    fun unidadMapeaEnumYFecha() {
        val dto = LearningUnitDto(
            id = 4,
            asignaturaId = 2,
            titulo = "Fracciones",
            estado = "AUTORIZADO",
            autorizadaPorId = 1,
            fechaAutorizacion = "2026-04-06T08:00:00Z"
        )

        val unit = dto.toDomain()

        assertEquals(ContentStatus.AUTHORIZED, unit.estado)
        assertEquals(Instant.parse("2026-04-06T08:00:00Z"), unit.fechaAutorizacion)
    }

    @Test
    fun asignacionMapeaFechaInicio() {
        val dto = AssignmentDto(
            id = 9,
            cursoId = 1,
            unidadId = 2,
            docenteId = 3,
            maxIntentos = 2,
            pruebaFinalHabilitada = true,
            fecha = "2026-04-06T08:00:00Z"
        )

        val assignment = dto.toDomain()

        assertEquals(Instant.parse("2026-04-06T08:00:00Z"), assignment.fecha)
        assertEquals(2, assignment.maxIntentos)
    }

    @Test
    fun solicitudDeContenidoUsaWireDeOrigen() {
        val dto = CreateContentRequest(
            oaId = 3,
            titulo = "Introducción",
            explicacion = "Texto",
            origen = ContentOrigin.OWN,
            orden = 2
        ).toDto()

        assertEquals("PROPIO", dto.origen)
        assertEquals(2, dto.orden)
    }

    @Test
    fun solicitudDeAsignacionSerializaFechaComoIso() {
        val dto = CreateAssignmentRequest(
            cursoId = 1,
            unidadId = 2,
            docenteId = 3,
            fecha = Instant.parse("2026-04-06T08:00:00Z")
        ).toDto()

        assertEquals("2026-04-06T08:00:00Z", dto.fecha)
    }

    @Test
    fun fechaInvalidaLanzaErrorDeMapeo() {
        val dto = LearningUnitDto(
            id = 1,
            asignaturaId = 1,
            titulo = "Unidad",
            estado = "BORRADOR",
            fechaAutorizacion = "06-04-2026"
        )

        assertThrows(IllegalArgumentException::class.java) { dto.toDomain() }
    }

    @Test
    fun enumDesconocidoLanzaErrorDeMapeo() {
        val dto = LearningUnitDto(
            id = 1,
            asignaturaId = 1,
            titulo = "Unidad",
            estado = "DESCONOCIDO"
        )

        assertThrows(IllegalArgumentException::class.java) { dto.toDomain() }
    }

    @Test
    fun detalleDeInformeUsaIdEstablePorInformeYObjetivo() {
        val detail = ReportObjectiveDetail(
            oaId = 3,
            total = 10,
            correctas = 7,
            nivel = com.ccstudio.mpaz_pro_6.data.enums.ObjectiveLevel.STRENGTH
        )

        val entity = detail.toEntity(reportId = 7)

        assertEquals((7 shl 16) xor 3, entity.id)
        assertEquals(7, entity.informeId)
        assertEquals(10, entity.totalPreguntas)
    }

    @Test
    fun detalleWireMapeaNivel() {
        val dto = ReportObjectiveDetailDto(
            oaId = 3,
            total = 10,
            correctas = 6,
            nivel = "REFUERZO"
        )

        assertEquals(
            com.ccstudio.mpaz_pro_6.data.enums.ObjectiveLevel.SUPPORT,
            dto.toDomain().nivel
        )
    }

    @Test
    fun contenidoWireConservaLaUnidadDerivada() {
        val dto = ContentDto(
            id = 1,
            oaId = 2,
            unidadId = 7,
            titulo = "Contenido",
            explicacion = "Explicación",
            origen = "PROPIO"
        )

        val content = dto.toDomain()

        assertEquals(7, content.unidadId)
        assertEquals(7, content.toEntity().unidadId)
    }

    @Test
    fun actividadWireConservaContenidoYTransversalidad() {
        val withContent = ActivityDto(
            id = 1,
            unidadId = 2,
            contenidoId = 3,
            titulo = "Actividad",
            tipo = "ACTIVIDAD"
        ).toDomain()

        val transversal = ActivityDto(
            id = 2,
            unidadId = 2,
            contenidoId = null,
            titulo = "Prueba final",
            tipo = "PRUEBA_FINAL"
        ).toDomain()

        assertEquals(3, withContent.contenidoId)
        assertNull(transversal.contenidoId)
    }

    @Test
    fun solicitudDeActividadMapeaContenidoYDesasociacion() {
        val create = CreateActivityRequest(
            unidadId = 1,
            titulo = "Actividad",
            tipo = ActivityType.ACTIVITY,
            contenidoId = 4
        ).toDto()

        val update = UpdateActivityRequest(desasociarContenido = true).toDto()

        assertEquals(4, create.contenidoId)
        assertEquals(true, update.desasociarContenido)
        assertNull(update.contenidoId)
    }
}

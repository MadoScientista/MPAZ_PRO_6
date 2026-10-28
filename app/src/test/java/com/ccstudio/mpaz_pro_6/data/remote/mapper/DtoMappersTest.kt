package com.ccstudio.mpaz_pro_6.data.remote.mapper

import com.ccstudio.mpaz_pro_6.data.enums.ContentOrigin
import com.ccstudio.mpaz_pro_6.data.enums.ContentStatus
import com.ccstudio.mpaz_pro_6.data.model.ReportObjectiveDetail
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateAssignmentRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateContentRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateDocenciaRequest
import com.ccstudio.mpaz_pro_6.data.local.mapper.toEntity
import com.ccstudio.mpaz_pro_6.data.remote.dto.AssignmentDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.DocenciaDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.LearningUnitDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.PedagogicalReportDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.ReportObjectiveDetailDto
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
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
    fun asignacionDelBackendSinDocenteSeDeserializa() {
        val dto = Json.decodeFromString<AssignmentDto>(
            """
            {
              "id": 9,
              "cursoId": 1,
              "unidadId": 2,
              "maxIntentos": 2,
              "pruebaFinalHabilitada": true,
              "fecha": "2026-04-06T08:00:00Z"
            }
            """.trimIndent()
        )

        val assignment = dto.toDomain()

        assertEquals(9, assignment.id)
        assertEquals(2, assignment.maxIntentos)
        assertEquals(Instant.parse("2026-04-06T08:00:00Z"), assignment.fecha)
    }

    @Test
    fun docenciaMapeaNombresYRelaciones() {
        val dto = DocenciaDto(
            id = 4,
            docenteId = 2,
            docenteNombre = "Diego Salinas",
            cursoId = 1,
            cursoNombre = "6A",
            asignaturaId = 3,
            asignaturaNombre = "Ciencias Naturales"
        )

        val docencia = dto.toDomain()

        assertEquals(2, docencia.docenteId)
        assertEquals("6A", docencia.cursoNombre)
        assertEquals("Ciencias Naturales", docencia.asignaturaNombre)
    }

    @Test
    fun informeMapeaNombresDelBackend() {
        val dto = PedagogicalReportDto(
            id = 1,
            estudianteId = 3,
            estudianteNombre = "Camila Rivas",
            unidadId = 1,
            unidadTitulo = "Lectura y comprensión",
            asignaturaNombre = "Lengua y Comunicación",
            docenteId = 2,
            docenteNombre = "Diego Salinas",
            fechaGeneracion = "2026-05-08T09:00:00Z",
            estado = "PENDIENTE_VALIDACION"
        )

        val report = dto.toDomain()

        assertEquals("Camila Rivas", report.estudianteNombre)
        assertEquals("Lectura y comprensión", report.unidadTitulo)
        assertEquals("Diego Salinas", report.docenteNombre)
    }

    @Test
    fun solicitudDeDocenciaSeMapeaAlCuerpoWire() {
        val dto = CreateDocenciaRequest(
            docenteId = 2,
            cursoId = 1,
            asignaturaId = 3
        ).toDto()

        assertEquals(2, dto.docenteId)
        assertEquals(1, dto.cursoId)
        assertEquals(3, dto.asignaturaId)
    }

    @Test
    fun solicitudDeAsignacionSerializaFechaComoIso() {
        val dto = CreateAssignmentRequest(
            cursoId = 1,
            unidadId = 2,
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
}

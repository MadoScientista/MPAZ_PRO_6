package com.ccstudio.mpaz_pro_6.data.remote

import com.ccstudio.mpaz_pro_6.data.dto.curriculo.AsignaturaResponse
import com.ccstudio.mpaz_pro_6.data.dto.docencia.ActividadCursoResponse
import com.ccstudio.mpaz_pro_6.data.dto.docencia.AsignaturaCursoResponse
import com.ccstudio.mpaz_pro_6.data.dto.evaluacion.IntentoResponse
import com.ccstudio.mpaz_pro_6.data.dto.identidad.CursoResponse
import com.ccstudio.mpaz_pro_6.data.dto.informe.InformePedagogicoResponse
import com.ccstudio.mpaz_pro_6.data.dto.informe.InformePreliminarResponse
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Contrato de serializacion con el backend de 27 tablas.
 *
 * Los JSON de este test reflejan las respuestas reales de los endpoints y
 * protegen contra cambios de nombres o tipos que rompan el mobile.
 */
class ContractSerializationTest {

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        encodeDefaults = true
    }

    @Test
    fun loginResponseSeDeserializaComoEsperaElMobile() {
        val response = json.decodeFromString<LoginResponseDto>(
            """
            {
              "token":"jwt-demo",
              "tokenType":"Bearer",
              "expiresIn":3600,
              "usuario":{
                "id":6,
                "pnombre":"Elena",
                "snombre":null,
                "apaterno":"Castro",
                "amaterno":null,
                "nombreUsuario":"alumno",
                "rol":{"id":3,"nombre":"ESTUDIANTE"},
                "activo":true
              }
            }
            """.trimIndent()
        )

        assertEquals("jwt-demo", response.token)
        assertEquals("Bearer", response.tokenType)
        assertEquals(3600L, response.expiresIn)
        assertEquals("alumno", response.usuario.nombreUsuario)
        assertEquals("ESTUDIANTE", response.usuario.rol.nombre)
    }

    @Test
    fun cursoYAsignaturaCursoSeDeserializan() {
        val curso = json.decodeFromString<CursoResponse>(
            """
            {"id":1,"annio":2026,"nivel":{"id":1,"nombre":"Sexto Básico"},"seccion":{"id":1,"letra":"A"}}
            """.trimIndent()
        )
        assertEquals(2026, curso.annio)
        assertEquals("A", curso.seccion.letra)

        val asignatura = json.decodeFromString<AsignaturaResponse>(
            """
            {"id":1,"nombre":"Lenguaje y Comunicación","nivel":{"id":1,"nombre":"Sexto Básico"}}
            """.trimIndent()
        )
        assertEquals("Lenguaje y Comunicación", asignatura.nombre)

        val docencia = json.decodeFromString<AsignaturaCursoResponse>(
            """
            {
              "id":1,
              "curso":{"id":1,"annio":2026,"nivel":{"id":1,"nombre":"Sexto Básico"},"seccion":{"id":1,"letra":"A"}},
              "asignatura":{"id":1,"nombre":"Lenguaje y Comunicación","nivel":{"id":1,"nombre":"Sexto Básico"}},
              "docente":{"id":2,"nombreUsuario":"docente","nombreCompleto":"Ana Silva"}
            }
            """.trimIndent()
        )
        assertEquals("docente", docencia.docente?.nombreUsuario)
        assertEquals(1L, docencia.curso.id)
    }

    @Test
    fun intentoActividadCursoYRespuestaSeDeserializan() {
        val actividadCurso = json.decodeFromString<ActividadCursoResponse>(
            """
            {
              "id":3,
              "actividad":{"id":2,"instrucciones":"Selecciona la alternativa correcta."},
              "curso":{"id":1,"annio":2026,"nivel":{"id":1,"nombre":"Sexto Básico"},"seccion":{"id":1,"letra":"A"}},
              "fechaInicio":null,
              "fechaFin":null,
              "habilitada":true,
              "visible":true,
              "maxIntentos":3
            }
            """.trimIndent()
        )
        assertEquals(3L, actividadCurso.id)
        assertTrue(actividadCurso.habilitada)
        assertEquals(3, actividadCurso.maxIntentos)

        val intento = json.decodeFromString<IntentoResponse>(
            """
            {
              "id":1,
              "fechaInicio":"2026-11-14T10:00:00Z",
              "fechaFin":null,
              "usuario":{"id":6,"nombreUsuario":"alumno","nombreCompleto":"Elena Castro"},
              "actividadCurso":{
                "id":3,
                "actividad":{"id":2,"instrucciones":"Selecciona la alternativa correcta."},
                "curso":{"id":1,"annio":2026,"nivel":{"id":1,"nombre":"Sexto Básico"},"seccion":{"id":1,"letra":"A"}},
                "fechaInicio":null,
                "fechaFin":null,
                "habilitada":true,
                "visible":true,
                "maxIntentos":3
              }
            }
            """.trimIndent()
        )
        assertEquals(1L, intento.id)
        assertEquals("alumno", intento.usuario.nombreUsuario)
        assertEquals(3L, intento.actividadCurso?.id)
    }

    @Test
    fun informeYPreviewSeDeserializan() {
        val informe = json.decodeFromString<InformePedagogicoResponse>(
            """
            {
              "id":1,
              "usuario":{"id":6,"nombreUsuario":"alumno","nombreCompleto":"Elena Castro"},
              "unidad":{"id":2,"nombre":"Números y operaciones","asignatura":{"id":2,"nombre":"Matemática","nivel":{"id":1,"nombre":"Sexto Básico"}}},
              "fecha":"2026-11-14T10:00:00Z",
              "descripcion":"Informe de prueba",
              "fechaCorte":"2026-11-14T10:00:00Z",
              "origen":"AUTOMATICO"
            }
            """.trimIndent()
        )
        assertEquals(1L, informe.id)
        assertEquals("AUTOMATICO", informe.origen)
        assertNotNull(informe.fechaCorte)

        val preview = json.decodeFromString<InformePreliminarResponse>(
            """
            {
              "usuario":{"id":6,"nombreUsuario":"alumno","nombreCompleto":"Elena Castro"},
              "unidad":{"id":2,"nombre":"Números y operaciones","asignatura":{"id":2,"nombre":"Matemática","nivel":{"id":1,"nombre":"Sexto Básico"}}},
              "fechaCorte":"2026-11-14T10:00:00Z",
              "actividades":[
                {
                  "actividadCursoId":3,
                  "actividadId":2,
                  "instrucciones":"Actividad",
                  "intentoId":1,
                  "fechaFin":"2026-11-01T10:15:00Z"
                }
              ],
              "detalles":[
                {
                  "objetivoAprendizaje":{"id":3,"codigo":"MA06 OA 01","descripcion":"Factores y múltiplos"},
                  "correctas":3,
                  "incorrectas":0,
                  "total":3,
                  "porcentaje":100.0
                }
              ],
              "totalCorrectas":3,
              "totalIncorrectas":0
            }
            """.trimIndent()
        )
        assertEquals(1, preview.actividades.size)
        assertEquals(3, preview.detalles.first().correctas)
    }
}

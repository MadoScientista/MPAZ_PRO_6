package com.ccstudio.mpaz_pro_6.data.mock

import com.ccstudio.mpaz_pro_6.data.enums.ActivityType
import com.ccstudio.mpaz_pro_6.data.enums.ContentOrigin
import com.ccstudio.mpaz_pro_6.data.enums.ContentResourceType
import com.ccstudio.mpaz_pro_6.data.enums.ContentStatus
import com.ccstudio.mpaz_pro_6.data.enums.UserRole
import com.ccstudio.mpaz_pro_6.data.model.Activity
import com.ccstudio.mpaz_pro_6.data.model.Assignment
import com.ccstudio.mpaz_pro_6.data.model.Content
import com.ccstudio.mpaz_pro_6.data.model.ContentResource
import com.ccstudio.mpaz_pro_6.data.model.Course
import com.ccstudio.mpaz_pro_6.data.model.LearningObjective
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.data.model.User
import java.time.Instant

/**
 * Datos en memoria que alimentan la app mientras no exista el backend.
 *
 * La semilla es coherente entre sí: las unidades apuntan a asignaturas
 * existentes, la asignación apunta a un curso, una unidad y un docente reales, y
 * las unidades autorizadas y publicadas lo están por el usuario con rol `UTP`.
 *
 * Usuarios de prueba (la contraseña vive en `MockStore.passwords`, nunca en el
 * modelo `User`):
 *
 * | usuario   | contraseña | rol        |
 * |-----------|------------|------------|
 * | `elena`   | `utp123`   | `UTP`      |
 * | `marco`   | `doc123`   | `DOCENTE`  |
 * | `camila`  | `est123`   | `ESTUDIANTE` |
 * | `sofia`   | `est456`   | `ESTUDIANTE` |
 */
object MockData {

    /**
     * Construye un [MockStore] con la semilla cargada.
     */
    fun crearStore(): MockStore {

        val subjects = mutableListOf(
            Subject(id = 1, nombre = "Matemática"),
            Subject(id = 2, nombre = "Ciencias Naturales"),
            Subject(id = 3, nombre = "Lenguaje")
        )

        val courses = mutableListOf(
            Course(id = 1, nombre = "6A", anio = 2026),
            Course(id = 2, nombre = "6B", anio = 2026)
        )

        val units = mutableListOf(
            LearningUnit(
                id = 1,
                asignaturaId = 1,
                nivelEducativo = "Sexto Basico",
                titulo = "Números enteros y operaciones",
                orden = 1,
                estado = ContentStatus.DRAFT,
                autorizadaPorId = null,
                fechaAutorizacion = null
            ),
            LearningUnit(
                id = 2,
                asignaturaId = 2,
                nivelEducativo = "Sexto Basico",
                titulo = "Ecosistemas y biodiversidad",
                orden = 1,
                estado = ContentStatus.IN_REVIEW,
                autorizadaPorId = null,
                fechaAutorizacion = null
            ),
            LearningUnit(
                id = 3,
                asignaturaId = 1,
                nivelEducativo = "Sexto Basico",
                titulo = "Fracciones y decimales",
                orden = 2,
                estado = ContentStatus.DRAFT,
                autorizadaPorId = null,
                fechaAutorizacion = null
            ),
            LearningUnit(
                id = 4,
                asignaturaId = 3,
                nivelEducativo = "Sexto Basico",
                titulo = "Comprensión de textos narrativos",
                orden = 1,
                estado = ContentStatus.IN_REVIEW,
                autorizadaPorId = null,
                fechaAutorizacion = null
            ),
            LearningUnit(
                id = 5,
                asignaturaId = 2,
                nivelEducativo = "Sexto Basico",
                titulo = "Ciclo del agua",
                orden = 2,
                estado = ContentStatus.IN_REVIEW,
                autorizadaPorId = null,
                fechaAutorizacion = null
            ),
            LearningUnit(
                id = 6,
                asignaturaId = 3,
                nivelEducativo = "Sexto Basico",
                titulo = "Géneros literarios",
                orden = 2,
                estado = ContentStatus.AUTHORIZED,
                autorizadaPorId = 1,
                fechaAutorizacion = Instant.parse("2026-03-02T10:00:00Z")
            ),
            LearningUnit(
                id = 7,
                asignaturaId = 2,
                nivelEducativo = "Sexto Basico",
                titulo = "La materia y sus cambios",
                orden = 3,
                estado = ContentStatus.PUBLISHED,
                autorizadaPorId = 1,
                fechaAutorizacion = Instant.parse("2026-03-10T09:30:00Z")
            )
        )

        val assignments = mutableListOf(
            Assignment(
                id = 1,
                cursoId = 1,
                unidadId = 1,
                docenteId = 2,
                maxIntentos = 2,
                pruebaFinalHabilitada = true,
                fecha = Instant.parse("2026-04-06T08:00:00Z")
            )
        )

        val users = mutableListOf(
            User(
                id = 1,
                nombre = "Elena Soto",
                username = "elena",
                rol = UserRole.UTP,
                cursoId = null
            ),
            User(
                id = 2,
                nombre = "Marco Díaz",
                username = "marco",
                rol = UserRole.DOCENTE,
                cursoId = null
            ),
            User(
                id = 3,
                nombre = "Camila Rivas",
                username = "camila",
                rol = UserRole.ESTUDIANTE,
                cursoId = 1
            ),
            User(
                id = 4,
                nombre = "Sofía Rojas",
                username = "sofia",
                rol = UserRole.ESTUDIANTE,
                cursoId = 2
            )
        )

        val passwords = mutableMapOf(
            1 to "utp123",
            2 to "doc123",
            3 to "est123",
            4 to "est456"
        )

        val objectives = mutableListOf(
            LearningObjective(
                id = 1,
                unidadId = 2,
                codigo = "OA01",
                descripcion = "Identificar componentes de un ecosistema",
                eje = "Sistemas vivos",
                textoReferencia = "Texto escolar Ciencias Naturales"
            ),
            LearningObjective(
                id = 2,
                unidadId = 2,
                codigo = "OA02",
                descripcion = "Explicar relaciones tróficas",
                eje = "Sistemas vivos",
                textoReferencia = "Texto escolar Ciencias Naturales"
            ),
            LearningObjective(
                id = 3,
                unidadId = 4,
                codigo = "OA01",
                descripcion = "Reconocer elementos narrativos",
                eje = "Lectura",
                textoReferencia = "Texto escolar Lenguaje"
            ),
            LearningObjective(
                id = 4,
                unidadId = 5,
                codigo = "OA01",
                descripcion = "Explicar el ciclo del agua",
                eje = "Tierra y universo",
                textoReferencia = "Texto escolar Ciencias Naturales"
            )
        )

        val contents = mutableListOf(
            Content(
                id = 1,
                oaId = 1,
                titulo = "¿Qué es un ecosistema?",
                explicacion = "Un ecosistema está formado por seres vivos y factores no vivos que interactúan entre sí.",
                ejemplos = "Un bosque, un río o un desierto son ejemplos de ecosistemas.",
                instrucciones = "Identifica seres bióticos y abióticos en un ejemplo.",
                orden = 1,
                origen = ContentOrigin.OWN,
                estado = ContentStatus.AUTHORIZED
            ),
            Content(
                id = 2,
                oaId = 2,
                titulo = "Cadenas tróficas",
                explicacion = "Las cadenas tróficas muestran quién se alimenta de quién en un ecosistema.",
                ejemplos = "Planta -> Conejo -> Zorro",
                instrucciones = "Completa una cadena trófica con los organismos dados.",
                orden = 1,
                origen = ContentOrigin.OWN,
                estado = ContentStatus.AUTHORIZED
            ),
            Content(
                id = 3,
                oaId = 3,
                titulo = "Narrador y personajes",
                explicacion = "Todo relato tiene un narrador y personajes que desarrollan la acción.",
                ejemplos = "Cuento, fábula o leyenda.",
                instrucciones = "Identifica el tipo de narrador en el texto.",
                orden = 1,
                origen = ContentOrigin.OWN,
                estado = ContentStatus.AUTHORIZED
            ),
            Content(
                id = 4,
                oaId = 4,
                titulo = "Procesos del ciclo del agua",
                explicacion = "La evaporación, condensación, precipitación y filtración forman el ciclo del agua.",
                ejemplos = "La lluvia, el rocío y las nubes.",
                instrucciones = "Ordena los procesos del ciclo del agua.",
                orden = 1,
                origen = ContentOrigin.OWN,
                estado = ContentStatus.AUTHORIZED
            )
        )

        val contentResources = mutableListOf(
            ContentResource(
                id = 1,
                contenidoId = 1,
                tipo = ContentResourceType.IMAGE,
                url = "https://ejemplo.test/ecosistema.png",
                descripcion = "Esquema de un ecosistema",
                origen = ContentOrigin.OWN,
                estado = ContentStatus.AUTHORIZED
            ),
            ContentResource(
                id = 2,
                contenidoId = 4,
                tipo = ContentResourceType.IMAGE,
                url = "https://ejemplo.test/ciclo-agua.png",
                descripcion = "Diagrama del ciclo del agua",
                origen = ContentOrigin.OWN,
                estado = ContentStatus.AUTHORIZED
            )
        )

        val activities = mutableListOf(
            Activity(
                id = 1,
                unidadId = 2,
                titulo = "Clasificar seres vivos y no vivos",
                tipo = ActivityType.ACTIVITY,
                orden = 1
            ),
            Activity(
                id = 2,
                unidadId = 4,
                titulo = "Identificar elementos narrativos",
                tipo = ActivityType.ACTIVITY,
                orden = 1
            ),
            Activity(
                id = 3,
                unidadId = 5,
                titulo = "Desafío: ciclo del agua",
                tipo = ActivityType.CHALLENGE,
                orden = 1
            )
        )

        return MockStore(
            subjects = subjects,
            courses = courses,
            units = units,
            assignments = assignments,
            users = users,
            passwords = passwords,
            objectives = objectives,
            contents = contents,
            contentResources = contentResources,
            activities = activities
        )
    }
}

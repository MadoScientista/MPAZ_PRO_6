package com.ccstudio.mpaz_pro_6.data.mock

import com.ccstudio.mpaz_pro_6.data.enums.ContentStatus
import com.ccstudio.mpaz_pro_6.data.enums.UserRole
import com.ccstudio.mpaz_pro_6.data.model.Assignment
import com.ccstudio.mpaz_pro_6.data.model.Course
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
                estado = ContentStatus.AUTHORIZED,
                autorizadaPorId = 1,
                fechaAutorizacion = Instant.parse("2026-03-02T10:00:00Z")
            ),
            LearningUnit(
                id = 5,
                asignaturaId = 2,
                nivelEducativo = "Sexto Basico",
                titulo = "Ciclo del agua",
                orden = 2,
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

        return MockStore(
            subjects = subjects,
            courses = courses,
            units = units,
            assignments = assignments,
            users = users,
            passwords = passwords
        )
    }
}

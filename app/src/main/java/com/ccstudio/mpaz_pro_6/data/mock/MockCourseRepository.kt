package com.ccstudio.mpaz_pro_6.data.mock

import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.Course
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateCourseRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateCourseRequest
import com.ccstudio.mpaz_pro_6.data.repository.CourseRepository
import kotlinx.coroutines.sync.withLock

/**
 * Implementación en memoria de `CourseRepository`.
 *
 * Reproduce las reglas del backend: el nombre es obligatorio y admite hasta 50
 * caracteres, el año debe estar entre 2000 y 2100, el par nombre-año es único y
 * eliminar un curso con estudiantes, docencias o asignaciones asociadas devuelve
 * `ApiError.Conflict`.
 */
class MockCourseRepository(
    private val store: MockStore
) : CourseRepository {

    override suspend fun list(anio: Int?): Result<List<Course>> = store.mutex.withLock {

        if (anio != null && anio !in 2000..2100) {
            return Result.failure(
                ApiError.BadRequest("El año debe estar entre 2000 y 2100.")
            )
        }

        val courses = store.courses
            .filter { anio == null || it.anio == anio }
            .sortedWith(compareBy({ it.anio }, { it.nombre }))

        Result.success(courses)
    }

    override suspend fun getById(id: Int): Result<Course> = store.mutex.withLock {
        val course = store.courses.find { it.id == id }

        if (course == null) {
            Result.failure(ApiError.NotFound("No existe el curso con id $id."))
        } else {
            Result.success(course)
        }
    }

    override suspend fun create(request: CreateCourseRequest): Result<Course> =
        store.mutex.withLock {
            val nombre = request.nombre.trim()

            if (nombre.isEmpty()) {
                return Result.failure(ApiError.BadRequest("El nombre del curso es obligatorio."))
            }

            if (nombre.length > 50) {
                return Result.failure(
                    ApiError.BadRequest("El nombre admite hasta 50 caracteres.")
                )
            }

            if (request.anio !in 2000..2100) {
                return Result.failure(
                    ApiError.BadRequest("El año debe estar entre 2000 y 2100.")
                )
            }

            if (existeConNombreYAnio(nombre, request.anio)) {
                return Result.failure(
                    ApiError.Conflict(
                        "Ya existe un curso \"$nombre\" para el año ${request.anio}."
                    )
                )
            }

            val course = Course(
                id = store.nextCourseId++,
                nombre = nombre,
                anio = request.anio
            )

            store.courses.add(course)

            Result.success(course)
        }

    override suspend fun update(id: Int, request: UpdateCourseRequest): Result<Course> =
        store.mutex.withLock {
            val course = store.courses.find { it.id == id }

            if (course == null) {
                return Result.failure(ApiError.NotFound("No existe el curso con id $id."))
            }

            if (request.nombre == null && request.anio == null) {
                return Result.failure(
                    ApiError.BadRequest("Debes enviar al menos un campo para actualizar.")
                )
            }

            val nombre = request.nombre?.trim() ?: course.nombre
            val anio = request.anio ?: course.anio

            if (nombre.isEmpty()) {
                return Result.failure(ApiError.BadRequest("El nombre del curso es obligatorio."))
            }

            if (nombre.length > 50) {
                return Result.failure(
                    ApiError.BadRequest("El nombre admite hasta 50 caracteres.")
                )
            }

            if (anio !in 2000..2100) {
                return Result.failure(
                    ApiError.BadRequest("El año debe estar entre 2000 y 2100.")
                )
            }

            if (existeConNombreYAnio(nombre, anio, id)) {
                return Result.failure(
                    ApiError.Conflict(
                        "Ya existe un curso \"$nombre\" para el año $anio."
                    )
                )
            }

            val updated = course.copy(nombre = nombre, anio = anio)
            store.courses[store.courses.indexOf(course)] = updated

            Result.success(updated)
        }

    override suspend fun delete(id: Int): Result<Unit> = store.mutex.withLock {
        val course = store.courses.find { it.id == id }

        if (course == null) {
            return Result.failure(ApiError.NotFound("No existe el curso con id $id."))
        }

        val estudiantes = store.users.count { it.cursoId == id }
        val docencias = store.docencias.count { it.cursoId == id }
        val asignaciones = store.assignments.count { it.cursoId == id }

        if (estudiantes > 0 || docencias > 0 || asignaciones > 0) {
            return Result.failure(
                ApiError.Conflict(
                    "No se puede eliminar el curso: tiene $estudiantes estudiante(s), " +
                        "$docencias docencia(s) y $asignaciones asignación(es) asociadas."
                )
            )
        }

        store.courses.remove(course)

        Result.success(Unit)
    }

    /**
     * Indica si ya existe un curso con ese nombre y año.
     *
     * @param excludeId id que se ignora, para no comparar la entidad consigo misma
     * durante una actualización.
     */
    private fun existeConNombreYAnio(nombre: String, anio: Int, excludeId: Int? = null): Boolean =
        store.courses.any {
            it.id != excludeId &&
                    it.nombre.equals(nombre, ignoreCase = true) &&
                    it.anio == anio
        }
}

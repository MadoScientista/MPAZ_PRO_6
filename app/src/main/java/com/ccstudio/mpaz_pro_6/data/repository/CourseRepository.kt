package com.ccstudio.mpaz_pro_6.data.repository

import com.ccstudio.mpaz_pro_6.data.model.Course
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateCourseRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateCourseRequest

/**
 * Repositorio de cursos (`/api/cursos`).
 *
 * Todas las operaciones son `suspend` y devuelven `Result`; el fallo es siempre
 * un `ApiError`.
 */
interface CourseRepository {

    /**
     * Lista los cursos ordenados por año y nombre.
     *
     * @param anio filtro opcional del backend (`?anio`), entre 2000 y 2100.
     */
    suspend fun list(anio: Int? = null): Result<List<Course>>

    /** Obtiene un curso por id. */
    suspend fun getById(id: Int): Result<Course>

    /** Crea un curso. */
    suspend fun create(request: CreateCourseRequest): Result<Course>

    /** Actualiza nombre, año o ambos. Solo se aplican los valores enviados. */
    suspend fun update(id: Int, request: UpdateCourseRequest): Result<Course>

    /** Elimina un curso. Falla con `ApiError.Conflict` si tiene estudiantes o asignaciones. */
    suspend fun delete(id: Int): Result<Unit>
}

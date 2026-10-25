package com.ccstudio.mpaz_pro_6.data.local.repository

import com.ccstudio.mpaz_pro_6.data.error.apiCall
import com.ccstudio.mpaz_pro_6.data.error.requireSuccess
import com.ccstudio.mpaz_pro_6.data.local.dao.CatalogDao
import com.ccstudio.mpaz_pro_6.data.local.mapper.toDomain
import com.ccstudio.mpaz_pro_6.data.local.mapper.toEntity
import com.ccstudio.mpaz_pro_6.data.model.Course
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateCourseRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateCourseRequest
import com.ccstudio.mpaz_pro_6.data.remote.MpazApi
import com.ccstudio.mpaz_pro_6.data.remote.mapper.toDomain
import com.ccstudio.mpaz_pro_6.data.remote.mapper.toDto
import com.ccstudio.mpaz_pro_6.data.repository.CourseRepository

/** Repositorio de cursos con respaldo en la caché Room. */
class OfflineFirstCourseRepository(
    private val api: MpazApi,
    private val dao: CatalogDao
) : CourseRepository {

    override suspend fun list(anio: Int?): Result<List<Course>> =
        apiCall { api.getCourses(anio).map { it.toDomain() } }.fold(
            onSuccess = { courses ->
                dao.upsertCourses(courses.map { it.toEntity() })
                Result.success(courses)
            },
            onFailure = { error ->
                cachedValueOrError(error) {
                    val entities = if (anio == null) {
                        dao.getCourses()
                    } else {
                        dao.getCoursesByYear(anio)
                    }
                    entities.map { it.toDomain() }.takeIf { it.isNotEmpty() }
                }
            }
        )

    override suspend fun getById(id: Int): Result<Course> =
        apiCall { api.getCourse(id).toDomain() }.fold(
            onSuccess = { course ->
                dao.upsertCourse(course.toEntity())
                Result.success(course)
            },
            onFailure = { error ->
                cachedValueOrError(error) { dao.getCourseById(id)?.toDomain() }
            }
        )

    override suspend fun create(request: CreateCourseRequest): Result<Course> =
        apiCall { api.createCourse(request.toDto()).toDomain() }.fold(
            onSuccess = { course ->
                dao.upsertCourse(course.toEntity())
                Result.success(course)
            },
            onFailure = { Result.failure(it) }
        )

    override suspend fun update(id: Int, request: UpdateCourseRequest): Result<Course> =
        apiCall { api.updateCourse(id, request.toDto()).toDomain() }.fold(
            onSuccess = { course ->
                dao.upsertCourse(course.toEntity())
                Result.success(course)
            },
            onFailure = { Result.failure(it) }
        )

    override suspend fun delete(id: Int): Result<Unit> =
        apiCall {
            api.deleteCourse(id).requireSuccess()
            dao.deleteCourseById(id)
        }
}

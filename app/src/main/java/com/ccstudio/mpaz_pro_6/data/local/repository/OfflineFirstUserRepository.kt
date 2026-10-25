package com.ccstudio.mpaz_pro_6.data.local.repository

import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.error.apiCall
import com.ccstudio.mpaz_pro_6.data.error.requireSuccess
import com.ccstudio.mpaz_pro_6.data.local.dao.UserDao
import com.ccstudio.mpaz_pro_6.data.local.mapper.toDomain
import com.ccstudio.mpaz_pro_6.data.local.mapper.toEntity
import com.ccstudio.mpaz_pro_6.data.model.Course
import com.ccstudio.mpaz_pro_6.data.model.User
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateUserRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateUserRequest
import com.ccstudio.mpaz_pro_6.data.remote.MpazApi
import com.ccstudio.mpaz_pro_6.data.remote.mapper.toDomain
import com.ccstudio.mpaz_pro_6.data.remote.mapper.toDto
import com.ccstudio.mpaz_pro_6.data.repository.UserRepository

/** Repositorio de usuarios con respaldo en la caché Room. */
class OfflineFirstUserRepository(
    private val api: MpazApi,
    private val dao: UserDao
) : UserRepository {

    override suspend fun list(): Result<List<User>> =
        apiCall { api.getUsers().map { it.toDomain() } }.fold(
            onSuccess = { users ->
                dao.upsertUsers(users.map { it.toEntity() })
                Result.success(users)
            },
            onFailure = { error ->
                cachedValueOrError(error) {
                    dao.getUsers().map { it.toDomain() }.takeIf { it.isNotEmpty() }
                }
            }
        )

    override suspend fun getById(id: Int): Result<User> =
        apiCall { api.getUser(id).toDomain() }.fold(
            onSuccess = { user ->
                dao.upsertUser(user.toEntity())
                Result.success(user)
            },
            onFailure = { error ->
                cachedValueOrError(error) { dao.getUserById(id)?.toDomain() }
            }
        )

    override suspend fun authenticate(username: String, password: String): Result<User> =
        Result.failure(
            ApiError.Unauthorized(
                "El inicio de sesión se realiza contra /api/auth/login."
            )
        )

    override suspend fun create(request: CreateUserRequest): Result<User> =
        apiCall { api.createUser(request.toDto()).toDomain() }.fold(
            onSuccess = { user ->
                dao.upsertUser(user.toEntity())
                Result.success(user)
            },
            onFailure = { Result.failure(it) }
        )

    override suspend fun update(id: Int, request: UpdateUserRequest): Result<User> =
        apiCall { api.updateUser(id, request.toDto()).toDomain() }.fold(
            onSuccess = { user ->
                dao.upsertUser(user.toEntity())
                Result.success(user)
            },
            onFailure = { Result.failure(it) }
        )

    override suspend fun delete(id: Int): Result<Unit> =
        apiCall {
            api.deleteUser(id).requireSuccess()
            dao.deleteUserById(id)
        }

    override suspend fun listCourses(userId: Int): Result<List<Course>> =
        apiCall { api.getTeacherCourses(userId).map { it.toDomain() } }.fold(
            onSuccess = { courses ->
                dao.upsertCourses(courses.map { it.toEntity() })
                Result.success(courses)
            },
            onFailure = { error ->
                cachedValueOrError(error) {
                    dao.getCoursesForTeacher(userId)
                        .map { it.toDomain() }
                        .takeIf { it.isNotEmpty() }
                }
            }
        )
}

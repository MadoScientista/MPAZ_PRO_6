package com.ccstudio.mpaz_pro_6.data.repository

import com.ccstudio.mpaz_pro_6.data.model.Course
import com.ccstudio.mpaz_pro_6.data.model.User
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateUserRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateUserRequest

/**
 * Repositorio de usuarios (`/api/usuarios`).
 *
 * Todas las operaciones son `suspend` y devuelven `Result`; el fallo es siempre
 * un `ApiError`.
 */
interface UserRepository {

    /** Lista los usuarios ordenados por id. */
    suspend fun list(): Result<List<User>>

    /** Obtiene un usuario por id. */
    suspend fun getById(id: Int): Result<User>

    /**
     * Resuelve el inicio de sesión local contra el mock.
     *
     * Se conserva solo para pruebas y datos de demostración. El flujo real de la
     * app usa [AuthRepository], que llama a `/api/auth/login` y persiste el token.
     */
    @Deprecated("La app autentica con AuthRepository y /api/auth/login.")
    suspend fun authenticate(username: String, password: String): Result<User>

    /** Crea un usuario (UTP, DOCENTE o ESTUDIANTE). */
    suspend fun create(request: CreateUserRequest): Result<User>

    /** Actualiza nombre, username, password, rol, curso o una combinación no vacía. */
    suspend fun update(id: Int, request: UpdateUserRequest): Result<User>

    /** Elimina un usuario. Falla con `ApiError.Conflict` si tiene intentos, informes, unidades autorizadas o asignaciones. */
    suspend fun delete(id: Int): Result<Unit>

    /**
     * Lista los cursos distintos que maneja un docente.
     *
     * Falla con `ApiError.BadRequest` si el usuario no tiene rol `DOCENTE`.
     */
    suspend fun listCourses(userId: Int): Result<List<Course>>
}

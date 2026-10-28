package com.ccstudio.mpaz_pro_6.data.mock

import com.ccstudio.mpaz_pro_6.data.enums.UserRole
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.Course
import com.ccstudio.mpaz_pro_6.data.model.User
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateUserRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateUserRequest
import com.ccstudio.mpaz_pro_6.data.repository.UserRepository
import kotlinx.coroutines.sync.withLock

/**
 * Implementación en memoria de `UserRepository`.
 *
 * Reproduce las reglas del backend: el `username` es único y admite letras,
 * dígitos, punto, guion y guion bajo; el `cursoId` solo se asigna a estudiantes;
 * y eliminar un usuario con intentos, informes, unidades autorizadas o docencias
 * devuelve `ApiError.Conflict`.
 */
class MockUserRepository(
    private val store: MockStore
) : UserRepository {

    override suspend fun list(): Result<List<User>> = store.mutex.withLock {
        Result.success(store.users.sortedBy { it.id })
    }

    override suspend fun getById(id: Int): Result<User> = store.mutex.withLock {
        val user = store.users.find { it.id == id }

        if (user == null) {
            Result.failure(ApiError.NotFound("No existe el usuario con id $id."))
        } else {
            Result.success(user)
        }
    }

    override suspend fun authenticate(username: String, password: String): Result<User> =
        store.mutex.withLock {
            val user = store.users.find { it.username.equals(username.trim(), ignoreCase = true) }

            if (user == null) {
                return Result.failure(
                    ApiError.BadRequest("El usuario o la contraseña no son correctos.")
                )
            }

            if (store.passwords[user.id] != password) {
                return Result.failure(
                    ApiError.BadRequest("El usuario o la contraseña no son correctos.")
                )
            }

            Result.success(user)
        }

    override suspend fun create(request: CreateUserRequest): Result<User> =
        store.mutex.withLock {
            if (request.nombre.isBlank()) {
                return Result.failure(ApiError.BadRequest("El nombre es obligatorio."))
            }

            if (request.password.isEmpty()) {
                return Result.failure(ApiError.BadRequest("La contraseña es obligatoria."))
            }

            val username = request.username.trim()

            if (!cumpleFormatoUsername(username)) {
                return Result.failure(
                    ApiError.BadRequest(
                        "El nombre de usuario admite de 3 a 100 caracteres de letras, " +
                            "dígitos, punto, guion y guion bajo."
                    )
                )
            }

            if (existeConUsername(username)) {
                return Result.failure(
                    ApiError.Conflict("El nombre de usuario \"$username\" ya está registrado.")
                )
            }

            if (request.cursoId != null && request.rol != UserRole.ESTUDIANTE) {
                return Result.failure(
                    ApiError.BadRequest("Solo un estudiante puede tener un curso asignado.")
                )
            }

            if (request.cursoId != null && store.courses.none { it.id == request.cursoId }) {
                return Result.failure(
                    ApiError.NotFound("No existe el curso con id ${request.cursoId}.")
                )
            }

            val user = User(
                id = store.nextUserId++,
                nombre = request.nombre.trim(),
                username = username,
                rol = request.rol,
                cursoId = request.cursoId
            )

            store.users.add(user)
            store.passwords[user.id] = request.password

            Result.success(user)
        }

    override suspend fun update(id: Int, request: UpdateUserRequest): Result<User> =
        store.mutex.withLock {
            val user = store.users.find { it.id == id }

            if (user == null) {
                return Result.failure(ApiError.NotFound("No existe el usuario con id $id."))
            }

            val sinCampos =
                request.nombre == null &&
                        request.username == null &&
                        request.password == null &&
                        request.rol == null &&
                        request.cursoId == null

            if (sinCampos) {
                return Result.failure(
                    ApiError.BadRequest("Debes enviar al menos un campo para actualizar.")
                )
            }

            val username = request.username?.trim() ?: user.username

            if (!cumpleFormatoUsername(username)) {
                return Result.failure(
                    ApiError.BadRequest(
                        "El nombre de usuario admite de 3 a 100 caracteres de letras, " +
                            "dígitos, punto, guion y guion bajo."
                    )
                )
            }

            if (existeConUsername(username, id)) {
                return Result.failure(
                    ApiError.Conflict("El nombre de usuario \"$username\" ya está registrado.")
                )
            }

            val rol = request.rol ?: user.rol

            // Cambiar el rol a uno distinto de estudiante limpia el curso.
            val cursoId =
                if (rol != UserRole.ESTUDIANTE) {
                    null
                } else {
                    request.cursoId ?: user.cursoId
                }

            if (rol != UserRole.ESTUDIANTE && request.cursoId != null) {
                return Result.failure(
                    ApiError.BadRequest("Solo un estudiante puede tener un curso asignado.")
                )
            }

            if (cursoId != null && store.courses.none { it.id == cursoId }) {
                return Result.failure(
                    ApiError.NotFound("No existe el curso con id $cursoId.")
                )
            }

            val updated = user.copy(
                nombre = request.nombre?.trim() ?: user.nombre,
                username = username,
                rol = rol,
                cursoId = cursoId
            )

            store.users[store.users.indexOf(user)] = updated

            request.password?.takeIf { it.isNotEmpty() }?.let { store.passwords[updated.id] = it }

            Result.success(updated)
        }

    override suspend fun delete(id: Int): Result<Unit> = store.mutex.withLock {
        val user = store.users.find { it.id == id }

        if (user == null) {
            return Result.failure(ApiError.NotFound("No existe el usuario con id $id."))
        }

        // Intentos e informes todavía no tienen repositorio mock: la comprobación
        // se completa cuando existan.
        val unidadesAutorizadas = store.units.count { it.autorizadaPorId == id }
        val docencias = store.docencias.count { it.docenteId == id }

        if (unidadesAutorizadas > 0 || docencias > 0) {
            return Result.failure(
                ApiError.Conflict(
                    "No se puede eliminar el usuario: tiene $unidadesAutorizadas unidad(es) " +
                        "autorizada(s) y $docencias docencia(s) asociadas."
                )
            )
        }

        store.users.remove(user)
        store.passwords.remove(id)

        Result.success(Unit)
    }

    override suspend fun listCourses(userId: Int): Result<List<Course>> = store.mutex.withLock {
        val user = store.users.find { it.id == userId }

        if (user == null) {
            return Result.failure(ApiError.NotFound("No existe el usuario con id $userId."))
        }

        if (user.rol != UserRole.DOCENTE) {
            return Result.failure(
                ApiError.BadRequest("Solo un docente tiene cursos asociados.")
            )
        }

        val cursoIds = store.docencias
            .filter { it.docenteId == userId }
            .map { it.cursoId }
            .distinct()

        val courses = store.courses
            .filter { cursoIds.contains(it.id) }
            .sortedBy { it.id }

        Result.success(courses)
    }

    override suspend fun listStudentsByCourse(courseId: Int): Result<List<User>> =
        store.mutex.withLock {
            val course = store.courses.find { it.id == courseId }

            if (course == null) {
                return Result.failure(
                    ApiError.NotFound("No existe el curso con id $courseId.")
                )
            }

            Result.success(
                store.users
                    .filter { it.rol == UserRole.ESTUDIANTE && it.cursoId == courseId }
                    .sortedBy { it.nombre.lowercase() }
            )
        }

    /**
     * El `username` admite de 3 a 100 caracteres de letras, dígitos, punto, guion
     * y guion bajo.
     */
    private fun cumpleFormatoUsername(username: String): Boolean =
        username.length in 3..100 && username.matches(Regex("^[A-Za-z0-9._-]+$"))

    /**
     * Indica si ya existe un usuario con ese `username`.
     *
     * @param excludeId id que se ignora, para no comparar el usuario consigo mismo
     * durante una actualización.
     */
    private fun existeConUsername(username: String, excludeId: Int? = null): Boolean =
        store.users.any {
            it.id != excludeId && it.username.equals(username, ignoreCase = true)
        }
}

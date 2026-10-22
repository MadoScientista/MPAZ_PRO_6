package com.ccstudio.mpaz_pro_6.data.mock

import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateSubjectRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateSubjectRequest
import com.ccstudio.mpaz_pro_6.data.repository.SubjectRepository
import kotlinx.coroutines.sync.withLock

/**
 * Implementación en memoria de `SubjectRepository`.
 *
 * Reproduce las reglas del backend: el nombre es obligatorio y admite hasta 100
 * caracteres, los nombres duplicados devuelven `ApiError.Conflict` y eliminar una
 * asignatura con unidades asociadas también devuelve `ApiError.Conflict`.
 */
class MockSubjectRepository(
    private val store: MockStore
) : SubjectRepository {

    override suspend fun list(): Result<List<Subject>> = store.mutex.withLock {
        Result.success(store.subjects.sortedBy { it.nombre })
    }

    override suspend fun getById(id: Int): Result<Subject> = store.mutex.withLock {
        val subject = store.subjects.find { it.id == id }

        if (subject == null) {
            Result.failure(ApiError.NotFound("No existe la asignatura con id $id."))
        } else {
            Result.success(subject)
        }
    }

    override suspend fun create(request: CreateSubjectRequest): Result<Subject> =
        store.mutex.withLock {
            val nombre = request.nombre.trim()

            if (nombre.isEmpty()) {
                return Result.failure(
                    ApiError.BadRequest("El nombre de la asignatura es obligatorio.")
                )
            }

            if (nombre.length > 100) {
                return Result.failure(
                    ApiError.BadRequest("El nombre admite hasta 100 caracteres.")
                )
            }

            if (existeConNombre(nombre)) {
                return Result.failure(
                    ApiError.Conflict("Ya existe una asignatura con el nombre \"$nombre\".")
                )
            }

            val subject = Subject(
                id = store.nextSubjectId++,
                nombre = nombre
            )

            store.subjects.add(subject)

            Result.success(subject)
        }

    override suspend fun update(id: Int, request: UpdateSubjectRequest): Result<Subject> =
        store.mutex.withLock {
            val subject = store.subjects.find { it.id == id }

            if (subject == null) {
                return Result.failure(ApiError.NotFound("No existe la asignatura con id $id."))
            }

            val nombre = request.nombre.trim()

            if (nombre.isEmpty()) {
                return Result.failure(
                    ApiError.BadRequest("El nombre de la asignatura es obligatorio.")
                )
            }

            if (nombre.length > 100) {
                return Result.failure(
                    ApiError.BadRequest("El nombre admite hasta 100 caracteres.")
                )
            }

            if (existeConNombre(nombre, id)) {
                return Result.failure(
                    ApiError.Conflict("Ya existe otra asignatura con el nombre \"$nombre\".")
                )
            }

            val updated = subject.copy(nombre = nombre)
            store.subjects[store.subjects.indexOf(subject)] = updated

            Result.success(updated)
        }

    override suspend fun delete(id: Int): Result<Unit> = store.mutex.withLock {
        val subject = store.subjects.find { it.id == id }

        if (subject == null) {
            return Result.failure(ApiError.NotFound("No existe la asignatura con id $id."))
        }

        val unidades = store.units.filter { it.asignaturaId == id }

        if (unidades.isNotEmpty()) {
            return Result.failure(
                ApiError.Conflict(
                    "No se puede eliminar la asignatura: tiene ${unidades.size} " +
                        "unidad(es) asociada(s)."
                )
            )
        }

        store.subjects.remove(subject)

        Result.success(Unit)
    }

    /**
     * Indica si ya existe una asignatura con ese nombre.
     *
     * @param excludeId id que se ignora, para no comparar la entidad consigo misma
     * durante una actualización.
     */
    private fun existeConNombre(nombre: String, excludeId: Int? = null): Boolean =
        store.subjects.any {
            it.id != excludeId && it.nombre.equals(nombre, ignoreCase = true)
        }
}

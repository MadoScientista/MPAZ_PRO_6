package com.ccstudio.mpaz_pro_6.data.mock

import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.LearningObjective
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateLearningObjectiveRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateLearningObjectiveRequest
import com.ccstudio.mpaz_pro_6.data.repository.LearningObjectiveRepository
import kotlinx.coroutines.sync.withLock

/**
 * Implementación en memoria de `LearningObjectiveRepository`.
 */
class MockLearningObjectiveRepository(
    private val store: MockStore
) : LearningObjectiveRepository {

    override suspend fun list(unitId: Int?): Result<List<LearningObjective>> =
        store.mutex.withLock {
            if (unitId != null && store.units.none { it.id == unitId }) {
                return Result.failure(ApiError.NotFound("No existe la unidad con id $unitId."))
            }

            val objectives = store.objectives
                .filter { unitId == null || it.unidadId == unitId }
                .sortedWith(compareBy({ it.unidadId }, { it.codigo }))

            Result.success(objectives)
        }

    override suspend fun getById(id: Int): Result<LearningObjective> = store.mutex.withLock {
        val objective = store.objectives.find { it.id == id }
        if (objective == null) {
            Result.failure(ApiError.NotFound("No existe el objetivo con id $id."))
        } else {
            Result.success(objective)
        }
    }

    override suspend fun create(request: CreateLearningObjectiveRequest): Result<LearningObjective> =
        store.mutex.withLock {
            if (store.units.none { it.id == request.unidadId }) {
                return Result.failure(ApiError.NotFound("No existe la unidad con id ${request.unidadId}."))
            }

            if (request.descripcion.isBlank()) {
                return Result.failure(ApiError.BadRequest("La descripción del objetivo es obligatoria."))
            }

            if (request.eje.isBlank()) {
                return Result.failure(ApiError.BadRequest("El eje del objetivo es obligatorio."))
            }

            val codigo = request.codigo?.trim()?.takeIf { it.isNotEmpty() }
            if (codigo != null) {
                val existe = store.objectives.any { it.unidadId == request.unidadId && it.codigo.equals(codigo, ignoreCase = true) }
                if (existe) {
                    return Result.failure(ApiError.Conflict("Ya existe un objetivo con ese código en la unidad."))
                }
            }

            val objective = LearningObjective(
                id = store.nextObjectiveId++,
                unidadId = request.unidadId,
                codigo = codigo,
                descripcion = request.descripcion.trim(),
                eje = request.eje.trim(),
                textoReferencia = request.textoReferencia?.trim()?.takeIf { it.isNotEmpty() }
            )

            store.objectives.add(objective)
            Result.success(objective)
        }

    override suspend fun update(id: Int, request: UpdateLearningObjectiveRequest): Result<LearningObjective> =
        store.mutex.withLock {
            val objective = store.objectives.find { it.id == id }
                ?: return Result.failure(ApiError.NotFound("No existe el objetivo con id $id."))

            val sinCampos = request.codigo == null && request.descripcion == null && request.eje == null && request.textoReferencia == null
            if (sinCampos) {
                return Result.failure(ApiError.BadRequest("Debes enviar al menos un campo para actualizar."))
            }

            val codigo = request.codigo?.trim()?.takeIf { it.isNotEmpty() } ?: objective.codigo
            if (codigo != null) {
                val existe = store.objectives.any { it.id != id && it.unidadId == objective.unidadId && it.codigo.equals(codigo, ignoreCase = true) }
                if (existe) {
                    return Result.failure(ApiError.Conflict("Ya existe un objetivo con ese código en la unidad."))
                }
            }

            val descripcion = request.descripcion?.trim() ?: objective.descripcion
            if (descripcion.isBlank()) {
                return Result.failure(ApiError.BadRequest("La descripción del objetivo es obligatoria."))
            }

            val eje = request.eje?.trim() ?: objective.eje
            if (eje.isBlank()) {
                return Result.failure(ApiError.BadRequest("El eje del objetivo es obligatorio."))
            }

            val updated = objective.copy(
                codigo = codigo,
                descripcion = descripcion,
                eje = eje,
                textoReferencia = request.textoReferencia?.trim()?.takeIf { it.isNotEmpty() } ?: objective.textoReferencia
            )

            store.objectives[store.objectives.indexOf(objective)] = updated
            Result.success(updated)
        }

    override suspend fun delete(id: Int): Result<Unit> = store.mutex.withLock {
        val objective = store.objectives.find { it.id == id }
            ?: return Result.failure(ApiError.NotFound("No existe el objetivo con id $id."))

        val tieneContenidos = store.contents.any { it.oaId == id }
        if (tieneContenidos) {
            return Result.failure(ApiError.Conflict("No se puede eliminar el objetivo: tiene contenidos asociados."))
        }

        store.objectives.remove(objective)
        Result.success(Unit)
    }
}

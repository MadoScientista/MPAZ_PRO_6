package com.ccstudio.mpaz_pro_6.data.mock

import com.ccstudio.mpaz_pro_6.data.enums.ActivityType
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.Activity
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateActivityRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateActivityRequest
import com.ccstudio.mpaz_pro_6.data.repository.ActivityRepository
import kotlinx.coroutines.sync.withLock

/**
 * Implementación en memoria de `ActivityRepository`.
 */
class MockActivityRepository(
    private val store: MockStore
) : ActivityRepository {

    override suspend fun list(unitId: Int?): Result<List<Activity>> =
        store.mutex.withLock {
            if (unitId != null && store.units.none { it.id == unitId }) {
                return Result.failure(ApiError.NotFound("No existe la unidad con id $unitId."))
            }

            val activities = store.activities
                .filter { unitId == null || it.unidadId == unitId }
                .sortedWith(compareBy({ it.unidadId }, { it.orden }))

            Result.success(activities)
        }

    override suspend fun getById(id: Int): Result<Activity> = store.mutex.withLock {
        val activity = store.activities.find { it.id == id }
        if (activity == null) {
            Result.failure(ApiError.NotFound("No existe la actividad con id $id."))
        } else {
            Result.success(activity)
        }
    }

    override suspend fun create(request: CreateActivityRequest): Result<Activity> =
        store.mutex.withLock {
            if (store.units.none { it.id == request.unidadId }) {
                return Result.failure(ApiError.NotFound("No existe la unidad con id ${request.unidadId}."))
            }

            if (request.titulo.isBlank()) {
                return Result.failure(ApiError.BadRequest("El título de la actividad es obligatorio."))
            }

            val tituloRepetido = store.activities.any { it.unidadId == request.unidadId && it.titulo.equals(request.titulo.trim(), ignoreCase = true) }
            if (tituloRepetido) {
                return Result.failure(ApiError.Conflict("Ya existe una actividad con ese título en la unidad."))
            }

            if (request.tipo == ActivityType.FINAL_TEST) {
                val existePrueba = store.activities.any { it.unidadId == request.unidadId && it.tipo == ActivityType.FINAL_TEST }
                if (existePrueba) {
                    return Result.failure(ApiError.Conflict("Ya existe una PRUEBA_FINAL en la unidad."))
                }
            }

            val orden = request.orden ?: 1
            if (orden <= 0) {
                return Result.failure(ApiError.BadRequest("El orden debe ser mayor que cero."))
            }

            val activity = Activity(
                id = store.nextActivityId++,
                unidadId = request.unidadId,
                titulo = request.titulo.trim(),
                tipo = request.tipo,
                orden = orden
            )

            store.activities.add(activity)
            Result.success(activity)
        }

    override suspend fun update(id: Int, request: UpdateActivityRequest): Result<Activity> =
        store.mutex.withLock {
            val activity = store.activities.find { it.id == id }
                ?: return Result.failure(ApiError.NotFound("No existe la actividad con id $id."))

            val sinCampos = request.titulo == null && request.tipo == null && request.orden == null
            if (sinCampos) {
                return Result.failure(ApiError.BadRequest("Debes enviar al menos un campo para actualizar."))
            }

            val titulo = request.titulo?.trim() ?: activity.titulo
            if (titulo.isBlank()) {
                return Result.failure(ApiError.BadRequest("El título de la actividad es obligatorio."))
            }

            val tituloRepetido = store.activities.any { it.id != id && it.unidadId == activity.unidadId && it.titulo.equals(titulo, ignoreCase = true) }
            if (tituloRepetido) {
                return Result.failure(ApiError.Conflict("Ya existe una actividad con ese título en la unidad."))
            }

            val tipo = request.tipo ?: activity.tipo
            if (tipo == ActivityType.FINAL_TEST && activity.tipo != ActivityType.FINAL_TEST) {
                val existePrueba = store.activities.any { it.unidadId == activity.unidadId && it.tipo == ActivityType.FINAL_TEST }
                if (existePrueba) {
                    return Result.failure(ApiError.Conflict("Ya existe una PRUEBA_FINAL en la unidad."))
                }
            }

            val orden = request.orden ?: activity.orden
            if (orden <= 0) {
                return Result.failure(ApiError.BadRequest("El orden debe ser mayor que cero."))
            }

            val updated = activity.copy(
                titulo = titulo,
                tipo = tipo,
                orden = orden
            )

            store.activities[store.activities.indexOf(activity)] = updated
            Result.success(updated)
        }

    override suspend fun delete(id: Int): Result<Unit> = store.mutex.withLock {
        val activity = store.activities.find { it.id == id }
            ?: return Result.failure(ApiError.NotFound("No existe la actividad con id $id."))

        store.activities.remove(activity)
        Result.success(Unit)
    }
}

package com.ccstudio.mpaz_pro_6.data.repository

import com.ccstudio.mpaz_pro_6.data.model.Activity
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateActivityRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateActivityRequest

/**
 * Repositorio de actividades (`/api/actividades`).
 *
 * Todas las operaciones son `suspend` y devuelven `Result`; el fallo es siempre
 * un `ApiError`.
 */
interface ActivityRepository {

    /**
     * Lista las actividades ordenadas por unidad y orden.
     *
     * @param unitId filtro opcional del backend (`?unidadId`).
     */
    suspend fun list(unitId: Int? = null): Result<List<Activity>>

    /** Obtiene una actividad por id. */
    suspend fun getById(id: Int): Result<Activity>

    /** Crea una actividad asociada a una unidad existente. */
    suspend fun create(request: CreateActivityRequest): Result<Activity>

    /** Actualiza título, tipo u orden. Solo se aplican los valores enviados. */
    suspend fun update(id: Int, request: UpdateActivityRequest): Result<Activity>

    /**
     * Elimina una actividad.
     *
     * Falla con `ApiError.Conflict` si tiene preguntas, asignaciones o intentos
     * asociados.
     */
    suspend fun delete(id: Int): Result<Unit>
}

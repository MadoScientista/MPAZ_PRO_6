package com.ccstudio.mpaz_pro_6.data.repository

import com.ccstudio.mpaz_pro_6.data.model.Attempt

/**
 * Repositorio de intentos (`/api/intentos`).
 *
 * Todas las operaciones son `suspend` y devuelven `Result`; el fallo es siempre
 * un `ApiError`.
 */
interface AttemptRepository {

    /**
     * Lista los intentos ordenados por estudiante, actividad y número.
     *
     * @param studentId filtro opcional del backend (`?estudianteId`).
     * @param activityId filtro opcional del backend (`?actividadId`).
     * Ambos filtros son combinables.
     */
    suspend fun list(
        studentId: Int? = null,
        activityId: Int? = null
    ): Result<List<Attempt>>

    /** Obtiene un intento por id. */
    suspend fun getById(id: Int): Result<Attempt>

    /**
     * Inicia un intento de un estudiante en una actividad.
     *
     * El backend fija la fecha de inicio y calcula el número correlativo del par
     * estudiante-actividad. Un intento en progreso es el que no tiene fecha de
     * fin.
     */
    suspend fun start(studentId: Int, activityId: Int): Result<Attempt>

    /** Marca el intento como completado, fijando la fecha de fin. */
    suspend fun complete(id: Int): Result<Attempt>
}

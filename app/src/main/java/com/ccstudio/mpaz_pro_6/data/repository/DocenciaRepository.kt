package com.ccstudio.mpaz_pro_6.data.repository

import com.ccstudio.mpaz_pro_6.data.model.Docencia
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateDocenciaRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateDocenciaRequest

/**
 * Repositorio de docencias (`/api/docencias`).
 *
 * Una docencia relaciona un docente con un curso y una asignatura. Es la fuente
 * de verdad para saber qué puede administrar cada docente y sustituye al antiguo
 * `docenteId` de las asignaciones.
 *
 * Todas las operaciones son `suspend` y devuelven `Result`; el fallo es siempre
 * un `ApiError`. La lectura se apoya en la caché local cuando el backend falla
 * por red o por un error desconocido.
 */
interface DocenciaRepository {

    /**
     * Lista las docencias.
     *
     * @param docenteId filtro opcional del backend (`?docenteId`). Un docente
     * solo puede consultar las propias; la UTP puede consultar todas.
     */
    suspend fun list(docenteId: Int? = null): Result<List<Docencia>>

    /** Obtiene una docencia por id. */
    suspend fun getById(id: Int): Result<Docencia>

    /** Crea una docencia. Solo la UTP puede hacerlo. */
    suspend fun create(request: CreateDocenciaRequest): Result<Docencia>

    /** Cambia el docente responsable. Solo la UTP puede hacerlo. */
    suspend fun update(id: Int, request: UpdateDocenciaRequest): Result<Docencia>

    /**
     * Elimina una docencia.
     *
     * Falla con `ApiError.Conflict` si tiene asignaciones asociadas para el
     * mismo curso y asignatura.
     */
    suspend fun delete(id: Int): Result<Unit>
}

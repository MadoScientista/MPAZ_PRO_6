package com.ccstudio.mpaz_pro_6.data.repository

import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateSubjectRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateSubjectRequest

/**
 * Repositorio de asignaturas (`/api/asignaturas`).
 *
 * Contrato estable con la UI: todas las operaciones son `suspend` y devuelven
 * `Result`. El éxito trae el dato y el fallo es siempre un `ApiError`, que
 * permite a la pantalla explicar el motivo (por ejemplo, `Conflict` cuando el
 * backend rechaza un borrado por unidades asociadas).
 */
interface SubjectRepository {

    /** Lista las asignaturas ordenadas por nombre. */
    suspend fun list(): Result<List<Subject>>

    /** Obtiene una asignatura por id. */
    suspend fun getById(id: Int): Result<Subject>

    /** Crea una asignatura. */
    suspend fun create(request: CreateSubjectRequest): Result<Subject>

    /** Actualiza el nombre de una asignatura existente. */
    suspend fun update(id: Int, request: UpdateSubjectRequest): Result<Subject>

    /** Elimina una asignatura. Falla con `ApiError.Conflict` si tiene unidades asociadas. */
    suspend fun delete(id: Int): Result<Unit>
}

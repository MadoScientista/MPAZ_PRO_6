package com.ccstudio.mpaz_pro_6.data.repository

import com.ccstudio.mpaz_pro_6.data.model.StudentAnswer
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateStudentAnswerRequest

/**
 * Repositorio de respuestas de estudiante (`/api/intentos/{intentoId}/respuestas`).
 *
 * Todas las operaciones son `suspend` y devuelven `Result`; el fallo es siempre
 * un `ApiError`.
 */
interface StudentAnswerRepository {

    /** Lista las respuestas registradas en el intento, ordenadas por id. */
    suspend fun list(attemptId: Int): Result<List<StudentAnswer>>

    /**
     * Registra la respuesta del estudiante a una pregunta del intento.
     *
     * El backend deriva `esCorrecta` de la alternativa seleccionada, por lo que
     * la app no lo recalcula.
     */
    suspend fun register(
        attemptId: Int,
        request: CreateStudentAnswerRequest
    ): Result<StudentAnswer>
}

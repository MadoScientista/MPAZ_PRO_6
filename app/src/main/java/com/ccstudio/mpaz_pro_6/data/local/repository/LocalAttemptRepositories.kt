package com.ccstudio.mpaz_pro_6.data.local.repository

import com.ccstudio.mpaz_pro_6.data.local.dao.AttemptDao
import com.ccstudio.mpaz_pro_6.data.local.dao.CurriculumDao
import com.ccstudio.mpaz_pro_6.data.local.dao.UserDao
import com.ccstudio.mpaz_pro_6.data.local.entity.AttemptEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.StudentAnswerEntity
import com.ccstudio.mpaz_pro_6.data.local.mapper.toDomain
import com.ccstudio.mpaz_pro_6.data.model.Attempt
import com.ccstudio.mpaz_pro_6.data.model.StudentAnswer
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateStudentAnswerRequest
import com.ccstudio.mpaz_pro_6.data.repository.AttemptRepository
import com.ccstudio.mpaz_pro_6.data.repository.StudentAnswerRepository
import java.time.Instant

/** Repositorio local de intentos. */
class LocalAttemptRepository(
    private val dao: AttemptDao,
    private val userDao: UserDao,
    private val curriculumDao: CurriculumDao
) : AttemptRepository {

    override suspend fun list(
        studentId: Int?,
        activityId: Int?
    ): Result<List<Attempt>> = localCall {
        dao.getAttempts()
            .filter { studentId == null || it.estudianteId == studentId }
            .filter { activityId == null || it.actividadId == activityId }
            .map { it.toDomain() }
    }

    override suspend fun getById(id: Int): Result<Attempt> = localCall {
        dao.getAttemptById(id).orNotFound("Intento").getOrThrow().toDomain()
    }

    override suspend fun start(studentId: Int, activityId: Int): Result<Attempt> =
        localCall {
            userDao.getUserById(studentId).orNotFound("Estudiante").getOrThrow()
            curriculumDao.getActivityById(activityId).orNotFound("Actividad").getOrThrow()
            val attempts = dao.getAttempts()
            val number = attempts
                .filter { it.estudianteId == studentId && it.actividadId == activityId }
                .maxOfOrNull { it.numero }
                ?.plus(1) ?: 1
            val entity = AttemptEntity(
                id = attempts.nextId { it.id },
                estudianteId = studentId,
                actividadId = activityId,
                numero = number,
                fechaInicio = Instant.now(),
                fechaFin = null
            )
            dao.upsertAttempt(entity)
            entity.toDomain()
        }

    override suspend fun complete(id: Int): Result<Attempt> = localCall {
        val current = dao.getAttemptById(id).orNotFound("Intento").getOrThrow()
        require(current.fechaFin == null) { "El intento ya está completado." }
        val updated = current.copy(fechaFin = Instant.now())
        dao.upsertAttempt(updated)
        updated.toDomain()
    }
}

/** Repositorio local de respuestas de estudiante. */
class LocalStudentAnswerRepository(
    private val dao: AttemptDao,
    private val curriculumDao: CurriculumDao
) : StudentAnswerRepository {

    override suspend fun list(attemptId: Int): Result<List<StudentAnswer>> = localCall {
        dao.getAttemptById(attemptId).orNotFound("Intento").getOrThrow()
        dao.getStudentAnswersByAttempt(attemptId).map { it.toDomain() }
    }

    override suspend fun register(
        attemptId: Int,
        request: CreateStudentAnswerRequest
    ): Result<StudentAnswer> = localCall {
        dao.getAttemptById(attemptId).orNotFound("Intento").getOrThrow()
        curriculumDao.getQuestionById(request.preguntaId)
            .orNotFound("Pregunta")
            .getOrThrow()
        val alternative = curriculumDao.getAlternativeById(request.alternativaId)
            .orNotFound("Alternativa")
            .getOrThrow()
        require(alternative.preguntaId == request.preguntaId) {
            "La alternativa no pertenece a la pregunta."
        }
        val existing = dao.getStudentAnswersByAttempt(attemptId)
        require(existing.none { it.preguntaId == request.preguntaId }) {
            "La pregunta ya fue respondida en este intento."
        }
        val entity = StudentAnswerEntity(
            id = dao.getStudentAnswers().nextId { it.id },
            intentoId = attemptId,
            preguntaId = request.preguntaId,
            alternativaId = request.alternativaId,
            esCorrecta = alternative.esCorrecta
        )
        dao.upsertStudentAnswer(entity)
        entity.toDomain()
    }
}

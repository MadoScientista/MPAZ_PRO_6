package com.ccstudio.mpaz_pro_6.data.local.repository

import com.ccstudio.mpaz_pro_6.data.enums.ActivityType
import com.ccstudio.mpaz_pro_6.data.enums.ContentStatus
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.local.dao.CurriculumDao
import com.ccstudio.mpaz_pro_6.data.local.dao.UnitDao
import com.ccstudio.mpaz_pro_6.data.local.entity.ActivityEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.AlternativeEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.ContentEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.ContentResourceEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.LearningObjectiveEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.QuestionEntity
import com.ccstudio.mpaz_pro_6.data.local.mapper.toDomain
import com.ccstudio.mpaz_pro_6.data.model.Activity
import com.ccstudio.mpaz_pro_6.data.model.Alternative
import com.ccstudio.mpaz_pro_6.data.model.Content
import com.ccstudio.mpaz_pro_6.data.model.ContentResource
import com.ccstudio.mpaz_pro_6.data.model.LearningObjective
import com.ccstudio.mpaz_pro_6.data.model.Question
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateActivityRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateAlternativeRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateContentRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateContentResourceRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateLearningObjectiveRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateQuestionRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateActivityRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateAlternativeRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateContentRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateContentResourceRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateLearningObjectiveRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateQuestionRequest
import com.ccstudio.mpaz_pro_6.data.repository.ActivityRepository
import com.ccstudio.mpaz_pro_6.data.repository.AlternativeRepository
import com.ccstudio.mpaz_pro_6.data.repository.ContentRepository
import com.ccstudio.mpaz_pro_6.data.repository.ContentResourceRepository
import com.ccstudio.mpaz_pro_6.data.repository.LearningObjectiveRepository
import com.ccstudio.mpaz_pro_6.data.repository.QuestionRepository

/** CRUD local de objetivos de aprendizaje sobre Room. */
class LocalLearningObjectiveRepository(
    private val dao: CurriculumDao,
    private val unitDao: UnitDao
) : LearningObjectiveRepository {

    override suspend fun list(unitId: Int?): Result<List<LearningObjective>> = localCall {
        val entities = if (unitId == null) dao.getObjectives() else dao.getObjectivesByUnit(unitId)
        entities.map { it.toDomain() }
    }

    override suspend fun getById(id: Int): Result<LearningObjective> = localCall {
        dao.getObjectiveById(id).orNotFound("Objetivo de aprendizaje").getOrThrow().toDomain()
    }

    override suspend fun create(
        request: CreateLearningObjectiveRequest
    ): Result<LearningObjective> = localCall {
        require(request.descripcion.isNotBlank()) { "La descripción es obligatoria." }
        require(request.eje.isNotBlank()) { "El eje es obligatorio." }
        unitDao.getUnitById(request.unidadId).orNotFound("Unidad").getOrThrow()
        val entities = dao.getObjectives()
        val entity = LearningObjectiveEntity(
            id = entities.nextId { it.id },
            unidadId = request.unidadId,
            codigo = request.codigo?.trim()?.takeIf(String::isNotBlank),
            descripcion = request.descripcion.trim(),
            eje = request.eje.trim(),
            textoReferencia = request.textoReferencia
        )
        dao.upsertObjective(entity)
        entity.toDomain()
    }

    override suspend fun update(
        id: Int,
        request: UpdateLearningObjectiveRequest
    ): Result<LearningObjective> = localCall {
        val current = dao.getObjectiveById(id)
            .orNotFound("Objetivo de aprendizaje")
            .getOrThrow()
        val updated = current.copy(
            codigo = request.codigo?.trim()?.takeIf(String::isNotBlank) ?: current.codigo,
            descripcion = request.descripcion?.trim() ?: current.descripcion,
            eje = request.eje?.trim() ?: current.eje,
            textoReferencia = request.textoReferencia ?: current.textoReferencia
        )
        require(updated.descripcion.isNotBlank()) { "La descripción es obligatoria." }
        require(updated.eje.isNotBlank()) { "El eje es obligatorio." }
        dao.upsertObjective(updated)
        updated.toDomain()
    }

    override suspend fun delete(id: Int): Result<Unit> = localCall {
        dao.getObjectiveById(id).orNotFound("Objetivo de aprendizaje").getOrThrow()
        dao.deleteObjectiveById(id)
    }
}

/** CRUD local de contenidos sobre Room. */
class LocalContentRepository(
    private val dao: CurriculumDao
) : ContentRepository {

    override suspend fun list(learningObjectiveId: Int?): Result<List<Content>> = localCall {
        val entities = if (learningObjectiveId == null) {
            dao.getContents()
        } else {
            dao.getContentsByObjective(learningObjectiveId)
        }
        entities.map { it.toDomain() }
    }

    override suspend fun getById(id: Int): Result<Content> = localCall {
        dao.getContentById(id).orNotFound("Contenido").getOrThrow().toDomain()
    }

    override suspend fun create(request: CreateContentRequest): Result<Content> = localCall {
        require(request.titulo.isNotBlank()) { "El título es obligatorio." }
        require(request.explicacion.isNotBlank()) { "La explicación es obligatoria." }
        val objective = dao.getObjectiveById(request.oaId)
            .orNotFound("Objetivo de aprendizaje")
            .getOrThrow()
        val entities = dao.getContents()
        val entity = ContentEntity(
            id = entities.nextId { it.id },
            oaId = request.oaId,
            unidadId = objective.unidadId,
            titulo = request.titulo.trim(),
            explicacion = request.explicacion,
            ejemplos = request.ejemplos,
            instrucciones = request.instrucciones,
            orden = request.orden ?: 1,
            origen = request.origen,
            estado = ContentStatus.DRAFT
        )
        dao.upsertContent(entity)
        entity.toDomain()
    }

    override suspend fun update(id: Int, request: UpdateContentRequest): Result<Content> =
        localCall {
            val current = dao.getContentById(id).orNotFound("Contenido").getOrThrow()
            val updated = current.copy(
                titulo = request.titulo?.trim() ?: current.titulo,
                explicacion = request.explicacion ?: current.explicacion,
                ejemplos = request.ejemplos ?: current.ejemplos,
                instrucciones = request.instrucciones ?: current.instrucciones,
                orden = request.orden ?: current.orden,
                origen = request.origen ?: current.origen
            )
            require(updated.titulo.isNotBlank()) { "El título es obligatorio." }
            require(updated.explicacion.isNotBlank()) { "La explicación es obligatoria." }
            dao.upsertContent(updated)
            updated.toDomain()
        }

    override suspend fun delete(id: Int): Result<Unit> = localCall {
        dao.getContentById(id).orNotFound("Contenido").getOrThrow()
        if (dao.countActivitiesByContent(id) > 0) {
            throw ApiError.Conflict("No se puede eliminar: tiene actividades asociadas.")
        }
        dao.deleteContentCascade(id)
    }
}

/** CRUD local de recursos de contenido sobre Room. */
class LocalContentResourceRepository(
    private val dao: CurriculumDao
) : ContentResourceRepository {

    override suspend fun list(contentId: Int?): Result<List<ContentResource>> = localCall {
        val entities = if (contentId == null) {
            dao.getContentResources()
        } else {
            dao.getContentResourcesByContent(contentId)
        }
        entities.map { it.toDomain() }
    }

    override suspend fun getById(id: Int): Result<ContentResource> = localCall {
        dao.getContentResourceById(id).orNotFound("Recurso").getOrThrow().toDomain()
    }

    override suspend fun create(
        request: CreateContentResourceRequest
    ): Result<ContentResource> = localCall {
        require(request.url.isNotBlank()) { "La URL es obligatoria." }
        dao.getContentById(request.contenidoId).orNotFound("Contenido").getOrThrow()
        val entities = dao.getContentResources()
        val entity = ContentResourceEntity(
            id = entities.nextId { it.id },
            contenidoId = request.contenidoId,
            tipo = request.tipo,
            url = request.url.trim(),
            descripcion = request.descripcion,
            origen = request.origen,
            estado = ContentStatus.DRAFT
        )
        dao.upsertContentResource(entity)
        entity.toDomain()
    }

    override suspend fun update(
        id: Int,
        request: UpdateContentResourceRequest
    ): Result<ContentResource> = localCall {
        val current = dao.getContentResourceById(id).orNotFound("Recurso").getOrThrow()
        val updated = current.copy(
            tipo = request.tipo ?: current.tipo,
            url = request.url?.trim() ?: current.url,
            descripcion = request.descripcion ?: current.descripcion,
            origen = request.origen ?: current.origen
        )
        require(updated.url.isNotBlank()) { "La URL es obligatoria." }
        dao.upsertContentResource(updated)
        updated.toDomain()
    }

    override suspend fun delete(id: Int): Result<Unit> = localCall {
        dao.getContentResourceById(id).orNotFound("Recurso").getOrThrow()
        dao.deleteContentResourceById(id)
    }
}

/** CRUD local de actividades sobre Room. */
class LocalActivityRepository(
    private val dao: CurriculumDao,
    private val unitDao: UnitDao
) : ActivityRepository {

    override suspend fun list(
        unitId: Int?,
        contentId: Int?,
        onlyTransversal: Boolean
    ): Result<List<Activity>> = localCall {
        require(contentId == null || !onlyTransversal) {
            "No se puede filtrar por contenido y por actividades transversales a la vez."
        }
        require(unitId != null || (contentId == null && !onlyTransversal)) {
            "Los filtros por contenido y por transversales requieren la unidad."
        }
        val entities = when {
            contentId != null -> {
                val targetUnitId = requireNotNull(unitId)
                unitDao.getUnitById(targetUnitId).orNotFound("Unidad").getOrThrow()
                val content = dao.getContentById(contentId)
                    .orNotFound("Contenido")
                    .getOrThrow()
                if (content.unidadId != targetUnitId) {
                    throw ApiError.Conflict("El contenido pertenece a otra unidad.")
                }
                dao.getActivitiesByUnitAndContent(targetUnitId, contentId)
            }

            onlyTransversal -> {
                val targetUnitId = requireNotNull(unitId)
                unitDao.getUnitById(targetUnitId).orNotFound("Unidad").getOrThrow()
                dao.getTransversalActivitiesByUnit(targetUnitId)
            }

            unitId != null -> {
                unitDao.getUnitById(unitId).orNotFound("Unidad").getOrThrow()
                dao.getActivitiesByUnit(unitId)
            }

            else -> dao.getActivities()
        }
        entities.map { it.toDomain() }
    }

    override suspend fun getById(id: Int): Result<Activity> = localCall {
        dao.getActivityById(id).orNotFound("Actividad").getOrThrow().toDomain()
    }

    override suspend fun create(request: CreateActivityRequest): Result<Activity> = localCall {
        require(request.titulo.isNotBlank()) { "El título es obligatorio." }
        unitDao.getUnitById(request.unidadId).orNotFound("Unidad").getOrThrow()
        if (request.contenidoId != null) {
            val content = dao.getContentById(request.contenidoId)
                .orNotFound("Contenido")
                .getOrThrow()
            if (content.unidadId != request.unidadId) {
                throw ApiError.Conflict("El contenido pertenece a otra unidad.")
            }
        }
        if (request.tipo == ActivityType.FINAL_TEST) {
            val hasFinalTest = dao.getActivitiesByUnit(request.unidadId)
                .any { it.tipo == ActivityType.FINAL_TEST }
            require(!hasFinalTest) { "La unidad ya tiene una prueba final." }
        }
        val entities = dao.getActivities()
        val entity = ActivityEntity(
            id = entities.nextId { it.id },
            unidadId = request.unidadId,
            contenidoId = request.contenidoId,
            titulo = request.titulo.trim(),
            tipo = request.tipo,
            orden = request.orden ?: 1
        )
        dao.upsertActivity(entity)
        entity.toDomain()
    }

    override suspend fun update(id: Int, request: UpdateActivityRequest): Result<Activity> =
        localCall {
            val current = dao.getActivityById(id).orNotFound("Actividad").getOrThrow()
            if (request.contenidoId != null && request.desasociarContenido == true) {
                throw ApiError.BadRequest(
                    "No se puede asociar y desasociar un contenido en la misma solicitud."
                )
            }
            val nextContentId = when {
                request.desasociarContenido == true -> null
                request.contenidoId != null -> {
                    val content = dao.getContentById(request.contenidoId)
                        .orNotFound("Contenido")
                        .getOrThrow()
                    if (content.unidadId != current.unidadId) {
                        throw ApiError.Conflict("El contenido pertenece a otra unidad.")
                    }
                    request.contenidoId
                }

                else -> current.contenidoId
            }
            val nextType = request.tipo ?: current.tipo
            if (nextType == ActivityType.FINAL_TEST && current.tipo != ActivityType.FINAL_TEST) {
                val hasFinalTest = dao.getActivitiesByUnit(current.unidadId)
                    .any { it.tipo == ActivityType.FINAL_TEST && it.id != id }
                require(!hasFinalTest) { "La unidad ya tiene una prueba final." }
            }
            val updated = current.copy(
                titulo = request.titulo?.trim() ?: current.titulo,
                tipo = nextType,
                orden = request.orden ?: current.orden,
                contenidoId = nextContentId
            )
            require(updated.titulo.isNotBlank()) { "El título es obligatorio." }
            dao.upsertActivity(updated)
            updated.toDomain()
        }

    override suspend fun delete(id: Int): Result<Unit> = localCall {
        dao.getActivityById(id).orNotFound("Actividad").getOrThrow()
        dao.deleteActivityById(id)
    }
}

/** CRUD local de preguntas sobre Room. */
class LocalQuestionRepository(
    private val dao: CurriculumDao
) : QuestionRepository {

    override suspend fun list(
        activityId: Int?,
        learningObjectiveId: Int?
    ): Result<List<Question>> = localCall {
        val entities = when {
            activityId != null -> dao.getQuestionsByActivity(activityId)
            learningObjectiveId != null -> dao.getQuestionsByObjective(learningObjectiveId)
            else -> dao.getQuestions()
        }
        entities
            .filter { activityId == null || it.actividadId == activityId }
            .filter { learningObjectiveId == null || it.oaId == learningObjectiveId }
            .map { it.toDomain() }
    }

    override suspend fun getById(id: Int): Result<Question> = localCall {
        dao.getQuestionById(id).orNotFound("Pregunta").getOrThrow().toDomain()
    }

    override suspend fun create(request: CreateQuestionRequest): Result<Question> = localCall {
        require(request.enunciado.isNotBlank()) { "El enunciado es obligatorio." }
        val activity = dao.getActivityById(request.actividadId)
            .orNotFound("Actividad")
            .getOrThrow()
        val objective = dao.getObjectiveById(request.oaId)
            .orNotFound("Objetivo de aprendizaje")
            .getOrThrow()
        require(activity.unidadId == request.unidadId && objective.unidadId == request.unidadId) {
            "La actividad, la unidad y el objetivo deben pertenecer a la misma unidad."
        }
        val entities = dao.getQuestions()
        val entity = QuestionEntity(
            id = entities.nextId { it.id },
            actividadId = request.actividadId,
            unidadId = request.unidadId,
            oaId = request.oaId,
            enunciado = request.enunciado.trim(),
            criterioRespuesta = request.criterioRespuesta,
            dificultad = request.dificultad,
            orden = request.orden ?: 1
        )
        dao.upsertQuestion(entity)
        entity.toDomain()
    }

    override suspend fun update(id: Int, request: UpdateQuestionRequest): Result<Question> =
        localCall {
            val current = dao.getQuestionById(id).orNotFound("Pregunta").getOrThrow()
            val updated = current.copy(
                enunciado = request.enunciado?.trim() ?: current.enunciado,
                criterioRespuesta = request.criterioRespuesta ?: current.criterioRespuesta,
                dificultad = request.dificultad ?: current.dificultad,
                orden = request.orden ?: current.orden
            )
            require(updated.enunciado.isNotBlank()) { "El enunciado es obligatorio." }
            dao.upsertQuestion(updated)
            updated.toDomain()
        }

    override suspend fun delete(id: Int): Result<Unit> = localCall {
        dao.getQuestionById(id).orNotFound("Pregunta").getOrThrow()
        dao.deleteQuestionById(id)
    }
}

/** CRUD local de alternativas sobre Room. */
class LocalAlternativeRepository(
    private val dao: CurriculumDao
) : AlternativeRepository {

    override suspend fun list(questionId: Int?): Result<List<Alternative>> = localCall {
        val entities = if (questionId == null) {
            dao.getAlternatives()
        } else {
            dao.getAlternativesByQuestion(questionId)
        }
        entities.map { it.toDomain() }
    }

    override suspend fun getById(id: Int): Result<Alternative> = localCall {
        dao.getAlternativeById(id).orNotFound("Alternativa").getOrThrow().toDomain()
    }

    override suspend fun create(request: CreateAlternativeRequest): Result<Alternative> =
        localCall {
            require(request.texto.isNotBlank()) { "El texto es obligatorio." }
            dao.getQuestionById(request.preguntaId).orNotFound("Pregunta").getOrThrow()
            val correct = request.esCorrecta ?: false
            if (correct) {
                ensureNoOtherCorrect(request.preguntaId)
            }
            val entities = dao.getAlternatives()
            val entity = AlternativeEntity(
                id = entities.nextId { it.id },
                preguntaId = request.preguntaId,
                texto = request.texto.trim(),
                esCorrecta = correct
            )
            dao.upsertAlternative(entity)
            entity.toDomain()
        }

    override suspend fun update(
        id: Int,
        request: UpdateAlternativeRequest
    ): Result<Alternative> = localCall {
        val current = dao.getAlternativeById(id).orNotFound("Alternativa").getOrThrow()
        val nextCorrect = request.esCorrecta ?: current.esCorrecta
        if (nextCorrect && !current.esCorrecta) {
            ensureNoOtherCorrect(current.preguntaId, exceptId = id)
        }
        val updated = current.copy(
            texto = request.texto?.trim() ?: current.texto,
            esCorrecta = nextCorrect
        )
        require(updated.texto.isNotBlank()) { "El texto es obligatorio." }
        dao.upsertAlternative(updated)
        updated.toDomain()
    }

    override suspend fun delete(id: Int): Result<Unit> = localCall {
        dao.getAlternativeById(id).orNotFound("Alternativa").getOrThrow()
        dao.deleteAlternativeById(id)
    }

    private suspend fun ensureNoOtherCorrect(questionId: Int, exceptId: Int? = null) {
        val hasCorrect = dao.getAlternativesByQuestion(questionId)
            .any { it.esCorrecta && it.id != exceptId }
        require(!hasCorrect) { "La pregunta ya tiene una alternativa correcta." }
    }
}

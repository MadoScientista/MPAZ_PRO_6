package com.ccstudio.mpaz_pro_6.data.local.repository

import com.ccstudio.mpaz_pro_6.data.enums.ContentStatus
import com.ccstudio.mpaz_pro_6.data.enums.UserRole
import com.ccstudio.mpaz_pro_6.data.local.dao.CatalogDao
import com.ccstudio.mpaz_pro_6.data.local.dao.UnitDao
import com.ccstudio.mpaz_pro_6.data.local.dao.UserDao
import com.ccstudio.mpaz_pro_6.data.local.entity.LearningUnitEntity
import com.ccstudio.mpaz_pro_6.data.local.mapper.toDomain
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateLearningUnitRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateLearningUnitRequest
import com.ccstudio.mpaz_pro_6.data.repository.UnitRepository

/** CRUD local de unidades de aprendizaje sobre Room. */
class LocalUnitRepository(
    private val dao: UnitDao,
    private val catalogDao: CatalogDao,
    private val userDao: UserDao
) : UnitRepository {

    override suspend fun list(subjectId: Int?): Result<List<LearningUnit>> = localCall {
        val entities = if (subjectId == null) dao.getUnits() else dao.getUnitsBySubject(subjectId)
        entities.map { it.toDomain() }
    }

    override suspend fun getById(id: Int): Result<LearningUnit> = localCall {
        dao.getUnitById(id).orNotFound("Unidad").getOrThrow().toDomain()
    }

    override suspend fun create(request: CreateLearningUnitRequest): Result<LearningUnit> =
        localCall {
            require(request.titulo.isNotBlank()) { "El título es obligatorio." }
            require(request.estado == ContentStatus.DRAFT || request.estado == ContentStatus.IN_REVIEW) {
                "La unidad solo puede crearse en borrador o en revisión."
            }
            catalogDao.getSubjectById(request.asignaturaId)
                .orNotFound("Asignatura")
                .getOrThrow()
            val entities = dao.getUnits()
            val entity = LearningUnitEntity(
                id = entities.nextId { it.id },
                asignaturaId = request.asignaturaId,
                nivelEducativo = request.nivelEducativo?.trim()
                    ?.takeIf(String::isNotBlank) ?: "Sexto Basico",
                titulo = request.titulo.trim(),
                orden = request.orden ?: 1,
                estado = request.estado,
                autorizadaPorId = null,
                fechaAutorizacion = null
            )
            dao.upsertUnit(entity)
            entity.toDomain()
        }

    override suspend fun update(
        id: Int,
        request: UpdateLearningUnitRequest
    ): Result<LearningUnit> = localCall {
        val current = dao.getUnitById(id).orNotFound("Unidad").getOrThrow()
        val nextState = request.estado ?: current.estado
        val nextAuthorizer = request.autorizadaPorId ?: current.autorizadaPorId
        val nextAuthorizationDate = request.fechaAutorizacion ?: current.fechaAutorizacion

        if (nextState == ContentStatus.AUTHORIZED || nextState == ContentStatus.PUBLISHED) {
            val authorizerId = nextAuthorizer
                ?: throw IllegalArgumentException("La autorización requiere un usuario UTP.")
            val authorizer = userDao.getUserById(authorizerId)
                .orNotFound("Usuario autorizante")
                .getOrThrow()
            require(authorizer.rol == UserRole.UTP) {
                "El usuario autorizante debe tener rol UTP."
            }
            require(nextAuthorizationDate != null) {
                "La autorización requiere una fecha."
            }
        }

        val updated = current.copy(
            nivelEducativo = request.nivelEducativo?.trim() ?: current.nivelEducativo,
            titulo = request.titulo?.trim() ?: current.titulo,
            orden = request.orden ?: current.orden,
            estado = nextState,
            autorizadaPorId = nextAuthorizer,
            fechaAutorizacion = nextAuthorizationDate
        )
        require(updated.titulo.isNotBlank()) { "El título es obligatorio." }
        dao.upsertUnit(updated)
        updated.toDomain()
    }

    override suspend fun delete(id: Int): Result<Unit> = localCall {
        dao.getUnitById(id).orNotFound("Unidad").getOrThrow()
        dao.deleteUnitById(id)
    }
}

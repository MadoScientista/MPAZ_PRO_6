package com.ccstudio.mpaz_pro_6.data.local.repository

import com.ccstudio.mpaz_pro_6.data.enums.UserRole
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.local.dao.CatalogDao
import com.ccstudio.mpaz_pro_6.data.local.dao.DocenciaDao
import com.ccstudio.mpaz_pro_6.data.local.dao.UserDao
import com.ccstudio.mpaz_pro_6.data.local.entity.DocenciaEntity
import com.ccstudio.mpaz_pro_6.data.local.mapper.toDomain
import com.ccstudio.mpaz_pro_6.data.model.Docencia
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateDocenciaRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateDocenciaRequest
import com.ccstudio.mpaz_pro_6.data.repository.DocenciaRepository

/**
 * CRUD local de docencias sobre Room.
 *
 * Reproduce las reglas del backend para el modo sin conexión: el docente debe
 * existir y tener rol `DOCENTE`, el curso y la asignatura deben existir, el par
 * curso-asignatura es único y una docencia con asignaciones asociadas no se puede
 * eliminar.
 */
class LocalDocenciaRepository(
    private val dao: DocenciaDao,
    private val userDao: UserDao,
    private val catalogDao: CatalogDao
) : DocenciaRepository {

    override suspend fun list(docenteId: Int?): Result<List<Docencia>> = localCall {
        dao.getDocencias()
            .filter { docenteId == null || it.docenteId == docenteId }
            .map { it.toDomain() }
    }

    override suspend fun getById(id: Int): Result<Docencia> = localCall {
        dao.getDocenciaById(id)
            ?.toDomain()
            ?: throw ApiError.NotFound("Docencia no encontrada.")
    }

    override suspend fun create(request: CreateDocenciaRequest): Result<Docencia> =
        localCall {
            validarDocente(request.docenteId)
            if (catalogDao.getCourseById(request.cursoId) == null) {
                throw ApiError.NotFound("Curso no encontrado.")
            }
            if (catalogDao.getSubjectById(request.asignaturaId) == null) {
                throw ApiError.NotFound("Asignatura no encontrada.")
            }

            val existing = dao.getDocencias()
            if (existing.any {
                    it.cursoId == request.cursoId &&
                        it.asignaturaId == request.asignaturaId
                }
            ) {
                throw ApiError.Conflict(
                    "Ya existe un docente para ese curso y asignatura."
                )
            }

            val entity = DocenciaEntity(
                id = existing.nextId { it.id },
                docenteId = request.docenteId,
                cursoId = request.cursoId,
                asignaturaId = request.asignaturaId
            )
            dao.upsertDocencia(entity)
            dao.getDocenciaById(entity.id)
                ?.toDomain()
                ?: throw ApiError.Unknown("No se pudo leer la docencia creada.")
        }

    override suspend fun update(
        id: Int,
        request: UpdateDocenciaRequest
    ): Result<Docencia> = localCall {
        val existing = dao.getDocenciaById(id)
            ?: throw ApiError.NotFound("Docencia no encontrada.")
        validarDocente(request.docenteId)

        val current = DocenciaEntity(
            id = existing.id,
            docenteId = request.docenteId,
            cursoId = existing.cursoId,
            asignaturaId = existing.asignaturaId
        )

        dao.upsertDocencia(current)
        dao.getDocenciaById(id)
            ?.toDomain()
            ?: throw ApiError.Unknown("No se pudo leer la docencia actualizada.")
    }

    override suspend fun delete(id: Int): Result<Unit> = localCall {
        val current = dao.getDocenciaById(id)
            ?: throw ApiError.NotFound("Docencia no encontrada.")

        if (dao.countAssignments(current.cursoId, current.asignaturaId) > 0) {
            throw ApiError.Conflict(
                "No se puede eliminar una docencia con asignaciones asociadas."
            )
        }

        dao.deleteDocenciaById(id)
    }

    /** Valida que el usuario exista y tenga rol `DOCENTE`. */
    private suspend fun validarDocente(docenteId: Int) {
        val docente = userDao.getUserById(docenteId)
            ?: throw ApiError.NotFound("Docente no encontrado.")
        if (docente.rol != UserRole.DOCENTE) {
            throw ApiError.BadRequest("El usuario debe tener rol DOCENTE.")
        }
    }
}

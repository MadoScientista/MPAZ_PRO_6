package com.ccstudio.mpaz_pro_6.data.mock

import com.ccstudio.mpaz_pro_6.data.enums.ContentStatus
import com.ccstudio.mpaz_pro_6.data.enums.UserRole
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateLearningUnitRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateLearningUnitRequest
import com.ccstudio.mpaz_pro_6.data.repository.UnitRepository
import kotlinx.coroutines.sync.withLock

/**
 * Implementación en memoria de `UnitRepository`.
 *
 * Reproduce las reglas del backend: el título es obligatorio y admite hasta 200
 * caracteres, el nivel hasta 50 y el orden debe ser mayor que cero; la creación
 * solo admite los estados iniciales; `AUTHORIZED` y `PUBLISHED` exigen un
 * autorizante con rol `UTP` y una fecha de autorización; y eliminar una unidad
 * con elementos asociados devuelve `ApiError.Conflict`.
 */
class MockUnitRepository(
    private val store: MockStore
) : UnitRepository {

    override suspend fun list(subjectId: Int?): Result<List<LearningUnit>> =
        store.mutex.withLock {
            if (subjectId != null && store.subjects.none { it.id == subjectId }) {
                return Result.failure(
                    ApiError.NotFound("No existe la asignatura con id $subjectId.")
                )
            }

            val units = store.units
                .filter { subjectId == null || it.asignaturaId == subjectId }
                .sortedWith(compareBy({ it.asignaturaId }, { it.orden }))

            Result.success(units)
        }

    override suspend fun getById(id: Int): Result<LearningUnit> = store.mutex.withLock {
        val unit = store.units.find { it.id == id }

        if (unit == null) {
            Result.failure(ApiError.NotFound("No existe la unidad con id $id."))
        } else {
            Result.success(unit)
        }
    }

    override suspend fun create(request: CreateLearningUnitRequest): Result<LearningUnit> =
        store.mutex.withLock {
            if (store.subjects.none { it.id == request.asignaturaId }) {
                return Result.failure(
                    ApiError.NotFound("No existe la asignatura con id ${request.asignaturaId}.")
                )
            }

            val titulo = request.titulo.trim()

            if (titulo.isEmpty()) {
                return Result.failure(ApiError.BadRequest("El título de la unidad es obligatorio."))
            }

            if (titulo.length > 200) {
                return Result.failure(
                    ApiError.BadRequest("El título admite hasta 200 caracteres.")
                )
            }

            val nivel = request.nivelEducativo?.trim()?.takeIf { it.isNotEmpty() } ?: "Sexto Basico"

            if (nivel.length > 50) {
                return Result.failure(
                    ApiError.BadRequest("El nivel educativo admite hasta 50 caracteres.")
                )
            }

            val orden = request.orden ?: 1

            if (orden <= 0) {
                return Result.failure(ApiError.BadRequest("El orden debe ser mayor que cero."))
            }

            if (request.estado !in ESTADOS_INICIALES) {
                return Result.failure(
                    ApiError.BadRequest(
                        "Una unidad solo puede crearse en BORRADOR o EN_REVISION."
                    )
                )
            }

            val unit = LearningUnit(
                id = store.nextUnitId++,
                asignaturaId = request.asignaturaId,
                nivelEducativo = nivel,
                titulo = titulo,
                orden = orden,
                estado = request.estado,
                autorizadaPorId = null,
                fechaAutorizacion = null
            )

            store.units.add(unit)

            Result.success(unit)
        }

    override suspend fun update(
        id: Int,
        request: UpdateLearningUnitRequest
    ): Result<LearningUnit> = store.mutex.withLock {
        val unit = store.units.find { it.id == id }

        if (unit == null) {
            return Result.failure(ApiError.NotFound("No existe la unidad con id $id."))
        }

        val sinCampos =
            request.nivelEducativo == null &&
                    request.titulo == null &&
                    request.orden == null &&
                    request.estado == null &&
                    request.autorizadaPorId == null &&
                    request.fechaAutorizacion == null

        if (sinCampos) {
            return Result.failure(
                ApiError.BadRequest("Debes enviar al menos un campo para actualizar.")
            )
        }

        val titulo = request.titulo?.trim() ?: unit.titulo

        if (titulo.isEmpty()) {
            return Result.failure(ApiError.BadRequest("El título de la unidad es obligatorio."))
        }

        if (titulo.length > 200) {
            return Result.failure(
                ApiError.BadRequest("El título admite hasta 200 caracteres.")
            )
        }

        val nivel = request.nivelEducativo?.trim()?.takeIf { it.isNotEmpty() } ?: unit.nivelEducativo

        if (nivel.length > 50) {
            return Result.failure(
                ApiError.BadRequest("El nivel educativo admite hasta 50 caracteres.")
            )
        }

        val orden = request.orden ?: unit.orden

        if (orden <= 0) {
            return Result.failure(ApiError.BadRequest("El orden debe ser mayor que cero."))
        }

        val estado = request.estado ?: unit.estado

        // Autorizar o publicar exige un autorizante con rol UTP y una fecha.
        if (estado in ESTADOS_AUTORIZADOS) {
            val autorizanteId = request.autorizadaPorId ?: unit.autorizadaPorId

            if (autorizanteId == null) {
                return Result.failure(
                    ApiError.BadRequest(
                        "Para ${estado.wireValue} la unidad necesita un autorizante."
                    )
                )
            }

            val autorizante = store.users.find { it.id == autorizanteId }

            if (autorizante == null) {
                return Result.failure(
                    ApiError.NotFound("No existe el usuario con id $autorizanteId.")
                )
            }

            if (autorizante.rol != UserRole.UTP) {
                return Result.failure(
                    ApiError.BadRequest("Solo un usuario con rol UTP puede autorizar la unidad.")
                )
            }

            val fecha = request.fechaAutorizacion ?: unit.fechaAutorizacion

            if (fecha == null) {
                return Result.failure(
                    ApiError.BadRequest(
                        "Para ${estado.wireValue} la unidad necesita una fecha de autorización."
                    )
                )
            }

            val updated = unit.copy(
                nivelEducativo = nivel,
                titulo = titulo,
                orden = orden,
                estado = estado,
                autorizadaPorId = autorizanteId,
                fechaAutorizacion = fecha
            )

            store.units[store.units.indexOf(unit)] = updated

            return Result.success(updated)
        }

        val updated = unit.copy(
            nivelEducativo = nivel,
            titulo = titulo,
            orden = orden,
            estado = estado
        )

        store.units[store.units.indexOf(unit)] = updated

        Result.success(updated)
    }

    override suspend fun delete(id: Int): Result<Unit> = store.mutex.withLock {
        val unit = store.units.find { it.id == id }

        if (unit == null) {
            return Result.failure(ApiError.NotFound("No existe la unidad con id $id."))
        }

        // Objetivos, actividades e informes todavía no tienen repositorio mock:
        // la comprobación se completa cuando existan.
        val asignaciones = store.assignments.count { it.unidadId == id }

        if (asignaciones > 0) {
            return Result.failure(
                ApiError.Conflict(
                    "No se puede eliminar la unidad: tiene $asignaciones asignación(es) " +
                        "asociada(s)."
                )
            )
        }

        store.units.remove(unit)

        Result.success(Unit)
    }

    companion object {

        /** Estados admitidos al crear una unidad. */
        private val ESTADOS_INICIALES = setOf(ContentStatus.DRAFT, ContentStatus.IN_REVIEW)

        /** Estados que exigen autorizante y fecha de autorización. */
        private val ESTADOS_AUTORIZADOS = setOf(ContentStatus.AUTHORIZED, ContentStatus.PUBLISHED)
    }
}

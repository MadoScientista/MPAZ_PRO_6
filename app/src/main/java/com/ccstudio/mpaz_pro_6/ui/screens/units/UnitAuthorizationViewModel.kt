package com.ccstudio.mpaz_pro_6.ui.screens.units

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ccstudio.mpaz_pro_6.data.enums.ContentStatus
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateLearningUnitRequest
import com.ccstudio.mpaz_pro_6.data.repository.SubjectRepository
import com.ccstudio.mpaz_pro_6.data.repository.UnitRepository
import kotlinx.coroutines.launch
import java.time.Instant

/**
 * Estado de la pantalla de autorización y publicación de una unidad.
 *
 * Permite pasar una unidad de `BORRADOR` o `EN_REVISION` a `AUTORIZADO`, de
 * `AUTORIZADO` a `PUBLISHED`, o devolverla a corrección (`BORRADOR`).
 * El backend exige un autorizante con rol `UTP` y una fecha de autorización
 * para `AUTORIZADO` y `PUBLISHED`.
 */
class UnitAuthorizationViewModel(
    private val unitRepository: UnitRepository,
    private val subjectRepository: SubjectRepository,
    private val unitId: Int
) : ViewModel() {

    /** Unidad que se está autorizando. */
    var unidad by mutableStateOf<LearningUnit?>(null)
        private set

    /** Asignatura a la que pertenece la unidad. */
    var asignatura by mutableStateOf<Subject?>(null)
        private set

    /** Indica que la pantalla se está cargando o guardando. */
    var isLoading by mutableStateOf(false)
        private set

    /** Error de la última operación, si lo hubo. */
    var error by mutableStateOf<ApiError?>(null)
        private set

    /** Mensaje de confirmación tras una operación correcta. */
    var mensaje by mutableStateOf<String?>(null)
        private set

    /** Carga la unidad que se va a autorizar. */
    fun cargar() {
        viewModelScope.launch {
            isLoading = true
            error = null

            val result = unitRepository.getById(unitId)

            if (result.isFailure) {
                error = result.exceptionOrNull() as? ApiError
            } else {
                val unit = result.getOrThrow()
                unidad = unit

                subjectRepository.getById(unit.asignaturaId).onSuccess { asignatura = it }
            }

            isLoading = false
        }
    }

    /**
     * Autoriza o publica la unidad.
     *
     * @param autorizanteId usuario con rol `UTP` que autoriza.
     */
    fun autorizar(autorizanteId: Int, onSuccess: () -> Unit = {}) {
        val unit = unidad ?: return

        val nuevoEstado = when (unit.estado) {
            ContentStatus.DRAFT -> ContentStatus.AUTHORIZED
            ContentStatus.IN_REVIEW -> ContentStatus.AUTHORIZED
            ContentStatus.AUTHORIZED -> ContentStatus.PUBLISHED
            ContentStatus.PUBLISHED -> null
        }

        if (nuevoEstado == null) {
            error = ApiError.BadRequest("La unidad ya está publicada.")
            return
        }

        viewModelScope.launch {
            isLoading = true
            error = null
            mensaje = null

            val result = unitRepository.update(
                id = unit.id,
                request = UpdateLearningUnitRequest(
                    estado = nuevoEstado,
                    autorizadaPorId = autorizanteId,
                    fechaAutorizacion = Instant.now()
                )
            )

            if (result.isFailure) {
                error = result.exceptionOrNull() as? ApiError
                isLoading = false
            } else {
                unidad = result.getOrThrow()
                mensaje = if (nuevoEstado == ContentStatus.PUBLISHED) {
                    "Unidad publicada."
                } else {
                    "Unidad autorizada."
                }
                isLoading = false
                onSuccess()
            }
        }
    }

    /**
     * Rechaza la unidad y la devuelve a `BORRADOR` para corrección.
     */
    fun rechazar(onSuccess: () -> Unit = {}) {
        val unit = unidad ?: return

        if (unit.estado == ContentStatus.PUBLISHED) {
            error = ApiError.BadRequest("No se puede rechazar una unidad publicada.")
            return
        }

        if (unit.estado == ContentStatus.DRAFT) {
            error = ApiError.BadRequest("La unidad ya está en borrador.")
            return
        }

        viewModelScope.launch {
            isLoading = true
            error = null
            mensaje = null

            val result = unitRepository.update(
                id = unit.id,
                request = UpdateLearningUnitRequest(
                    estado = ContentStatus.DRAFT
                )
            )

            if (result.isFailure) {
                error = result.exceptionOrNull() as? ApiError
                isLoading = false
            } else {
                unidad = result.getOrThrow()
                mensaje = "Unidad devuelta a borrador."
                isLoading = false
                onSuccess()
            }
        }
    }

    companion object {

        /** Factoría del ViewModel con los repositorios y el id de la unidad. */
        fun factory(
            unitRepository: UnitRepository,
            subjectRepository: SubjectRepository,
            unitId: Int
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                UnitAuthorizationViewModel(unitRepository, subjectRepository, unitId)
            }
        }
    }
}

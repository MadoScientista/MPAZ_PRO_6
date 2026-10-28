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
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateLearningUnitRequest
import com.ccstudio.mpaz_pro_6.data.repository.UnitRepository
import kotlinx.coroutines.launch
import java.time.Instant

/**
 * Estado de la pantalla de autorización y publicación de una unidad.
 *
 * Permite pasar una unidad de `EN_REVISION` a `AUTORIZADO` y de `AUTORIZADO`
 * a `PUBLISHED`. Un `BORRADOR` pertenece al docente y no se autoriza desde esta
 * pantalla: solo se autorizan unidades que ya fueron enviadas a revisión.
 *
 * El backend exige un autorizante con rol `UTP` y una fecha de autorización: una
 * autorización incompleta devuelve `400 Bad Request`.
 */
class UnitAuthorizationViewModel(
    private val unitRepository: UnitRepository,
    private val unitId: Int
) : ViewModel() {

    /** Unidad que se está autorizando. */
    var unidad by mutableStateOf<LearningUnit?>(null)
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
                unidad = result.getOrThrow()
            }

            isLoading = false
        }
    }

    /**
     * Autoriza o publica la unidad.
     *
     * Un `BORRADOR` no se autoriza: primero el docente debe enviarlo a revisión.
     *
     * @param autorizanteId usuario con rol `UTP` que autoriza.
     */
    fun autorizar(autorizanteId: Int, onSuccess: () -> Unit = {}) {
        val unit = unidad ?: return

        val nuevoEstado = when (unit.estado) {
            ContentStatus.IN_REVIEW -> ContentStatus.AUTHORIZED
            ContentStatus.AUTHORIZED -> ContentStatus.PUBLISHED
            ContentStatus.DRAFT, ContentStatus.PUBLISHED -> null
        }

        if (nuevoEstado == null) {
            error = if (unit.estado == ContentStatus.DRAFT) {
                ApiError.BadRequest(
                    "La unidad está en borrador. Solo se autorizan unidades en revisión."
                )
            } else {
                ApiError.BadRequest("La unidad ya está publicada.")
            }
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
            } else {
                unidad = result.getOrThrow()
                mensaje = if (nuevoEstado == ContentStatus.PUBLISHED) {
                    "Unidad publicada."
                } else {
                    "Unidad autorizada."
                }
                onSuccess()
            }

            isLoading = false
        }
    }

    companion object {

        /** Factoría del ViewModel con el repositorio y el id de la unidad. */
        fun factory(
            unitRepository: UnitRepository,
            unitId: Int
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                UnitAuthorizationViewModel(unitRepository, unitId)
            }
        }
    }
}

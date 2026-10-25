package com.ccstudio.mpaz_pro_6.ui.screens.contents

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
import com.ccstudio.mpaz_pro_6.data.model.Content
import com.ccstudio.mpaz_pro_6.data.model.ContentResource
import com.ccstudio.mpaz_pro_6.data.repository.ContentRepository
import com.ccstudio.mpaz_pro_6.data.repository.ContentResourceRepository
import kotlinx.coroutines.launch

/**
 * Estado del detalle de un contenido.
 *
 * Consume `GET /api/contenidos/{id}` y sus recursos publicados con
 * `GET /api/recursos-contenido?contenidoId=`. La vista es de solo lectura para el
 * estudiante.
 */
class ContentDetailViewModel(
    private val contentRepository: ContentRepository,
    private val contentResourceRepository: ContentResourceRepository,
    private val contentId: Int
) : ViewModel() {

    /** Contenido consultado. */
    var content by mutableStateOf<Content?>(null)
        private set

    /** Recursos publicados del contenido. */
    var resources by mutableStateOf<List<ContentResource>>(emptyList())
        private set

    /** Indica que el detalle se está cargando. */
    var isLoading by mutableStateOf(false)
        private set

    /** Error de la última carga, si lo hubo. */
    var error by mutableStateOf<ApiError?>(null)
        private set

    /** Carga el contenido y sus recursos. */
    fun cargar() {
        viewModelScope.launch {
            isLoading = true
            error = null

            val contentResult = contentRepository.getById(contentId)

            if (contentResult.isFailure) {
                error = contentResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val loadedContent = contentResult.getOrNull()

            if (loadedContent == null) {
                error = ApiError.NotFound("El contenido ya no existe.")
                isLoading = false
                return@launch
            }

            content = loadedContent

            val resourcesResult = contentResourceRepository.list(contentId)

            if (resourcesResult.isFailure) {
                error = resourcesResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            resources = resourcesResult.getOrDefault(emptyList())
                .filter { it.estado == ContentStatus.PUBLISHED }

            isLoading = false
        }
    }

    companion object {

        /** Factoría del detalle con los repositorios y el id del contenido. */
        fun factory(
            contentRepository: ContentRepository,
            contentResourceRepository: ContentResourceRepository,
            contentId: Int
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ContentDetailViewModel(
                    contentRepository = contentRepository,
                    contentResourceRepository = contentResourceRepository,
                    contentId = contentId
                )
            }
        }
    }
}

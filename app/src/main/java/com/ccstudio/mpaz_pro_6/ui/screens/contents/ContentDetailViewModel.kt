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
import com.ccstudio.mpaz_pro_6.data.enums.UserRole
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.Content
import com.ccstudio.mpaz_pro_6.data.model.ContentResource
import com.ccstudio.mpaz_pro_6.data.model.LearningObjective
import com.ccstudio.mpaz_pro_6.data.repository.ContentRepository
import com.ccstudio.mpaz_pro_6.data.repository.ContentResourceRepository
import com.ccstudio.mpaz_pro_6.data.repository.LearningObjectiveRepository
import kotlinx.coroutines.launch

/**
 * Estado del detalle de un contenido.
 *
 * Consume `GET /api/contenidos/{id}` y resuelve, además:
 *
 * - El objetivo de aprendizaje del contenido (`contenido.oaId`).
 * - Los recursos asociados, desde `GET /api/recursos-contenido?contenidoId={id}`.
 *
 * El objetivo y los recursos son complementarios: si fallan, el contenido se sigue
 * mostrando y la pantalla deja su sección vacía. Para el estudiante solo se
 * muestran los recursos publicados.
 */
class ContentDetailViewModel(
    private val contentRepository: ContentRepository,
    private val learningObjectiveRepository: LearningObjectiveRepository,
    private val contentResourceRepository: ContentResourceRepository,
    private val rol: UserRole,
    private val contentId: Int
) : ViewModel() {

    /** Contenido consultado. */
    var contenido by mutableStateOf<Content?>(null)
        private set

    /** Objetivo de aprendizaje del contenido. */
    var objetivo by mutableStateOf<LearningObjective?>(null)
        private set

    /** Recursos asociados al contenido. */
    var recursos by mutableStateOf<List<ContentResource>>(emptyList())
        private set

    /** Indica que el detalle se está cargando. */
    var isLoading by mutableStateOf(false)
        private set

    /** Error de la carga del contenido, si lo hubo. */
    var error by mutableStateOf<ApiError?>(null)
        private set

    /** Carga el contenido, su objetivo y sus recursos. */
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

            val content = contentResult.getOrThrow()
            contenido = content

            val objectiveResult = learningObjectiveRepository.getById(content.oaId)

            if (objectiveResult.isSuccess) {
                objetivo = objectiveResult.getOrNull()
            }

            val resourcesResult = contentResourceRepository.list(content.id)

            if (resourcesResult.isSuccess) {
                val allResources = resourcesResult.getOrDefault(emptyList())
                    .sortedBy { it.id }

                recursos = if (rol == UserRole.ESTUDIANTE) {
                    allResources.filter { it.estado == ContentStatus.PUBLISHED }
                } else {
                    allResources
                }
            }

            isLoading = false
        }
    }

    companion object {

        /** Factoría del ViewModel con los repositorios y el id del contenido. */
        fun factory(
            contentRepository: ContentRepository,
            learningObjectiveRepository: LearningObjectiveRepository,
            contentResourceRepository: ContentResourceRepository,
            rol: UserRole,
            contentId: Int
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ContentDetailViewModel(
                    contentRepository = contentRepository,
                    learningObjectiveRepository = learningObjectiveRepository,
                    contentResourceRepository = contentResourceRepository,
                    rol = rol,
                    contentId = contentId
                )
            }
        }
    }
}

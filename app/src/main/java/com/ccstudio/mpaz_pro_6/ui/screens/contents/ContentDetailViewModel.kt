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
import com.ccstudio.mpaz_pro_6.data.model.Activity
import com.ccstudio.mpaz_pro_6.data.model.Content
import com.ccstudio.mpaz_pro_6.data.model.ContentResource
import com.ccstudio.mpaz_pro_6.data.model.LearningObjective
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.repository.ActivityRepository
import com.ccstudio.mpaz_pro_6.data.repository.AssignmentRepository
import com.ccstudio.mpaz_pro_6.data.repository.ContentRepository
import com.ccstudio.mpaz_pro_6.data.repository.ContentResourceRepository
import com.ccstudio.mpaz_pro_6.data.repository.LearningObjectiveRepository
import com.ccstudio.mpaz_pro_6.data.repository.QuestionRepository
import com.ccstudio.mpaz_pro_6.data.repository.UnitRepository
import kotlinx.coroutines.launch

/**
 * Estado del detalle de un contenido.
 *
 * Consume `GET /api/contenidos/{id}` y resuelve, además:
 *
 * - El objetivo de aprendizaje del contenido (`contenido.oaId`).
 * - Los recursos asociados, desde `GET /api/recursos-contenido?contenidoId={id}`.
 * - Las actividades del contenido, desde
 *   `GET /api/actividades?unidadId={unidad}&contenidoId={id}`.
 *
 * El objetivo, los recursos y las actividades son complementarios: si fallan, el
 * contenido se sigue mostrando y la pantalla deja su sección vacía. Para el
 * estudiante solo se muestran los recursos publicados y las actividades
 * asignadas a su curso; la UTP y el docente ven el material completo.
 */
class ContentDetailViewModel(
    private val contentRepository: ContentRepository,
    private val learningObjectiveRepository: LearningObjectiveRepository,
    private val contentResourceRepository: ContentResourceRepository,
    private val activityRepository: ActivityRepository,
    private val questionRepository: QuestionRepository,
    private val assignmentRepository: AssignmentRepository,
    private val unitRepository: UnitRepository,
    private val rol: UserRole,
    private val courseId: Int?,
    private val contentId: Int
) : ViewModel() {

    /** Contenido consultado. */
    var contenido by mutableStateOf<Content?>(null)
        private set

    /** Objetivo de aprendizaje del contenido. */
    var objetivo by mutableStateOf<LearningObjective?>(null)
        private set

    /** Unidad del contenido, para habilitar la autoría del docente. */
    var unidad by mutableStateOf<LearningUnit?>(null)
        private set

    /** `true` si el docente puede crear actividades en esta unidad. */
    val puedeCrearActividad: Boolean
        get() = rol == UserRole.DOCENTE && unidad?.estado == ContentStatus.DRAFT

    /** Recursos asociados al contenido. */
    var recursos by mutableStateOf<List<ContentResource>>(emptyList())
        private set

    /** Actividades visibles del contenido. */
    var actividades by mutableStateOf<List<Activity>>(emptyList())
        private set

    /** Cantidad de preguntas por actividad. */
    var preguntasPorActividad by mutableStateOf<Map<Int, Int>>(emptyMap())
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
            contenido = null
            objetivo = null
            unidad = null
            recursos = emptyList()
            actividades = emptyList()
            preguntasPorActividad = emptyMap()

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
                objetivo?.let {
                    unidad = unitRepository.getById(it.unidadId).getOrNull()
                }
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

            val unitId = objectiveResult.getOrNull()?.unidadId
            if (unitId != null) {
                val activitiesResult = activityRepository.list(
                    unitId = unitId,
                    contentId = content.id
                )

                if (activitiesResult.isSuccess) {
                    val contentActivities = activitiesResult
                        .getOrDefault(emptyList())
                        .sortedWith(compareBy({ it.orden }, { it.id }))

                    actividades = if (rol == UserRole.ESTUDIANTE) {
                        actividadesAsignadas(unitId, contentActivities)
                    } else {
                        contentActivities
                    }

                    val activityIds = actividades.map { it.id }.toSet()
                    preguntasPorActividad = questionRepository.list()
                        .getOrDefault(emptyList())
                        .filter { it.actividadId in activityIds }
                        .groupingBy { it.actividadId }
                        .eachCount()
                }
            }

            isLoading = false
        }
    }

    /**
     * Filtra las actividades del contenido a las asignadas al curso del estudiante.
     *
     * Si el estudiante no tiene curso o no hay asignación para la unidad, la lista
     * queda vacía: el backend rechazaría un intento sobre una actividad no asignada.
     */
    private suspend fun actividadesAsignadas(
        unitId: Int,
        activities: List<Activity>
    ): List<Activity> {
        if (courseId == null) return emptyList()

        val assignments = assignmentRepository.list(
            courseId = courseId,
            unitId = unitId
        ).getOrDefault(emptyList())

        val assignedIds = assignments
            .flatMap { assignment ->
                assignmentRepository.listActivities(assignment.id)
                    .getOrDefault(emptyList())
                    .map { it.actividadId }
            }
            .toSet()

        return activities.filter { it.id in assignedIds }
    }

    companion object {

        /** Factoría del ViewModel con los repositorios y el id del contenido. */
        fun factory(
            contentRepository: ContentRepository,
            learningObjectiveRepository: LearningObjectiveRepository,
            contentResourceRepository: ContentResourceRepository,
            activityRepository: ActivityRepository,
            questionRepository: QuestionRepository,
            assignmentRepository: AssignmentRepository,
            unitRepository: UnitRepository,
            rol: UserRole,
            courseId: Int?,
            contentId: Int
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ContentDetailViewModel(
                    contentRepository = contentRepository,
                    learningObjectiveRepository = learningObjectiveRepository,
                    contentResourceRepository = contentResourceRepository,
                    activityRepository = activityRepository,
                    questionRepository = questionRepository,
                    assignmentRepository = assignmentRepository,
                    unitRepository = unitRepository,
                    rol = rol,
                    courseId = courseId,
                    contentId = contentId
                )
            }
        }
    }
}

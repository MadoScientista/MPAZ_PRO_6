package com.ccstudio.mpaz_pro_6.ui.screens.activities

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
import com.ccstudio.mpaz_pro_6.data.model.Activity
import com.ccstudio.mpaz_pro_6.data.model.Alternative
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.Question
import com.ccstudio.mpaz_pro_6.data.repository.ActivityRepository
import com.ccstudio.mpaz_pro_6.data.repository.AlternativeRepository
import com.ccstudio.mpaz_pro_6.data.repository.QuestionRepository
import com.ccstudio.mpaz_pro_6.data.repository.UnitRepository
import kotlinx.coroutines.launch

/**
 * Estado del detalle de una actividad para el docente.
 *
 * Carga la actividad, sus preguntas y las alternativas agrupadas por pregunta.
 * La pantalla permite crear preguntas y alternativas cuando el rol lo habilita.
 */
class ActivityDetailViewModel(
    private val activityRepository: ActivityRepository,
    private val questionRepository: QuestionRepository,
    private val alternativeRepository: AlternativeRepository,
    private val unitRepository: UnitRepository,
    private val activityId: Int
) : ViewModel() {

    /** Actividad consultada. */
    var activity by mutableStateOf<Activity?>(null)
        private set

    /** Unidad de la actividad, para habilitar la autoría del docente. */
    var unidad by mutableStateOf<LearningUnit?>(null)
        private set

    /** `true` si la unidad permite crear preguntas y alternativas. */
    val puedeGestionar: Boolean
        get() = unidad?.estado == ContentStatus.DRAFT

    /** Preguntas de la actividad. */
    var questions by mutableStateOf<List<Question>>(emptyList())
        private set

    /** Alternativas agrupadas por id de pregunta. */
    var alternativesByQuestion by mutableStateOf<Map<Int, List<Alternative>>>(emptyMap())
        private set

    /** Indica que se está cargando el detalle. */
    var isLoading by mutableStateOf(false)
        private set

    /** Error de la última carga, si lo hubo. */
    var error by mutableStateOf<ApiError?>(null)
        private set

    /** Carga la actividad, sus preguntas y alternativas. */
    fun cargar() {
        viewModelScope.launch {
            isLoading = true
            error = null

            val activityResult = activityRepository.getById(activityId)
            if (activityResult.isFailure) {
                error = activityResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            activity = activityResult.getOrNull()
            activity?.let {
                unidad = unitRepository.getById(it.unidadId).getOrNull()
            }

            val questionsResult = questionRepository.list(activityId = activityId)
            if (questionsResult.isFailure) {
                error = questionsResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val loadedQuestions = questionsResult.getOrDefault(emptyList())
                .sortedWith(compareBy({ it.orden }, { it.id }))
            questions = loadedQuestions

            val alternativesResult = alternativeRepository.list()
            if (alternativesResult.isFailure) {
                error = alternativesResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val questionIds = loadedQuestions.map { it.id }.toSet()
            alternativesByQuestion = alternativesResult
                .getOrDefault(emptyList())
                .filter { it.preguntaId in questionIds }
                .groupBy { it.preguntaId }

            isLoading = false
        }
    }

    companion object {

        /** Factoría del detalle con la actividad y sus preguntas. */
        fun factory(
            activityRepository: ActivityRepository,
            questionRepository: QuestionRepository,
            alternativeRepository: AlternativeRepository,
            unitRepository: UnitRepository,
            activityId: Int
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ActivityDetailViewModel(
                    activityRepository = activityRepository,
                    questionRepository = questionRepository,
                    alternativeRepository = alternativeRepository,
                    unitRepository = unitRepository,
                    activityId = activityId
                )
            }
        }
    }
}

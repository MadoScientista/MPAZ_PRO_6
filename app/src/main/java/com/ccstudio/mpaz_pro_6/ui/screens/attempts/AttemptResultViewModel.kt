package com.ccstudio.mpaz_pro_6.ui.screens.attempts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.Activity
import com.ccstudio.mpaz_pro_6.data.model.Alternative
import com.ccstudio.mpaz_pro_6.data.model.Attempt
import com.ccstudio.mpaz_pro_6.data.model.Question
import com.ccstudio.mpaz_pro_6.data.model.StudentAnswer
import com.ccstudio.mpaz_pro_6.data.repository.ActivityRepository
import com.ccstudio.mpaz_pro_6.data.repository.AlternativeRepository
import com.ccstudio.mpaz_pro_6.data.repository.AttemptRepository
import com.ccstudio.mpaz_pro_6.data.repository.QuestionRepository
import com.ccstudio.mpaz_pro_6.data.repository.StudentAnswerRepository
import kotlinx.coroutines.launch

/**
 * Estado del resultado de un intento.
 *
 * Carga el intento, sus respuestas, la actividad y las preguntas para mostrar el
 * detalle. Las respuestas llegan con `esCorrecta` ya calculado por el backend; la
 * pantalla solo cuenta las correctas y presenta el nivel.
 */
class AttemptResultViewModel(
    private val attemptRepository: AttemptRepository,
    private val studentAnswerRepository: StudentAnswerRepository,
    private val activityRepository: ActivityRepository,
    private val questionRepository: QuestionRepository,
    private val alternativeRepository: AlternativeRepository,
    private val attemptId: Int
) : ViewModel() {

    /** Intento consultado. */
    var attempt by mutableStateOf<Attempt?>(null)
        private set

    /** Actividad del intento. */
    var activity by mutableStateOf<Activity?>(null)
        private set

    /** Preguntas de la actividad. */
    var questions by mutableStateOf<List<Question>>(emptyList())
        private set

    /** Alternativas agrupadas por id de pregunta. */
    var alternativesByQuestion by mutableStateOf<Map<Int, List<Alternative>>>(emptyMap())
        private set

    /** Respuestas registradas, indexadas por id de pregunta. */
    var answers by mutableStateOf<Map<Int, StudentAnswer>>(emptyMap())
        private set

    /** Cantidad de respuestas correctas. */
    var correctas by mutableIntStateOf(0)
        private set

    /** Total de preguntas de la actividad. */
    var total by mutableIntStateOf(0)
        private set

    /** Porcentaje de respuestas correctas sobre el total. */
    var porcentaje by mutableIntStateOf(0)
        private set

    /** Indica que el resultado se está cargando. */
    var isLoading by mutableStateOf(false)
        private set

    /** Error de la última carga, si lo hubo. */
    var error by mutableStateOf<ApiError?>(null)
        private set

    /** Carga el intento, sus respuestas y el detalle de la actividad. */
    fun cargar() {
        viewModelScope.launch {
            isLoading = true
            error = null

            val attemptResult = attemptRepository.getById(attemptId)

            if (attemptResult.isFailure) {
                error = attemptResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val loadedAttempt = attemptResult.getOrNull()

            if (loadedAttempt == null) {
                error = ApiError.NotFound("El intento ya no existe.")
                isLoading = false
                return@launch
            }

            attempt = loadedAttempt

            val activityResult = activityRepository.getById(loadedAttempt.actividadId)

            if (activityResult.isFailure) {
                error = activityResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val questionsResult = questionRepository.list(activityId = loadedAttempt.actividadId)

            if (questionsResult.isFailure) {
                error = questionsResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val alternativesResult = alternativeRepository.list()

            if (alternativesResult.isFailure) {
                error = alternativesResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val answersResult = studentAnswerRepository.list(attemptId)

            if (answersResult.isFailure) {
                error = answersResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val loadedQuestions = questionsResult.getOrDefault(emptyList())
            val questionIds = loadedQuestions.map { it.id }.toSet()
            val loadedAnswers = answersResult.getOrDefault(emptyList())

            activity = activityResult.getOrNull()
            questions = loadedQuestions
            alternativesByQuestion = alternativesResult.getOrDefault(emptyList())
                .filter { it.preguntaId in questionIds }
                .groupBy { it.preguntaId }
            answers = loadedAnswers.associateBy { it.preguntaId }

            total = loadedQuestions.size
            correctas = loadedAnswers.count { it.esCorrecta }
            porcentaje = if (total > 0) correctas * 100 / total else 0

            isLoading = false
        }
    }

    companion object {

        /** Factoría del resultado con los repositorios del intento. */
        fun factory(
            attemptRepository: AttemptRepository,
            studentAnswerRepository: StudentAnswerRepository,
            activityRepository: ActivityRepository,
            questionRepository: QuestionRepository,
            alternativeRepository: AlternativeRepository,
            attemptId: Int
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AttemptResultViewModel(
                    attemptRepository = attemptRepository,
                    studentAnswerRepository = studentAnswerRepository,
                    activityRepository = activityRepository,
                    questionRepository = questionRepository,
                    alternativeRepository = alternativeRepository,
                    attemptId = attemptId
                )
            }
        }
    }
}

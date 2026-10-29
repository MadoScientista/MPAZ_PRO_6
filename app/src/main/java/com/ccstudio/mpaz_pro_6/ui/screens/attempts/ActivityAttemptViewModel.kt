package com.ccstudio.mpaz_pro_6.ui.screens.attempts

import androidx.compose.runtime.getValue
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
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateStudentAnswerRequest
import com.ccstudio.mpaz_pro_6.data.repository.ActivityRepository
import com.ccstudio.mpaz_pro_6.data.repository.AlternativeRepository
import com.ccstudio.mpaz_pro_6.data.repository.AttemptRepository
import com.ccstudio.mpaz_pro_6.data.repository.QuestionRepository
import com.ccstudio.mpaz_pro_6.data.repository.StudentAnswerRepository
import kotlinx.coroutines.launch

/**
 * Estado de la resolución de una actividad.
 *
 * Las selecciones se mantienen en local hasta que el estudiante finaliza. Eso
 * permite cambiar de alternativa sin chocar con la unicidad `(intento, pregunta)`
 * del backend. Al finalizar se registra una sola vez cada pregunta pendiente y
 * después se completa el intento.
 *
 * Si se reanuda un intento con respuestas ya registradas, esas preguntas quedan
 * bloqueadas y solo se pueden responder las que faltan.
 *
 * En modo de **vista previa** (`previewMode = true`), pensado para la UTP y el
 * docente, la actividad se abre directamente con la alternativa correcta de cada
 * pregunta ya seleccionada. No se consulta ni se escribe ningún intento y la
 * selección no se puede cambiar: es una vista de revisión, no de resolución.
 */
class ActivityAttemptViewModel(
    private val activityRepository: ActivityRepository,
    private val questionRepository: QuestionRepository,
    private val alternativeRepository: AlternativeRepository,
    private val attemptRepository: AttemptRepository,
    private val studentAnswerRepository: StudentAnswerRepository,
    private val studentId: Int,
    private val activityId: Int,
    private val previewMode: Boolean = false
) : ViewModel() {

    /** Actividad que se está resolviendo. */
    var activity by mutableStateOf<Activity?>(null)
        private set

    /** Preguntas de la actividad, ordenadas por el backend. */
    var questions by mutableStateOf<List<Question>>(emptyList())
        private set

    /** Alternativas agrupadas por id de pregunta. */
    var alternativesByQuestion by mutableStateOf<Map<Int, List<Alternative>>>(emptyMap())
        private set

    /** Intento en progreso; `null` si todavía no se inicia uno. */
    var attempt by mutableStateOf<Attempt?>(null)
        private set

    /** Respuestas ya registradas en el backend, indexadas por id de pregunta. */
    var registeredAnswers by mutableStateOf<Map<Int, StudentAnswer>>(emptyMap())
        private set

    /** Selecciones locales, indexadas por id de pregunta. */
    var selectedAlternatives by mutableStateOf<Map<Int, Int>>(emptyMap())
        private set

    /** Indica que se están cargando los datos de la actividad. */
    var isLoading by mutableStateOf(false)
        private set

    /** Indica que se está iniciando o finalizando el intento. */
    var isSaving by mutableStateOf(false)
        private set

    /** Error de carga de la pantalla. */
    var error by mutableStateOf<ApiError?>(null)
        private set

    /** Mensaje de una operación fallida, visible como aviso. */
    var mensaje by mutableStateOf<String?>(null)
        private set

    /** `true` cuando el intento ya tiene fecha de fin. */
    val isCompleted: Boolean
        get() = attempt?.fechaFin != null

    /** `true` cuando todas las preguntas tienen una selección local. */
    val allAnswered: Boolean
        get() = questions.isNotEmpty() && questions.all { it.id in selectedAlternatives }

    /** Cantidad de preguntas con selección. */
    val answeredCount: Int
        get() = selectedAlternatives.size

    /** Preguntas cuya respuesta ya está registrada y no se puede cambiar. */
    val registeredQuestionIds: Set<Int>
        get() = registeredAnswers.keys

    /** Carga la actividad, sus preguntas, alternativas e intento vigente. */
    fun cargar() {
        viewModelScope.launch {
            isLoading = true
            error = null
            mensaje = null

            val activityResult = activityRepository.getById(activityId)

            if (activityResult.isFailure) {
                error = activityResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val questionsResult = questionRepository.list(activityId = activityId)

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

            val loadedQuestions = questionsResult.getOrDefault(emptyList())
            val questionIds = loadedQuestions.map { it.id }.toSet()

            activity = activityResult.getOrNull()
            questions = loadedQuestions
            alternativesByQuestion = alternativesResult.getOrDefault(emptyList())
                .filter { it.preguntaId in questionIds }
                .groupBy { it.preguntaId }

            // La vista previa no consulta intentos ni respuestas del usuario: se
            // abre con la alternativa correcta de cada pregunta ya seleccionada.
            if (previewMode) {
                attempt = null
                registeredAnswers = emptyMap()
                selectedAlternatives = correctAlternatives()
                isLoading = false
                return@launch
            }

            val attemptsResult = attemptRepository.list(
                studentId = studentId,
                activityId = activityId
            )

            if (attemptsResult.isFailure) {
                error = attemptsResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val inProgress = attemptsResult.getOrDefault(emptyList())
                .filter { it.fechaFin == null }
                .maxByOrNull { it.numero }

            if (inProgress == null) {
                attempt = null
                registeredAnswers = emptyMap()
                selectedAlternatives = emptyMap()
                isLoading = false
                return@launch
            }

            val answersResult = studentAnswerRepository.list(inProgress.id)

            if (answersResult.isFailure) {
                error = answersResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val registered = answersResult.getOrDefault(emptyList())
                .associateBy { it.preguntaId }

            attempt = inProgress
            registeredAnswers = registered
            selectedAlternatives = registered.mapValues { it.value.alternativaId }
            isLoading = false
        }
    }

    /** Inicia un intento nuevo para el estudiante en esta actividad. */
    fun iniciarIntento() {
        if (previewMode || attempt != null || isSaving) return

        viewModelScope.launch {
            isSaving = true
            mensaje = null

            val result = attemptRepository.start(studentId, activityId)

            isSaving = false

            if (result.isFailure) {
                mensaje = mensajeDeInicio(result.exceptionOrNull() as? ApiError)
                return@launch
            }

            val started = result.getOrNull()

            if (started == null) {
                mensaje = "No se pudo iniciar el intento."
                return@launch
            }

            attempt = started
            registeredAnswers = emptyMap()
            selectedAlternatives = emptyMap()
        }
    }

    /**
     * Cambia la selección local de una pregunta.
     *
     * No llama al backend: la respuesta se registra al finalizar. Las preguntas
     * ya registradas no se pueden modificar.
     */
    fun seleccionar(questionId: Int, alternativeId: Int) {
        // La vista previa es de solo lectura: las correctas ya vienen seleccionadas.
        if (previewMode) return

        val currentAttempt = attempt ?: return

        if (currentAttempt.fechaFin != null || isSaving) return
        if (questionId in registeredAnswers) return

        selectedAlternatives = selectedAlternatives + (questionId to alternativeId)
        mensaje = null
    }

    /**
     * Registra las respuestas pendientes y completa el intento.
     *
     * Si una respuesta ya estaba registrada, se omite. Si falla una, se conserva
     * lo registrado y se avisa para que el estudiante reintente.
     */
    fun completar(onCompleted: (Int) -> Unit) {
        if (previewMode) return

        val currentAttempt = attempt ?: return

        if (currentAttempt.fechaFin != null || !allAnswered || isSaving) return

        viewModelScope.launch {
            isSaving = true
            mensaje = null

            for (question in questions) {
                if (question.id in registeredAnswers) continue

                val alternativeId = selectedAlternatives[question.id] ?: continue

                val result = studentAnswerRepository.register(
                    attemptId = currentAttempt.id,
                    request = CreateStudentAnswerRequest(
                        preguntaId = question.id,
                        alternativaId = alternativeId
                    )
                )

                if (result.isFailure) {
                    val fallo = result.exceptionOrNull() as? ApiError

                    // Sincroniza lo que el backend ya tenga registrado.
                    studentAnswerRepository.list(currentAttempt.id).getOrNull()?.let { lista ->
                        registeredAnswers = lista.associateBy { it.preguntaId }
                    }

                    mensaje = mensajeDeRespuesta(fallo)
                    isSaving = false
                    return@launch
                }

                result.getOrNull()?.let { answer ->
                    registeredAnswers = registeredAnswers + (question.id to answer)
                }
            }

            val completeResult = attemptRepository.complete(currentAttempt.id)

            if (completeResult.isFailure) {
                mensaje = mensajeDeCompletar(completeResult.exceptionOrNull() as? ApiError)
                isSaving = false
                return@launch
            }

            attempt = completeResult.getOrNull() ?: currentAttempt
            isSaving = false
            onCompleted(currentAttempt.id)
        }
    }

    /** Oculta el aviso de la última operación. */
    fun limpiarMensaje() {
        mensaje = null
    }

    /**
     * Alternativa correcta de cada pregunta, usada como selección inicial de la
     * vista previa. Las preguntas sin alternativa correcta quedan sin selección.
     */
    private fun correctAlternatives(): Map<Int, Int> = buildMap {
        alternativesByQuestion.forEach { (questionId, alternatives) ->
            alternatives.firstOrNull { it.esCorrecta }?.let { put(questionId, it.id) }
        }
    }

    private fun mensajeDeInicio(error: ApiError?): String = when (error) {
        is ApiError.BadRequest -> "No puedes iniciar esta actividad: ${error.message}"
        is ApiError.Conflict -> "No puedes iniciar un nuevo intento: ${error.message}"
        is ApiError.NotFound -> "La actividad ya no existe."
        is ApiError.Network -> "Sin conexión. Revisa tu red e intenta de nuevo."
        is ApiError.Unauthorized -> "Tu sesión venció. Vuelve a iniciar sesión."
        is ApiError.Unknown -> error.message
        null -> "No se pudo iniciar el intento."
    }

    private fun mensajeDeRespuesta(error: ApiError?): String = when (error) {
        is ApiError.Conflict -> "Una pregunta ya estaba registrada. Revisa tus respuestas e intenta finalizar de nuevo."
        is ApiError.BadRequest -> error.message
        is ApiError.NotFound -> "La pregunta o la alternativa ya no existen."
        is ApiError.Network -> "Sin conexión. Revisa tu red e intenta de nuevo."
        is ApiError.Unauthorized -> "Tu sesión venció. Vuelve a iniciar sesión."
        is ApiError.Unknown -> error.message
        null -> "No se pudo registrar la respuesta."
    }

    private fun mensajeDeCompletar(error: ApiError?): String = when (error) {
        is ApiError.Conflict -> "El intento ya está completado."
        is ApiError.BadRequest -> error.message
        is ApiError.NotFound -> "El intento ya no existe."
        is ApiError.Network -> "Sin conexión. Revisa tu red e intenta de nuevo."
        is ApiError.Unauthorized -> "Tu sesión venció. Vuelve a iniciar sesión."
        is ApiError.Unknown -> error.message
        null -> "No se pudo completar el intento."
    }

    companion object {

        /** Factoría del intento con los repositorios y el contexto del estudiante. */
        fun factory(
            activityRepository: ActivityRepository,
            questionRepository: QuestionRepository,
            alternativeRepository: AlternativeRepository,
            attemptRepository: AttemptRepository,
            studentAnswerRepository: StudentAnswerRepository,
            studentId: Int,
            activityId: Int,
            previewMode: Boolean = false
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ActivityAttemptViewModel(
                    activityRepository = activityRepository,
                    questionRepository = questionRepository,
                    alternativeRepository = alternativeRepository,
                    attemptRepository = attemptRepository,
                    studentAnswerRepository = studentAnswerRepository,
                    studentId = studentId,
                    activityId = activityId,
                    previewMode = previewMode
                )
            }
        }
    }
}

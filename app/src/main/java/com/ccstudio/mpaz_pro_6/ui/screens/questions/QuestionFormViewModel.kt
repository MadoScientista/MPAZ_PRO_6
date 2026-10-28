package com.ccstudio.mpaz_pro_6.ui.screens.questions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ccstudio.mpaz_pro_6.data.enums.Difficulty
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.LearningObjective
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateQuestionRequest
import com.ccstudio.mpaz_pro_6.data.repository.LearningObjectiveRepository
import com.ccstudio.mpaz_pro_6.data.repository.QuestionRepository
import kotlinx.coroutines.launch

/**
 * Estado del formulario de creación de una pregunta.
 *
 * La actividad y la unidad llegan fijadas por la pantalla de origen. El docente
 * elige el OA de la misma unidad y completa enunciado, criterio, dificultad y
 * orden.
 */
class QuestionFormViewModel(
    private val questionRepository: QuestionRepository,
    private val learningObjectiveRepository: LearningObjectiveRepository,
    private val activityId: Int,
    private val unitId: Int
) : ViewModel() {

    var objetivos by mutableStateOf<List<LearningObjective>>(emptyList())
        private set

    var selectedObjectiveId by mutableStateOf<Int?>(null)
        private set

    var enunciado by mutableStateOf("")
        private set

    var criterioRespuesta by mutableStateOf("")
        private set

    var dificultad by mutableStateOf(Difficulty.LOW)
        private set

    var orden by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    var isSaving by mutableStateOf(false)
        private set

    var loadError by mutableStateOf<ApiError?>(null)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    private var cargado = false

    fun onObjectiveChange(value: Int) {
        selectedObjectiveId = value
        error = null
    }

    fun onEnunciadoChange(value: String) {
        enunciado = value
        error = null
    }

    fun onCriterioRespuestaChange(value: String) {
        criterioRespuesta = value
        error = null
    }

    fun onDificultadChange(value: Difficulty) {
        dificultad = value
        error = null
    }

    fun onOrdenChange(value: String) {
        orden = value.filter { it.isDigit() }
        error = null
    }

    fun cargar() {
        if (cargado) return

        viewModelScope.launch {
            isLoading = true
            loadError = null

            val result = learningObjectiveRepository.list(unitId)
            if (result.isFailure) {
                loadError = result.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            objetivos = result.getOrDefault(emptyList())
                .filter { it.unidadId == unitId }
                .sortedWith(compareBy({ it.codigo.orEmpty() }, { it.id }))
            cargado = true
            isLoading = false
        }
    }

    fun guardar(onSaved: () -> Unit) {
        val objectiveId = selectedObjectiveId
        val enunciadoLimpio = enunciado.trim()
        val ordenValue = orden.trim()

        when {
            objectiveId == null -> {
                error = "Selecciona un objetivo de aprendizaje."
                return
            }

            enunciadoLimpio.isBlank() -> {
                error = "El enunciado es obligatorio."
                return
            }
        }

        val ordenNumber = if (ordenValue.isEmpty()) null else ordenValue.toIntOrNull()
        if (ordenValue.isNotEmpty() && (ordenNumber == null || ordenNumber <= 0)) {
            error = "El orden debe ser mayor que cero."
            return
        }

        viewModelScope.launch {
            isSaving = true
            error = null

            val result = questionRepository.create(
                CreateQuestionRequest(
                    actividadId = activityId,
                    unidadId = unitId,
                    oaId = objectiveId,
                    enunciado = enunciadoLimpio,
                    dificultad = dificultad,
                    criterioRespuesta = criterioRespuesta.trim().takeIf { it.isNotEmpty() },
                    orden = ordenNumber
                )
            )

            isSaving = false
            if (result.isFailure) {
                error = mensajeDeError(result.exceptionOrNull() as? ApiError)
                return@launch
            }

            onSaved()
        }
    }

    private fun mensajeDeError(error: ApiError?): String = when (error) {
        is ApiError.BadRequest -> error.message
        is ApiError.Conflict -> error.message
        is ApiError.NotFound -> "La actividad o el objetivo ya no existe."
        is ApiError.Network -> "Sin conexión. Revisa tu red e intenta de nuevo."
        is ApiError.Unauthorized -> "Tu sesión venció. Vuelve a iniciar sesión."
        is ApiError.Unknown -> error.message
        null -> "No se pudo guardar la pregunta."
    }

    companion object {

        fun factory(
            questionRepository: QuestionRepository,
            learningObjectiveRepository: LearningObjectiveRepository,
            activityId: Int,
            unitId: Int
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                QuestionFormViewModel(
                    questionRepository = questionRepository,
                    learningObjectiveRepository = learningObjectiveRepository,
                    activityId = activityId,
                    unitId = unitId
                )
            }
        }
    }
}

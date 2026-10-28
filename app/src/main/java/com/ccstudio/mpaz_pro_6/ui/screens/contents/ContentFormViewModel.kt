package com.ccstudio.mpaz_pro_6.ui.screens.contents

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ccstudio.mpaz_pro_6.data.enums.ContentOrigin
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.LearningObjective
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateContentRequest
import com.ccstudio.mpaz_pro_6.data.repository.ContentRepository
import com.ccstudio.mpaz_pro_6.data.repository.LearningObjectiveRepository
import kotlinx.coroutines.launch

/**
 * Estado del formulario de creación de contenido.
 *
 * Carga los objetivos de aprendizaje de la unidad, porque `oaId` es obligatorio
 * en `POST /api/contenidos` y no se puede cambiar después de crear el contenido.
 * La unidad no se elige: el backend la deriva del objetivo y la devuelve como
 * `unidadId`.
 *
 * `ejemplos`, `instrucciones` y `orden` son opcionales; si quedan vacíos no se
 * envían y el backend aplica su valor por defecto (`orden = 1`).
 */
class ContentFormViewModel(
    private val contentRepository: ContentRepository,
    private val learningObjectiveRepository: LearningObjectiveRepository,
    private val unitId: Int
) : ViewModel() {

    /** Objetivos de aprendizaje de la unidad, ordenados por código. */
    var objetivos by mutableStateOf<List<LearningObjective>>(emptyList())
        private set

    /** Objetivo elegido; `null` mientras el usuario no elige uno. */
    var selectedObjectiveId by mutableStateOf<Int?>(null)
        private set

    /** Título del contenido (obligatorio, máximo 200 caracteres). */
    var titulo by mutableStateOf("")
        private set

    /** Explicación del contenido (obligatoria). */
    var explicacion by mutableStateOf("")
        private set

    /** Ejemplos (opcional). */
    var ejemplos by mutableStateOf("")
        private set

    /** Instrucciones (opcional). */
    var instrucciones by mutableStateOf("")
        private set

    /** Orden dentro del objetivo (opcional, mayor que cero). */
    var orden by mutableStateOf("")
        private set

    /** Origen del material; `OWN` por defecto. */
    var origen by mutableStateOf(ContentOrigin.OWN)
        private set

    /** Indica que se están cargando los objetivos. */
    var isLoading by mutableStateOf(false)
        private set

    /** Indica que se está guardando el contenido. */
    var isSaving by mutableStateOf(false)
        private set

    /** Fallo al cargar los objetivos; se muestra con `ErrorState`. */
    var loadError by mutableStateOf<ApiError?>(null)
        private set

    /** Mensaje de validación o del backend, visible en el formulario. */
    var error by mutableStateOf<String?>(null)
        private set

    private var cargado = false

    /** Cambia el objetivo de aprendizaje elegido. */
    fun onObjectiveChange(objectiveId: Int) {
        selectedObjectiveId = objectiveId
        error = null
    }

    /** Cambia el título. */
    fun onTituloChange(value: String) {
        titulo = value
        error = null
    }

    /** Cambia la explicación. */
    fun onExplicacionChange(value: String) {
        explicacion = value
        error = null
    }

    /** Cambia los ejemplos. */
    fun onEjemplosChange(value: String) {
        ejemplos = value
        error = null
    }

    /** Cambia las instrucciones. */
    fun onInstruccionesChange(value: String) {
        instrucciones = value
        error = null
    }

    /** Cambia el orden dentro del objetivo. */
    fun onOrdenChange(value: String) {
        orden = value
        error = null
    }

    /** Cambia el origen del material. */
    fun onOrigenChange(value: ContentOrigin) {
        origen = value
        error = null
    }

    /** Carga los objetivos de la unidad. Solo se ejecuta la primera vez. */
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

    /** Crea el contenido y avisa a la pantalla cuando termina. */
    fun guardar(onSaved: () -> Unit) {
        val objectiveId = selectedObjectiveId

        if (objectiveId == null) {
            error = "Selecciona un objetivo de aprendizaje."
            return
        }

        val tituloLimpio = titulo.trim()
        val explicacionLimpia = explicacion.trim()
        val ordenValue = orden.trim()

        when {
            tituloLimpio.isBlank() -> {
                error = "El título es obligatorio."
                return
            }

            tituloLimpio.length > MAX_TITLE_LENGTH -> {
                error = "El título admite hasta $MAX_TITLE_LENGTH caracteres."
                return
            }

            explicacionLimpia.isBlank() -> {
                error = "La explicación es obligatoria."
                return
            }
        }

        val ordenNumber = if (ordenValue.isEmpty()) null else ordenValue.toIntOrNull()

        if (ordenValue.isNotEmpty() && (ordenNumber == null || ordenNumber <= 0)) {
            error = "El orden debe ser un número mayor que cero."
            return
        }

        viewModelScope.launch {
            isSaving = true
            error = null

            val result = contentRepository.create(
                CreateContentRequest(
                    oaId = objectiveId,
                    titulo = tituloLimpio,
                    explicacion = explicacionLimpia,
                    origen = origen,
                    ejemplos = ejemplos.trim().takeIf { it.isNotEmpty() },
                    instrucciones = instrucciones.trim().takeIf { it.isNotEmpty() },
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

    /** Traduce el fallo del repositorio a un mensaje para el formulario. */
    private fun mensajeDeError(error: ApiError?): String = when (error) {
        is ApiError.NotFound -> "El objetivo de aprendizaje ya no existe."
        is ApiError.BadRequest -> error.message
        is ApiError.Network -> "Sin conexión. Revisa tu red e intenta de nuevo."
        is ApiError.Unauthorized -> "Tu sesión venció. Vuelve a iniciar sesión."
        is ApiError.Unknown -> error.message
        null -> "No se pudo guardar el contenido."
        else -> "No se pudo guardar el contenido."
    }

    companion object {

        /** Longitud máxima del título según el contrato. */
        private const val MAX_TITLE_LENGTH = 200

        /** Factoría del formulario con los repositorios y la unidad. */
        fun factory(
            contentRepository: ContentRepository,
            learningObjectiveRepository: LearningObjectiveRepository,
            unitId: Int
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ContentFormViewModel(
                    contentRepository = contentRepository,
                    learningObjectiveRepository = learningObjectiveRepository,
                    unitId = unitId
                )
            }
        }
    }
}

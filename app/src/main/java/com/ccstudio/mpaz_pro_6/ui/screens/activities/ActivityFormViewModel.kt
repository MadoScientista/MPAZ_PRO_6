package com.ccstudio.mpaz_pro_6.ui.screens.activities

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ccstudio.mpaz_pro_6.data.enums.ActivityType
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.Activity
import com.ccstudio.mpaz_pro_6.data.model.Assignment
import com.ccstudio.mpaz_pro_6.data.model.Content
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateActivityRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateActivityRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateAssignmentRequest
import com.ccstudio.mpaz_pro_6.data.repository.ActivityRepository
import com.ccstudio.mpaz_pro_6.data.repository.AssignmentRepository
import com.ccstudio.mpaz_pro_6.data.repository.ContentRepository
import kotlinx.coroutines.launch

/**
 * Estado del formulario de creación o edición de una actividad.
 *
 * Carga los contenidos de la unidad para el selector opcional y las actividades
 * de la unidad para detectar si ya existe una `PRUEBA_FINAL` (solo se admite una
 * por unidad). Si [activityId] tiene valor, entra en modo edición.
 * Si la pantalla se abrió desde un contenido, [lockedContentId]
 * llega con su id y el contenido queda preseleccionado y bloqueado.
 *
 * Cuando la actividad es o pasa a ser una `PRUEBA_FINAL`, permite consultar y
 * editar la cantidad de `maxIntentos` asociada a la asignación de la unidad.
 */
class ActivityFormViewModel(
    private val activityRepository: ActivityRepository,
    private val contentRepository: ContentRepository,
    private val assignmentRepository: AssignmentRepository,
    private val unitId: Int,
    private val lockedContentId: Int?,
    private val activityId: Int? = null
) : ViewModel() {

    /** Actividad en edición si existe. */
    var activity by mutableStateOf<Activity?>(null)
        private set

    /** Asignación de la unidad para actualizar `maxIntentos`. */
    private var assignment: Assignment? = null

    /** Contenidos de la unidad, ordenados por objetivo y orden. */
    var contenidos by mutableStateOf<List<Content>>(emptyList())
        private set

    /** Título de la actividad (obligatorio, máximo 200 caracteres). */
    var titulo by mutableStateOf("")
        private set

    /** Tipo de actividad; `ACTIVITY` por defecto. */
    var tipo by mutableStateOf(ActivityType.ACTIVITY)
        private set

    /** Orden dentro de la unidad (opcional, mayor que cero). */
    var orden by mutableStateOf("")
        private set

    /** Cantidad de intentos máximos para la prueba final. */
    var maxIntentos by mutableStateOf("")
        private set

    /**
     * Contenido asociado; `null` significa actividad transversal.
     *
     * Si el formulario se abrió desde un contenido, arranca con ese id y no se
     * puede cambiar.
     */
    var contenidoId by mutableStateOf(lockedContentId)
        private set

    /** Indica si está en modo edición. */
    val isEditMode: Boolean
        get() = activityId != null

    /** Indica que la unidad ya tiene una `PRUEBA_FINAL` distinta de esta. */
    var hasFinalTest by mutableStateOf(false)
        private set

    /** Indica que se están cargando contenidos y actividades. */
    var isLoading by mutableStateOf(true)
        private set

    /** Indica que se está guardando la actividad. */
    var isSaving by mutableStateOf(false)
        private set

    /** Fallo al cargar el contexto del formulario; se muestra con `ErrorState`. */
    var loadError by mutableStateOf<ApiError?>(null)
        private set

    /** Mensaje de validación o del backend, visible en el formulario. */
    var error by mutableStateOf<String?>(null)
        private set

    private var cargado = false

    /** Indica si el contenido está fijado por la pantalla de origen. */
    val isContentLocked: Boolean
        get() = lockedContentId != null

    /** Cambia el título. */
    fun onTituloChange(value: String) {
        titulo = value
        error = null
    }

    /** Cambia el tipo de actividad. */
    fun onTipoChange(value: ActivityType) {
        tipo = value
        // La prueba final se propone sin contenido.
        if (value == ActivityType.FINAL_TEST) {
            contenidoId = null
        }
        error = null
    }

    /** Cambia el orden dentro de la unidad. */
    fun onOrdenChange(value: String) {
        orden = value
        error = null
    }

    /** Cambia los intentos máximos. */
    fun onMaxIntentosChange(value: String) {
        maxIntentos = value.filter { it.isDigit() }
        error = null
    }

    /**
     * Cambia el contenido asociado.
     *
     * No tiene efecto cuando el contenido está bloqueado por la pantalla de
     * origen.
     */
    fun onContenidoChange(value: Int?) {
        if (isContentLocked) return
        contenidoId = value
        error = null
    }

    /** Carga los contenidos de la unidad, la actividad y la asignación. */
    fun cargar() {
        if (cargado) return

        viewModelScope.launch {
            isLoading = true
            loadError = null

            val contentsResult = contentRepository.list()

            if (contentsResult.isFailure) {
                loadError = contentsResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            contenidos = contentsResult.getOrDefault(emptyList())
                .filter { it.unidadId == unitId }
                .sortedWith(compareBy({ it.oaId }, { it.orden }, { it.id }))

            val activitiesResult = activityRepository.list(unitId = unitId)

            if (activitiesResult.isFailure) {
                loadError = activitiesResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val activities = activitiesResult.getOrDefault(emptyList())

            hasFinalTest = activities.any {
                it.tipo == ActivityType.FINAL_TEST && it.id != activityId
            }

            if (activityId != null) {
                val act = activities.firstOrNull { it.id == activityId }
                    ?: activityRepository.getById(activityId).getOrNull()

                if (act != null) {
                    activity = act
                    titulo = act.titulo
                    tipo = act.tipo
                    orden = act.orden.toString()
                    contenidoId = act.contenidoId
                }
            }

            val assignmentsResult = assignmentRepository.list(unitId = unitId)
            val assignments = assignmentsResult.getOrDefault(emptyList())
            assignment = assignments.firstOrNull { it.unidadId == unitId }

            if (assignment != null && maxIntentos.isEmpty()) {
                maxIntentos = assignment?.maxIntentos?.toString().orEmpty()
            }

            cargado = true
            isLoading = false
        }
    }

    /** Crea o actualiza la actividad y avisa a la pantalla cuando termina. */
    fun guardar(onSaved: () -> Unit) {
        val tituloLimpio = titulo.trim()
        val ordenValue = orden.trim()
        val maxIntentosValue = maxIntentos.trim()

        when {
            tituloLimpio.isBlank() -> {
                error = "El título es obligatorio."
                return
            }

            tituloLimpio.length > MAX_TITLE_LENGTH -> {
                error = "El título admite hasta $MAX_TITLE_LENGTH caracteres."
                return
            }

            tipo == ActivityType.FINAL_TEST && hasFinalTest -> {
                error = "La unidad ya tiene una prueba final."
                return
            }
        }

        val ordenNumber = if (ordenValue.isEmpty()) null else ordenValue.toIntOrNull()

        if (ordenValue.isNotEmpty() && (ordenNumber == null || ordenNumber <= 0)) {
            error = "El orden debe ser un número mayor que cero."
            return
        }

        val maxIntentosNumber = if (maxIntentosValue.isEmpty()) null else maxIntentosValue.toIntOrNull()

        if (tipo == ActivityType.FINAL_TEST && maxIntentosValue.isNotEmpty() && (maxIntentosNumber == null || maxIntentosNumber <= 0)) {
            error = "Los intentos máximos deben ser un número mayor que cero."
            return
        }

        viewModelScope.launch {
            isSaving = true
            error = null

            val result = if (activityId != null) {
                activityRepository.update(
                    id = activityId,
                    request = UpdateActivityRequest(
                        titulo = tituloLimpio,
                        tipo = tipo,
                        orden = ordenNumber,
                        contenidoId = contenidoId,
                        desasociarContenido = if (contenidoId == null) true else null
                    )
                )
            } else {
                activityRepository.create(
                    CreateActivityRequest(
                        unidadId = unitId,
                        titulo = tituloLimpio,
                        tipo = tipo,
                        contenidoId = contenidoId,
                        orden = ordenNumber
                    )
                )
            }

            if (result.isFailure) {
                isSaving = false
                error = mensajeDeError(result.exceptionOrNull() as? ApiError)
                return@launch
            }

            // Si es prueba final, actualizar maxIntentos en la asignación correspondiente
            if (tipo == ActivityType.FINAL_TEST) {
                val currentAssign = assignment ?: assignmentRepository.list(unitId = unitId)
                    .getOrDefault(emptyList()).firstOrNull { it.unidadId == unitId }

                if (currentAssign != null) {
                    assignmentRepository.update(
                        id = currentAssign.id,
                        request = UpdateAssignmentRequest(
                            maxIntentos = maxIntentosNumber,
                            pruebaFinalHabilitada = true
                        )
                    )
                }
            }

            isSaving = false
            onSaved()
        }
    }

    /** Traduce el fallo del repositorio a un mensaje para el formulario. */
    private fun mensajeDeError(error: ApiError?): String = when (error) {
        is ApiError.Conflict -> error.message
        is ApiError.NotFound -> "La unidad o el contenido ya no existe."
        is ApiError.BadRequest -> error.message
        is ApiError.Network -> "Sin conexión. Revisa tu red e intenta de nuevo."
        is ApiError.Unauthorized -> "Tu sesión venció. Vuelve a iniciar sesión."
        is ApiError.Unknown -> error.message
        null -> "No se pudo guardar la actividad."
    }

    companion object {

        /** Longitud máxima del título según el contrato. */
        private const val MAX_TITLE_LENGTH = 200

        /** Factoría del formulario con los repositorios y la unidad. */
        fun factory(
            activityRepository: ActivityRepository,
            contentRepository: ContentRepository,
            assignmentRepository: AssignmentRepository,
            unitId: Int,
            lockedContentId: Int?,
            activityId: Int? = null
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ActivityFormViewModel(
                    activityRepository = activityRepository,
                    contentRepository = contentRepository,
                    assignmentRepository = assignmentRepository,
                    unitId = unitId,
                    lockedContentId = lockedContentId,
                    activityId = activityId
                )
            }
        }
    }
}

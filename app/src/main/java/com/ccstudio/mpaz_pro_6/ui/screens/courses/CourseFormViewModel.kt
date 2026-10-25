package com.ccstudio.mpaz_pro_6.ui.screens.courses

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateCourseRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateCourseRequest
import com.ccstudio.mpaz_pro_6.data.repository.CourseRepository
import kotlinx.coroutines.launch

/**
 * Estado del formulario de curso.
 *
 * Sin `courseId` crea; con id carga el curso y edita. En edición guarda los
 * valores originales para enviar solo los campos modificados, porque el `PUT` es
 * parcial y el backend exige al menos un campo.
 */
class CourseFormViewModel(
    private val courseRepository: CourseRepository,
    private val courseId: Int?
) : ViewModel() {

    /** Nombre del curso. */
    var nombre by mutableStateOf("")
        private set

    /** Año del curso como texto, para validarlo en el formulario. */
    var anio by mutableStateOf("")
        private set

    /** Indica si se está cargando el curso a editar. */
    var isLoading by mutableStateOf(false)
        private set

    /** Indica que se está guardando. */
    var isSaving by mutableStateOf(false)
        private set

    /** Mensaje de error visible en el formulario. */
    var error by mutableStateOf<String?>(null)
        private set

    /** `true` cuando se edita un curso existente. */
    val isEditMode: Boolean = courseId != null

    private var cargado = false
    private var originalNombre = ""
    private var originalAnio: Int? = null

    /** Cambia el nombre. */
    fun onNombreChange(value: String) {
        nombre = value
        error = null
    }

    /** Cambia el año. */
    fun onAnioChange(value: String) {
        anio = value.filter { it.isDigit() }
        error = null
    }

    /** Carga el curso cuando se edita. En creación no hace nada. */
    fun cargar() {
        if (courseId == null || cargado) return

        viewModelScope.launch {
            isLoading = true
            error = null

            val result = courseRepository.getById(courseId)

            if (result.isFailure) {
                error = mensajeDeError(result.exceptionOrNull() as? ApiError)
                isLoading = false
                return@launch
            }

            val course = result.getOrNull()
            nombre = course?.nombre.orEmpty()
            anio = course?.anio?.toString().orEmpty()
            originalNombre = nombre
            originalAnio = course?.anio
            cargado = true
            isLoading = false
        }
    }

    /** Guarda el curso y avisa a la pantalla cuando termina. */
    fun guardar(onSaved: () -> Unit) {
        val nombreLimpio = nombre.trim()
        val anioInt = anio.trim().toIntOrNull()

        when {
            nombreLimpio.isBlank() -> {
                error = "El nombre es obligatorio."
                return
            }

            nombreLimpio.length > MAX_NAME_LENGTH -> {
                error = "El nombre admite hasta $MAX_NAME_LENGTH caracteres."
                return
            }

            anioInt == null || anioInt !in MIN_YEAR..MAX_YEAR -> {
                error = "El año debe estar entre $MIN_YEAR y $MAX_YEAR."
                return
            }
        }

        viewModelScope.launch {
            isSaving = true
            error = null

            val result = if (courseId == null) {
                courseRepository.create(
                    CreateCourseRequest(nombre = nombreLimpio, anio = anioInt)
                )
            } else {
                val nombreCambiado = nombreLimpio.takeIf { it != originalNombre }
                val anioCambiado = anioInt.takeIf { it != originalAnio }

                if (nombreCambiado == null && anioCambiado == null) {
                    error = "No hay cambios para guardar."
                    isSaving = false
                    return@launch
                }

                courseRepository.update(
                    courseId,
                    UpdateCourseRequest(
                        nombre = nombreCambiado,
                        anio = anioCambiado
                    )
                )
            }

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
        is ApiError.Conflict -> "Ya existe un curso con ese nombre y año."
        is ApiError.BadRequest -> error.message
        is ApiError.NotFound -> "El curso ya no existe."
        is ApiError.Network -> "Sin conexión. Revisa tu red e intenta de nuevo."
        is ApiError.Unauthorized -> "Tu sesión venció. Vuelve a iniciar sesión."
        is ApiError.Unknown -> error.message
        null -> "No se pudo guardar el curso."
    }

    companion object {

        /** Factoría del formulario con el repositorio y el id opcional. */
        fun factory(
            courseRepository: CourseRepository,
            courseId: Int?
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                CourseFormViewModel(
                    courseRepository = courseRepository,
                    courseId = courseId
                )
            }
        }

        private const val MAX_NAME_LENGTH = 50
        private const val MIN_YEAR = 2000
        private const val MAX_YEAR = 2100
    }
}

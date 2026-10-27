package com.ccstudio.mpaz_pro_6.ui.screens.courses

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ccstudio.mpaz_pro_6.data.enums.UserRole
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.Course
import com.ccstudio.mpaz_pro_6.data.model.User
import com.ccstudio.mpaz_pro_6.data.repository.CourseRepository
import com.ccstudio.mpaz_pro_6.data.repository.UserRepository
import kotlinx.coroutines.launch

/**
 * Estado del detalle de un curso.
 *
 * Carga el curso con `GET /api/cursos/{id}` y sus estudiantes con
 * `GET /api/usuarios`, filtrando en cliente por rol `ESTUDIANTE` y `cursoId`.
 * El contrato no expone un listado de estudiantes por curso.
 */
class CourseDetailViewModel(
    private val courseRepository: CourseRepository,
    private val userRepository: UserRepository,
    private val courseId: Int
) : ViewModel() {

    /** Curso cargado, o `null` mientras carga o si falló. */
    var course by mutableStateOf<Course?>(null)
        private set

    /** Estudiantes del curso, ordenados por nombre. */
    var estudiantes by mutableStateOf<List<User>>(emptyList())
        private set

    /** Indica que el detalle se está cargando. */
    var isLoading by mutableStateOf(false)
        private set

    /** Error de la última carga, si lo hubo. */
    var error by mutableStateOf<ApiError?>(null)
        private set

    /** Carga el curso y sus estudiantes. */
    fun cargar() {
        viewModelScope.launch {
            isLoading = true
            error = null

            val courseResult = courseRepository.getById(courseId)

            if (courseResult.isFailure) {
                error = courseResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            course = courseResult.getOrNull()

            val usersResult = userRepository.list()

            if (usersResult.isFailure) {
                error = usersResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            estudiantes = usersResult.getOrDefault(emptyList())
                .filter { it.rol == UserRole.ESTUDIANTE && it.cursoId == courseId }
                .sortedBy { it.nombre.lowercase() }

            isLoading = false
        }
    }

    companion object {

        /** Factoría del ViewModel con los repositorios y el id del curso. */
        fun factory(
            courseRepository: CourseRepository,
            userRepository: UserRepository,
            courseId: Int
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                CourseDetailViewModel(
                    courseRepository = courseRepository,
                    userRepository = userRepository,
                    courseId = courseId
                )
            }
        }
    }
}

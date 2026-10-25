package com.ccstudio.mpaz_pro_6.ui.screens.users

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.Course
import com.ccstudio.mpaz_pro_6.data.model.User
import com.ccstudio.mpaz_pro_6.data.repository.CourseRepository
import com.ccstudio.mpaz_pro_6.data.repository.UserRepository
import kotlinx.coroutines.launch

/**
 * Estado del listado de usuarios.
 *
 * Carga `GET /api/usuarios` y el catálogo de cursos para resolver el nombre del
 * curso de cada estudiante. El filtro por rol es client-side porque el backend no
 * expone ese filtro. Si falla la carga de cursos, el listado de usuarios se
 * conserva y los estudiantes se muestran sin curso.
 */
class UserListViewModel(
    private val userRepository: UserRepository,
    private val courseRepository: CourseRepository
) : ViewModel() {

    /** Usuarios ordenados por id. */
    var usuarios by mutableStateOf<List<User>>(emptyList())
        private set

    /** Cursos, para resolver el curso de los estudiantes. */
    var cursos by mutableStateOf<List<Course>>(emptyList())
        private set

    /** Indica que el listado se está cargando. */
    var isLoading by mutableStateOf(false)
        private set

    /** Error de la última carga, si lo hubo. */
    var error by mutableStateOf<ApiError?>(null)
        private set

    /** Mensaje de una operación de borrado, para mostrarlo en el listado. */
    var mensajeOperacion by mutableStateOf<String?>(null)
        private set

    /** Carga los usuarios y, como apoyo, los cursos. */
    fun cargar() {
        viewModelScope.launch {
            isLoading = true
            error = null

            val usuariosResult = userRepository.list()

            if (usuariosResult.isFailure) {
                error = usuariosResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            usuarios = usuariosResult.getOrDefault(emptyList())

            // El curso es un dato de apoyo: si falla, no se bloquea el listado.
            cursos = courseRepository.list().getOrDefault(emptyList())

            isLoading = false
        }
    }

    /**
     * Elimina un usuario y recarga el listado.
     *
     * Un `409 Conflict` significa que tiene intentos, informes, unidades
     * autorizadas o asignaciones; el mensaje lo explica en la pantalla.
     */
    fun eliminar(id: Int) {
        viewModelScope.launch {
            mensajeOperacion = null

            val result = userRepository.delete(id)

            if (result.isFailure) {
                mensajeOperacion = when (val fallo = result.exceptionOrNull() as? ApiError) {
                    is ApiError.Conflict ->
                        "No se puede eliminar: tiene intentos, informes, unidades autorizadas o asignaciones."
                    is ApiError.NotFound -> "El usuario ya no existe."
                    is ApiError.Network -> "Sin conexión. Revisa tu red e intenta de nuevo."
                    is ApiError.Unauthorized -> "Tu sesión venció. Vuelve a iniciar sesión."
                    is ApiError.BadRequest -> fallo.message
                    is ApiError.Unknown -> fallo.message
                    null -> "No se pudo eliminar el usuario."
                }
                return@launch
            }

            cargar()
        }
    }

    /** Oculta el mensaje de la última operación. */
    fun limpiarMensaje() {
        mensajeOperacion = null
    }

    companion object {

        /** Factoría del ViewModel con usuarios y cursos. */
        fun factory(
            userRepository: UserRepository,
            courseRepository: CourseRepository
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                UserListViewModel(
                    userRepository = userRepository,
                    courseRepository = courseRepository
                )
            }
        }
    }
}
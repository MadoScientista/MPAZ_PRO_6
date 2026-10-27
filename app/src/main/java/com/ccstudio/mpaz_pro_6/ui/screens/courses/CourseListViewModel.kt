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
import com.ccstudio.mpaz_pro_6.data.repository.CourseRepository
import com.ccstudio.mpaz_pro_6.data.repository.UserRepository
import kotlinx.coroutines.launch

/**
 * Estado del listado de cursos.
 *
 * El filtro por año se delega al backend (`?anio`), porque así lo exige el
 * contrato. La lista de años disponibles se obtiene de la primera carga sin
 * filtro, para que el selector no pierda opciones al aplicar un año.
 *
 * Para el docente, [teacherId] no es nulo: el listado se limita a sus cursos y
 * el filtro por año se aplica en cliente, porque el endpoint de cursos por
 * usuario no recibe `?anio`.
 */
class CourseListViewModel(
    private val courseRepository: CourseRepository,
    private val userRepository: UserRepository,
    private val teacherId: Int? = null
) : ViewModel() {

    /** Cursos del año seleccionado, o todos si no hay filtro. */
    var cursos by mutableStateOf<List<Course>>(emptyList())
        private set

    /** Años presentes en el catálogo, de mayor a menor. */
    var aniosDisponibles by mutableStateOf<List<Int>>(emptyList())
        private set

    /** Cantidad de estudiantes por curso, para la tarjeta. */
    var estudiantesPorCurso by mutableStateOf<Map<Int, Int>>(emptyMap())
        private set

    /** Año seleccionado; `null` significa «Todos». */
    var anioSeleccionado by mutableStateOf<Int?>(null)
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

    private var aniosCargados = false

    /** Cambia el filtro de año y recarga desde el backend. */
    fun seleccionarAnio(anio: Int?) {
        if (anioSeleccionado == anio) return

        anioSeleccionado = anio
        cargar()
    }

    /** Carga los cursos aplicando el filtro actual. */
    fun cargar() {
        viewModelScope.launch {
            isLoading = true
            error = null

            val usersResult = userRepository.list()

            if (usersResult.isFailure) {
                error = usersResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            estudiantesPorCurso = usersResult.getOrDefault(emptyList())
                .filter { it.rol == UserRole.ESTUDIANTE && it.cursoId != null }
                .groupBy { it.cursoId!! }
                .mapValues { (_, students) -> students.size }

            if (teacherId != null) {
                val cursosResult = userRepository.listCourses(teacherId)

                if (cursosResult.isFailure) {
                    error = cursosResult.exceptionOrNull() as? ApiError
                    isLoading = false
                    return@launch
                }

                val todos = cursosResult.getOrDefault(emptyList())
                aniosDisponibles = todos.map { it.anio }.distinct().sortedDescending()
                cursos = todos.filter { curso ->
                    anioSeleccionado == null || curso.anio == anioSeleccionado
                }
                isLoading = false
                return@launch
            }

            if (!aniosCargados) {
                val todosResult = courseRepository.list()

                if (todosResult.isFailure) {
                    error = todosResult.exceptionOrNull() as? ApiError
                    isLoading = false
                    return@launch
                }

                val todos = todosResult.getOrDefault(emptyList())
                aniosDisponibles = todos.map { it.anio }.distinct().sortedDescending()
                aniosCargados = true

                if (anioSeleccionado == null) {
                    cursos = todos
                    isLoading = false
                    return@launch
                }
            }

            val result = courseRepository.list(anioSeleccionado)

            if (result.isFailure) {
                error = result.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            cursos = result.getOrDefault(emptyList())

            // Sin filtro, la lista es el catálogo completo: refresca los años.
            if (anioSeleccionado == null) {
                aniosDisponibles = cursos.map { it.anio }.distinct().sortedDescending()
            }

            isLoading = false
        }
    }

    /**
     * Elimina un curso y recarga el listado.
     *
     * Un `409 Conflict` significa que todavía tiene estudiantes o asignaciones;
     * el mensaje lo explica en la pantalla.
     */
    fun eliminar(id: Int) {
        viewModelScope.launch {
            mensajeOperacion = null

            val result = courseRepository.delete(id)

            if (result.isFailure) {
                mensajeOperacion = when (val fallo = result.exceptionOrNull() as? ApiError) {
                    is ApiError.Conflict -> "No se puede eliminar: tiene estudiantes o asignaciones."
                    is ApiError.NotFound -> "El curso ya no existe."
                    is ApiError.Network -> "Sin conexión. Revisa tu red e intenta de nuevo."
                    is ApiError.Unauthorized -> "Tu sesión venció. Vuelve a iniciar sesión."
                    is ApiError.BadRequest -> fallo.message
                    is ApiError.Unknown -> fallo.message
                    null -> "No se pudo eliminar el curso."
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

        /** Factoría del ViewModel con el repositorio de cursos. */
        fun factory(
            courseRepository: CourseRepository,
            userRepository: UserRepository,
            teacherId: Int? = null
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                CourseListViewModel(
                    courseRepository = courseRepository,
                    userRepository = userRepository,
                    teacherId = teacherId
                )
            }
        }
    }
}
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
import com.ccstudio.mpaz_pro_6.data.model.Course
import com.ccstudio.mpaz_pro_6.data.repository.CourseRepository
import kotlinx.coroutines.launch

/**
 * Estado del listado de cursos.
 *
 * El filtro por año se delega al backend (`?anio`), porque así lo exige el
 * contrato. La lista de años disponibles se obtiene de la primera carga sin
 * filtro, para que el selector no pierda opciones al aplicar un año.
 */
class CourseListViewModel(
    private val courseRepository: CourseRepository
) : ViewModel() {

    /** Cursos del año seleccionado, o todos si no hay filtro. */
    var cursos by mutableStateOf<List<Course>>(emptyList())
        private set

    /** Años presentes en el catálogo, de mayor a menor. */
    var aniosDisponibles by mutableStateOf<List<Int>>(emptyList())
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
            isLoading = false
        }
    }

    companion object {

        /** Factoría del ViewModel con el repositorio de cursos. */
        fun factory(
            courseRepository: CourseRepository
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                CourseListViewModel(courseRepository = courseRepository)
            }
        }
    }
}

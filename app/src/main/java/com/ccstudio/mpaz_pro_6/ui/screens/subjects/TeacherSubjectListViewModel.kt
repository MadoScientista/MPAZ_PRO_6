package com.ccstudio.mpaz_pro_6.ui.screens.subjects

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.data.repository.DocenciaRepository
import com.ccstudio.mpaz_pro_6.data.repository.UnitRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Estado del listado de asignaturas del docente.
 *
 * Solo muestra las asignaturas que el docente dicta según sus docencias. El
 * listado es de solo lectura: el docente no crea, edita ni elimina asignaturas.
 * Las unidades se cargan para mostrar el total por asignatura.
 */
class TeacherSubjectListViewModel(
    private val docenciaRepository: DocenciaRepository,
    private val unitRepository: UnitRepository,
    private val teacherId: Int,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate
) : ViewModel() {

    /** Asignaturas que dicta el docente, ordenadas por nombre. */
    var asignaturas by mutableStateOf<List<Subject>>(emptyList())
        private set

    /** Total de unidades del catálogo por asignatura. */
    var estadisticasPorAsignatura by mutableStateOf<Map<Int, SubjectStats>>(emptyMap())
        private set

    /** Cursos en los que se dicta cada asignatura. */
    var cursosPorAsignatura by mutableStateOf<Map<Int, List<String>>>(emptyMap())
        private set

    /** Indica que el listado se está cargando. */
    var isLoading by mutableStateOf(false)
        private set

    /** Error de la última carga, si lo hubo. */
    var error by mutableStateOf<ApiError?>(null)
        private set

    /** Carga las docencias y las unidades de sus asignaturas. */
    fun cargar() {
        viewModelScope.launch(dispatcher) {
            isLoading = true
            error = null

            val docenciasResult = docenciaRepository.list(teacherId)
            if (docenciasResult.isFailure) {
                error = docenciasResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val unidadesResult = unitRepository.list()
            if (unidadesResult.isFailure) {
                error = unidadesResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val docencias = docenciasResult.getOrDefault(emptyList())
            val subjectIds = docencias.map { it.asignaturaId }.toSet()
            val unidades = unidadesResult.getOrDefault(emptyList())
                .filter { it.asignaturaId in subjectIds }

            asignaturas = docencias
                .distinctBy { it.asignaturaId }
                .map { Subject(id = it.asignaturaId, nombre = it.asignaturaNombre) }
                .sortedBy { it.nombre }
            cursosPorAsignatura = docencias
                .groupBy { it.asignaturaId }
                .mapValues { (_, items) ->
                    items.map { it.cursoNombre }
                        .distinct()
                        .sorted()
                }
            estadisticasPorAsignatura = unidades
                .groupBy { it.asignaturaId }
                .mapValues { (_, items) ->
                    SubjectStats(
                        total = items.size,
                        pending = items.count { it.estado.esPendienteDeRevision() }
                    )
                }

            isLoading = false
        }
    }

    companion object {

        /** Factoría del ViewModel con los repositorios y el docente. */
        fun factory(
            docenciaRepository: DocenciaRepository,
            unitRepository: UnitRepository,
            teacherId: Int
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                TeacherSubjectListViewModel(
                    docenciaRepository = docenciaRepository,
                    unitRepository = unitRepository,
                    teacherId = teacherId
                )
            }
        }
    }
}

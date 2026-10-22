package com.ccstudio.mpaz_pro_6.ui.screens.units

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
import com.ccstudio.mpaz_pro_6.data.model.Content
import com.ccstudio.mpaz_pro_6.data.model.ContentResource
import com.ccstudio.mpaz_pro_6.data.model.LearningObjective
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.data.repository.ActivityRepository
import com.ccstudio.mpaz_pro_6.data.repository.ContentRepository
import com.ccstudio.mpaz_pro_6.data.repository.ContentResourceRepository
import com.ccstudio.mpaz_pro_6.data.repository.LearningObjectiveRepository
import com.ccstudio.mpaz_pro_6.data.repository.SubjectRepository
import com.ccstudio.mpaz_pro_6.data.repository.UnitRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Estado de la pantalla de detalle de una unidad.
 *
 * Muestra la información de la unidad, su asignatura y la estructura pedagógica
 * (objetivos, contenidos, recursos y actividades) para revisión efectiva por parte
 * del UTP. Todo el contenido es solo lectura.
 */
class UnitDetailViewModel(
    private val unitRepository: UnitRepository,
    private val subjectRepository: SubjectRepository,
    private val learningObjectiveRepository: LearningObjectiveRepository,
    private val contentRepository: ContentRepository,
    private val contentResourceRepository: ContentResourceRepository,
    private val activityRepository: ActivityRepository,
    private val unitId: Int
) : ViewModel() {

    /** Unidad que se está mostrando. */
    var unidad by mutableStateOf<LearningUnit?>(null)
        private set

    /** Asignatura a la que pertenece la unidad. */
    var asignatura by mutableStateOf<Subject?>(null)
        private set

    /** Objetivos de aprendizaje de la unidad. */
    var objetivos by mutableStateOf<List<LearningObjective>>(emptyList())
        private set

    /** Contenidos agrupados por objetivo de aprendizaje. */
    var contenidosPorObjetivo by mutableStateOf<Map<Int, List<Content>>>(emptyMap())
        private set

    /** Recursos agrupados por contenido. */
    var recursosPorContenido by mutableStateOf<Map<Int, List<ContentResource>>>(emptyMap())
        private set

    /** Actividades de la unidad. */
    var actividades by mutableStateOf<List<Activity>>(emptyList())
        private set

    /** Indica que la pantalla se está cargando. */
    var isLoading by mutableStateOf(false)
        private set

    /** Error de la última carga, si lo hubo. */
    var error by mutableStateOf<ApiError?>(null)
        private set

    /** Carga la unidad, su asignatura y la estructura pedagógica. */
    fun cargar() {
        viewModelScope.launch {
            isLoading = true
            error = null

            // Las llamadas al repositorio se hacen fuera del hilo principal: cuando
            // exista el backend serán peticiones de red y no deben bloquear la UI.
            val unitResult = withContext(Dispatchers.IO) {
                unitRepository.getById(unitId)
            }

            if (unitResult.isFailure) {
                error = unitResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val unit = unitResult.getOrThrow()
            unidad = unit

            val subjectDeferred = async {
                withContext(Dispatchers.IO) { subjectRepository.getById(unit.asignaturaId) }
            }
            val objetivosDeferred = async {
                withContext(Dispatchers.IO) { learningObjectiveRepository.list(unitId = unitId) }
            }
            val actividadesDeferred = async {
                withContext(Dispatchers.IO) { activityRepository.list(unitId = unitId) }
            }

            subjectDeferred.await().onSuccess { asignatura = it }

            val objetivosResult = objetivosDeferred.await()
            if (objetivosResult.isSuccess) {
                val objetivosList = objetivosResult.getOrThrow()
                objetivos = objetivosList

                // Los contenidos de cada objetivo y los recursos de cada contenido se
                // piden en paralelo: pedirlos uno a uno multiplica la espera.
                val porObjetivo = objetivosList.map { objetivo ->
                    async {
                        val contenidos = withContext(Dispatchers.IO) {
                            contentRepository.list(learningObjectiveId = objetivo.id)
                        }.getOrNull().orEmpty()

                        val recursos = contenidos.map { contenido ->
                            async {
                                contenido.id to withContext(Dispatchers.IO) {
                                    contentResourceRepository.list(contentId = contenido.id)
                                }.getOrNull().orEmpty()
                            }
                        }.awaitAll()

                        Triple(objetivo.id, contenidos, recursos.toMap())
                    }
                }.awaitAll()

                contenidosPorObjetivo = porObjetivo.associate { it.first to it.second }
                recursosPorContenido = porObjetivo.flatMap { it.third.toList() }.toMap()
            }

            actividadesDeferred.await().onSuccess { actividades = it.sortedBy { a -> a.orden } }

            isLoading = false
        }
    }

    companion object {

        /** Factoría del ViewModel con los repositorios necesarios. */
        fun factory(
            unitRepository: UnitRepository,
            subjectRepository: SubjectRepository,
            learningObjectiveRepository: LearningObjectiveRepository,
            contentRepository: ContentRepository,
            contentResourceRepository: ContentResourceRepository,
            activityRepository: ActivityRepository,
            unitId: Int
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                UnitDetailViewModel(
                    unitRepository = unitRepository,
                    subjectRepository = subjectRepository,
                    learningObjectiveRepository = learningObjectiveRepository,
                    contentRepository = contentRepository,
                    contentResourceRepository = contentResourceRepository,
                    activityRepository = activityRepository,
                    unitId = unitId
                )
            }
        }
    }
}

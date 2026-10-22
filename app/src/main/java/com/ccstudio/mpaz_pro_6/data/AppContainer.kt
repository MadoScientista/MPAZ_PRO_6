package com.ccstudio.mpaz_pro_6.data

import com.ccstudio.mpaz_pro_6.data.mock.MockActivityRepository
import com.ccstudio.mpaz_pro_6.data.mock.MockContentRepository
import com.ccstudio.mpaz_pro_6.data.mock.MockContentResourceRepository
import com.ccstudio.mpaz_pro_6.data.mock.MockCourseRepository
import com.ccstudio.mpaz_pro_6.data.mock.MockData
import com.ccstudio.mpaz_pro_6.data.mock.MockLearningObjectiveRepository
import com.ccstudio.mpaz_pro_6.data.mock.MockSubjectRepository
import com.ccstudio.mpaz_pro_6.data.mock.MockUnitRepository
import com.ccstudio.mpaz_pro_6.data.mock.MockUserRepository
import com.ccstudio.mpaz_pro_6.data.repository.ActivityRepository
import com.ccstudio.mpaz_pro_6.data.repository.ContentRepository
import com.ccstudio.mpaz_pro_6.data.repository.ContentResourceRepository
import com.ccstudio.mpaz_pro_6.data.repository.CourseRepository
import com.ccstudio.mpaz_pro_6.data.repository.LearningObjectiveRepository
import com.ccstudio.mpaz_pro_6.data.repository.SubjectRepository
import com.ccstudio.mpaz_pro_6.data.repository.UnitRepository
import com.ccstudio.mpaz_pro_6.data.repository.UserRepository
import com.ccstudio.mpaz_pro_6.data.session.Session

/**
 * Contenedor de dependencias de la app.
 *
 * Es el único punto donde se decide qué implementación de cada repositorio usa
 * la UI. Hoy son las de `data/mock/`, que sirven datos en memoria; cuando exista
 * el backend se escribirán las de `data/remote/` implementando las mismas
 * interfaces y **solo cambiará esta clase**.
 */
class AppContainer {

    /** Base de datos en memoria compartida por los repositorios. */
    val store = MockData.crearStore()

    /** Sesión local de la app. */
    val session = Session()

    /** Repositorio de asignaturas. */
    val subjectRepository: SubjectRepository = MockSubjectRepository(store)

    /** Repositorio de cursos. */
    val courseRepository: CourseRepository = MockCourseRepository(store)

    /** Repositorio de unidades de aprendizaje. */
    val unitRepository: UnitRepository = MockUnitRepository(store)

    /** Repositorio de usuarios. */
    val userRepository: UserRepository = MockUserRepository(store)

    /** Repositorio de objetivos de aprendizaje. */
    val learningObjectiveRepository: LearningObjectiveRepository = MockLearningObjectiveRepository(store)

    /** Repositorio de contenidos. */
    val contentRepository: ContentRepository = MockContentRepository(store)

    /** Repositorio de recursos de contenido. */
    val contentResourceRepository: ContentResourceRepository = MockContentResourceRepository(store)

    /** Repositorio de actividades. */
    val activityRepository: ActivityRepository = MockActivityRepository(store)
}

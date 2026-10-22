package com.ccstudio.mpaz_pro_6.data

import com.ccstudio.mpaz_pro_6.data.mock.MockCourseRepository
import com.ccstudio.mpaz_pro_6.data.mock.MockData
import com.ccstudio.mpaz_pro_6.data.mock.MockSubjectRepository
import com.ccstudio.mpaz_pro_6.data.mock.MockUnitRepository
import com.ccstudio.mpaz_pro_6.data.mock.MockUserRepository
import com.ccstudio.mpaz_pro_6.data.repository.CourseRepository
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
 *
 * No se usa un contenedor de inyección de dependencias externo (Hilt, Koin): con
 * este volumen de dependencias, una claseManual es suficiente y evita agregar
 * dependencias y.annotation processing al proyecto.
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
}

package com.ccstudio.mpaz_pro_6.data.mock

import com.ccstudio.mpaz_pro_6.data.model.Assignment
import com.ccstudio.mpaz_pro_6.data.model.Course
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.data.model.User
import kotlinx.coroutines.sync.Mutex

/**
 * Base de datos en memoria de la app.
 *
 * Es la fuente única de verdad mientras no exista el backend: todos los
 * repositorios mock comparten estas listas, de modo que las reglas que cruzan
 * entidades (el borrado en cascada de los recursos de un contenido, el `409`
 * de una unidad que tiene objetivos asociados, los cursos de un docente) se
 * resuelven con datos coherentes y no con repositorios aislados.
 *
 * `mutex` serializa las escrituras: los repositorios son `suspend` y pueden
 * llamarse desde varias corrutinas.
 *
 * `passwords` guarda la contraseña de cada usuario por id. El modelo `User` no
 * la incluye porque la contraseña nunca forma parte de una respuesta del
 * backend, pero la sesión se resuelve en local mientras no exista login.
 */
class MockStore(
    val subjects: MutableList<Subject> = mutableListOf(),
    val courses: MutableList<Course> = mutableListOf(),
    val units: MutableList<LearningUnit> = mutableListOf(),
    val assignments: MutableList<Assignment> = mutableListOf(),
    val users: MutableList<User> = mutableListOf(),
    val passwords: MutableMap<Int, String> = mutableMapOf(),
    val mutex: Mutex = Mutex()
) {

    /** Siguiente id disponible por entidad. */
    var nextSubjectId: Int = (subjects.maxOfOrNull { it.id } ?: 0) + 1
    var nextCourseId: Int = (courses.maxOfOrNull { it.id } ?: 0) + 1
    var nextUnitId: Int = (units.maxOfOrNull { it.id } ?: 0) + 1
    var nextAssignmentId: Int = (assignments.maxOfOrNull { it.id } ?: 0) + 1
    var nextUserId: Int = (users.maxOfOrNull { it.id } ?: 0) + 1
}

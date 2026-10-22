package com.ccstudio.mpaz_pro_6.data.mock

import com.ccstudio.mpaz_pro_6.data.model.Activity
import com.ccstudio.mpaz_pro_6.data.model.Assignment
import com.ccstudio.mpaz_pro_6.data.model.Content
import com.ccstudio.mpaz_pro_6.data.model.ContentResource
import com.ccstudio.mpaz_pro_6.data.model.Course
import com.ccstudio.mpaz_pro_6.data.model.LearningObjective
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.data.model.User
import kotlinx.coroutines.sync.Mutex

/**
 * Base de datos en memoria de la app.
 *
 * Es la fuente única de verdad mientras no exista el backend: todos los
 * repositorios mock comparten estas listas, de modo que las reglas que cruzan
 * entidades se resuelven con datos coherentes.
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
    val objectives: MutableList<LearningObjective> = mutableListOf(),
    val contents: MutableList<Content> = mutableListOf(),
    val contentResources: MutableList<ContentResource> = mutableListOf(),
    val activities: MutableList<Activity> = mutableListOf(),
    val mutex: Mutex = Mutex()
) {

    /** Siguiente id disponible por entidad. */
    var nextSubjectId: Int = (subjects.maxOfOrNull { it.id } ?: 0) + 1
    var nextCourseId: Int = (courses.maxOfOrNull { it.id } ?: 0) + 1
    var nextUnitId: Int = (units.maxOfOrNull { it.id } ?: 0) + 1
    var nextAssignmentId: Int = (assignments.maxOfOrNull { it.id } ?: 0) + 1
    var nextUserId: Int = (users.maxOfOrNull { it.id } ?: 0) + 1
    var nextObjectiveId: Int = (objectives.maxOfOrNull { it.id } ?: 0) + 1
    var nextContentId: Int = (contents.maxOfOrNull { it.id } ?: 0) + 1
    var nextContentResourceId: Int = (contentResources.maxOfOrNull { it.id } ?: 0) + 1
    var nextActivityId: Int = (activities.maxOfOrNull { it.id } ?: 0) + 1
}

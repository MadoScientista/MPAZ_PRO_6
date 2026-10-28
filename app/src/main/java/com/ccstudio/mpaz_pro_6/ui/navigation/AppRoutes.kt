package com.ccstudio.mpaz_pro_6.ui.navigation

/**
 * Rutas de navegación de la app.
 *
 * Los destinos del `NavHost`, agrupados por rol (`UserRole`) y con los
 * parámetros que cada endpoint necesita: id de unidad, id de actividad, id de
 * asignación, id de intento e id de informe.
 *
 * Las rutas son constantes y ningún valor literal queda incrustado en las
 * pantallas.
 */
object AppRoutes {

    /** Inicio de sesión. */
    const val LOGIN = "login"

    /** Panel de inicio según el rol del usuario. */
    const val HOME = "home"

    /** Listado de unidades. */
    const val UNITS = "units"

    /** Detalle de una unidad. */
    const val UNIT_DETAIL = "unitDetail/{unitId}"

    /** Autorización y publicación de una unidad. */
    const val UNIT_AUTHORIZATION = "unitAuthorization/{unitId}"

    /** Listado de asignaturas (UTP). */
    const val SUBJECTS = "subjects"

    /** Detalle de una asignatura con sus unidades. */
    const val SUBJECT_DETAIL = "subjectDetail/{subjectId}"

    /** Formulario de asignatura; sin `subjectId` crea, con id edita. */
    const val SUBJECT_FORM = "subjectForm?subjectId={subjectId}"

    /** Listado de cursos (UTP). */
    const val COURSES = "courses"

    /** Detalle de un curso con sus estudiantes. */
    const val COURSE_DETAIL = "courseDetail/{courseId}"

    /** Formulario de curso; sin `courseId` crea, con id edita. */
    const val COURSE_FORM = "courseForm?courseId={courseId}"

    /** Listado de usuarios (UTP). */
    const val USERS = "users"

    /** Formulario de usuario; sin `userId` crea, con id edita. */
    const val USER_FORM = "userForm?userId={userId}"

    /** Resolución de una actividad; con `preview=true`, vista previa sin intento. */
    const val ACTIVITY_ATTEMPT = "activityAttempt/{activityId}?preview={preview}"

    /** Detalle de un contenido. */
    const val CONTENT_DETAIL = "contentDetail/{contentId}"

    /** Resultado de un intento. */
    const val ATTEMPT_RESULT = "attemptResult/{attemptId}"

    /** Listado de actividades del estudiante. */
    const val ACTIVITIES = "activities"

    /** Detalle de un informe pedagógico. */
    const val REPORT_DETAIL = "reportDetail/{reportId}"

    /** Lista de asignaciones (docente). */
    const val ASSIGNMENTS = "assignments"

    /** Detalle de una asignación. */
    const val ASSIGNMENT_DETAIL = "assignmentDetail/{assignmentId}"

    /** Edición de una asignación. */
    const val ASSIGNMENT_FORM = "assignmentForm/{assignmentId}"

    /** Listado de informes pedagógicos (docente). */
    const val REPORTS = "reports"

    /** Construye la ruta del detalle de una unidad. */
    fun unitDetail(unitId: Int): String = "unitDetail/$unitId"

    /** Construye la ruta de autorización de una unidad. */
    fun unitAuthorization(unitId: Int): String = "unitAuthorization/$unitId"

    /** Construye la ruta del formulario de asignatura. */
    fun subjectForm(subjectId: Int? = null): String =
        if (subjectId == null) "subjectForm" else "subjectForm?subjectId=$subjectId"

    /** Construye la ruta del detalle de una asignatura. */
    fun subjectDetail(subjectId: Int): String = "subjectDetail/$subjectId"

    /** Construye la ruta del formulario de curso. */
    fun courseForm(courseId: Int? = null): String =
        if (courseId == null) "courseForm" else "courseForm?courseId=$courseId"

    /** Construye la ruta del detalle de un curso. */
    fun courseDetail(courseId: Int): String = "courseDetail/$courseId"

    /** Construye la ruta del formulario de usuario. */
    fun userForm(userId: Int? = null): String =
        if (userId == null) "userForm" else "userForm?userId=$userId"

    /**
     * Construye la ruta de resolución de una actividad.
     *
     * @param preview `true` para abrir la vista previa de la UTP o el docente, que
     *   no crea intentos ni guarda respuestas.
     */
    fun activityAttempt(activityId: Int, preview: Boolean = false): String =
        if (preview) {
            "activityAttempt/$activityId?preview=true"
        } else {
            "activityAttempt/$activityId"
        }

    /** Construye la ruta de detalle de un contenido. */
    fun contentDetail(contentId: Int): String = "contentDetail/$contentId"

    /** Construye la ruta del resultado de un intento. */
    fun attemptResult(attemptId: Int): String = "attemptResult/$attemptId"

    /** Construye la ruta del detalle de un informe. */
    fun reportDetail(reportId: Int): String = "reportDetail/$reportId"

    /** Construye la ruta del detalle de una asignación. */
    fun assignmentDetail(assignmentId: Int): String = "assignmentDetail/$assignmentId"

    /** Construye la ruta de edición de una asignación. */
    fun assignmentForm(assignmentId: Int): String = "assignmentForm/$assignmentId"
}

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

    /** Detalle de una unidad; `courseId` es opcional para mostrar intentos. */
    const val UNIT_DETAIL = "unitDetail/{unitId}?courseId={courseId}"

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

    /** Listado de docencias (UTP); el curso es un filtro inicial opcional. */
    const val DOCENCIAS = "docencias?courseId={courseId}"

    /** Formulario de docencia; sin `docenciaId` crea, con id edita. */
    const val DOCENCIA_FORM = "docenciaForm?docenciaId={docenciaId}&courseId={courseId}"

    /** Listado de usuarios (UTP). */
    const val USERS = "users"

    /** Formulario de usuario; sin `userId` crea, con id edita. */
    const val USER_FORM = "userForm?userId={userId}"

    /** Resolución de una actividad por el estudiante. */
    const val ACTIVITY_ATTEMPT = "activityAttempt/{activityId}"

    /** Detalle de un contenido. */
    const val CONTENT_DETAIL = "contentDetail/{contentId}"

    /** Formulario de creación de contenido dentro de una unidad. */
    const val CONTENT_FORM = "contentForm/{unitId}"

    /** Detalle de una actividad para gestionar sus preguntas. */
    const val ACTIVITY_DETAIL = "activityDetail/{activityId}"

    /** Formulario de creación de actividad dentro de un contenido. */
    const val ACTIVITY_FORM = "activityForm/{unitId}?contentId={contentId}"

    /** Edición de una actividad existente. */
    const val ACTIVITY_EDIT = "activityEdit/{unitId}/{activityId}"

    /** Vista previa de solo lectura de una actividad. */
    const val ACTIVITY_PREVIEW = "activityPreview/{activityId}"

    /** Formulario de creación de una pregunta. */
    const val QUESTION_FORM = "questionForm/{activityId}/{unitId}"

    /** Formulario de creación de una alternativa. */
    const val ALTERNATIVE_FORM = "alternativeForm/{questionId}"

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

    /** Creación de una asignación. */
    const val ASSIGNMENT_CREATE = "assignmentCreate"

    /** Edición de una asignación. */
    const val ASSIGNMENT_FORM = "assignmentForm/{assignmentId}"

    /** Listado de informes pedagógicos (docente). */
    const val REPORTS = "reports"

    /** Generación de un informe pedagógico (docente). */
    const val REPORT_GENERATE = "reportGenerate"

    /** Construye la ruta del detalle de una unidad. */
    fun unitDetail(unitId: Int, courseId: Int? = null): String =
        if (courseId == null) "unitDetail/$unitId" else "unitDetail/$unitId?courseId=$courseId"

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

    /** Construye la ruta del listado de docencias, con filtro inicial opcional. */
    fun docencias(courseId: Int? = null): String =
        if (courseId == null) "docencias" else "docencias?courseId=$courseId"

    /** Construye la ruta del formulario de docencia. */
    fun docenciaForm(docenciaId: Int? = null, courseId: Int? = null): String {
        val params = buildList {
            docenciaId?.let { add("docenciaId=$it") }
            courseId?.let { add("courseId=$it") }
        }
        return if (params.isEmpty()) {
            "docenciaForm"
        } else {
            "docenciaForm?${params.joinToString("&")}"
        }
    }

    /** Construye la ruta del formulario de usuario. */
    fun userForm(userId: Int? = null): String =
        if (userId == null) "userForm" else "userForm?userId=$userId"

    /** Construye la ruta de resolución de una actividad. */
    fun activityAttempt(activityId: Int): String = "activityAttempt/$activityId"

    /** Construye la ruta de detalle de un contenido. */
    fun contentDetail(contentId: Int): String = "contentDetail/$contentId"

    /** Construye la ruta del formulario de contenido de una unidad. */
    fun contentForm(unitId: Int): String = "contentForm/$unitId"

    /** Construye la ruta de detalle de una actividad. */
    fun activityDetail(activityId: Int): String = "activityDetail/$activityId"

    /** Construye la ruta del formulario de actividad de un contenido. */
    fun activityForm(unitId: Int, contentId: Int): String =
        "activityForm/$unitId?contentId=$contentId"

    /** Construye la ruta de edición de una actividad. */
    fun activityEdit(unitId: Int, activityId: Int): String =
        "activityEdit/$unitId/$activityId"

    /** Construye la ruta de vista previa de una actividad. */
    fun activityPreview(activityId: Int): String =
        "activityPreview/$activityId"

    /** Construye la ruta del formulario de pregunta. */
    fun questionForm(activityId: Int, unitId: Int): String =
        "questionForm/$activityId/$unitId"

    /** Construye la ruta del formulario de alternativa. */
    fun alternativeForm(questionId: Int): String =
        "alternativeForm/$questionId"

    /** Construye la ruta del resultado de un intento. */
    fun attemptResult(attemptId: Int): String = "attemptResult/$attemptId"

    /** Construye la ruta del detalle de un informe. */
    fun reportDetail(reportId: Int): String = "reportDetail/$reportId"

    /** Construye la ruta del detalle de una asignación. */
    fun assignmentDetail(assignmentId: Int): String = "assignmentDetail/$assignmentId"

    /** Construye la ruta de edición de una asignación. */
    fun assignmentForm(assignmentId: Int): String = "assignmentForm/$assignmentId"
}

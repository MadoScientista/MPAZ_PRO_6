package com.ccstudio.mpaz_pro_6.data.local.mapper

import com.ccstudio.mpaz_pro_6.data.local.entity.ActivityEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.AlternativeEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.AssignmentActivityEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.AssignmentEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.AssignmentObjectiveEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.AttemptEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.ContentEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.ContentResourceEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.CourseEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.LearningObjectiveEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.LearningUnitEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.PedagogicalReportEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.QuestionEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.ReportObjectiveDetailEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.StudentAnswerEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.SubjectEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.UserEntity
import com.ccstudio.mpaz_pro_6.data.model.Activity
import com.ccstudio.mpaz_pro_6.data.model.Alternative
import com.ccstudio.mpaz_pro_6.data.model.Assignment
import com.ccstudio.mpaz_pro_6.data.model.AssignmentActivity
import com.ccstudio.mpaz_pro_6.data.model.AssignmentObjective
import com.ccstudio.mpaz_pro_6.data.model.Attempt
import com.ccstudio.mpaz_pro_6.data.model.Content
import com.ccstudio.mpaz_pro_6.data.model.ContentResource
import com.ccstudio.mpaz_pro_6.data.model.Course
import com.ccstudio.mpaz_pro_6.data.model.LearningObjective
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.PedagogicalReport
import com.ccstudio.mpaz_pro_6.data.model.Question
import com.ccstudio.mpaz_pro_6.data.model.ReportObjectiveDetail
import com.ccstudio.mpaz_pro_6.data.model.StudentAnswer
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.data.model.User

/** Convierte una entidad de curso en modelo de dominio. */
fun CourseEntity.toDomain() = Course(id = id, nombre = nombre, anio = anio)

/** Convierte un curso de dominio en entidad Room. */
fun Course.toEntity() = CourseEntity(id = id, nombre = nombre, anio = anio)

/** Convierte una entidad de usuario en modelo de dominio. */
fun UserEntity.toDomain() = User(
    id = id,
    nombre = nombre,
    username = username,
    rol = rol,
    cursoId = cursoId
)

/** Convierte un usuario de dominio en entidad Room. */
fun User.toEntity() = UserEntity(
    id = id,
    nombre = nombre,
    username = username,
    rol = rol,
    cursoId = cursoId
)

/** Convierte una entidad de asignatura en modelo de dominio. */
fun SubjectEntity.toDomain() = Subject(id = id, nombre = nombre)

/** Convierte una asignatura de dominio en entidad Room. */
fun Subject.toEntity() = SubjectEntity(id = id, nombre = nombre)

/** Convierte una entidad de unidad en modelo de dominio. */
fun LearningUnitEntity.toDomain() = LearningUnit(
    id = id,
    asignaturaId = asignaturaId,
    nivelEducativo = nivelEducativo,
    titulo = titulo,
    orden = orden,
    estado = estado,
    autorizadaPorId = autorizadaPorId,
    fechaAutorizacion = fechaAutorizacion
)

/** Convierte una unidad de dominio en entidad Room. */
fun LearningUnit.toEntity() = LearningUnitEntity(
    id = id,
    asignaturaId = asignaturaId,
    nivelEducativo = nivelEducativo,
    titulo = titulo,
    orden = orden,
    estado = estado,
    autorizadaPorId = autorizadaPorId,
    fechaAutorizacion = fechaAutorizacion
)

/** Convierte una entidad de objetivo en modelo de dominio. */
fun LearningObjectiveEntity.toDomain() = LearningObjective(
    id = id,
    unidadId = unidadId,
    codigo = codigo,
    descripcion = descripcion,
    eje = eje,
    textoReferencia = textoReferencia
)

/** Convierte un objetivo de dominio en entidad Room. */
fun LearningObjective.toEntity() = LearningObjectiveEntity(
    id = id,
    unidadId = unidadId,
    codigo = codigo,
    descripcion = descripcion,
    eje = eje,
    textoReferencia = textoReferencia
)

/** Convierte una entidad de contenido en modelo de dominio. */
fun ContentEntity.toDomain() = Content(
    id = id,
    oaId = oaId,
    titulo = titulo,
    explicacion = explicacion,
    ejemplos = ejemplos,
    instrucciones = instrucciones,
    orden = orden,
    origen = origen,
    estado = estado
)

/** Convierte un contenido de dominio en entidad Room. */
fun Content.toEntity() = ContentEntity(
    id = id,
    oaId = oaId,
    titulo = titulo,
    explicacion = explicacion,
    ejemplos = ejemplos,
    instrucciones = instrucciones,
    orden = orden,
    origen = origen,
    estado = estado
)

/** Convierte una entidad de recurso en modelo de dominio. */
fun ContentResourceEntity.toDomain() = ContentResource(
    id = id,
    contenidoId = contenidoId,
    tipo = tipo,
    url = url,
    descripcion = descripcion,
    origen = origen,
    estado = estado
)

/** Convierte un recurso de dominio en entidad Room. */
fun ContentResource.toEntity() = ContentResourceEntity(
    id = id,
    contenidoId = contenidoId,
    tipo = tipo,
    url = url,
    descripcion = descripcion,
    origen = origen,
    estado = estado
)

/** Convierte una entidad de actividad en modelo de dominio. */
fun ActivityEntity.toDomain() = Activity(
    id = id,
    unidadId = unidadId,
    titulo = titulo,
    tipo = tipo,
    orden = orden
)

/** Convierte una actividad de dominio en entidad Room. */
fun Activity.toEntity() = ActivityEntity(
    id = id,
    unidadId = unidadId,
    titulo = titulo,
    tipo = tipo,
    orden = orden
)

/** Convierte una entidad de pregunta en modelo de dominio. */
fun QuestionEntity.toDomain() = Question(
    id = id,
    actividadId = actividadId,
    unidadId = unidadId,
    oaId = oaId,
    enunciado = enunciado,
    criterioRespuesta = criterioRespuesta,
    dificultad = dificultad,
    orden = orden
)

/** Convierte una pregunta de dominio en entidad Room. */
fun Question.toEntity() = QuestionEntity(
    id = id,
    actividadId = actividadId,
    unidadId = unidadId,
    oaId = oaId,
    enunciado = enunciado,
    criterioRespuesta = criterioRespuesta,
    dificultad = dificultad,
    orden = orden
)

/** Convierte una entidad de alternativa en modelo de dominio. */
fun AlternativeEntity.toDomain() = Alternative(
    id = id,
    preguntaId = preguntaId,
    texto = texto,
    esCorrecta = esCorrecta
)

/** Convierte una alternativa de dominio en entidad Room. */
fun Alternative.toEntity() = AlternativeEntity(
    id = id,
    preguntaId = preguntaId,
    texto = texto,
    esCorrecta = esCorrecta
)

/** Convierte una entidad de asignación en modelo de dominio. */
fun AssignmentEntity.toDomain() = Assignment(
    id = id,
    cursoId = cursoId,
    unidadId = unidadId,
    docenteId = docenteId,
    maxIntentos = maxIntentos,
    pruebaFinalHabilitada = pruebaFinalHabilitada,
    fecha = fecha
)

/** Convierte una asignación de dominio en entidad Room. */
fun Assignment.toEntity() = AssignmentEntity(
    id = id,
    cursoId = cursoId,
    unidadId = unidadId,
    docenteId = docenteId,
    maxIntentos = maxIntentos,
    pruebaFinalHabilitada = pruebaFinalHabilitada,
    fecha = fecha
)

/** Convierte una asociación asignación-objetivo en modelo de dominio. */
fun AssignmentObjectiveEntity.toDomain() = AssignmentObjective(
    asignacionId = asignacionId,
    oaId = oaId,
    unidadId = unidadId
)

/** Convierte una asociación asignación-actividad en modelo de dominio. */
fun AssignmentActivityEntity.toDomain() = AssignmentActivity(
    asignacionId = asignacionId,
    actividadId = actividadId,
    unidadId = unidadId
)

/** Convierte una entidad de intento en modelo de dominio. */
fun AttemptEntity.toDomain() = Attempt(
    id = id,
    estudianteId = estudianteId,
    actividadId = actividadId,
    numero = numero,
    fechaInicio = fechaInicio,
    fechaFin = fechaFin
)

/** Convierte un intento de dominio en entidad Room. */
fun Attempt.toEntity() = AttemptEntity(
    id = id,
    estudianteId = estudianteId,
    actividadId = actividadId,
    numero = numero,
    fechaInicio = fechaInicio,
    fechaFin = fechaFin
)

/** Convierte una entidad de respuesta en modelo de dominio. */
fun StudentAnswerEntity.toDomain() = StudentAnswer(
    id = id,
    intentoId = intentoId,
    preguntaId = preguntaId,
    alternativaId = alternativaId,
    esCorrecta = esCorrecta
)

/** Convierte una respuesta de dominio en entidad Room. */
fun StudentAnswer.toEntity() = StudentAnswerEntity(
    id = id,
    intentoId = intentoId,
    preguntaId = preguntaId,
    alternativaId = alternativaId,
    esCorrecta = esCorrecta
)

/** Convierte una entidad de informe en modelo de dominio. */
fun PedagogicalReportEntity.toDomain() = PedagogicalReport(
    id = id,
    estudianteId = estudianteId,
    unidadId = unidadId,
    docenteId = docenteId,
    fechaGeneracion = fechaGeneracion,
    fechaValidacion = fechaValidacion,
    estado = estado
)

/** Convierte un informe de dominio en entidad Room. */
fun PedagogicalReport.toEntity() = PedagogicalReportEntity(
    id = id,
    estudianteId = estudianteId,
    unidadId = unidadId,
    docenteId = docenteId,
    fechaGeneracion = fechaGeneracion,
    fechaValidacion = fechaValidacion,
    estado = estado
)

/** Convierte una entidad de detalle en modelo de dominio. */
fun ReportObjectiveDetailEntity.toDomain() = ReportObjectiveDetail(
    oaId = oaId,
    total = totalPreguntas,
    correctas = correctas,
    nivel = nivel
)

/**
 * Convierte un detalle de dominio en entidad Room.
 *
 * La respuesta del backend no trae id: se sintetiza uno estable a partir del
 * informe y del objetivo para que el upsert sea idempotente.
 */
fun ReportObjectiveDetail.toEntity(reportId: Int) = ReportObjectiveDetailEntity(
    id = (reportId shl 16) xor oaId,
    informeId = reportId,
    oaId = oaId,
    totalPreguntas = total,
    correctas = correctas,
    nivel = nivel
)

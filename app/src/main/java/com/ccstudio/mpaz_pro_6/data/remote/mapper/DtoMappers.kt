package com.ccstudio.mpaz_pro_6.data.remote.mapper

import com.ccstudio.mpaz_pro_6.data.enums.ActivityType
import com.ccstudio.mpaz_pro_6.data.enums.ContentOrigin
import com.ccstudio.mpaz_pro_6.data.enums.ContentResourceType
import com.ccstudio.mpaz_pro_6.data.enums.ContentStatus
import com.ccstudio.mpaz_pro_6.data.enums.Difficulty
import com.ccstudio.mpaz_pro_6.data.enums.ObjectiveLevel
import com.ccstudio.mpaz_pro_6.data.enums.ReportStatus
import com.ccstudio.mpaz_pro_6.data.enums.UserRole
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
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateActivityRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateAlternativeRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateAssignmentRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateContentRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateContentResourceRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateCourseRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateLearningObjectiveRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateLearningUnitRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.CreatePedagogicalReportRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateQuestionRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateStudentAnswerRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateSubjectRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateUserRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateActivityRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateAlternativeRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateAssignmentRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateContentRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateContentResourceRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateCourseRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateLearningObjectiveRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateLearningUnitRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateQuestionRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateSubjectRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateUserRequest
import com.ccstudio.mpaz_pro_6.data.remote.dto.ActivityDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.AlternativeDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.AssignmentActivityDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.AssignmentDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.AssignmentObjectiveDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.AttemptDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.ContentDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.ContentResourceDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.CourseDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.CreateActivityRequestDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.CreateAlternativeRequestDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.CreateAssignmentRequestDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.CreateContentRequestDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.CreateContentResourceRequestDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.CreateCourseRequestDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.CreateLearningObjectiveRequestDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.CreateLearningUnitRequestDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.CreatePedagogicalReportRequestDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.CreateQuestionRequestDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.CreateStudentAnswerRequestDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.CreateSubjectRequestDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.CreateUserRequestDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.LearningObjectiveDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.LearningUnitDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.PedagogicalReportDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.QuestionDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.ReportObjectiveDetailDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.StudentAnswerDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.SubjectDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.UpdateActivityRequestDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.UpdateAlternativeRequestDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.UpdateAssignmentRequestDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.UpdateContentRequestDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.UpdateContentResourceRequestDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.UpdateCourseRequestDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.UpdateLearningObjectiveRequestDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.UpdateLearningUnitRequestDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.UpdateQuestionRequestDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.UpdateSubjectRequestDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.UpdateUserRequestDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.UserDto
import java.time.Instant

/** Convierte una materia wire en modelo de dominio. */
fun SubjectDto.toDomain() = Subject(id = id, nombre = nombre)

/** Convierte un curso wire en modelo de dominio. */
fun CourseDto.toDomain() = Course(id = id, nombre = nombre, anio = anio)

/** Convierte un usuario wire en modelo de dominio. */
fun UserDto.toDomain() = User(
    id = id,
    nombre = nombre,
    username = username,
    rol = UserRole.fromWire(rol) ?: unknown("UserRole", rol),
    cursoId = cursoId
)

/** Convierte una unidad wire en modelo de dominio. */
fun LearningUnitDto.toDomain() = LearningUnit(
    id = id,
    asignaturaId = asignaturaId,
    nivelEducativo = nivelEducativo,
    titulo = titulo,
    orden = orden,
    estado = ContentStatus.fromWire(estado) ?: unknown("ContentStatus", estado),
    autorizadaPorId = autorizadaPorId,
    fechaAutorizacion = fechaAutorizacion?.toInstant()
)

/** Convierte un objetivo wire en modelo de dominio. */
fun LearningObjectiveDto.toDomain() = LearningObjective(
    id = id,
    unidadId = unidadId,
    codigo = codigo,
    descripcion = descripcion,
    eje = eje,
    textoReferencia = textoReferencia
)

/** Convierte un contenido wire en modelo de dominio. */
fun ContentDto.toDomain() = Content(
    id = id,
    oaId = oaId,
    unidadId = unidadId,
    titulo = titulo,
    explicacion = explicacion,
    ejemplos = ejemplos,
    instrucciones = instrucciones,
    orden = orden,
    origen = ContentOrigin.fromWire(origen) ?: unknown("ContentOrigin", origen),
    estado = ContentStatus.fromWire(estado) ?: unknown("ContentStatus", estado)
)

/** Convierte un recurso wire en modelo de dominio. */
fun ContentResourceDto.toDomain() = ContentResource(
    id = id,
    contenidoId = contenidoId,
    tipo = ContentResourceType.fromWire(tipo) ?: unknown("ContentResourceType", tipo),
    url = url,
    descripcion = descripcion,
    origen = ContentOrigin.fromWire(origen) ?: unknown("ContentOrigin", origen),
    estado = ContentStatus.fromWire(estado) ?: unknown("ContentStatus", estado)
)

/** Convierte una actividad wire en modelo de dominio. */
fun ActivityDto.toDomain() = Activity(
    id = id,
    unidadId = unidadId,
    contenidoId = contenidoId,
    titulo = titulo,
    tipo = ActivityType.fromWire(tipo) ?: unknown("ActivityType", tipo),
    orden = orden
)

/** Convierte una pregunta wire en modelo de dominio. */
fun QuestionDto.toDomain() = Question(
    id = id,
    actividadId = actividadId,
    unidadId = unidadId,
    oaId = oaId,
    enunciado = enunciado,
    criterioRespuesta = criterioRespuesta,
    dificultad = Difficulty.fromWire(dificultad) ?: unknown("Difficulty", dificultad),
    orden = orden
)

/** Convierte una alternativa wire en modelo de dominio. */
fun AlternativeDto.toDomain() = Alternative(
    id = id,
    preguntaId = preguntaId,
    texto = texto,
    esCorrecta = esCorrecta
)

/** Convierte una asignación wire en modelo de dominio. */
fun AssignmentDto.toDomain() = Assignment(
    id = id,
    cursoId = cursoId,
    unidadId = unidadId,
    docenteId = docenteId,
    maxIntentos = maxIntentos,
    pruebaFinalHabilitada = pruebaFinalHabilitada,
    fecha = fecha.toInstant()
)

/** Convierte una asociación asignación-objetivo wire en modelo de dominio. */
fun AssignmentObjectiveDto.toDomain() = AssignmentObjective(
    asignacionId = asignacionId,
    oaId = oaId,
    unidadId = unidadId
)

/** Convierte una asociación asignación-actividad wire en modelo de dominio. */
fun AssignmentActivityDto.toDomain() = AssignmentActivity(
    asignacionId = asignacionId,
    actividadId = actividadId,
    unidadId = unidadId
)

/** Convierte un intento wire en modelo de dominio. */
fun AttemptDto.toDomain() = Attempt(
    id = id,
    estudianteId = estudianteId,
    actividadId = actividadId,
    numero = numero,
    fechaInicio = fechaInicio.toInstant(),
    fechaFin = fechaFin?.toInstant()
)

/** Convierte una respuesta wire en modelo de dominio. */
fun StudentAnswerDto.toDomain() = StudentAnswer(
    id = id,
    intentoId = intentoId,
    preguntaId = preguntaId,
    alternativaId = alternativaId,
    esCorrecta = esCorrecta
)

/** Convierte un informe wire en modelo de dominio. */
fun PedagogicalReportDto.toDomain() = PedagogicalReport(
    id = id,
    estudianteId = estudianteId,
    unidadId = unidadId,
    docenteId = docenteId,
    fechaGeneracion = fechaGeneracion.toInstant(),
    fechaValidacion = fechaValidacion?.toInstant(),
    estado = ReportStatus.fromWire(estado) ?: unknown("ReportStatus", estado)
)

/** Convierte un detalle de informe wire en modelo de dominio. */
fun ReportObjectiveDetailDto.toDomain() = ReportObjectiveDetail(
    oaId = oaId,
    total = total,
    correctas = correctas,
    nivel = ObjectiveLevel.fromWire(nivel) ?: unknown("ObjectiveLevel", nivel)
)

/** Convierte una solicitud de asignatura al cuerpo wire. */
fun CreateSubjectRequest.toDto() = CreateSubjectRequestDto(nombre = nombre)

/** Convierte una actualización de asignatura al cuerpo wire. */
fun UpdateSubjectRequest.toDto() = UpdateSubjectRequestDto(nombre = nombre)

/** Convierte una solicitud de curso al cuerpo wire. */
fun CreateCourseRequest.toDto() = CreateCourseRequestDto(nombre = nombre, anio = anio)

/** Convierte una actualización de curso al cuerpo wire. */
fun UpdateCourseRequest.toDto() = UpdateCourseRequestDto(nombre = nombre, anio = anio)

/** Convierte una solicitud de unidad al cuerpo wire. */
fun CreateLearningUnitRequest.toDto() = CreateLearningUnitRequestDto(
    asignaturaId = asignaturaId,
    titulo = titulo,
    estado = estado.wireValue,
    nivelEducativo = nivelEducativo,
    orden = orden
)

/** Convierte una actualización parcial de unidad al cuerpo wire. */
fun UpdateLearningUnitRequest.toDto() = UpdateLearningUnitRequestDto(
    nivelEducativo = nivelEducativo,
    titulo = titulo,
    orden = orden,
    estado = estado?.wireValue,
    autorizadaPorId = autorizadaPorId,
    fechaAutorizacion = fechaAutorizacion?.toString()
)

/** Convierte una solicitud de usuario al cuerpo wire. */
fun CreateUserRequest.toDto() = CreateUserRequestDto(
    nombre = nombre,
    username = username,
    password = password,
    rol = rol.wireValue,
    cursoId = cursoId
)

/** Convierte una actualización parcial de usuario al cuerpo wire. */
fun UpdateUserRequest.toDto() = UpdateUserRequestDto(
    nombre = nombre,
    username = username,
    password = password,
    rol = rol?.wireValue,
    cursoId = cursoId
)

/** Convierte una solicitud de objetivo al cuerpo wire. */
fun CreateLearningObjectiveRequest.toDto() = CreateLearningObjectiveRequestDto(
    unidadId = unidadId,
    descripcion = descripcion,
    eje = eje,
    codigo = codigo,
    textoReferencia = textoReferencia
)

/** Convierte una actualización parcial de objetivo al cuerpo wire. */
fun UpdateLearningObjectiveRequest.toDto() = UpdateLearningObjectiveRequestDto(
    codigo = codigo,
    descripcion = descripcion,
    eje = eje,
    textoReferencia = textoReferencia
)

/** Convierte una solicitud de contenido al cuerpo wire. */
fun CreateContentRequest.toDto() = CreateContentRequestDto(
    oaId = oaId,
    titulo = titulo,
    explicacion = explicacion,
    origen = origen.wireValue,
    ejemplos = ejemplos,
    instrucciones = instrucciones,
    orden = orden
)

/** Convierte una actualización parcial de contenido al cuerpo wire. */
fun UpdateContentRequest.toDto() = UpdateContentRequestDto(
    titulo = titulo,
    explicacion = explicacion,
    ejemplos = ejemplos,
    instrucciones = instrucciones,
    orden = orden,
    origen = origen?.wireValue
)

/** Convierte una solicitud de recurso al cuerpo wire. */
fun CreateContentResourceRequest.toDto() = CreateContentResourceRequestDto(
    contenidoId = contenidoId,
    tipo = tipo.wireValue,
    url = url,
    origen = origen.wireValue,
    descripcion = descripcion
)

/** Convierte una actualización parcial de recurso al cuerpo wire. */
fun UpdateContentResourceRequest.toDto() = UpdateContentResourceRequestDto(
    tipo = tipo?.wireValue,
    url = url,
    descripcion = descripcion,
    origen = origen?.wireValue
)

/** Convierte una solicitud de actividad al cuerpo wire. */
fun CreateActivityRequest.toDto() = CreateActivityRequestDto(
    unidadId = unidadId,
    titulo = titulo,
    tipo = tipo.wireValue,
    contenidoId = contenidoId,
    orden = orden
)

/** Convierte una actualización parcial de actividad al cuerpo wire. */
fun UpdateActivityRequest.toDto() = UpdateActivityRequestDto(
    titulo = titulo,
    tipo = tipo?.wireValue,
    orden = orden,
    contenidoId = contenidoId,
    desasociarContenido = desasociarContenido
)

/** Convierte una solicitud de pregunta al cuerpo wire. */
fun CreateQuestionRequest.toDto() = CreateQuestionRequestDto(
    actividadId = actividadId,
    unidadId = unidadId,
    oaId = oaId,
    enunciado = enunciado,
    dificultad = dificultad.wireValue,
    criterioRespuesta = criterioRespuesta,
    orden = orden
)

/** Convierte una actualización parcial de pregunta al cuerpo wire. */
fun UpdateQuestionRequest.toDto() = UpdateQuestionRequestDto(
    enunciado = enunciado,
    criterioRespuesta = criterioRespuesta,
    dificultad = dificultad?.wireValue,
    orden = orden
)

/** Convierte una solicitud de alternativa al cuerpo wire. */
fun CreateAlternativeRequest.toDto() = CreateAlternativeRequestDto(
    preguntaId = preguntaId,
    texto = texto,
    esCorrecta = esCorrecta
)

/** Convierte una actualización parcial de alternativa al cuerpo wire. */
fun UpdateAlternativeRequest.toDto() = UpdateAlternativeRequestDto(
    texto = texto,
    esCorrecta = esCorrecta
)

/** Convierte una solicitud de asignación al cuerpo wire. */
fun CreateAssignmentRequest.toDto() = CreateAssignmentRequestDto(
    cursoId = cursoId,
    unidadId = unidadId,
    docenteId = docenteId,
    fecha = fecha.toString(),
    maxIntentos = maxIntentos,
    pruebaFinalHabilitada = pruebaFinalHabilitada
)

/** Convierte una actualización parcial de asignación al cuerpo wire. */
fun UpdateAssignmentRequest.toDto() = UpdateAssignmentRequestDto(
    maxIntentos = maxIntentos,
    pruebaFinalHabilitada = pruebaFinalHabilitada,
    fecha = fecha?.toString()
)

/** Convierte una solicitud de informe al cuerpo wire. */
fun CreatePedagogicalReportRequest.toDto() = CreatePedagogicalReportRequestDto(
    estudianteId = estudianteId,
    unidadId = unidadId,
    docenteId = docenteId
)

/** Convierte una solicitud de respuesta al cuerpo wire. */
fun CreateStudentAnswerRequest.toDto() = CreateStudentAnswerRequestDto(
    preguntaId = preguntaId,
    alternativaId = alternativaId
)

private fun String.toInstant(): Instant =
    runCatching { Instant.parse(this) }
        .getOrElse { throw IllegalArgumentException("Fecha ISO-8601 inválida: $this") }

private fun unknown(enumName: String, value: String): Nothing =
    throw IllegalArgumentException("Valor desconocido para $enumName: $value")

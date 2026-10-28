package com.ccstudio.mpaz_pro_6.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Credenciales de `POST /api/auth/login`. */
@Serializable
data class LoginRequestDto(
    val username: String,
    val password: String
)

/** Respuesta de `POST /api/auth/login`. */
@Serializable
data class LoginResponseDto(
    val token: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long = 0,
    val usuario: UserDto
)

@Serializable
data class SubjectDto(
    val id: Int,
    val nombre: String
)

@Serializable
data class CourseDto(
    val id: Int,
    val nombre: String,
    val anio: Int
)

@Serializable
data class LearningUnitDto(
    val id: Int,
    val asignaturaId: Int,
    val nivelEducativo: String = "Sexto Basico",
    val titulo: String,
    val orden: Int = 1,
    val estado: String,
    val autorizadaPorId: Int? = null,
    val fechaAutorizacion: String? = null
)

@Serializable
data class LearningObjectiveDto(
    val id: Int,
    val unidadId: Int,
    val codigo: String? = null,
    val descripcion: String,
    val eje: String,
    val textoReferencia: String? = null
)

@Serializable
data class ContentDto(
    val id: Int,
    val oaId: Int,
    val unidadId: Int,
    val titulo: String,
    val explicacion: String,
    val ejemplos: String? = null,
    val instrucciones: String? = null,
    val orden: Int = 1,
    val origen: String,
    val estado: String = "BORRADOR"
)

@Serializable
data class ContentResourceDto(
    val id: Int,
    val contenidoId: Int,
    val tipo: String,
    val url: String,
    val descripcion: String? = null,
    val origen: String,
    val estado: String = "BORRADOR"
)

@Serializable
data class ActivityDto(
    val id: Int,
    val unidadId: Int,
    val contenidoId: Int? = null,
    val titulo: String,
    val tipo: String,
    val orden: Int = 1
)

@Serializable
data class QuestionDto(
    val id: Int,
    val actividadId: Int,
    val unidadId: Int,
    val oaId: Int,
    val enunciado: String,
    val criterioRespuesta: String? = null,
    val dificultad: String,
    val orden: Int = 1
)

@Serializable
data class AlternativeDto(
    val id: Int,
    val preguntaId: Int,
    val texto: String,
    val esCorrecta: Boolean
)

@Serializable
data class AssignmentDto(
    val id: Int,
    val cursoId: Int,
    val unidadId: Int,
    val docenteId: Int,
    val maxIntentos: Int? = null,
    val pruebaFinalHabilitada: Boolean = false,
    val fecha: String
)

@Serializable
data class AssignmentObjectiveDto(
    val asignacionId: Int,
    val oaId: Int,
    val unidadId: Int
)

@Serializable
data class AssignmentActivityDto(
    val asignacionId: Int,
    val actividadId: Int,
    val unidadId: Int
)

@Serializable
data class AttemptDto(
    val id: Int,
    val estudianteId: Int,
    val actividadId: Int,
    val numero: Int,
    val fechaInicio: String,
    val fechaFin: String? = null
)

@Serializable
data class StudentAnswerDto(
    val id: Int,
    val intentoId: Int,
    val preguntaId: Int,
    val alternativaId: Int,
    val esCorrecta: Boolean
)

@Serializable
data class PedagogicalReportDto(
    val id: Int,
    val estudianteId: Int,
    val unidadId: Int,
    val docenteId: Int,
    val fechaGeneracion: String,
    val fechaValidacion: String? = null,
    val estado: String
)

@Serializable
data class ReportObjectiveDetailDto(
    val oaId: Int,
    val total: Int,
    val correctas: Int,
    val nivel: String
)

@Serializable
data class UserDto(
    val id: Int,
    val nombre: String,
    val username: String,
    val rol: String,
    val cursoId: Int? = null
)

@Serializable
data class CreateSubjectRequestDto(
    val nombre: String
)

@Serializable
data class UpdateSubjectRequestDto(
    val nombre: String? = null
)

@Serializable
data class CreateCourseRequestDto(
    val nombre: String,
    val anio: Int
)

@Serializable
data class UpdateCourseRequestDto(
    val nombre: String? = null,
    val anio: Int? = null
)

@Serializable
data class CreateLearningUnitRequestDto(
    @SerialName("asignaturaId") val asignaturaId: Int,
    val titulo: String,
    val estado: String,
    val nivelEducativo: String? = null,
    val orden: Int? = null
)

@Serializable
data class UpdateLearningUnitRequestDto(
    val nivelEducativo: String? = null,
    val titulo: String? = null,
    val orden: Int? = null,
    val estado: String? = null,
    val autorizadaPorId: Int? = null,
    val fechaAutorizacion: String? = null
)

@Serializable
data class CreateUserRequestDto(
    val nombre: String,
    val username: String,
    val password: String,
    val rol: String,
    val cursoId: Int? = null
)

@Serializable
data class UpdateUserRequestDto(
    val nombre: String? = null,
    val username: String? = null,
    val password: String? = null,
    val rol: String? = null,
    val cursoId: Int? = null
)

@Serializable
data class CreateLearningObjectiveRequestDto(
    val unidadId: Int,
    val descripcion: String,
    val eje: String,
    val codigo: String? = null,
    val textoReferencia: String? = null
)

@Serializable
data class UpdateLearningObjectiveRequestDto(
    val codigo: String? = null,
    val descripcion: String? = null,
    val eje: String? = null,
    val textoReferencia: String? = null
)

@Serializable
data class CreateContentRequestDto(
    val oaId: Int,
    val titulo: String,
    val explicacion: String,
    val origen: String,
    val ejemplos: String? = null,
    val instrucciones: String? = null,
    val orden: Int? = null
)

@Serializable
data class UpdateContentRequestDto(
    val titulo: String? = null,
    val explicacion: String? = null,
    val ejemplos: String? = null,
    val instrucciones: String? = null,
    val orden: Int? = null,
    val origen: String? = null
)

@Serializable
data class CreateContentResourceRequestDto(
    val contenidoId: Int,
    val tipo: String,
    val url: String,
    val origen: String,
    val descripcion: String? = null
)

@Serializable
data class UpdateContentResourceRequestDto(
    val tipo: String? = null,
    val url: String? = null,
    val descripcion: String? = null,
    val origen: String? = null
)

@Serializable
data class CreateActivityRequestDto(
    val unidadId: Int,
    val titulo: String,
    val tipo: String,
    val contenidoId: Int? = null,
    val orden: Int? = null
)

@Serializable
data class UpdateActivityRequestDto(
    val titulo: String? = null,
    val tipo: String? = null,
    val orden: Int? = null,
    val contenidoId: Int? = null,
    val desasociarContenido: Boolean? = null
)

@Serializable
data class CreateQuestionRequestDto(
    val actividadId: Int,
    val unidadId: Int,
    val oaId: Int,
    val enunciado: String,
    val dificultad: String,
    val criterioRespuesta: String? = null,
    val orden: Int? = null
)

@Serializable
data class UpdateQuestionRequestDto(
    val enunciado: String? = null,
    val criterioRespuesta: String? = null,
    val dificultad: String? = null,
    val orden: Int? = null
)

@Serializable
data class CreateAlternativeRequestDto(
    val preguntaId: Int,
    val texto: String,
    val esCorrecta: Boolean? = null
)

@Serializable
data class UpdateAlternativeRequestDto(
    val texto: String? = null,
    val esCorrecta: Boolean? = null
)

@Serializable
data class CreateAssignmentRequestDto(
    val cursoId: Int,
    val unidadId: Int,
    val docenteId: Int,
    val fecha: String,
    val maxIntentos: Int? = null,
    val pruebaFinalHabilitada: Boolean? = null
)

@Serializable
data class UpdateAssignmentRequestDto(
    val maxIntentos: Int? = null,
    val pruebaFinalHabilitada: Boolean? = null,
    val fecha: String? = null
)

@Serializable
data class CreatePedagogicalReportRequestDto(
    val estudianteId: Int,
    val unidadId: Int,
    val docenteId: Int
)

@Serializable
data class CreateStudentAnswerRequestDto(
    val preguntaId: Int,
    val alternativaId: Int
)

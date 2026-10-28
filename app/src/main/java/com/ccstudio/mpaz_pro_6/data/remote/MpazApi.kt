package com.ccstudio.mpaz_pro_6.data.remote

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
import com.ccstudio.mpaz_pro_6.data.remote.dto.CreateDocenciaRequestDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.CreateLearningObjectiveRequestDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.CreateLearningUnitRequestDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.CreatePedagogicalReportRequestDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.CreateQuestionRequestDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.CreateStudentAnswerRequestDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.CreateSubjectRequestDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.CreateUserRequestDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.DocenciaDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.LearningObjectiveDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.LearningUnitDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.LoginRequestDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.LoginResponseDto
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
import com.ccstudio.mpaz_pro_6.data.remote.dto.UpdateDocenciaRequestDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.UpdateLearningObjectiveRequestDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.UpdateLearningUnitRequestDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.UpdateQuestionRequestDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.UpdateSubjectRequestDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.UpdateUserRequestDto
import com.ccstudio.mpaz_pro_6.data.remote.dto.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Contrato HTTP de MPAZ.
 *
 * Los nombres de ruta y de campo siguen `specs/endpoints`. Las operaciones aún
 * no usadas por la UI quedan disponibles para las siguientes fases.
 */
interface MpazApi {

    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequestDto): LoginResponseDto

    @GET("api/auth/me")
    suspend fun me(): UserDto

    @POST("api/auth/logout")
    suspend fun logout(): Response<Unit>

    @GET("api/asignaturas")
    suspend fun getSubjects(): List<SubjectDto>

    @GET("api/asignaturas/{id}")
    suspend fun getSubject(@Path("id") id: Int): SubjectDto

    @POST("api/asignaturas")
    suspend fun createSubject(@Body body: CreateSubjectRequestDto): SubjectDto

    @PUT("api/asignaturas/{id}")
    suspend fun updateSubject(
        @Path("id") id: Int,
        @Body body: UpdateSubjectRequestDto
    ): SubjectDto

    @DELETE("api/asignaturas/{id}")
    suspend fun deleteSubject(@Path("id") id: Int): Response<Unit>

    @GET("api/cursos")
    suspend fun getCourses(@Query("anio") anio: Int? = null): List<CourseDto>

    @GET("api/cursos/{id}")
    suspend fun getCourse(@Path("id") id: Int): CourseDto

    @GET("api/cursos/{id}/estudiantes")
    suspend fun getCourseStudents(@Path("id") id: Int): List<UserDto>

    @POST("api/cursos")
    suspend fun createCourse(@Body body: CreateCourseRequestDto): CourseDto

    @PUT("api/cursos/{id}")
    suspend fun updateCourse(
        @Path("id") id: Int,
        @Body body: UpdateCourseRequestDto
    ): CourseDto

    @DELETE("api/cursos/{id}")
    suspend fun deleteCourse(@Path("id") id: Int): Response<Unit>

    @GET("api/unidades")
    suspend fun getUnits(@Query("asignaturaId") subjectId: Int? = null): List<LearningUnitDto>

    @GET("api/unidades/{id}")
    suspend fun getUnit(@Path("id") id: Int): LearningUnitDto

    @POST("api/unidades")
    suspend fun createUnit(@Body body: CreateLearningUnitRequestDto): LearningUnitDto

    @PUT("api/unidades/{id}")
    suspend fun updateUnit(
        @Path("id") id: Int,
        @Body body: UpdateLearningUnitRequestDto
    ): LearningUnitDto

    @PATCH("api/unidades/{id}/envio-revision")
    suspend fun sendUnitToReview(@Path("id") id: Int): LearningUnitDto

    @DELETE("api/unidades/{id}")
    suspend fun deleteUnit(@Path("id") id: Int): Response<Unit>

    @GET("api/usuarios")
    suspend fun getUsers(): List<UserDto>

    @GET("api/usuarios/{id}")
    suspend fun getUser(@Path("id") id: Int): UserDto

    @GET("api/usuarios/{id}/cursos")
    suspend fun getTeacherCourses(@Path("id") id: Int): List<CourseDto>

    @POST("api/usuarios")
    suspend fun createUser(@Body body: CreateUserRequestDto): UserDto

    @PUT("api/usuarios/{id}")
    suspend fun updateUser(
        @Path("id") id: Int,
        @Body body: UpdateUserRequestDto
    ): UserDto

    @DELETE("api/usuarios/{id}")
    suspend fun deleteUser(@Path("id") id: Int): Response<Unit>

    @GET("api/objetivos-aprendizaje")
    suspend fun getObjectives(
        @Query("unidadId") unitId: Int? = null
    ): List<LearningObjectiveDto>

    @GET("api/objetivos-aprendizaje/{id}")
    suspend fun getObjective(@Path("id") id: Int): LearningObjectiveDto

    @POST("api/objetivos-aprendizaje")
    suspend fun createObjective(
        @Body body: CreateLearningObjectiveRequestDto
    ): LearningObjectiveDto

    @PUT("api/objetivos-aprendizaje/{id}")
    suspend fun updateObjective(
        @Path("id") id: Int,
        @Body body: UpdateLearningObjectiveRequestDto
    ): LearningObjectiveDto

    @DELETE("api/objetivos-aprendizaje/{id}")
    suspend fun deleteObjective(@Path("id") id: Int): Response<Unit>

    @GET("api/contenidos")
    suspend fun getContents(@Query("oaId") oaId: Int? = null): List<ContentDto>

    @GET("api/contenidos/{id}")
    suspend fun getContent(@Path("id") id: Int): ContentDto

    @POST("api/contenidos")
    suspend fun createContent(@Body body: CreateContentRequestDto): ContentDto

    @PUT("api/contenidos/{id}")
    suspend fun updateContent(
        @Path("id") id: Int,
        @Body body: UpdateContentRequestDto
    ): ContentDto

    @DELETE("api/contenidos/{id}")
    suspend fun deleteContent(@Path("id") id: Int): Response<Unit>

    @GET("api/recursos-contenido")
    suspend fun getContentResources(
        @Query("contenidoId") contentId: Int? = null
    ): List<ContentResourceDto>

    @GET("api/recursos-contenido/{id}")
    suspend fun getContentResource(@Path("id") id: Int): ContentResourceDto

    @POST("api/recursos-contenido")
    suspend fun createContentResource(
        @Body body: CreateContentResourceRequestDto
    ): ContentResourceDto

    @PUT("api/recursos-contenido/{id}")
    suspend fun updateContentResource(
        @Path("id") id: Int,
        @Body body: UpdateContentResourceRequestDto
    ): ContentResourceDto

    @DELETE("api/recursos-contenido/{id}")
    suspend fun deleteContentResource(@Path("id") id: Int): Response<Unit>

    @GET("api/actividades")
    suspend fun getActivities(
        @Query("unidadId") unitId: Int? = null,
        @Query("contenidoId") contentId: Int? = null,
        @Query("soloTransversales") onlyTransversal: Boolean? = null
    ): List<ActivityDto>

    @GET("api/actividades/{id}")
    suspend fun getActivity(@Path("id") id: Int): ActivityDto

    @POST("api/actividades")
    suspend fun createActivity(@Body body: CreateActivityRequestDto): ActivityDto

    @PUT("api/actividades/{id}")
    suspend fun updateActivity(
        @Path("id") id: Int,
        @Body body: UpdateActivityRequestDto
    ): ActivityDto

    @DELETE("api/actividades/{id}")
    suspend fun deleteActivity(@Path("id") id: Int): Response<Unit>

    @GET("api/preguntas")
    suspend fun getQuestions(
        @Query("actividadId") activityId: Int? = null,
        @Query("oaId") oaId: Int? = null
    ): List<QuestionDto>

    @GET("api/preguntas/{id}")
    suspend fun getQuestion(@Path("id") id: Int): QuestionDto

    @POST("api/preguntas")
    suspend fun createQuestion(@Body body: CreateQuestionRequestDto): QuestionDto

    @PUT("api/preguntas/{id}")
    suspend fun updateQuestion(
        @Path("id") id: Int,
        @Body body: UpdateQuestionRequestDto
    ): QuestionDto

    @DELETE("api/preguntas/{id}")
    suspend fun deleteQuestion(@Path("id") id: Int): Response<Unit>

    @GET("api/alternativas")
    suspend fun getAlternatives(
        @Query("preguntaId") questionId: Int? = null
    ): List<AlternativeDto>

    @GET("api/alternativas/{id}")
    suspend fun getAlternative(@Path("id") id: Int): AlternativeDto

    @POST("api/alternativas")
    suspend fun createAlternative(
        @Body body: CreateAlternativeRequestDto
    ): AlternativeDto

    @PUT("api/alternativas/{id}")
    suspend fun updateAlternative(
        @Path("id") id: Int,
        @Body body: UpdateAlternativeRequestDto
    ): AlternativeDto

    @DELETE("api/alternativas/{id}")
    suspend fun deleteAlternative(@Path("id") id: Int): Response<Unit>

    @GET("api/docencias")
    suspend fun getDocencias(
        @Query("docenteId") docenteId: Int? = null
    ): List<DocenciaDto>

    @GET("api/docencias/{id}")
    suspend fun getDocencia(@Path("id") id: Int): DocenciaDto

    @POST("api/docencias")
    suspend fun createDocencia(@Body body: CreateDocenciaRequestDto): DocenciaDto

    @PUT("api/docencias/{id}")
    suspend fun updateDocencia(
        @Path("id") id: Int,
        @Body body: UpdateDocenciaRequestDto
    ): DocenciaDto

    @DELETE("api/docencias/{id}")
    suspend fun deleteDocencia(@Path("id") id: Int): Response<Unit>

    @GET("api/asignaciones")
    suspend fun getAssignments(
        @Query("cursoId") courseId: Int? = null,
        @Query("unidadId") unitId: Int? = null
    ): List<AssignmentDto>

    @GET("api/asignaciones/{id}")
    suspend fun getAssignment(@Path("id") id: Int): AssignmentDto

    @POST("api/asignaciones")
    suspend fun createAssignment(@Body body: CreateAssignmentRequestDto): AssignmentDto

    @PUT("api/asignaciones/{id}")
    suspend fun updateAssignment(
        @Path("id") id: Int,
        @Body body: UpdateAssignmentRequestDto
    ): AssignmentDto

    @DELETE("api/asignaciones/{id}")
    suspend fun deleteAssignment(@Path("id") id: Int): Response<Unit>

    @GET("api/asignaciones/{id}/objetivos-aprendizaje")
    suspend fun getAssignmentObjectives(
        @Path("id") assignmentId: Int
    ): List<AssignmentObjectiveDto>

    @POST("api/asignaciones/{assignmentId}/objetivos-aprendizaje/{objectiveId}")
    suspend fun addAssignmentObjective(
        @Path("assignmentId") assignmentId: Int,
        @Path("objectiveId") objectiveId: Int
    ): AssignmentObjectiveDto

    @DELETE("api/asignaciones/{assignmentId}/objetivos-aprendizaje/{objectiveId}")
    suspend fun removeAssignmentObjective(
        @Path("assignmentId") assignmentId: Int,
        @Path("objectiveId") objectiveId: Int
    ): Response<Unit>

    @GET("api/asignaciones/{id}/actividades")
    suspend fun getAssignmentActivities(
        @Path("id") assignmentId: Int
    ): List<AssignmentActivityDto>

    @POST("api/asignaciones/{assignmentId}/actividades/{activityId}")
    suspend fun addAssignmentActivity(
        @Path("assignmentId") assignmentId: Int,
        @Path("activityId") activityId: Int
    ): AssignmentActivityDto

    @DELETE("api/asignaciones/{assignmentId}/actividades/{activityId}")
    suspend fun removeAssignmentActivity(
        @Path("assignmentId") assignmentId: Int,
        @Path("activityId") activityId: Int
    ): Response<Unit>

    @GET("api/intentos")
    suspend fun getAttempts(
        @Query("estudianteId") studentId: Int? = null,
        @Query("actividadId") activityId: Int? = null
    ): List<AttemptDto>

    @GET("api/intentos/{id}")
    suspend fun getAttempt(@Path("id") id: Int): AttemptDto

    @POST("api/estudiantes/{studentId}/actividades/{activityId}/intentos")
    suspend fun startAttempt(
        @Path("studentId") studentId: Int,
        @Path("activityId") activityId: Int
    ): AttemptDto

    @PATCH("api/intentos/{id}/completar")
    suspend fun completeAttempt(@Path("id") id: Int): AttemptDto

    @GET("api/intentos/{id}/respuestas")
    suspend fun getStudentAnswers(@Path("id") attemptId: Int): List<StudentAnswerDto>

    @POST("api/intentos/{id}/respuestas")
    suspend fun registerStudentAnswer(
        @Path("id") attemptId: Int,
        @Body body: CreateStudentAnswerRequestDto
    ): StudentAnswerDto

    @GET("api/informes-pedagogicos")
    suspend fun getReports(
        @Query("estudianteId") studentId: Int? = null,
        @Query("unidadId") unitId: Int? = null
    ): List<PedagogicalReportDto>

    @GET("api/informes-pedagogicos/{id}")
    suspend fun getReport(@Path("id") id: Int): PedagogicalReportDto

    @POST("api/informes-pedagogicos")
    suspend fun generateReport(
        @Body body: CreatePedagogicalReportRequestDto
    ): PedagogicalReportDto

    @PATCH("api/informes-pedagogicos/{id}/validacion")
    suspend fun validateReport(@Path("id") id: Int): PedagogicalReportDto

    @GET("api/informes-pedagogicos/{id}/objetivos-aprendizaje")
    suspend fun getReportDetails(
        @Path("id") reportId: Int
    ): List<ReportObjectiveDetailDto>
}

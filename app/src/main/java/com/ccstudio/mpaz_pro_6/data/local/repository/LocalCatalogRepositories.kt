package com.ccstudio.mpaz_pro_6.data.local.repository

import com.ccstudio.mpaz_pro_6.data.enums.UserRole
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.local.dao.CatalogDao
import com.ccstudio.mpaz_pro_6.data.local.dao.UserDao
import com.ccstudio.mpaz_pro_6.data.local.entity.CourseEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.SubjectEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.UserEntity
import com.ccstudio.mpaz_pro_6.data.local.mapper.toDomain
import com.ccstudio.mpaz_pro_6.data.model.Course
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.data.model.User
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateCourseRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateSubjectRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateUserRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateCourseRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateSubjectRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateUserRequest
import com.ccstudio.mpaz_pro_6.data.repository.CourseRepository
import com.ccstudio.mpaz_pro_6.data.repository.SubjectRepository
import com.ccstudio.mpaz_pro_6.data.repository.UserRepository

/** CRUD local de asignaturas sobre Room. */
class LocalSubjectRepository(
    private val dao: CatalogDao
) : SubjectRepository {

    override suspend fun list(): Result<List<Subject>> = localCall {
        dao.getSubjects().map { it.toDomain() }
    }

    override suspend fun getById(id: Int): Result<Subject> = localCall {
        dao.getSubjectById(id).orNotFound("Asignatura").getOrThrow().toDomain()
    }

    override suspend fun create(request: CreateSubjectRequest): Result<Subject> = localCall {
        require(request.nombre.isNotBlank()) { "El nombre es obligatorio." }
        val entities = dao.getSubjects()
        val entity = SubjectEntity(
            id = entities.nextId { it.id },
            nombre = request.nombre.trim()
        )
        dao.upsertSubject(entity)
        entity.toDomain()
    }

    override suspend fun update(id: Int, request: UpdateSubjectRequest): Result<Subject> =
        localCall {
            val current = dao.getSubjectById(id)
                .orNotFound("Asignatura")
                .getOrThrow()
            require(request.nombre.isNotBlank()) { "El nombre es obligatorio." }
            val updated = current.copy(nombre = request.nombre.trim())
            dao.upsertSubject(updated)
            updated.toDomain()
        }

    override suspend fun delete(id: Int): Result<Unit> = localCall {
        dao.getSubjectById(id).orNotFound("Asignatura").getOrThrow()
        dao.deleteSubjectById(id)
    }
}

/** CRUD local de cursos sobre Room. */
class LocalCourseRepository(
    private val dao: CatalogDao
) : CourseRepository {

    override suspend fun list(anio: Int?): Result<List<Course>> = localCall {
        val entities = if (anio == null) dao.getCourses() else dao.getCoursesByYear(anio)
        entities.map { it.toDomain() }
    }

    override suspend fun getById(id: Int): Result<Course> = localCall {
        dao.getCourseById(id).orNotFound("Curso").getOrThrow().toDomain()
    }

    override suspend fun create(request: CreateCourseRequest): Result<Course> = localCall {
        require(request.nombre.isNotBlank()) { "El nombre es obligatorio." }
        require(request.anio in 2000..2100) { "El año debe estar entre 2000 y 2100." }
        val entities = dao.getCourses()
        val entity = CourseEntity(
            id = entities.nextId { it.id },
            nombre = request.nombre.trim(),
            anio = request.anio
        )
        dao.upsertCourse(entity)
        entity.toDomain()
    }

    override suspend fun update(id: Int, request: UpdateCourseRequest): Result<Course> =
        localCall {
            val current = dao.getCourseById(id).orNotFound("Curso").getOrThrow()
            request.anio?.let { require(it in 2000..2100) { "El año debe estar entre 2000 y 2100." } }
            val updated = current.copy(
                nombre = request.nombre?.trim() ?: current.nombre,
                anio = request.anio ?: current.anio
            )
            require(updated.nombre.isNotBlank()) { "El nombre es obligatorio." }
            dao.upsertCourse(updated)
            updated.toDomain()
        }

    override suspend fun delete(id: Int): Result<Unit> = localCall {
        dao.getCourseById(id).orNotFound("Curso").getOrThrow()
        dao.deleteCourseById(id)
    }
}

/** CRUD local de usuarios. La contraseña no se guarda en Room. */
class LocalUserRepository(
    private val dao: UserDao
) : UserRepository {

    override suspend fun list(): Result<List<User>> = localCall {
        dao.getUsers().map { it.toDomain() }
    }

    override suspend fun getById(id: Int): Result<User> = localCall {
        dao.getUserById(id).orNotFound("Usuario").getOrThrow().toDomain()
    }

    @Deprecated("La app autentica con AuthRepository y /api/auth/login.")
    override suspend fun authenticate(username: String, password: String): Result<User> =
        localCall {
            val user = dao.getUserByUsername(username)
                .orNotFound("Usuario")
                .getOrThrow()
                .toDomain()
            if (password != com.ccstudio.mpaz_pro_6.data.auth.LocalAuthRepository.LOCAL_PASSWORD) {
                throw IllegalArgumentException("Usuario o contraseña incorrectos.")
            }
            user
        }

    override suspend fun create(request: CreateUserRequest): Result<User> = localCall {
        require(request.nombre.isNotBlank()) { "El nombre es obligatorio." }
        require(request.username.isNotBlank()) { "El username es obligatorio." }
        require(request.password.isNotBlank()) { "La contraseña es obligatoria." }
        require(request.rol == UserRole.ESTUDIANTE || request.cursoId == null) {
            "Solo los estudiantes pueden tener un curso."
        }
        val entities = dao.getUsers()
        val entity = UserEntity(
            id = entities.nextId { it.id },
            nombre = request.nombre.trim(),
            username = request.username.trim(),
            rol = request.rol,
            cursoId = request.cursoId
        )
        dao.upsertUser(entity)
        entity.toDomain()
    }

    override suspend fun update(id: Int, request: UpdateUserRequest): Result<User> =
        localCall {
            val current = dao.getUserById(id).orNotFound("Usuario").getOrThrow()
            val nextRole = request.rol ?: current.rol
            val requestedCourse = if (request.rol != null && nextRole != UserRole.ESTUDIANTE) {
                null
            } else {
                request.cursoId ?: current.cursoId
            }
            val updated = current.copy(
                nombre = request.nombre?.trim() ?: current.nombre,
                username = request.username?.trim() ?: current.username,
                rol = nextRole,
                cursoId = if (nextRole == UserRole.ESTUDIANTE) requestedCourse else null
            )
            require(updated.nombre.isNotBlank()) { "El nombre es obligatorio." }
            require(updated.username.isNotBlank()) { "El username es obligatorio." }
            dao.upsertUser(updated)
            updated.toDomain()
        }

    override suspend fun delete(id: Int): Result<Unit> = localCall {
        dao.getUserById(id).orNotFound("Usuario").getOrThrow()
        dao.deleteUserById(id)
    }

    override suspend fun listCourses(userId: Int): Result<List<Course>> = localCall {
        val user = dao.getUserById(userId).orNotFound("Usuario").getOrThrow()
        if (user.rol != UserRole.DOCENTE) {
            throw IllegalArgumentException("El usuario no tiene rol DOCENTE.")
        }
        dao.getCoursesForTeacher(userId).map { it.toDomain() }
    }
}

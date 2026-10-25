package com.ccstudio.mpaz_pro_6.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ccstudio.mpaz_pro_6.data.local.entity.CourseEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.UserEntity

/** Consultas de usuarios y de los cursos que maneja un docente. */
@Dao
interface UserDao {

    @Query("SELECT * FROM usuario ORDER BY id")
    suspend fun getUsers(): List<UserEntity>

    @Query("SELECT * FROM usuario WHERE id = :id")
    suspend fun getUserById(id: Int): UserEntity?

    @Query("SELECT * FROM usuario WHERE username = :username COLLATE NOCASE LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Upsert
    suspend fun upsertUser(entity: UserEntity)

    @Upsert
    suspend fun upsertUsers(entities: List<UserEntity>)

    @Upsert
    suspend fun upsertCourses(entities: List<CourseEntity>)

    @Query("DELETE FROM usuario")
    suspend fun deleteAllUsers()

    @Query("DELETE FROM usuario WHERE id = :id")
    suspend fun deleteUserById(id: Int)

    @Query(
        """
        SELECT DISTINCT curso.*
        FROM curso
        INNER JOIN asignacion ON asignacion.curso_id = curso.id
        WHERE asignacion.docente_id = :docenteId
        ORDER BY curso.anio, curso.nombre
        """
    )
    suspend fun getCoursesForTeacher(docenteId: Int): List<CourseEntity>
}

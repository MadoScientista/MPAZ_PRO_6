package com.ccstudio.mpaz_pro_6.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.AsignaturaEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.ObjetivoAprendizajeEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.UnidadEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.identidad.UsuarioEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.informe.DetalleInformePedagogicoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.informe.InformePedagogicoEntity

data class InformePedagogicoDetalle(
    @Embedded val informe: InformePedagogicoEntity,
    @Embedded(prefix = "u_") val estudiante: UsuarioEntity,
    @Embedded(prefix = "un_") val unidad: UnidadEntity,
    @Embedded(prefix = "asig_") val asignatura: AsignaturaEntity
)

data class DetalleInformeConObjetivo(
    @Embedded val detalle: DetalleInformePedagogicoEntity,
    @Embedded(prefix = "oa_") val objetivo: ObjetivoAprendizajeEntity
)

@Dao
interface InformeDao {
    // InformePedagogico
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInformePedagogico(entity: InformePedagogicoEntity): Long

    @Update
    suspend fun updateInformePedagogico(entity: InformePedagogicoEntity)

    @Delete
    suspend fun deleteInformePedagogico(entity: InformePedagogicoEntity)

    @Query("DELETE FROM informe_pedagogico WHERE informe_pedagogico_id = :id")
    suspend fun deleteInformePedagogicoById(id: Long)

    @Query("SELECT * FROM informe_pedagogico WHERE informe_pedagogico_id = :id")
    suspend fun getInformePedagogicoById(id: Long): InformePedagogicoEntity?

    @Query("SELECT * FROM informe_pedagogico WHERE usuario_id = :usuarioId ORDER BY informe_pedagogico_id DESC")
    suspend fun getInformesByUsuarioId(usuarioId: Long): List<InformePedagogicoEntity>

    @Query("""
        SELECT 
            ip.*,
            u.usuario_id AS u_usuario_id,
            u.nombre_usuario AS u_nombre_usuario,
            u.password_hash AS u_password_hash,
            u.pnombre AS u_pnombre,
            u.snombre AS u_snombre,
            u.apaterno AS u_apaterno,
            u.amaterno AS u_amaterno,
            u.rol_id AS u_rol_id,
            u.activo AS u_activo,
            un.unidad_id AS un_unidad_id,
            un.nombre AS un_nombre,
            un.asignatura_id AS un_asignatura_id,
            un.estado_id AS un_estado_id,
            a.asignatura_id AS asig_asignatura_id,
            a.nombre AS asig_nombre,
            a.nivel_id AS asig_nivel_id
        FROM informe_pedagogico ip
        INNER JOIN usuario u ON ip.usuario_id = u.usuario_id
        INNER JOIN unidad un ON ip.unidad_id = un.unidad_id
        INNER JOIN asignatura a ON un.asignatura_id = a.asignatura_id
        ORDER BY ip.informe_pedagogico_id DESC
    """)
    suspend fun getAllInformesDetallados(): List<InformePedagogicoDetalle>

    @Query("""
        SELECT 
            ip.*,
            u.usuario_id AS u_usuario_id,
            u.nombre_usuario AS u_nombre_usuario,
            u.password_hash AS u_password_hash,
            u.pnombre AS u_pnombre,
            u.snombre AS u_snombre,
            u.apaterno AS u_apaterno,
            u.amaterno AS u_amaterno,
            u.rol_id AS u_rol_id,
            u.activo AS u_activo,
            un.unidad_id AS un_unidad_id,
            un.nombre AS un_nombre,
            un.asignatura_id AS un_asignatura_id,
            un.estado_id AS un_estado_id,
            a.asignatura_id AS asig_asignatura_id,
            a.nombre AS asig_nombre,
            a.nivel_id AS asig_nivel_id
        FROM informe_pedagogico ip
        INNER JOIN usuario u ON ip.usuario_id = u.usuario_id
        INNER JOIN unidad un ON ip.unidad_id = un.unidad_id
        INNER JOIN asignatura a ON un.asignatura_id = a.asignatura_id
        WHERE ip.informe_pedagogico_id = :id
    """)
    suspend fun getInformeDetalleById(id: Long): InformePedagogicoDetalle?

    @Query("""
        SELECT 
            ip.*,
            u.usuario_id AS u_usuario_id,
            u.nombre_usuario AS u_nombre_usuario,
            u.password_hash AS u_password_hash,
            u.pnombre AS u_pnombre,
            u.snombre AS u_snombre,
            u.apaterno AS u_apaterno,
            u.amaterno AS u_amaterno,
            u.rol_id AS u_rol_id,
            u.activo AS u_activo,
            un.unidad_id AS un_unidad_id,
            un.nombre AS un_nombre,
            un.asignatura_id AS un_asignatura_id,
            un.estado_id AS un_estado_id,
            a.asignatura_id AS asig_asignatura_id,
            a.nombre AS asig_nombre,
            a.nivel_id AS asig_nivel_id
        FROM informe_pedagogico ip
        INNER JOIN usuario u ON ip.usuario_id = u.usuario_id
        INNER JOIN unidad un ON ip.unidad_id = un.unidad_id
        INNER JOIN asignatura a ON un.asignatura_id = a.asignatura_id
        WHERE ip.usuario_id = :usuarioId
        ORDER BY ip.informe_pedagogico_id DESC
    """)
    suspend fun getInformesDetalladosByUsuarioId(usuarioId: Long): List<InformePedagogicoDetalle>

    // DetalleInformePedagogico
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetalleInforme(entity: DetalleInformePedagogicoEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetallesInforme(entities: List<DetalleInformePedagogicoEntity>): List<Long>

    @Update
    suspend fun updateDetalleInforme(entity: DetalleInformePedagogicoEntity)

    @Delete
    suspend fun deleteDetalleInforme(entity: DetalleInformePedagogicoEntity)

    @Query("DELETE FROM detalle_informe_pedagogico WHERE informe_pedagogico_id = :informeId")
    suspend fun deleteDetallesByInformeId(informeId: Long)

    @Query("SELECT * FROM detalle_informe_pedagogico WHERE informe_pedagogico_id = :informeId")
    suspend fun getDetallesByInformeId(informeId: Long): List<DetalleInformePedagogicoEntity>

    @Query("""
        SELECT 
            d.*,
            oa.objetivo_aprendizaje_id AS oa_objetivo_aprendizaje_id,
            oa.codigo AS oa_codigo,
            oa.descripcion AS oa_descripcion
        FROM detalle_informe_pedagogico d
        INNER JOIN objetivo_aprendizaje oa ON d.objetivo_aprendizaje_id = oa.objetivo_aprendizaje_id
        WHERE d.informe_pedagogico_id = :informeId
    """)
    suspend fun getDetallesConObjetivoByInformeId(informeId: Long): List<DetalleInformeConObjetivo>
}

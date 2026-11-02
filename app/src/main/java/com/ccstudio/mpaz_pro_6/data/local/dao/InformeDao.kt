package com.ccstudio.mpaz_pro_6.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ccstudio.mpaz_pro_6.data.local.entity.informe.DetalleInformePedagogicoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.informe.InformePedagogicoEntity

@Dao
interface InformeDao {
    // InformePedagogico
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInformePedagogico(entity: InformePedagogicoEntity): Long

    @Update
    suspend fun updateInformePedagogico(entity: InformePedagogicoEntity)

    @Delete
    suspend fun deleteInformePedagogico(entity: InformePedagogicoEntity)

    @Query("SELECT * FROM informe_pedagogico WHERE informe_pedagogico_id = :id")
    suspend fun getInformePedagogicoById(id: Long): InformePedagogicoEntity?

    @Query("SELECT * FROM informe_pedagogico WHERE usuario_id = :usuarioId")
    suspend fun getInformesByUsuarioId(usuarioId: Long): List<InformePedagogicoEntity>

    // DetalleInformePedagogico
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetalleInforme(entity: DetalleInformePedagogicoEntity): Long

    @Update
    suspend fun updateDetalleInforme(entity: DetalleInformePedagogicoEntity)

    @Delete
    suspend fun deleteDetalleInforme(entity: DetalleInformePedagogicoEntity)

    @Query("SELECT * FROM detalle_informe_pedagogico WHERE informe_pedagogico_id = :informeId")
    suspend fun getDetallesByInformeId(informeId: Long): List<DetalleInformePedagogicoEntity>
}

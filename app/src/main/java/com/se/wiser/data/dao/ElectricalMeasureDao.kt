package com.se.wiser.data.dao

import androidx.room.*
import com.se.wiser.data.entity.ElectricalMeasureEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ElectricalMeasureDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertElectricalMeasures(vararg device: ElectricalMeasureEntity)

    @Update
    suspend fun updateElectricalMeasures(vararg device: ElectricalMeasureEntity)

    @Delete
    suspend fun deleteElectricalMeasures(vararg device: ElectricalMeasureEntity)

    @Query("SELECT * FROM electrical_measure_device")
    fun getAllElectricalMeasures(): Flow<List<ElectricalMeasureEntity>>
}
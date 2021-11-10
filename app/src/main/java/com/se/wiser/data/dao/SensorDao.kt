package com.se.wiser.data.dao

import androidx.room.*
import com.se.wiser.data.entity.SensorEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SensorDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSensors(vararg device: SensorEntity)

    @Update
    suspend fun updateSensors(vararg device: SensorEntity)

    @Delete
    suspend fun deleteSensors(vararg device: SensorEntity)

    @Query("SELECT * FROM sensor_device")
    fun getAllSensors(): Flow<List<SensorEntity>>
}
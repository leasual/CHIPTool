package com.se.wiser.data.dao

import androidx.room.*
import com.se.wiser.data.entity.HumidityAndTempEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HumidityAndTempDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHumidityAndTemps(vararg device: HumidityAndTempEntity)

    @Update
    suspend fun updateHumidityAndTemps(vararg device: HumidityAndTempEntity)

    @Delete
    suspend fun deleteHumidityAndTemps(vararg device: HumidityAndTempEntity)

    @Query("SELECT * FROM humidity_temp_device")
    fun getAllHumidityAndTemps(): Flow<List<HumidityAndTempEntity>>
}
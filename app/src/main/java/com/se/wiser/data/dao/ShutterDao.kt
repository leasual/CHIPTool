package com.se.wiser.data.dao

import androidx.room.*
import com.se.wiser.data.entity.ShutterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShutterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShutters(vararg device: ShutterEntity)

    @Update
    suspend fun updateShutters(vararg device: ShutterEntity)

    @Delete
    suspend fun deleteShutters(vararg device: ShutterEntity)

    @Query("SELECT * FROM shutter_device")
    suspend fun getAllShutters(): Flow<List<ShutterEntity>>
}
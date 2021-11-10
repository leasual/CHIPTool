package com.se.wiser.data.dao

import androidx.room.*
import com.se.wiser.data.entity.DimmerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DimmerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDimmers(vararg device: DimmerEntity)

    @Update
    suspend fun updateDimmers(vararg device: DimmerEntity)

    @Delete
    suspend fun deleteDimmers(vararg device: DimmerEntity)

    @Query("SELECT * FROM dimmer_device")
    fun getAllDimmers(): Flow<List<DimmerEntity>>
}
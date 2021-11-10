package com.se.wiser.data.dao

import androidx.room.*
import com.se.wiser.data.entity.SwitchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SwitchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSwitch(vararg device: SwitchEntity)

    @Update
    suspend fun updateSwitch(vararg device: SwitchEntity)

    @Delete
    suspend fun deleteSwitch(vararg device: SwitchEntity)

    @Query("SELECT * FROM switch_device")
    suspend fun getAllSwitches(): Flow<List<SwitchEntity>>
}
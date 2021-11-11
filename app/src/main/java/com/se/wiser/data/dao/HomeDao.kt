package com.se.wiser.data.dao

import androidx.room.*
import com.se.wiser.data.entity.HomeEntity
import com.se.wiser.data.entity.UserEntity
import com.se.wiser.data.model.UserAndHomeList
import kotlinx.coroutines.flow.Flow

@Dao
interface HomeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHomes(vararg home: HomeEntity)

    @Update
    suspend fun updateHomes(vararg home: HomeEntity)

    @Delete
    suspend fun deleteHomes(vararg home: HomeEntity)

    @Query("SELECT * FROM home")
    fun getAllHomes(): Flow<List<HomeEntity>>

    @Transaction
    @Query("SELECT * FROM home WHERE id = currentHomeId")
    fun getCurrentHome(): Flow<HomeEntity>

    @Update
    suspend fun updateCurrentHomeId(vararg home: HomeEntity)
}
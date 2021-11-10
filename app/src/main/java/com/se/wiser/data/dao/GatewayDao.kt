package com.se.wiser.data.dao

import androidx.room.*
import com.se.wiser.data.entity.GatewayEntity
import com.se.wiser.data.model.GatewayAndDeviceList
import kotlinx.coroutines.flow.Flow

@Dao
interface GatewayDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGateways(vararg device: GatewayEntity)

    @Update
    suspend fun updateGateways(vararg device: GatewayEntity)

    @Delete
    suspend fun deleteGateways(vararg device: GatewayEntity)

    @Query("SELECT * FROM gateway")
    suspend fun getAllGateways(): Flow<List<GatewayEntity>>

    @Transaction
    @Query("SELECT * FROM gateway WHERE gatewayId = :userId AND homeCreatorId =:homeId")
    suspend fun getUsersWithPlaylists(userId: Long, homeId: Long): Flow<List<GatewayAndDeviceList>>
}
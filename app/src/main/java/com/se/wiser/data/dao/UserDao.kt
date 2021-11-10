package com.se.wiser.data.dao

import androidx.room.*
import com.se.wiser.data.entity.UserEntity
import com.se.wiser.data.model.UserAndHomeList
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(vararg users: UserEntity)

    @Update
    suspend fun updateUsers(vararg users: UserEntity)

    @Delete
    suspend fun deleteUsers(vararg users: UserEntity)

    @Query("SELECT * FROM user")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Transaction
    @Query("SELECT * FROM user")
    fun getAllUsersAndHome(): Flow<List<UserAndHomeList>>
}
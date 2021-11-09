package com.se.wiser.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface MatterDatabaseDao {
    @Query("SELECT * from user")
    fun getUser(): List<UserItem>
}
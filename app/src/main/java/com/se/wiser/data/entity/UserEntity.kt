package com.se.wiser.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "username")
    var userName: String,

    @ColumnInfo(name = "phone")
    var phone: String = "",

    @ColumnInfo(name = "currentUserId")
    var currentUserId: Long = 0L
)
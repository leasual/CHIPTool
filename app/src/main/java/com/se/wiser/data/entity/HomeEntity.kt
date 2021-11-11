package com.se.wiser.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "home")
data class HomeEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name = "userId")
    var userId: Long,

    @ColumnInfo(name = "homeName")
    var homeName: String,

    @ColumnInfo(name = "location")
    var location: String = "",

    @ColumnInfo(name = "currentHomeId")
    var currentHomeId: Long = 0L
)
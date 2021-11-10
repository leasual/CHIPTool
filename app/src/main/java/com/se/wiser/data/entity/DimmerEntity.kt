package com.se.wiser.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dimmer_device")
data class DimmerEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name = "userId")
    var userId: Int,

    @ColumnInfo(name = "homeId")
    var homeId: Int,

    @ColumnInfo(name = "gatewayId")
    var gatewayId: Int,

    @ColumnInfo(name = "endpoint")
    var endpoint: Int,

    @ColumnInfo(name = "nodeId")
    var nodeId: Long,

    @ColumnInfo(name = "productModeId")
    var productModeId: String,

    @ColumnInfo(name = "deviceName")
    var deviceName: String,

    @ColumnInfo(name = "state")
    var state: Int,

    @ColumnInfo(name = "brightness")
    var brightness: Int,
)
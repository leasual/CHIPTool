package com.se.wiser.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shutter_device")
data class ShutterEntity(
    @PrimaryKey(autoGenerate = true)
    var deviceId: Long = 0L,

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

    @ColumnInfo(name = "liftPercentage")
    var liftPercentage: Int,

    @ColumnInfo(name = "tiltPercentage")
    var tiltPercentage: Int,
)
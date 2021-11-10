package com.se.wiser.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "electrical_measure_device")
data class ElectricalMeasureEntity(

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

    @ColumnInfo(name = "state")
    var state: Int,

    @ColumnInfo(name = "activePower")
    var activePower: Int,

    @ColumnInfo(name = "totalActivePower")
    var totalActivePower: Long,

    @ColumnInfo(name = "voltage")
    var voltage: Int,

    @ColumnInfo(name = "current")
    var current: Int,

    @ColumnInfo(name = "voltageMax")
    var voltageMax: Int,

    @ColumnInfo(name = "voltageMin")
    var voltageMin: Int,

    @ColumnInfo(name = "currentMax")
    var currentMax: Int,

    @ColumnInfo(name = "currentMin")
    var currentMin: Int,

)
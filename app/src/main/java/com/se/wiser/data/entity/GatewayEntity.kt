package com.se.wiser.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gateway")
data class GatewayEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name = "userId")
    var userId: Int,

    @ColumnInfo(name = "homeId")
    var homeId: Int,

    @ColumnInfo(name = "type")
    var type: Int,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "location")
    var location: String,

    @ColumnInfo(name = "vendorId")
    var vendorId: Int,

    @ColumnInfo(name = "productId")
    var productId: Int,

    @ColumnInfo(name = "discriminator")
    var discriminator: Int,

    @ColumnInfo(name = "setupPinCode")
    var setupPinCode: Long,
)
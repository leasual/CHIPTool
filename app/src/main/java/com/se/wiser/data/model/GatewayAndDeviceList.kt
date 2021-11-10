package com.se.wiser.data.model

import androidx.room.Embedded
import androidx.room.Relation
import com.se.wiser.data.entity.*

data class GatewayAndDeviceList(
    @Embedded val gateway: GatewayEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "gatewayId"
    )
    val sensorList: List<SensorEntity>?,
    @Relation(
        parentColumn = "id",
        entityColumn = "gatewayId"
    )
    val shutterList: List<ShutterEntity>?,
    @Relation(
        parentColumn = "id",
        entityColumn = "gatewayId"
    )
    val HumidityAndTempList: List<HumidityAndTempEntity>?,
    @Relation(
        parentColumn = "id",
        entityColumn = "gatewayId"
    )
    val switchList: List<SwitchEntity>?,
    @Relation(
        parentColumn = "id",
        entityColumn = "gatewayId"
    )
    val dimmerList: List<DimmerEntity>?,
    @Relation(
        parentColumn = "id",
        entityColumn = "gatewayId"
    )
    val electricalMeasureList: List<ElectricalMeasureEntity>?,
)

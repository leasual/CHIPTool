package com.se.wiser.data.model

import androidx.room.Embedded
import androidx.room.Relation
import com.se.wiser.data.entity.*

data class GatewayAndDeviceList(
    @Embedded val user: GatewayEntity,
    @Relation(
        parentColumn = "gatewayId",
        entityColumn = "deviceCreatorId"
    )
    val sensorList: List<SensorEntity>?,
    val shutterList: List<ShutterEntity>?,
    val HumidityAndTempList: List<HumidityAndTempEntity>?,
    val switchList: List<SwitchEntity>?,
    val dimmerList: List<DimmerEntity>?,
    val electricalMeasureList: List<ElectricalMeasureEntity>?,
)

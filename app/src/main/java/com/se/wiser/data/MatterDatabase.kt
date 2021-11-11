package com.se.wiser.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.se.wiser.data.dao.*
import com.se.wiser.data.entity.*

@Database(entities = [
    DimmerEntity::class,
    ElectricalMeasureEntity::class,
    GatewayEntity::class,
    HomeEntity::class,
    HumidityAndTempEntity::class,
    SceneEntity::class,
    SensorEntity::class,
    ShutterEntity::class,
    SwitchEntity::class,
    UserEntity::class], exportSchema = false, version = 1)
abstract class MatterDatabase: RoomDatabase() {
    abstract fun dimmerDao(): DimmerDao
    abstract fun electricalMeasureDao(): ElectricalMeasureDao
    abstract fun gatewayDao(): GatewayDao
    abstract fun humidityAndTempDao(): HumidityAndTempDao
    abstract fun sensorDao(): SensorDao
    abstract fun shutterDao(): ShutterDao
    abstract fun switchDao(): SwitchDao
    abstract fun userDao(): UserDao
    abstract fun homeDao(): HomeDao
}
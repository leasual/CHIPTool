package com.se.wiser.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.se.wiser.data.dao.*
import com.se.wiser.data.entity.UserEntity

@Database(entities = [UserEntity::class], version = 1)
abstract class MatterDatabase: RoomDatabase() {
    abstract fun dimmerDao(): DimmerDao
    abstract fun electricalMeasureDao(): ElectricalMeasureDao
    abstract fun gatewayDao(): GatewayDao
    abstract fun humidityAndTempDao(): HumidityAndTempDao
    abstract fun sensorDao(): SensorDao
    abstract fun shutterDao(): ShutterDao
    abstract fun switchDao(): SwitchDao
    abstract fun userDao(): UserDao

    companion object {
        private var INSTANCE: MatterDatabase? = null

        fun getInstance(context: Context): MatterDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        MatterDatabase::class.java,
                        "matter_database"
                    ).fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
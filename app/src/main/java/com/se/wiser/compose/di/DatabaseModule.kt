package com.se.wiser.compose.di

import android.content.Context
import androidx.room.Room
import com.se.wiser.App
import com.se.wiser.data.MatterDatabase
import com.se.wiser.data.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context):
            MatterDatabase {
        return Room.databaseBuilder(
            appContext,
            MatterDatabase::class.java,
            "matter_database",
        ).build()
    }

    @Provides
    fun provideDimmerDao(matterDatabase: MatterDatabase): DimmerDao = matterDatabase.dimmerDao()

    @Provides
    fun provideElectricalMeasureDao(matterDatabase: MatterDatabase): ElectricalMeasureDao =
        matterDatabase.electricalMeasureDao()

    @Provides
    fun provideGatewayDao(matterDatabase: MatterDatabase): GatewayDao = matterDatabase.gatewayDao()

    @Provides
    fun provideHumidityAndTempDao(matterDatabase: MatterDatabase): HumidityAndTempDao =
        matterDatabase.humidityAndTempDao()

    @Provides
    fun provideSensorDao(matterDatabase: MatterDatabase): SensorDao = matterDatabase.sensorDao()

    @Provides
    fun provideShutterDao(matterDatabase: MatterDatabase): ShutterDao = matterDatabase.shutterDao()


    @Provides
    fun provideSwitchDao(matterDatabase: MatterDatabase): SwitchDao = matterDatabase.switchDao()


    @Provides
    fun provideUserDao(matterDatabase: MatterDatabase): UserDao = matterDatabase.userDao()

    @Provides
    fun provideHomeDao(matterDatabase: MatterDatabase): HomeDao = matterDatabase.homeDao()

}
package com.se.wiser.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserItem::class], version = 1)
abstract class MatterDatabase: RoomDatabase() {
    abstract fun userDao(): MatterDatabaseDao

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
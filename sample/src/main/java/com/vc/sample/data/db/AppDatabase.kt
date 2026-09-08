package com.vc.sample.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Sample Application Room Database.
 */
@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}

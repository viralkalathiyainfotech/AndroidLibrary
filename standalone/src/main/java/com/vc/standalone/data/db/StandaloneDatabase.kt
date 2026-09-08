package com.vc.standalone.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Standalone Room Database definition.
 */
@Database(
    entities = [StandaloneUserEntity::class],
    version = 1,
    exportSchema = false
)
abstract class StandaloneDatabase : RoomDatabase() {
    abstract fun userDao(): StandaloneUserDao
}

package com.vc.androidcore.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration

/**
 * Factory and builder for creating [RoomDatabase] instances safely.
 * Destructive migration is strictly disabled by default.
 */
object DatabaseProvider {

    class Builder<T : RoomDatabase>(
        private val context: Context,
        private val klass: Class<T>,
        private val name: String
    ) {
        private val migrations = mutableListOf<Migration>()
        private var fallbackToDestructiveMigration: Boolean = false
        private var fallbackToDestructiveMigrationOnDowngrade: Boolean = false
        private val callbacks = mutableListOf<RoomDatabase.Callback>()

        fun addMigrations(vararg migrations: Migration) = apply {
            this.migrations.addAll(migrations)
        }

        fun fallbackToDestructiveMigration(enable: Boolean = true) = apply {
            this.fallbackToDestructiveMigration = enable
        }

        fun fallbackToDestructiveMigrationOnDowngrade(enable: Boolean = true) = apply {
            this.fallbackToDestructiveMigrationOnDowngrade = enable
        }

        fun addCallback(callback: RoomDatabase.Callback) = apply {
            this.callbacks.add(callback)
        }

        fun build(): T {
            val builder = Room.databaseBuilder(context.applicationContext, klass, name)

            if (migrations.isNotEmpty()) {
                builder.addMigrations(*migrations.toTypedArray())
            }

            if (fallbackToDestructiveMigration) {
                builder.fallbackToDestructiveMigration()
            }

            if (fallbackToDestructiveMigrationOnDowngrade) {
                builder.fallbackToDestructiveMigrationOnDowngrade()
            }

            callbacks.forEach { builder.addCallback(it) }

            return builder.build()
        }
    }

    /**
     * Creates a builder for persistent Room database.
     */
    fun <T : RoomDatabase> builder(
        context: Context,
        klass: Class<T>,
        databaseName: String
    ): Builder<T> = Builder(context, klass, databaseName)

    /**
     * Creates an in-memory Room database for unit/instrumentation tests.
     */
    fun <T : RoomDatabase> createInMemory(
        context: Context,
        klass: Class<T>
    ): T {
        return Room.inMemoryDatabaseBuilder(context.applicationContext, klass)
            .allowMainThreadQueries()
            .build()
    }
}

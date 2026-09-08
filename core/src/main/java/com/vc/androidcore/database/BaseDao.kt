package com.vc.androidcore.database

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import androidx.room.Upsert

/**
 * Generic Base Data Access Object (DAO) defining common CRUD operations.
 *
 * @param T Entity type managed by this DAO.
 */
interface BaseDao<T> {

    /**
     * Inserts an item into the database, replacing existing item on conflict.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: T)

    /**
     * Inserts a list of items into the database, replacing existing items on conflict.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<T>)

    /**
     * Updates an existing item in the database.
     */
    @Update
    suspend fun update(item: T)

    /**
     * Updates multiple existing items in the database.
     */
    @Update
    suspend fun updateAll(items: List<T>)

    /**
     * Deletes an item from the database.
     */
    @Delete
    suspend fun delete(item: T)

    /**
     * Deletes multiple items from the database.
     */
    @Delete
    suspend fun deleteAll(items: List<T>)

    /**
     * Inserts or updates an item in the database.
     */
    @Upsert
    suspend fun upsert(item: T)

    /**
     * Inserts or updates multiple items in the database.
     */
    @Upsert
    suspend fun upsertAll(items: List<T>)
}

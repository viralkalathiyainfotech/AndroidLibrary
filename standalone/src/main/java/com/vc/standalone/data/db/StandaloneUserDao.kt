package com.vc.standalone.data.db

import androidx.room.Dao
import androidx.room.Query
import com.vc.androidcore.database.BaseDao
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object extending [BaseDao] to inherit standard CRUD methods
 * and defining custom queries.
 */
@Dao
interface StandaloneUserDao : BaseDao<StandaloneUserEntity> {

    @Query("SELECT * FROM standalone_users ORDER BY id ASC")
    fun getAllUsersFlow(): Flow<List<StandaloneUserEntity>>

    @Query("SELECT * FROM standalone_users ORDER BY id ASC")
    suspend fun getAllUsers(): List<StandaloneUserEntity>

    @Query("SELECT * FROM standalone_users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Int): StandaloneUserEntity?

    @Query("DELETE FROM standalone_users")
    suspend fun clearAll()
}

package com.vc.sample.data.db

import androidx.room.Dao
import androidx.room.Query
import com.vc.androidcore.database.BaseDao
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Users extending [BaseDao] from the core library.
 */
@Dao
interface UserDao : BaseDao<UserEntity> {

    @Query("SELECT * FROM users ORDER BY name ASC")
    fun getUsersFlow(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users ORDER BY name ASC")
    suspend fun getUsersOnce(): List<UserEntity>

    @Query("SELECT * FROM users WHERE name LIKE '%' || :query || '%' OR email LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchUsersFlow(query: String): Flow<List<UserEntity>>

    @Query("DELETE FROM users")
    suspend fun clearUsers()
}

package com.vc.sample.data.repository

import com.vc.androidcore.network.NetworkMonitor
import com.vc.androidcore.network.NetworkResult
import com.vc.androidcore.repository.BaseRepository
import com.vc.androidcore.state.UiState
import com.vc.sample.data.api.UserApiService
import com.vc.sample.data.db.UserDao
import com.vc.sample.data.db.toEntity
import com.vc.sample.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Repository demonstrating Clean Architecture with:
 * 1. Isolated Remote API Operations (API Only)
 * 2. Isolated Local Room Database Operations (Room DB Only)
 * 3. Combined Offline-First Sync Workflow:
 *    [API Call] -> [Save to Room DB] -> [Get from Room DB] -> [Display in UI List]
 */
class UserRepository(
    private val apiService: UserApiService,
    private val userDao: UserDao,
    private val networkMonitor: NetworkMonitor
) : BaseRepository() {

    // =========================================================================
    // 1. SEPARATE LOGIC: REMOTE API ONLY (NO ROOM DB INVOLVED)
    // =========================================================================

    /**
     * Executes standalone network API call to JSONPlaceholder.
     * Returns domain models wrapped in [NetworkResult] without modifying local database.
     */
    suspend fun fetchUsersFromRemote(): NetworkResult<List<User>> {
        val result = executeApiCall { apiService.getUsers() }
        return result.map { dtoList ->
            dtoList.map { dto ->
                User(
                    id = dto.id,
                    name = dto.name,
                    username = dto.username,
                    email = dto.email,
                    phone = dto.phone ?: "",
                    website = dto.website ?: "",
                    companyName = dto.company?.name ?: ""
                )
            }
        }
    }

    // =========================================================================
    // 2. SEPARATE LOGIC: LOCAL ROOM DATABASE ONLY (NO NETWORK CALLS INVOLVED)
    // =========================================================================

    /**
     * Reactively streams users stored in the local Room database using [Flow].
     */
    fun getUsersFromLocal(): Flow<List<User>> {
        return userDao.getUsersFlow().map { entities ->
            entities.map { it.toDomain() }
        }.flowOn(ioDispatcher)
    }

    /**
     * One-shot read of all users currently saved in the local Room database.
     */
    suspend fun getUsersFromLocalOnce(): List<User> = withContext(ioDispatcher) {
        userDao.getUsersOnce().map { it.toDomain() }
    }

    /**
     * Saves a list of users directly into the local Room database using [BaseDao.insertAll].
     */
    suspend fun saveUsersToLocal(users: List<User>) = withContext(ioDispatcher) {
        val entities = users.map { it.toEntity() }
        userDao.insertAll(entities)
    }

    /**
     * Wipes all user records from the local Room database.
     */
    suspend fun clearLocalUsers() = withContext(ioDispatcher) {
        userDao.clearUsers()
    }

    /**
     * Searches users matching the query from the local Room database.
     */
    fun searchUsersInLocal(query: String): Flow<List<User>> {
        return userDao.searchUsersFlow(query).map { entities ->
            entities.map { it.toDomain() }
        }.flowOn(ioDispatcher)
    }

    // =========================================================================
    // 3. COMBINED WORKFLOW: API CALL -> SAVE TO ROOM DB -> GET FROM ROOM -> LIST
    // =========================================================================

    /**
     * Complete coordinated offline-first workflow demonstrating:
     * Step 1: Call API (fetchUsersFromRemote)
     * Step 2: On API response, save payload to Room Database (saveUsersToLocal)
     * Step 3: Retrieve values from Room Database (getUsersFromLocal)
     * Step 4: Stream Room values to the UI List
     *
     * If offline or API fails, streams cached Room items with appropriate fallback error state.
     */
    fun syncApiToDatabaseAndObserve(): Flow<UiState<List<User>>> = flow {
        emit(UiState.Loading)

        // Read current local Room cache first
        val cachedUsers = getUsersFromLocalOnce()
        if (cachedUsers.isNotEmpty()) {
            emit(UiState.Success(cachedUsers))
        }

        // Step 1: Call Remote API
        if (networkMonitor.isCurrentlyOnline()) {
            when (val apiResult = fetchUsersFromRemote()) {
                is NetworkResult.Success -> {
                    // Step 2: Save API response into Room Database
                    saveUsersToLocal(apiResult.data)

                    // Step 3 & 4: Get values from Room Database and set to list
                    emitAll(getUsersFromLocal().map { UiState.Success(it) })
                }
                is NetworkResult.Error -> {
                    // If API fails but cache exists, keep showing cache + emit error
                    if (cachedUsers.isEmpty()) {
                        emit(UiState.Error(message = apiResult.message, appError = apiResult.appError))
                    } else {
                        emit(UiState.Success(cachedUsers))
                    }
                }
                is NetworkResult.Loading -> {
                    emit(UiState.Loading)
                }
            }
        } else {
            // Offline scenario: show Room cached data or empty state
            if (cachedUsers.isNotEmpty()) {
                emit(UiState.Success(cachedUsers))
            } else {
                emit(UiState.Error(message = "Offline: No cached data in Room database."))
            }
        }
    }.flowOn(ioDispatcher)
}

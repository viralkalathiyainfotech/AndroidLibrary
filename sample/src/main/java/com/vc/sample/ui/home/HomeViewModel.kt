package com.vc.sample.ui.home

import androidx.lifecycle.viewModelScope
import com.vc.androidcore.base.BaseViewModel
import com.vc.androidcore.network.NetworkMonitor
import com.vc.androidcore.network.NetworkResult
import com.vc.androidcore.preferences.DataStoreManager
import com.vc.androidcore.state.UiEvent
import com.vc.androidcore.state.UiState
import com.vc.sample.data.model.User
import com.vc.sample.data.repository.UserRepository
import com.vc.sample.ui.login.LoginActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

/**
 * ViewModel demonstrating:
 * 1. Isolated API Call (Network Only)
 * 2. Isolated Room DB Call (Room Only)
 * 3. Combined Offline-First Pipeline: API -> Save to Room -> Get from Room -> Display in List
 */
class HomeViewModel(
    private val userRepository: UserRepository,
    private val networkMonitor: NetworkMonitor,
    private val dataStoreManager: DataStoreManager
) : BaseViewModel() {

    private val _usersState = MutableStateFlow<UiState<List<User>>>(UiState.Idle)
    val usersState: StateFlow<UiState<List<User>>> = _usersState.asStateFlow()

    private val _pipelineStatus = MutableStateFlow("Ready. Select an operation above.")
    val pipelineStatus: StateFlow<String> = _pipelineStatus.asStateFlow()

    val isOnline: StateFlow<Boolean> = networkMonitor.isOnline

    init {
        loadFromSync()
    }

    // =========================================================================
    // WORKFLOW 1: COMBINED PIPELINE (API -> SAVE ROOM -> READ ROOM -> LIST)
    // =========================================================================
    fun loadFromSync() {
        viewModelScope.launch {
            _usersState.value = UiState.Loading
            _pipelineStatus.value = "🔄 [Sync Pipeline] Starting: 1. Calling Remote API..."

            if (!networkMonitor.isCurrentlyOnline()) {
                _pipelineStatus.value = "⚠️ [Offline] Network offline. Reading cached data from Room DB..."
                val localUsers = userRepository.getUsersFromLocalOnce()
                if (localUsers.isNotEmpty()) {
                    _usersState.value = UiState.Success(localUsers)
                    _pipelineStatus.value = "✅ [Offline Mode] Loaded ${localUsers.size} cached users from Room DB."
                } else {
                    _usersState.value = UiState.Error("Offline: Room database is empty.")
                    _pipelineStatus.value = "❌ [Offline Mode] Room database cache is empty."
                }
                return@launch
            }

            // Step 1: Call Remote API
            when (val apiResult = userRepository.fetchUsersFromRemote()) {
                is NetworkResult.Success -> {
                    val remoteUsers = apiResult.data
                    _pipelineStatus.value = "📥 [Sync Pipeline] 1. API Returned ${remoteUsers.size} users -> 2. Saving to Room DB..."

                    // Step 2: Save into Room Database
                    userRepository.saveUsersToLocal(remoteUsers)
                    delay(300)

                    // Step 3 & 4: Read from Room Database and set in UI List
                    _pipelineStatus.value = "📖 [Sync Pipeline] 3. Querying Room DB -> 4. Displaying in List..."
                    val roomUsers = userRepository.getUsersFromLocalOnce()
                    _usersState.value = UiState.Success(roomUsers)
                    _pipelineStatus.value = "✅ [Sync Pipeline Complete] API (${remoteUsers.size}) ➔ Room DB (${roomUsers.size}) ➔ Rendered in List."
                }
                is NetworkResult.Error -> {
                    _pipelineStatus.value = "⚠️ [API Error] ${apiResult.message}. Falling back to Room DB cache..."
                    val cachedUsers = userRepository.getUsersFromLocalOnce()
                    if (cachedUsers.isNotEmpty()) {
                        _usersState.value = UiState.Success(cachedUsers)
                        _pipelineStatus.value = "✅ [Fallback] Showing ${cachedUsers.size} cached users from Room DB."
                    } else {
                        _usersState.value = UiState.Error(apiResult.message, apiResult.throwable, apiResult.code, apiResult.appError)
                        _pipelineStatus.value = "❌ [Failed] API failed and no cached data in Room DB."
                    }
                }
                is NetworkResult.Loading -> {
                    _usersState.value = UiState.Loading
                }
            }
        }
    }

    // =========================================================================
    // WORKFLOW 2: SEPARATE REMOTE API ONLY (NO ROOM DB INVOLVED)
    // =========================================================================
    fun loadFromApiOnly() {
        launchSafe(showLoading = false) {
            _usersState.value = UiState.Loading
            _pipelineStatus.value = "🌐 [API Only] Calling remote JSONPlaceholder API directly (Room DB bypassed)..."

            when (val result = userRepository.fetchUsersFromRemote()) {
                is NetworkResult.Success -> {
                    val users = result.data
                    _usersState.value = UiState.Success(users)
                    _pipelineStatus.value = "✅ [API Only] Displaying ${users.size} live users directly from API (Room DB was NOT touched)."
                }
                is NetworkResult.Error -> {
                    _usersState.value = UiState.Error(result.message, result.throwable, result.code, result.appError)
                    _pipelineStatus.value = "❌ [API Only Error] ${result.message}"
                }
                is NetworkResult.Loading -> {
                    _usersState.value = UiState.Loading
                }
            }
        }
    }

    // =========================================================================
    // WORKFLOW 3: SEPARATE LOCAL ROOM DATABASE ONLY (NO API CALLED)
    // =========================================================================
    fun loadFromRoomOnly() {
        viewModelScope.launch {
            _usersState.value = UiState.Loading
            _pipelineStatus.value = "💾 [Room DB Only] Querying SQLite Room database directly (Network bypassed)..."
            delay(200.milliseconds)

            val roomUsers = userRepository.getUsersFromLocalOnce()
            if (roomUsers.isNotEmpty()) {
                _usersState.value = UiState.Success(roomUsers)
                _pipelineStatus.value = "✅ [Room DB Only] Displaying ${roomUsers.size} users stored in local Room DB."
            } else {
                _usersState.value = UiState.Success(emptyList())
                _pipelineStatus.value = "ℹ️ [Room DB Only] Room database is currently empty (0 records)."
            }
        }
    }

    // =========================================================================
    // WORKFLOW 4: CLEAR ROOM DATABASE
    // =========================================================================
    fun clearRoomDatabase() {
        viewModelScope.launch {
            _pipelineStatus.value = "🗑️ Clearing all user records from Room database..."
            userRepository.clearLocalUsers()
            delay(200.milliseconds)
            _usersState.value = UiState.Success(emptyList())
            _pipelineStatus.value = "🗑️ [Room Cleared] Local Room database is now empty. Tap 'Room Only' or 'Sync' to test."
            sendEvent(UiEvent.ShowToast("Room database cleared successfully."))
        }
    }

    fun search(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                loadFromRoomOnly()
            } else {
                userRepository.searchUsersInLocal(query).collectLatest { users ->
                    _usersState.value = UiState.Success(users)
                    _pipelineStatus.value = "🔍 Found ${users.size} users matching '$query' in Room DB."
                }
            }
        }
    }

    fun logout() {
        launchSafe(showLoading = true) {
            dataStoreManager.clearAll()
            userRepository.clearLocalUsers()
            sendEvent(UiEvent.Navigate(destination = LoginActivity::class.java, finishCurrent = true))
        }
    }
}

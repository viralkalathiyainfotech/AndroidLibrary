package com.vc.standalone.ui

import android.Manifest
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.vc.androidcore.base.BaseActivity
import com.vc.androidcore.database.DatabaseProvider
import com.vc.androidcore.logging.CoreLogger
import com.vc.androidcore.network.LiveNetworkMonitor
import com.vc.androidcore.network.NetworkResult
import com.vc.androidcore.network.RetrofitProvider
import com.vc.androidcore.network.multipart.MultipartHelper
import com.vc.androidcore.network.safeApiCall
import com.vc.androidcore.preferences.DataStoreManager
import com.vc.androidcore.utils.DateUtils
import com.vc.androidcore.utils.getColorCompat
import com.vc.androidcore.utils.gone
import com.vc.androidcore.utils.setOnDebouncedClickListener
import com.vc.androidcore.utils.visible
import com.vc.standalone.R
import com.vc.standalone.data.api.StandaloneApiService
import com.vc.standalone.data.db.StandaloneDatabase
import com.vc.standalone.data.db.StandaloneUserEntity
import com.vc.standalone.data.model.StandaloneUser
import com.vc.standalone.data.model.StandaloneUserDto
import com.vc.standalone.databinding.ActivityStandaloneBinding
import com.vc.standalone.ui.adapter.StandaloneUserAdapter
import com.vc.standalone.ui.dialog.UserDetailBottomSheet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * [StandaloneActivity] demonstrates making network API calls directly from an Activity
 * WITHOUT a ViewModel, while comprehensively utilizing core common classes:
 *
 * - [BaseActivity] for lifecycle management, loading dialogs, toasts, snackbars, and error presentation.
 * - [RetrofitProvider] for building isolated, secure Retrofit interfaces.
 * - [safeApiCall] for coroutine-safe API execution with automatic exception mapping.
 * - [NetworkResult] for handling Success, Error, and Loading states.
 * - [LiveNetworkMonitor] for synchronous and reactive connectivity verification.
 * - [BaseListAdapter] and [DiffUtilItemCallback] for reactive list rendering.
 * - [DatabaseProvider] & [BaseDao] for Room CRUD operations directly in coroutines.
 * - [DataStoreManager] for key-value preference storage without ViewModel mediation.
 * - [BaseBottomSheetDialog] for modal bottom sheet presentation.
 * - [CoreLogger] for sensitive data sanitized logging.
 * - View extensions ([visible], [gone], [setOnDebouncedClickListener]).
 */
class StandaloneActivity : BaseActivity<ActivityStandaloneBinding>() {

    // 1. Networking service created via Core's RetrofitProvider
    private val apiService: StandaloneApiService by lazy {
        RetrofitProvider.createService("https://jsonplaceholder.typicode.com/")
    }

    // 2. Room Database created via Core's DatabaseProvider
    private val database: StandaloneDatabase by lazy {
        DatabaseProvider.builder(
            context = this,
            klass = StandaloneDatabase::class.java,
            databaseName = "standalone_users.db"
        )
            .fallbackToDestructiveMigration(true)
            .build()
    }

    // 3. DataStore preferences manager from Core
    private val dataStore: DataStoreManager by lazy {
        DataStoreManager(applicationContext)
    }

    // 4. User list adapter extending Core's BaseListAdapter
    private lateinit var userAdapter: StandaloneUserAdapter

    override fun inflateBinding(): ActivityStandaloneBinding {
        return ActivityStandaloneBinding.inflate(layoutInflater)
    }

    override fun setupUI() {
        // Setup Toolbar using BaseActivity helper
        setupToolbar(
            binding.toolbar,
            title = getString(R.string.standalone_title),
            displayHomeAsUp = false
        )

        // Initialize RecyclerView with BaseListAdapter
        userAdapter = StandaloneUserAdapter { selectedUser ->
            // Demonstrate BaseBottomSheetDialog on user card click
            UserDetailBottomSheet(selectedUser).show(supportFragmentManager, "UserDetailSheet")
        }

        binding.rvUsers.apply {
            layoutManager = LinearLayoutManager(this@StandaloneActivity)
            adapter = userAdapter
        }

        updateListDisplay(emptyList())
    }

    override fun setupListeners() {
        // Button 1: Pure Direct API Call (No ViewModel)
        binding.btnFetchApi.setOnDebouncedClickListener {
            fetchUsersDirectlyFromApi()
        }

        // Button 2: Direct API Call + Save into Room Database (No ViewModel)
        binding.btnFetchAndCache.setOnDebouncedClickListener {
            fetchFromApiAndSaveToRoom()
        }

        // Button 3: Direct Load from Room Database (No ViewModel)
        binding.btnLoadRoom.setOnDebouncedClickListener {
            loadUsersFromRoomDatabase()
        }

        // Button 4: Direct Read/Write Jetpack DataStore (No ViewModel)
        binding.btnTestDataStore.setOnDebouncedClickListener {
            testDataStoreOperations()
        }

        // Button 5: Modern Permission Manager (No ViewModel)
        binding.btnTestPermissions.setOnDebouncedClickListener {
            testPermissionManager()
        }

        // Button 6: Multipart Upload & Progress (No ViewModel)
        binding.btnTestMultipart.setOnDebouncedClickListener {
            testMultipartUpload()
        }

        // Button 7: Clear Room Database
        binding.btnClearData.setOnDebouncedClickListener {
            clearRoomDatabase()
        }
    }

    override fun observeData() {
        // Observe real-time network connectivity changes using BaseActivity's collectLifecycleFlow
        collectLifecycleFlow(networkMonitor.isOnline) { isOnline ->
            if (isOnline) {
                binding.tvNetworkStatus.text = "ONLINE"
                binding.tvNetworkStatus.setBackgroundColor(getColorCompat(R.color.standalone_success))
            } else {
                binding.tvNetworkStatus.text = "OFFLINE"
                binding.tvNetworkStatus.setBackgroundColor(getColorCompat(R.color.standalone_error))
                showSnackbar("Network is currently offline")
            }
        }
    }

    // =============================================================================================
    // 1. Direct API Call (Ultra-Clean Single Call - No ViewModel)
    // =============================================================================================
    private fun fetchUsersDirectlyFromApi() {
        CoreLogger.d("Starting single-call API via launchApiCallMapped (No ViewModel)")
        updateTrace("Calling API: https://jsonplaceholder.typicode.com/users ...")

        // Single call: Auto network check, loading dialog, IO dispatch, error handling & DTO mapping
        launchApiCallMapped(
            loadingMessage = "Fetching users from API...",
            request = { apiService.getUsers() },
            transform = { dtoList -> dtoList.map { it.toDomain() } }
        ) { users ->
            CoreLogger.d("Single-call API succeeded. Received ${users.size} users.")
            updateTrace("API Success! Loaded ${users.size} items directly.")
            updateListDisplay(users)
            showToast("Loaded ${users.size} users from API")
        }
    }

//    private fun dataFromApi() {
//        executeApi<List<StandaloneUserDto>> {
//            request { apiService.getUsers() }
//            loading(message = "Fetching users...")
//            onSuccess { users ->
//            }
//        }
//
//        launchApiCall(
//            request = { apiService.getUsers() },
//            onSuccess = { users -> adapter.submitList(users) }
//        )
//
//    }


    // =============================================================================================
    // 2. Direct API Call + Save into Room Database (No ViewModel)
    // =============================================================================================
    private fun fetchFromApiAndSaveToRoom() {
        updateTrace("Step 1: Fetching API data...")

        // Fetch remotely via single-call helper, then write to Room on IO
        launchApiCallMapped(
            loadingMessage = "Syncing API to Room...",
            request = { apiService.getUsers() },
            transform = { dtoList -> dtoList.map { it.toDomain() } }
        ) { users ->
            updateTrace("Step 2: Saving ${users.size} entities to Room DB...")

            lifecycleScope.launch(Dispatchers.IO) {
                val entities = users.map { StandaloneUserEntity.fromDomain(it) }
                database.userDao().insertAll(entities)

                val cachedUsers = database.userDao().getAllUsers().map { it.toDomain() }
                withContext(Dispatchers.Main) {
                    updateTrace("Step 3: Rendered ${cachedUsers.size} items from Room DB.")
                    updateListDisplay(cachedUsers)
                    showToast("Synced ${cachedUsers.size} users into Room Database")
                }
            }
        }
    }

    // =============================================================================================
    // 3. Direct Load from Room Database (No ViewModel)
    // =============================================================================================
    private fun loadUsersFromRoomDatabase() {
        updateTrace("Reading local cached users from Room DB...")

        lifecycleScope.launch {
            showLoading("Querying Room Database...")

            val localUsers = withContext(Dispatchers.IO) {
                database.userDao().getAllUsers().map { it.toDomain() }
            }

            hideLoading()

            if (localUsers.isNotEmpty()) {
                updateTrace("Room DB: Loaded ${localUsers.size} cached records.")
                updateListDisplay(localUsers)
                showToast("Retrieved ${localUsers.size} users from Room")
            } else {
                updateTrace("Room DB is empty. Run 'API ➔ Cache to Room' first.")
                updateListDisplay(emptyList())
                showSnackbar("Room DB is currently empty")
            }
        }
    }

    // =============================================================================================
    // 4. Direct DataStore Read & Write (No ViewModel)
    // =============================================================================================
    private fun testDataStoreOperations() {
        lifecycleScope.launch {
            showLoading("Writing to DataStore...")

            val timestamp = System.currentTimeMillis()
            val formattedTime = DateUtils.formatDateTime(timestamp)

            // Save key-values directly using Core's DataStoreManager
            dataStore.putString("last_action", "DataStore direct test at $formattedTime")
            dataStore.putLong("last_test_epoch", timestamp)

            // Read values back
            val savedAction = dataStore.getString("last_action")
            val savedEpoch = dataStore.getLong("last_test_epoch")

            hideLoading()
            updateTrace("DataStore Verified!\nKey 'last_action': $savedAction\nEpoch: $savedEpoch")
            showToast("DataStore read/write successful")
        }
    }

    // =============================================================================================
    // 5. Clear Room Database
    // =============================================================================================
    private fun clearRoomDatabase() {
        lifecycleScope.launch {
            showLoading("Clearing Room Database...")

            withContext(Dispatchers.IO) {
                database.userDao().clearAll()
            }

            hideLoading()
            updateTrace("Room Database wiped. Local user count: 0")
            updateListDisplay(emptyList())
            showToast("Room database cleared")
        }
    }

    // =============================================================================================
    // 6. Test Modern Permission Manager
    // =============================================================================================
    private fun testPermissionManager() {
        // Demonstrate requesting runtime permission using BaseActivity's requestPermission
        requestPermission(Manifest.permission.CAMERA) { isGranted ->
            if (isGranted) {
                showToast("Camera permission GRANTED!")
                updateTrace("Permission Manager: Camera permission granted.")
            } else {
                showSnackbar("Camera permission was DENIED", "Open Settings") {
                    openAppSettings()
                }
                updateTrace("Permission Manager: Camera permission denied. Click Settings to grant.")
            }
        }
    }

    // =============================================================================================
    // 7. Test File / Image Upload & Multipart Helper
    // =============================================================================================
    private fun testMultipartUpload() {
        lifecycleScope.launch {
            showLoading("Building multipart body...")

            // 1. Create simulated in-memory binary payload
            val fakeImageBytes =
                "Simulated binary PNG/JPEG content for testing progress".toByteArray()

            // 2. Build MultipartBody.Part with live progress listener via MultipartHelper
            val multipartPart = MultipartHelper.createPartFromBytes(
                bytes = fakeImageBytes,
                partName = "avatar",
                fileName = "avatar_test.jpg",
                mimeType = "image/jpeg",
                onProgress = { bytesWritten, totalBytes, percent ->
                    updateTrace("Uploading: $percent% ($bytesWritten / $totalBytes bytes)")
                }
            )

            // 3. Create companion form text fields using MultipartHelper
            val textParams = MultipartHelper.createPartMap(
                mapOf(
                    "userId" to "101",
                    "description" to "Test profile avatar upload"
                )
            )

            hideLoading()
            updateTrace("MultipartHelper: Part generated (${multipartPart.body.contentType()}). Parts: ${textParams.size + 1}")
            showToast("Multipart prepared successfully (${multipartPart.body.contentLength()} bytes)")
        }
    }

    // =============================================================================================
    // UI State Helpers
    // =============================================================================================
    private fun updateListDisplay(users: List<StandaloneUser>) {
        userAdapter.submitList(users)
        if (users.isEmpty()) {
            binding.tvEmptyState.visible()
            binding.rvUsers.gone()
            binding.tvListHeader.text = "Loaded Users (0):"
        } else {
            binding.tvEmptyState.gone()
            binding.rvUsers.visible()
            binding.tvListHeader.text = "Loaded Users (${users.size}):"
        }
    }

    private fun updateTrace(message: String) {
        binding.tvTraceLog.text = message
        CoreLogger.d("[Direct Trace] $message")
    }
}

package com.vc.sample

import android.app.Application
import com.vc.androidcore.config.CoreConfig
import com.vc.androidcore.config.CoreLibrary
import com.vc.androidcore.database.DatabaseProvider
import com.vc.androidcore.network.LiveNetworkMonitor
import com.vc.androidcore.network.NetworkMonitor
import com.vc.androidcore.network.OkHttpProvider
import com.vc.androidcore.network.RetrofitProvider
import com.vc.androidcore.network.TokenProvider
import com.vc.androidcore.preferences.DataStoreManager
import com.vc.sample.data.api.UserApiService
import com.vc.sample.data.db.AppDatabase
import com.vc.sample.data.repository.UserRepository
import kotlinx.coroutines.runBlocking

/**
 * Custom Application class demonstrating initialization and DI wiring of AndroidCoreLibrary.
 */
class SampleApplication : Application() {

    lateinit var dataStoreManager: DataStoreManager
        private set

    lateinit var networkMonitor: NetworkMonitor
        private set

    lateinit var database: AppDatabase
        private set

    lateinit var userRepository: UserRepository
        private set

    companion object {
        lateinit var instance: SampleApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // 1. Initialize Core Library Configuration
        CoreLibrary.initialize(
            context = this,
            config = CoreConfig(
                enableLogging = true,
                enableNetworkLogging = true,
                defaultTimeout = 30L,
                baseUrl = "https://jsonplaceholder.typicode.com/"
            )
        )

        // 2. Initialize DataStore
        dataStoreManager = DataStoreManager(this)

        // 3. Initialize Network Monitoring
        networkMonitor = LiveNetworkMonitor(this)

        // 4. TokenProvider implementation reading from DataStore
        val tokenProvider = object : TokenProvider {
            override fun getAccessToken(): String? {
                return runBlocking { dataStoreManager.getString("auth_token") }
            }
        }

        // 5. Initialize Networking
        val okHttpClient = OkHttpProvider.builder()
            .tokenProvider(tokenProvider)
            .logging(true)
            .build()

        val apiService = RetrofitProvider.createService<UserApiService>(
            baseUrl = "https://jsonplaceholder.typicode.com/",
            okHttpClient = okHttpClient
        )

        // 6. Initialize Room Database using DatabaseProvider
        database = DatabaseProvider.builder(
            context = this,
            klass = AppDatabase::class.java,
            databaseName = "sample_users.db"
        ).build()

        // 7. Initialize Repository
        userRepository = UserRepository(
            apiService = apiService,
            userDao = database.userDao(),
            networkMonitor = networkMonitor
        )
    }
}

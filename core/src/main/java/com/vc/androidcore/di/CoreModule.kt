package com.vc.androidcore.di

import android.content.Context
import com.vc.androidcore.network.LiveNetworkMonitor
import com.vc.androidcore.network.NetworkMonitor
import com.vc.androidcore.network.OkHttpProvider
import com.vc.androidcore.network.TokenProvider
import com.vc.androidcore.preferences.DataStoreManager
import com.vc.androidcore.preferences.PreferenceManager
import okhttp3.OkHttpClient

/**
 * Reusable dependency provider usable with or without Hilt/Koin.
 */
object CoreModule {

    fun provideDataStoreManager(context: Context): DataStoreManager {
        return DataStoreManager(context.applicationContext)
    }

    fun providePreferenceManager(context: Context): PreferenceManager {
        return PreferenceManager(context.applicationContext)
    }

    fun provideNetworkMonitor(context: Context): NetworkMonitor {
        return LiveNetworkMonitor(context.applicationContext)
    }

    fun provideOkHttpClient(tokenProvider: TokenProvider? = null): OkHttpClient {
        return OkHttpProvider.createDefault(tokenProvider)
    }
}

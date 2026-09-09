package com.vc.composesample

import android.app.Application
import com.vc.androidcore.config.CoreConfig
import com.vc.androidcore.config.CoreLibrary

class SampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        CoreLibrary.initialize(
            context = this,
            config = CoreConfig(
                enableLogging = true,
                enableNetworkLogging = true
            )
        )
    }
}

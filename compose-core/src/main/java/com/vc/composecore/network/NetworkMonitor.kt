package com.vc.composecore.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.vc.composecore.logging.CoreLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Interface for monitoring active network connectivity state in Compose applications.
 */
interface NetworkMonitor {
    /**
     * Flow emitting true when internet connectivity is active, false otherwise.
     */
    val isOnline: StateFlow<Boolean>

    /**
     * Synchronously returns current online status.
     */
    fun isCurrentlyOnline(): Boolean
}

/**
 * Production implementation of [NetworkMonitor] using modern Android [ConnectivityManager.NetworkCallback].
 */
class LiveNetworkMonitor(
    context: Context,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
) : NetworkMonitor {

    private val connectivityManager =
        context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

    private val _isOnline = MutableStateFlow(isCurrentlyOnline())
    override val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    private val validNetworks = mutableSetOf<Network>()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            val capabilities = connectivityManager?.getNetworkCapabilities(network)
            val hasInternet = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
            if (hasInternet) {
                validNetworks.add(network)
                updateState()
            }
        }

        override fun onLost(network: Network) {
            validNetworks.remove(network)
            updateState()
        }

        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
            val hasInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            if (hasInternet) {
                validNetworks.add(network)
            } else {
                validNetworks.remove(network)
            }
            updateState()
        }
    }

    init {
        registerCallback()
    }

    private fun registerCallback() {
        try {
            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()
            connectivityManager?.registerNetworkCallback(request, networkCallback)
        } catch (e: Exception) {
            CoreLogger.e("Failed to register NetworkCallback: ${e.message}")
        }
    }

    private fun updateState() {
        _isOnline.value = validNetworks.isNotEmpty()
    }

    override fun isCurrentlyOnline(): Boolean {
        val activeNetwork = connectivityManager?.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    /**
     * Stops monitoring and unregisters the network callback.
     */
    fun stop() {
        unregister()
    }

    /**
     * Unregisters callback when monitoring is completely torn down.
     */
    fun unregister() {
        try {
            connectivityManager?.unregisterNetworkCallback(networkCallback)
        } catch (e: Exception) {
            CoreLogger.w("Failed to unregister NetworkCallback: ${e.message}")
        }
    }
}

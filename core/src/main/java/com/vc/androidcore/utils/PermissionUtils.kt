package com.vc.androidcore.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

/**
 * Modern permission helper leveraging AndroidX Activity Result APIs.
 */
class PermissionRequester private constructor(
    private val launcher: ActivityResultLauncher<Array<String>>,
    private val contextProvider: () -> Context
) {

    private var onAllGranted: (() -> Unit)? = null
    private var onDenied: ((deniedPermissions: List<String>) -> Unit)? = null

    companion object {

        fun register(activity: ComponentActivity): PermissionRequester {
            var requester: PermissionRequester? = null
            val launcher = activity.registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { resultMap ->
                requester?.handleResult(resultMap)
            }
            requester = PermissionRequester(launcher) { activity }
            return requester
        }

        fun register(fragment: Fragment): PermissionRequester {
            var requester: PermissionRequester? = null
            val launcher = fragment.registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { resultMap ->
                requester?.handleResult(resultMap)
            }
            requester = PermissionRequester(launcher) { fragment.requireContext() }
            return requester
        }

        fun hasPermission(context: Context, permission: String): Boolean {
            return ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }

        fun hasPermissions(context: Context, vararg permissions: String): Boolean {
            return permissions.all { hasPermission(context, it) }
        }
    }

    private fun handleResult(resultMap: Map<String, Boolean>) {
        val deniedList = resultMap.filterValues { !it }.keys.toList()
        if (deniedList.isEmpty()) {
            onAllGranted?.invoke()
        } else {
            onDenied?.invoke(deniedList)
        }
    }

    /**
     * Requests permissions with callback handlers.
     */
    fun request(
        permissions: Array<String>,
        onDenied: ((List<String>) -> Unit)? = null,
        onAllGranted: () -> Unit
    ) {
        this.onAllGranted = onAllGranted
        this.onDenied = onDenied

        val context = contextProvider()
        val notGranted = permissions.filter { !hasPermission(context, it) }

        if (notGranted.isEmpty()) {
            onAllGranted()
        } else {
            launcher.launch(permissions)
        }
    }

    fun requestSingle(
        permission: String,
        onDenied: (() -> Unit)? = null,
        onGranted: () -> Unit
    ) {
        request(
            permissions = arrayOf(permission),
            onDenied = { onDenied?.invoke() },
            onAllGranted = onGranted
        )
    }
}

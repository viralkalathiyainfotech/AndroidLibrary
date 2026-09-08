package com.vc.androidcore.base

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.vc.androidcore.error.AppError
import com.vc.androidcore.network.ApiCallBuilder
import com.vc.androidcore.network.LiveNetworkMonitor
import com.vc.androidcore.network.NetworkResult
import com.vc.androidcore.network.safeApiCall
import com.vc.androidcore.permission.PermissionHelper
import com.vc.androidcore.permission.PermissionResult
import com.vc.androidcore.state.UiEvent
import com.vc.androidcore.ui.dialog.LoadingDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

/**
 * Base activity class providing ViewBinding inflation, lifecycle-aware coroutine collection,
 * loading dialogs, toasts, snackbars, keyboard helpers, and error presentation.
 */
abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    private var _binding: VB? = null
    protected val binding: VB get() = _binding!!

    private var loadingDialog: LoadingDialog? = null

    /**
     * Modern Activity Result API permission manager.
     */
    val permissionHelper = PermissionHelper(this)

    /**
     * Reactive and synchronous network connectivity monitor.
     */
    val networkMonitor: LiveNetworkMonitor by lazy { LiveNetworkMonitor(applicationContext) }

    abstract fun inflateBinding(): VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = inflateBinding()
        setContentView(binding.root)

        loadingDialog = LoadingDialog(this)

        setupUI()
        setupListeners()
        observeData()
    }

    protected open fun setupUI() {}
    protected open fun setupListeners() {}
    protected open fun observeData() {}

    /**
     * Helper to safely collect any [Flow] lifecycle-aware in [Lifecycle.State.STARTED].
     */
    fun <T> collectLifecycleFlow(
        flow: Flow<T>,
        minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
        collector: suspend (T) -> Unit
    ) {
        lifecycleScope.launch {
            repeatOnLifecycle(minActiveState) {
                flow.collect { collector(it) }
            }
        }
    }

    /**
     * Standard observer for [BaseViewModel] events.
     */
    protected fun observeBaseEvents(viewModel: BaseViewModel) {
        collectLifecycleFlow(viewModel.loadingState) { isLoading ->
            if (isLoading) showLoading() else hideLoading()
        }

        collectLifecycleFlow(viewModel.errorState) { error ->
            error?.let { handleAppError(it) }
        }

        collectLifecycleFlow(viewModel.uiEvent) { event ->
            handleUiEvent(event)
        }
    }

    protected open fun handleUiEvent(event: UiEvent) {
        when (event) {
            is UiEvent.ShowToast -> showToast(event.message, event.isLong)
            is UiEvent.ShowSnackbar -> showSnackbar(event.message, event.actionText, event.action)
            is UiEvent.ShowDialog -> showError(event.message)
            is UiEvent.ApiError -> handleAppError(event.error)
            is UiEvent.Navigate -> {
                event.destination?.let { dest ->
                    navigateTo(dest, event.extras, finishCurrent = event.finishCurrent)
                }
            }
            is UiEvent.Logout -> onLogoutRequested()
            is UiEvent.Custom -> onCustomEvent(event.payload)
        }
    }

    protected open fun onLogoutRequested() {}
    protected open fun onCustomEvent(payload: Any?) {}

    // --- Loading Helpers ---
    open fun showLoading(message: String = "Loading...", isCancelable: Boolean = false) {
        if (!isFinishing && !isDestroyed) {
            loadingDialog?.show(message, isCancelable)
        }
    }

    open fun hideLoading() {
        loadingDialog?.dismiss()
    }

    // --- Feedback Helpers ---
    open fun showToast(message: String, isLong: Boolean = false) {
        val duration = if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
        Toast.makeText(this, message, duration).show()
    }

    open fun showSnackbar(
        message: String,
        actionText: String? = null,
        action: (() -> Unit)? = null
    ) {
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
        if (actionText != null && action != null) {
            snackbar.setAction(actionText) { action() }
        }
        snackbar.show()
    }

    open fun showError(message: String) {
        showSnackbar(message)
    }

    open fun handleAppError(error: AppError) {
        showError(error.userMessage)
    }

    // --- Toolbar Helpers ---
    protected fun setupToolbar(
        toolbar: Toolbar,
        title: String? = null,
        displayHomeAsUp: Boolean = true,
        onNavigationClick: (() -> Unit)? = null
    ) {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title?.let { this.title = it }
            setDisplayHomeAsUpEnabled(displayHomeAsUp)
        }
        if (displayHomeAsUp) {
            toolbar.setNavigationOnClickListener {
                onNavigationClick?.invoke() ?: finish()
            }
        }
    }

    // --- Navigation Helpers ---
    fun navigateTo(
        destination: Class<*>,
        extras: Bundle? = null,
        finishCurrent: Boolean = false
    ) {
        val intent = Intent(this, destination).apply {
            extras?.let { putExtras(it) }
        }
        startActivity(intent)
        if (finishCurrent) {
            finish()
        }
    }

    // --- Keyboard Helpers ---
    fun hideKeyboard() {
        val view = currentFocus ?: binding.root
        val controller = WindowInsetsControllerCompat(window, view)
        controller.hide(WindowInsetsCompat.Type.ime())
    }

    fun showKeyboard(view: View) {
        view.requestFocus()
        val controller = WindowInsetsControllerCompat(window, view)
        controller.show(WindowInsetsCompat.Type.ime())
    }

    // --- Permission Helpers ---
    fun requestPermission(permission: String, onResult: (Boolean) -> Unit) {
        permissionHelper.request(permission, onResult)
    }

    fun requestPermissions(vararg permissions: String, onResult: (PermissionResult) -> Unit) {
        permissionHelper.request(*permissions, onResult = onResult)
    }

    fun hasPermission(permission: String): Boolean = permissionHelper.hasPermission(permission)

    fun hasPermissions(vararg permissions: String): Boolean = permissionHelper.hasPermissions(*permissions)

    fun openAppSettings() = permissionHelper.openAppSettings()

    // --- Direct Single-Call API Helpers ---

    /**
     * Executes an API call in a single line with automated lifecycle management,
     * optional loading dialog, network availability check, Dispatchers.IO switching,
     * and standardized error handling.
     *
     * Example:
     * ```kotlin
     * launchApiCall(
     *     request = { apiService.getUsers() },
     *     onSuccess = { users -> adapter.submitList(users) }
     * )
     * ```
     */
    fun <T> launchApiCall(
        showLoading: Boolean = true,
        loadingMessage: String = "Loading...",
        checkNetwork: Boolean = true,
        offlineMessage: String = "No internet connection",
        onError: ((AppError) -> Unit)? = null,
        request: suspend () -> Response<T>,
        onSuccess: (T) -> Unit
    ): Job {
        return lifecycleScope.launch {
            if (checkNetwork && !networkMonitor.isCurrentlyOnline()) {
                val error = AppError.Network
                showSnackbar(offlineMessage)
                onError?.invoke(error) ?: handleAppError(error)
                return@launch
            }

            if (showLoading) showLoading(loadingMessage)
            try {
                when (val result = safeApiCall { request() }) {
                    is NetworkResult.Success -> onSuccess(result.data)
                    is NetworkResult.Error -> {
                        onError?.invoke(result.appError) ?: handleAppError(result.appError)
                    }
                    is NetworkResult.Loading -> {}
                }
            } finally {
                if (showLoading) hideLoading()
            }
        }
    }

    /**
     * Executes an API call with direct DTO to Domain transformation in a single call.
     *
     * Example:
     * ```kotlin
     * launchApiCallMapped(
     *     request = { apiService.getUsers() },
     *     transform = { dtoList -> dtoList.map { it.toDomain() } },
     *     onSuccess = { domainUsers -> adapter.submitList(domainUsers) }
     * )
     * ```
     */
    fun <DTO, Domain> launchApiCallMapped(
        showLoading: Boolean = true,
        loadingMessage: String = "Loading...",
        checkNetwork: Boolean = true,
        offlineMessage: String = "No internet connection",
        onError: ((AppError) -> Unit)? = null,
        request: suspend () -> Response<DTO>,
        transform: (DTO) -> Domain,
        onSuccess: (Domain) -> Unit
    ): Job {
        return launchApiCall(
            showLoading = showLoading,
            loadingMessage = loadingMessage,
            checkNetwork = checkNetwork,
            offlineMessage = offlineMessage,
            onError = onError,
            request = request,
            onSuccess = { dtoData ->
                val domainData = transform(dtoData)
                onSuccess(domainData)
            }
        )
    }

    /**
     * Executes an API call using a fluent Kotlin DSL builder.
     *
     * Example:
     * ```kotlin
     * executeApi<List<UserDto>> {
     *     request { apiService.getUsers() }
     *     loading("Fetching users...")
     *     onSuccess { users -> adapter.submitList(users) }
     * }
     * ```
     */
    fun <T> executeApi(block: ApiCallBuilder<T, T>.() -> Unit): Job {
        val builder = ApiCallBuilder<T, T>().apply {
            transformAction = { it }
            block()
        }
        return executeApiInternal(builder)
    }

    /**
     * Executes an API call with DTO to Domain transformation using a fluent Kotlin DSL builder.
     */
    fun <T, R> executeApiMapped(block: ApiCallBuilder<T, R>.() -> Unit): Job {
        val builder = ApiCallBuilder<T, R>().apply(block)
        return executeApiInternal(builder)
    }

    private fun <T, R> executeApiInternal(builder: ApiCallBuilder<T, R>): Job {
        val req = builder.apiCallAction ?: error("API request block must be provided in executeApi / executeApiMapped")
        val transform = builder.transformAction ?: error("Transform block must be provided in executeApiMapped")

        return launchApiCall(
            showLoading = builder.showLoadingEnabled,
            loadingMessage = builder.loadingMessageText,
            checkNetwork = builder.checkNetworkEnabled,
            offlineMessage = builder.offlineMessageText,
            onError = builder.onErrorAction,
            request = req,
            onSuccess = { rawData ->
                val domainData = transform(rawData)
                builder.onSuccessAction?.invoke(domainData)
            }
        )
    }

    override fun onDestroy() {
        hideLoading()
        loadingDialog = null
        _binding = null
        super.onDestroy()
    }
}

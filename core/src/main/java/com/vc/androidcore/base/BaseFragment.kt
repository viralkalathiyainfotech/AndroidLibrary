package com.vc.androidcore.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import retrofit2.Response

/**
 * Base [Fragment] providing leak-safe ViewBinding lifecycle management,
 * coroutine collection helpers, loading dialogs, toasts, snackbars, and keyboard controls.
 */
abstract class BaseFragment<VB : ViewBinding> : Fragment() {

    private var _binding: VB? = null
    protected val binding: VB
        get() = _binding ?: throw IllegalStateException(
            "ViewBinding accessed before onCreateView() or after onDestroyView()."
        )

    private var loadingDialog: LoadingDialog? = null

    /**
     * Modern Activity Result API permission manager.
     */
    val permissionHelper = PermissionHelper(this)

    /**
     * Reactive and synchronous network connectivity monitor.
     */
    val networkMonitor: LiveNetworkMonitor by lazy {
        LiveNetworkMonitor(requireContext().applicationContext)
    }

    abstract fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflateBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context?.let { loadingDialog = LoadingDialog(it) }

        setupUI()
        setupListeners()
        observeData()
    }

    protected open fun setupUI() {}
    protected open fun setupListeners() {}
    protected open fun observeData() {}

    /**
     * Safely collects a [Flow] lifecycle-aware within [viewLifecycleOwner].
     */
    fun <T> collectLifecycleFlow(
        flow: Flow<T>,
        minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
        collector: suspend (T) -> Unit
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(minActiveState) {
                flow.collect { collector(it) }
            }
        }
    }

    /**
     * Observes standard [BaseViewModel] streams.
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
            is UiEvent.Logout -> onLogoutRequested()
            else -> {}
        }
    }

    protected open fun onLogoutRequested() {}

    // --- Loading Helpers ---
    open fun showLoading(message: String = "Loading...", isCancelable: Boolean = false) {
        loadingDialog?.show(message, isCancelable)
    }

    open fun hideLoading() {
        loadingDialog?.dismiss()
    }

    // --- Feedback Helpers ---
    open fun showToast(message: String, isLong: Boolean = false) {
        context?.let {
            val duration = if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
            Toast.makeText(it, message, duration).show()
        }
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

    // --- Keyboard Helpers ---
    fun hideKeyboard() {
        val window = activity?.window ?: return
        val view = view ?: return
        val controller = WindowInsetsControllerCompat(window, view)
        controller.hide(WindowInsetsCompat.Type.ime())
    }

    fun showKeyboard(targetView: View) {
        val window = activity?.window ?: return
        targetView.requestFocus()
        val controller = WindowInsetsControllerCompat(window, targetView)
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

    // --- Direct Single-Call API Helpers ---

    /**
     * Executes an API call in a single line within Fragment viewLifecycleOwner.
     * Automated lifecycle management, loading dialog, network check, Dispatchers.IO,
     * and standardized error handling.
     */
    fun <T> launchApiCall(
        showLoading: Boolean = true,
        loadingMessage: String = "Loading...",
        checkNetwork: Boolean = true,
        offlineMessage: String = "No internet connection",
        retryCount: Int = 0,
        retryDelayMs: Long = 1000L,
        onError: ((AppError) -> Unit)? = null,
        request: suspend () -> Response<T>,
        onSuccess: (T) -> Unit
    ): Job {
        return viewLifecycleOwner.lifecycleScope.launch {
            if (checkNetwork && !networkMonitor.isCurrentlyOnline()) {
                val error = AppError.Network
                showSnackbar(offlineMessage)
                onError?.invoke(error) ?: handleAppError(error)
                return@launch
            }

            if (showLoading) showLoading(loadingMessage)
            try {
                when (val result = safeApiCall(retryCount = retryCount, retryDelayMs = retryDelayMs) { request() }) {
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
     */
    fun <DTO, Domain> launchApiCallMapped(
        showLoading: Boolean = true,
        loadingMessage: String = "Loading...",
        checkNetwork: Boolean = true,
        offlineMessage: String = "No internet connection",
        retryCount: Int = 0,
        retryDelayMs: Long = 1000L,
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
            retryCount = retryCount,
            retryDelayMs = retryDelayMs,
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
            retryCount = builder.retryCountValue,
            retryDelayMs = builder.retryDelayMsValue,
            onError = builder.onErrorAction,
            request = req,
            onSuccess = { rawData ->
                val domainData = transform(rawData)
                builder.onSuccessAction?.invoke(domainData)
            }
        )
    }

    /**
     * Crucial: Clears binding in onDestroyView() to prevent activity/view memory leaks.
     */
    override fun onDestroyView() {
        hideLoading()
        loadingDialog = null
        _binding = null
        super.onDestroyView()
    }
}

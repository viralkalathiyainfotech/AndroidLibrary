package com.vc.androidcore.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vc.androidcore.error.AppError
import com.vc.androidcore.error.ErrorMapper
import com.vc.androidcore.logging.CoreLogger
import com.vc.androidcore.network.NetworkResult
import com.vc.androidcore.network.safeApiCall
import com.vc.androidcore.state.UiEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

/**
 * Base [ViewModel] providing coroutine execution, state streams, event dispatch, and error management.
 */
abstract class BaseViewModel : ViewModel() {

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> = _loadingState.asStateFlow()

    private val _errorState = MutableStateFlow<AppError?>(null)
    val errorState: StateFlow<AppError?> = _errorState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    /**
     * Default coroutine exception handler logging errors and posting to [_errorState].
     */
    protected open val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        CoreLogger.e("Unhandled coroutine exception in ViewModel", throwable = throwable)
        val appError = ErrorMapper.map(throwable)
        _errorState.value = appError
        hideLoading()
    }

    /**
     * Executes a coroutine block safely within [viewModelScope] with error and loading handling.
     */
    protected fun launchSafe(
        showLoading: Boolean = false,
        onError: ((AppError) -> Unit)? = null,
        block: suspend CoroutineScope.() -> Unit
    ) {
        if (showLoading) showLoading()
        viewModelScope.launch(exceptionHandler) {
            try {
                block()
            } catch (t: Throwable) {
                val appError = ErrorMapper.map(t)
                _errorState.value = appError
                onError?.invoke(appError)
            } finally {
                if (showLoading) hideLoading()
            }
        }
    }

    /**
     * Executes a network call returning [NetworkResult] and maps the outcome into callbacks.
     */
    protected fun <T> executeApiCall(
        showLoading: Boolean = true,
        apiCall: suspend () -> NetworkResult<T>,
        onError: ((AppError) -> Unit)? = null,
        onSuccess: (T) -> Unit
    ) {
        launchSafe(showLoading = showLoading) {
            when (val result = apiCall()) {
                is NetworkResult.Success -> {
                    onSuccess(result.data)
                }
                is NetworkResult.Error -> {
                    _errorState.value = result.appError
                    onError?.invoke(result.appError)
                }
                is NetworkResult.Loading -> {
                    if (result.isLoading) showLoading() else hideLoading()
                }
            }
        }
    }

    /**
     * Executes a database suspend call safely.
     */
    protected fun <T> executeDatabaseCall(
        showLoading: Boolean = false,
        dbCall: suspend () -> T,
        onError: ((Throwable) -> Unit)? = null,
        onSuccess: (T) -> Unit
    ) {
        launchSafe(showLoading = showLoading) {
            try {
                val result = dbCall()
                onSuccess(result)
            } catch (t: Throwable) {
                CoreLogger.e("Database operation failed", throwable = t)
                onError?.invoke(t) ?: run {
                    _errorState.value = ErrorMapper.map(t)
                }
            }
        }
    }

    /**
     * Dispatches a single-shot [UiEvent] to observers.
     */
    protected fun sendEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }

    /**
     * Ultra-clean single-line API caller for ViewModels.
     * Automatically triggers loadingState, dispatches on IO, updates errorState on failure,
     * and supports retryCount.
     *
     * Example:
     * ```kotlin
     * fun loadUsers() = launchApi(
     *     call = { apiService.getUsers() },
     *     onSuccess = { users -> _users.value = users }
     * )
     * ```
     */
    fun <T> launchApi(
        showLoading: Boolean = true,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        retryCount: Int = 0,
        retryDelayMs: Long = 1000L,
        onError: ((AppError) -> Unit)? = null,
        call: suspend () -> Response<T>,
        onSuccess: suspend (T) -> Unit
    ): Job {
        if (showLoading) showLoading()
        return viewModelScope.launch(exceptionHandler) {
            try {
                when (val result = safeApiCall(dispatcher = dispatcher, retryCount = retryCount, retryDelayMs = retryDelayMs) { call() }) {
                    is NetworkResult.Success -> onSuccess(result.data)
                    is NetworkResult.Error -> {
                        _errorState.value = result.appError
                        onError?.invoke(result.appError)
                    }
                    is NetworkResult.Loading -> {}
                }
            } finally {
                if (showLoading) hideLoading()
            }
        }
    }

    /**
     * Single-line API caller for ViewModels with inline DTO-to-Domain transformation.
     *
     * Example:
     * ```kotlin
     * fun loadUsers() = launchApiMapped(
     *     call = { apiService.getUsers() },
     *     transform = { dtoList -> dtoList.map { it.toDomain() } },
     *     onSuccess = { domainUsers -> _users.value = domainUsers }
     * )
     * ```
     */
    fun <DTO, Domain> launchApiMapped(
        showLoading: Boolean = true,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        retryCount: Int = 0,
        retryDelayMs: Long = 1000L,
        onError: ((AppError) -> Unit)? = null,
        call: suspend () -> Response<DTO>,
        transform: (DTO) -> Domain,
        onSuccess: suspend (Domain) -> Unit
    ): Job {
        return launchApi(
            showLoading = showLoading,
            dispatcher = dispatcher,
            retryCount = retryCount,
            retryDelayMs = retryDelayMs,
            onError = onError,
            call = call,
            onSuccess = { rawDto ->
                val domain = transform(rawDto)
                onSuccess(domain)
            }
        )
    }

    fun showLoading() {
        _loadingState.value = true
    }

    fun hideLoading() {
        _loadingState.value = false
    }

    fun clearError() {
        _errorState.value = null
    }
}

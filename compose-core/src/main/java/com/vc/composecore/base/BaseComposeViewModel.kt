package com.vc.composecore.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vc.androidcore.error.AppError
import com.vc.androidcore.error.ErrorMapper
import com.vc.androidcore.logging.CoreLogger
import com.vc.androidcore.network.NetworkResult
import com.vc.androidcore.network.safeApiCall
import com.vc.composecore.state.UiAction
import com.vc.composecore.state.UiEffect
import com.vc.composecore.state.ViewState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Response

/**
 * Base [ViewModel] for Jetpack Compose applications implementing Unidirectional Data Flow (MVI/UDF).
 *
 * @param initialState The default initial [ViewState] when this ViewModel is instantiated.
 */
abstract class BaseComposeViewModel<STATE : ViewState, ACTION : UiAction, EFFECT : UiEffect>(
    initialState: STATE
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<STATE> = _state.asStateFlow()

    val currentState: STATE get() = _state.value

    private val _effectChannel = Channel<EFFECT>(Channel.BUFFERED)
    val effect: Flow<EFFECT> = _effectChannel.receiveAsFlow()

    protected open val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        CoreLogger.e("Unhandled coroutine exception in BaseComposeViewModel", throwable = throwable)
        handleException(throwable)
    }

    /**
     * Entry point for processing user actions dispatched from UI components.
     */
    abstract fun dispatch(action: ACTION)

    /**
     * Alias for [dispatch].
     */
    fun sendAction(action: ACTION) = dispatch(action)

    /**
     * Updates the state atomically.
     */
    protected fun updateState(reducer: STATE.() -> STATE) {
        _state.update(reducer)
    }

    /**
     * Emits a one-time side effect to the UI stream.
     */
    protected fun sendEffect(effect: EFFECT) {
        viewModelScope.launch {
            _effectChannel.send(effect)
        }
    }

    /**
     * Custom exception handler hook that can be overridden by subclasses.
     */
    protected open fun handleException(throwable: Throwable) {
        // Base logging; subclasses can map to error state or error effect
    }

    /**
     * Safely executes an asynchronous coroutine block with error handling.
     */
    protected fun launchSafe(
        dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
        onError: ((AppError) -> Unit)? = null,
        block: suspend CoroutineScope.() -> Unit
    ): Job {
        return viewModelScope.launch(dispatcher + exceptionHandler) {
            try {
                block()
            } catch (t: Throwable) {
                val appError = ErrorMapper.map(t)
                onError?.invoke(appError) ?: handleException(t)
            }
        }
    }

    /**
     * Clean API invocation integrating with Retrofit and [safeApiCall] from the core library.
     */
    protected fun <T> launchApi(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        retryCount: Int = 0,
        retryDelayMs: Long = 1000L,
        onError: ((AppError) -> Unit)? = null,
        call: suspend () -> Response<T>,
        onSuccess: suspend (T) -> Unit
    ): Job {
        return viewModelScope.launch(dispatcher + exceptionHandler) {
            when (val result = safeApiCall(dispatcher = dispatcher, retryCount = retryCount, retryDelayMs = retryDelayMs) { call() }) {
                is NetworkResult.Success -> onSuccess(result.data)
                is NetworkResult.Error -> {
                    onError?.invoke(result.appError) ?: handleException(result.throwable ?: Exception(result.message))
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    /**
     * Clean API invocation with inline DTO to Domain transformation.
     */
    protected fun <DTO, Domain> launchApiMapped(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        retryCount: Int = 0,
        retryDelayMs: Long = 1000L,
        onError: ((AppError) -> Unit)? = null,
        call: suspend () -> Response<DTO>,
        transform: (DTO) -> Domain,
        onSuccess: suspend (Domain) -> Unit
    ): Job {
        return launchApi(
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
}

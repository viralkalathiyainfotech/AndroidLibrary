package com.vc.androidcore.network

import com.vc.androidcore.error.AppError
import retrofit2.Response

/**
 * Type-safe Kotlin DSL builder for configuring and executing network API requests.
 *
 * @param T Raw response payload type from Retrofit [Response].
 * @param R Transformed result type delivered to [onSuccessAction].
 */
class ApiCallBuilder<T, R> {

    internal var apiCallAction: (suspend () -> Response<T>)? = null
    internal var transformAction: ((T) -> R)? = null
    internal var showLoadingEnabled: Boolean = true
    internal var loadingMessageText: String = "Loading..."
    internal var checkNetworkEnabled: Boolean = true
    internal var offlineMessageText: String = "No Internet connection available"
    internal var onSuccessAction: ((R) -> Unit)? = null
    internal var onErrorAction: ((AppError) -> Unit)? = null

    /**
     * Defines the suspend Retrofit API call to execute.
     */
    fun request(call: suspend () -> Response<T>) {
        this.apiCallAction = call
    }

    /**
     * Defines a transformation function from raw API payload [T] to domain model [R].
     */
    fun transform(block: (T) -> R) {
        this.transformAction = block
    }

    /**
     * Configures loading dialog visibility and text message.
     */
    fun loading(show: Boolean = true, message: String = "Loading...") {
        this.showLoadingEnabled = show
        this.loadingMessageText = message
    }

    /**
     * Configures automatic network connectivity pre-check.
     */
    fun checkNetwork(check: Boolean = true, offlineMessage: String = "No Internet connection available") {
        this.checkNetworkEnabled = check
        this.offlineMessageText = offlineMessage
    }

    /**
     * Callback triggered on the main thread when the API call succeeds with HTTP 2xx.
     */
    fun onSuccess(block: (R) -> Unit) {
        this.onSuccessAction = block
    }

    /**
     * Callback triggered on the main thread when an error occurs.
     * If omitted, errors default to [handleAppError].
     */
    fun onError(block: (AppError) -> Unit) {
        this.onErrorAction = block
    }
}

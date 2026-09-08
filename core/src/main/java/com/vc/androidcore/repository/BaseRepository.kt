package com.vc.androidcore.repository

import com.vc.androidcore.error.ErrorMapper
import com.vc.androidcore.network.NetworkResult
import com.vc.androidcore.network.safeApiCall
import com.vc.androidcore.state.UiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import retrofit2.Response

/**
 * Base repository implementing Clean Architecture repository abstractions.
 * Provides safe API execution and offline-first database caching patterns.
 */
abstract class BaseRepository(
    val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    /**
     * Executes a network call safely using [safeApiCall].
     */
    protected suspend fun <T> executeApiCall(
        apiCall: suspend () -> Response<T>
    ): NetworkResult<T> {
        return safeApiCall(ioDispatcher, apiCall)
    }

    /**
     * Offline-first repository pattern implementing:
     * 1. Query local database via Flow
     * 2. Emit Loading state with cached database items
     * 3. If shouldFetch is true, request remote API
     * 4. Save remote response into local database
     * 5. Observe local database updates reactively
     * 6. If remote call fails, emit Error with cached items if available
     */
    protected inline fun <ResultType, RequestType> networkBoundResource(
        crossinline query: () -> Flow<ResultType>,
        crossinline fetch: suspend () -> Response<RequestType>,
        crossinline saveFetchResult: suspend (RequestType) -> Unit,
        crossinline shouldFetch: (ResultType) -> Boolean = { true },
        crossinline onFetchFailed: (Throwable) -> Unit = {}
    ): Flow<UiState<ResultType>> = flow {
        emit(UiState.Loading)

        val localData = query().first()

        if (shouldFetch(localData)) {
            val networkResult = safeApiCall(ioDispatcher) { fetch() }

            when (networkResult) {
                is NetworkResult.Success -> {
                    saveFetchResult(networkResult.data)
                    emitAll(query().map { UiState.Success(it) })
                }
                is NetworkResult.Error -> {
                    onFetchFailed(networkResult.throwable ?: Exception(networkResult.message))
                    emit(
                        UiState.Error(
                            message = networkResult.message,
                            throwable = networkResult.throwable,
                            code = networkResult.code,
                            appError = networkResult.appError
                        )
                    )
                    // Continue emitting local cached data even on error
                    emitAll(query().map { UiState.Success(it) })
                }
                is NetworkResult.Loading -> {
                    emit(UiState.Loading)
                }
            }
        } else {
            emitAll(query().map { UiState.Success(it) })
        }
    }.flowOn(ioDispatcher)
}

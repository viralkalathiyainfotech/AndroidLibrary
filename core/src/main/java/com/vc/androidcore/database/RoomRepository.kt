package com.vc.androidcore.database

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Base repository abstraction for Room database interactions.
 */
abstract class RoomRepository(
    protected val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    /**
     * Executes a database query or write safely within the IO dispatcher.
     */
    protected suspend fun <T> executeDb(block: suspend () -> T): Result<T> {
        return withContext(ioDispatcher) {
            try {
                Result.success(block())
            } catch (e: Throwable) {
                Result.failure(e)
            }
        }
    }
}

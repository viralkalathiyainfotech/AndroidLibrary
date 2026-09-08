package com.vc.androidcore

import app.cash.turbine.test
import com.vc.androidcore.repository.BaseRepository
import com.vc.androidcore.state.UiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class RepositoryTest {

    private val testDispatcher = StandardTestDispatcher()

    private class TestRepository(dispatcher: kotlinx.coroutines.CoroutineDispatcher) : BaseRepository(dispatcher) {
        fun <ResultType, RequestType> testNetworkBoundResource(
            query: () -> Flow<ResultType>,
            fetch: suspend () -> Response<RequestType>,
            saveFetchResult: suspend (RequestType) -> Unit,
            shouldFetch: (ResultType) -> Boolean = { true }
        ): Flow<UiState<ResultType>> {
            return networkBoundResource(
                query = query,
                fetch = fetch,
                saveFetchResult = saveFetchResult,
                shouldFetch = shouldFetch
            )
        }
    }

    @Test
    fun networkBoundResource_fetchesAndSaves_emitsSuccess() = runTest(testDispatcher) {
        val repository = TestRepository(testDispatcher)

        var localData = "Local Cached"
        var savedToDb = false

        val flow = repository.testNetworkBoundResource(
            query = { flowOf(localData) },
            fetch = { Response.success("Remote Fresh") },
            saveFetchResult = { remote ->
                savedToDb = true
                localData = remote
            },
            shouldFetch = { true }
        )

        flow.test {
            // 1. Initial Loading state
            assertEquals(UiState.Loading, awaitItem())

            // 2. Fresh local data emitted after network response saved
            val successItem = awaitItem()
            assertTrue(successItem is UiState.Success)
            assertEquals("Remote Fresh", (successItem as UiState.Success).data)
            assertTrue(savedToDb)

            awaitComplete()
        }
    }

    @Test
    fun networkBoundResource_onNetworkError_emitsErrorAndCachedData() = runTest(testDispatcher) {
        val repository = TestRepository(testDispatcher)

        val localData = "Cached Fallback"

        val flow = repository.testNetworkBoundResource(
            query = { flowOf(localData) },
            fetch = { Response.error<String>(500, "Server down".toResponseBody(null)) },
            saveFetchResult = {},
            shouldFetch = { true }
        )

        flow.test {
            assertEquals(UiState.Loading, awaitItem())

            // Emits error
            val errorItem = awaitItem()
            assertTrue(errorItem is UiState.Error)

            // Still emits cached fallback
            val cachedItem = awaitItem()
            assertTrue(cachedItem is UiState.Success)
            assertEquals("Cached Fallback", (cachedItem as UiState.Success).data)

            awaitComplete()
        }
    }
}

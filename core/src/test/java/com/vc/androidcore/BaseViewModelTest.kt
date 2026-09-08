package com.vc.androidcore

import app.cash.turbine.test
import com.vc.androidcore.base.BaseViewModel
import com.vc.androidcore.error.AppError
import com.vc.androidcore.state.UiEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class BaseViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private class TestViewModel : BaseViewModel() {
        fun testSafeLaunch(showLoading: Boolean, block: suspend () -> Unit) {
            launchSafe(showLoading = showLoading) {
                block()
            }
        }

        fun testSendEvent(event: UiEvent) {
            sendEvent(event)
        }
    }

    private lateinit var viewModel: TestViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = TestViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun launchSafe_togglesLoadingState() = runTest(testDispatcher) {
        viewModel.loadingState.test {
            assertEquals(false, awaitItem())

            viewModel.testSafeLaunch(showLoading = true) {
                // Inside block
            }

            testScheduler.advanceUntilIdle()
            // After completion, loading state must return to false
            assertEquals(false, expectMostRecentItem())
        }
    }

    @Test
    fun launchSafe_capturesExceptionInErrorState() = runTest(testDispatcher) {
        viewModel.errorState.test {
            assertEquals(null, awaitItem())

            viewModel.testSafeLaunch(showLoading = false) {
                throw IOException("Network offline")
            }

            testScheduler.advanceUntilIdle()
            val error = awaitItem()
            assertEquals(AppError.Network, error)
        }
    }

    @Test
    fun sendEvent_emitsToUiEventStream() = runTest(testDispatcher) {
        viewModel.uiEvent.test {
            viewModel.testSendEvent(UiEvent.ShowToast("Test Toast"))
            val event = awaitItem()
            assertTrue(event is UiEvent.ShowToast)
            assertEquals("Test Toast", (event as UiEvent.ShowToast).message)
        }
    }

    @Test
    fun launchApi_onSuccess_deliversDataAndManagesLoading() = runTest(testDispatcher) {
        var receivedData: String? = null

        viewModel.loadingState.test {
            assertEquals(false, awaitItem())

            viewModel.launchApi(
                showLoading = true,
                dispatcher = testDispatcher,
                call = { Response.success("ViewModel API Success") },
                onSuccess = { data -> receivedData = data }
            )

            testScheduler.advanceUntilIdle()
            assertEquals("ViewModel API Success", receivedData)
            assertEquals(false, expectMostRecentItem())
        }
    }

    @Test
    fun launchApiMapped_transformsDataCorrectly() = runTest(testDispatcher) {
        var transformedLength: Int? = null

        viewModel.launchApiMapped(
            showLoading = false,
            dispatcher = testDispatcher,
            call = { Response.success("Hello Kotlin") },
            transform = { it.length },
            onSuccess = { len -> transformedLength = len }
        )

        testScheduler.advanceUntilIdle()
        assertEquals(12, transformedLength)
    }
}

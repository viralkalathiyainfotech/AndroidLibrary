package com.vc.composecore

import app.cash.turbine.test
import com.vc.composecore.base.BaseComposeViewModel
import com.vc.composecore.resources.asCoreText
import com.vc.composecore.state.UiAction
import com.vc.composecore.state.UiEffect
import com.vc.composecore.state.ViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

data class TestState(
    val count: Int = 0,
    val text: String = ""
) : ViewState

sealed interface TestAction : UiAction {
    data object Increment : TestAction
    data class SetText(val text: String) : TestAction
    data object TriggerToast : TestAction
}

sealed interface TestEffect : UiEffect {
    data class ShowToast(val message: String) : TestEffect
}

class TestViewModel : BaseComposeViewModel<TestState, TestAction, TestEffect>(TestState()) {
    override fun dispatch(action: TestAction) {
        when (action) {
            is TestAction.Increment -> updateState { copy(count = count + 1) }
            is TestAction.SetText -> updateState { copy(text = action.text) }
            is TestAction.TriggerToast -> sendEffect(TestEffect.ShowToast("Clicked!"))
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class BaseComposeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `dispatch actions should update state deterministically`() = runTest {
        val viewModel = TestViewModel()

        assertEquals(0, viewModel.state.value.count)

        viewModel.dispatch(TestAction.Increment)
        testScheduler.advanceUntilIdle()
        assertEquals(1, viewModel.state.value.count)

        viewModel.dispatch(TestAction.SetText("Compose Core"))
        testScheduler.advanceUntilIdle()
        assertEquals("Compose Core", viewModel.state.value.text)
    }

    @Test
    fun `sendEffect should emit one-time side effects to flow`() = runTest {
        val viewModel = TestViewModel()

        viewModel.effect.test {
            viewModel.dispatch(TestAction.TriggerToast)
            val effect = awaitItem()
            assertEquals(TestEffect.ShowToast("Clicked!"), effect)
            expectNoEvents()
        }
    }
}

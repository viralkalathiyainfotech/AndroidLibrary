package com.vc.composecore

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.vc.composecore.base.CoreStateScreen
import com.vc.composecore.resources.asCoreText
import com.vc.composecore.state.ScreenState
import com.vc.composecore.theme.CoreTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class CoreStateScreenUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun coreStateScreen_rendersSuccessContent() {
        val state = ScreenState.Success("Loaded Content Data")

        composeTestRule.setContent {
            CoreTheme {
                CoreStateScreen(
                    state = state,
                    onRetry = {}
                ) { data ->
                    Text("Result: $data")
                }
            }
        }

        composeTestRule.onNodeWithText("Result: Loaded Content Data")
            .assertIsDisplayed()
    }

    @Test
    fun coreStateScreen_rendersErrorAndTriggersRetry() {
        var retryCount = 0
        val state = ScreenState.Error("Server Connection Failed".asCoreText())

        composeTestRule.setContent {
            CoreTheme {
                CoreStateScreen<String>(
                    state = state,
                    onRetry = { retryCount++ }
                ) {
                    Text("Success")
                }
            }
        }

        composeTestRule.onNodeWithText("Server Connection Failed")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Try Again")
            .assertIsDisplayed()
            .performClick()

        assertEquals(1, retryCount)
    }
}

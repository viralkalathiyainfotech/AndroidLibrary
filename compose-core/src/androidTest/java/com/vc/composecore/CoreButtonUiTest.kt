package com.vc.composecore

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.vc.composecore.components.button.CoreButton
import com.vc.composecore.theme.CoreTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class CoreButtonUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun coreButton_clickTriggersCallback() {
        var clicked = 0

        composeTestRule.setContent {
            CoreTheme {
                CoreButton(
                    text = "Submit Action",
                    onClick = { clicked++ }
                )
            }
        }

        composeTestRule.onNodeWithText("Submit Action")
            .assertIsEnabled()
            .performClick()

        assertEquals(1, clicked)
    }

    @Test
    fun coreButton_disabledState_blocksClick() {
        var clicked = 0

        composeTestRule.setContent {
            CoreTheme {
                CoreButton(
                    text = "Disabled Action",
                    enabled = false,
                    onClick = { clicked++ }
                )
            }
        }

        composeTestRule.onNodeWithText("Disabled Action")
            .assertIsNotEnabled()

        assertEquals(0, clicked)
    }
}

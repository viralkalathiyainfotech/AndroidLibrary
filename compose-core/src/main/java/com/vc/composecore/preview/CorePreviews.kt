package com.vc.composecore.preview

import android.content.res.Configuration
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.vc.composecore.theme.CoreTheme
import com.vc.composecore.theme.ThemeMode

@Preview(name = "Light Mode", group = "Themes", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
annotation class CoreLightPreview

@Preview(name = "Dark Mode", group = "Themes", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
annotation class CoreDarkPreview

@CoreLightPreview
@CoreDarkPreview
annotation class CorePreview

@Preview(name = "Phone", device = Devices.PIXEL_4, showSystemUi = true)
@Preview(name = "Tablet", device = Devices.PIXEL_C, showSystemUi = true)
@Preview(name = "Landscape", device = "spec:parent=pixel_4,orientation=landscape", showSystemUi = true)
annotation class CoreDevicePreview

/**
 * Preview wrapper rendering contents wrapped in [CoreTheme].
 */
@Composable
fun CorePreviewWrapper(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    CoreTheme(
        themeMode = if (darkTheme) ThemeMode.Dark else ThemeMode.Light
    ) {
        Surface(color = CoreTheme.colors.background) {
            content()
        }
    }
}

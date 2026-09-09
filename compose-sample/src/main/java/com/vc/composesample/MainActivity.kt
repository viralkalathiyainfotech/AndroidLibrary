package com.vc.composesample

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vc.composecore.base.BaseComposeActivity
import com.vc.composecore.navigation.CoreNavHost
import com.vc.composecore.navigation.CoreNavigator
import com.vc.composecore.navigation.rememberCoreNavigator
import com.vc.composecore.theme.CoreTheme
import com.vc.composecore.theme.ThemeMode
import com.vc.composesample.ui.ComponentShowcaseScreen
import com.vc.composesample.ui.screens.BottomSheetDemoScreen
import com.vc.composesample.ui.screens.DialogDemoScreen
import com.vc.composesample.ui.screens.FormDemoScreen
import com.vc.composesample.ui.screens.HomeScreen
import com.vc.composesample.ui.screens.ListScreen
import com.vc.composesample.ui.screens.LoginScreen
import com.vc.composesample.ui.screens.MediaDemoScreen
import com.vc.composesample.ui.screens.OfflineDemoScreen
import com.vc.composesample.ui.screens.PermissionDemoScreen
import com.vc.composesample.ui.screens.ProfileDemoScreen
import com.vc.composesample.ui.screens.SearchScreen
import com.vc.composesample.ui.screens.SettingsDemoScreen
import com.vc.composesample.ui.screens.SplashScreen
import com.vc.composesample.ui.screens.ThemeDemoScreen

class MainActivity : BaseComposeActivity() {

    @Composable
    override fun Content() {
        var currentThemeMode by remember { mutableStateOf(ThemeMode.System) }
        val navController = rememberNavController()
        val navigator = rememberCoreNavigator(navController)

        CoreTheme(themeMode = currentThemeMode) {
            CoreNavHost(
                navController = navController,
                startDestination = "splash"
            ) {
                composable("splash") {
                    SplashScreen(
                        onFinish = {
                            navigator.clearBackStack("home")
                        }
                    )
                }
                composable("login") {
                    LoginScreen(
                        onLoginSuccess = {
                            navigator.navigate("home", popUpToRoute = "login", inclusive = true)
                        }
                    )
                }
                composable("home") {
                    HomeScreen(
                        onNavigateTo = { destination ->
                            navigator.navigate(destination)
                        }
                    )
                }
                composable("showcase") {
                    ComponentShowcaseScreen(
                        onBack = { navigator.navigateBack() }
                    )
                }
                composable("search") {
                    SearchScreen(
                        onBack = { navigator.navigateBack() }
                    )
                }
                composable("list") {
                    ListScreen(
                        onBack = { navigator.navigateBack() }
                    )
                }
                composable("form") {
                    FormDemoScreen(
                        onBack = { navigator.navigateBack() }
                    )
                }
                composable("settings") {
                    SettingsDemoScreen(
                        currentTheme = currentThemeMode,
                        onThemeChange = { mode -> currentThemeMode = mode },
                        onBack = { navigator.navigateBack() }
                    )
                }
                composable("profile") {
                    ProfileDemoScreen(
                        onBack = { navigator.navigateBack() }
                    )
                }
                composable("dialog") {
                    DialogDemoScreen(
                        onBack = { navigator.navigateBack() }
                    )
                }
                composable("bottom_sheet") {
                    BottomSheetDemoScreen(
                        onBack = { navigator.navigateBack() }
                    )
                }
                composable("theme") {
                    ThemeDemoScreen(
                        onBack = { navigator.navigateBack() }
                    )
                }
                composable("offline") {
                    OfflineDemoScreen(
                        onBack = { navigator.navigateBack() }
                    )
                }
                composable("permission") {
                    PermissionDemoScreen(
                        onBack = { navigator.navigateBack() }
                    )
                }
                composable("media") {
                    MediaDemoScreen(
                        onBack = { navigator.navigateBack() }
                    )
                }
            }
        }
    }
}

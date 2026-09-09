package com.vc.composesample.ui.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.vc.composecore.base.BaseScreen
import com.vc.composecore.components.appbar.CoreTopAppBar
import com.vc.composecore.components.badge.CoreBadge
import com.vc.composecore.components.button.CoreAsyncButton
import com.vc.composecore.components.button.CoreButton
import com.vc.composecore.components.button.CoreOutlinedButton
import com.vc.composecore.components.card.CoreClickableCard
import com.vc.composecore.components.card.CoreElevatedCard
import com.vc.composecore.components.card.CoreOutlinedCard
import com.vc.composecore.components.dialog.CoreAlertDialog
import com.vc.composecore.components.dialog.CoreConfirmDialog
import com.vc.composecore.components.dialog.CoreErrorDialog
import com.vc.composecore.components.dialog.CoreLoadingDialog
import com.vc.composecore.components.dialog.CoreSuccessDialog
import com.vc.composecore.components.image.AvatarStatus
import com.vc.composecore.components.image.CoreAvatar
import com.vc.composecore.components.image.CoreAvatarGroup
import com.vc.composecore.lists.CoreSwipeableItem
import com.vc.composecore.components.navigation.CoreNavigationBar
import com.vc.composecore.components.navigation.CoreNavigationItemData
import com.vc.composecore.components.search.CoreFilterBar
import com.vc.composecore.components.search.CoreSearchBar
import com.vc.composecore.components.search.rememberDebouncedSearch
import com.vc.composecore.components.selection.CoreCheckbox
import com.vc.composecore.components.selection.CoreRadioButton
import com.vc.composecore.components.selection.CoreSwitch
import com.vc.composecore.components.sheet.CoreModalBottomSheet
import com.vc.composecore.components.status.CoreNoInternetView
import com.vc.composecore.components.textfield.CoreDateField
import com.vc.composecore.components.textfield.CoreDropdownField
import com.vc.composecore.components.textfield.CoreEmailField
import com.vc.composecore.components.textfield.CoreNumberField
import com.vc.composecore.components.textfield.CoreOutlinedTextField
import com.vc.composecore.components.textfield.CorePasswordField
import com.vc.composecore.components.textfield.CorePhoneField
import com.vc.composecore.forms.CoreFormFieldState
import com.vc.composecore.forms.CoreFormState
import com.vc.composecore.forms.EmailValidator
import com.vc.composecore.forms.MinLengthValidator
import com.vc.composecore.forms.RequiredValidator
import com.vc.composecore.media.rememberFilePicker
import com.vc.composecore.media.rememberImagePicker
import com.vc.composecore.media.rememberMultipleImagePicker
import com.vc.composecore.permissions.CorePermissionHandler
import com.vc.composecore.permissions.rememberCorePermissionState
import com.vc.composecore.theme.CoreRadius
import com.vc.composecore.theme.CoreTheme
import com.vc.composecore.theme.ThemeMode
import com.vc.composecore.utils.CoreEnvironment
import com.vc.composecore.utils.CoreNetworkStatusBanner
import com.vc.composecore.window.SecureScreen
import com.vc.composecore.window.SensitiveContent
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SplashScreen(onFinish: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1200.milliseconds)
        onFinish()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CoreTheme.colors.primary),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(CoreTheme.colors.onPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Widgets,
                    contentDescription = null,
                    tint = CoreTheme.colors.primary,
                    modifier = Modifier.size(48.dp)
                )
            }
            Spacer(modifier = Modifier.height(CoreTheme.spacing.md))
            Text(
                text = "Compose Core",
                style = CoreTheme.typography.headlineMedium,
                color = CoreTheme.colors.onPrimary
            )
            Spacer(modifier = Modifier.height(CoreTheme.spacing.xxs))
            Text(
                text = "Enterprise Jetpack Compose Foundation",
                style = CoreTheme.typography.bodyMedium,
                color = CoreTheme.colors.onPrimary.copy(alpha = 0.8f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    val emailField = remember { CoreFormFieldState("", listOf(RequiredValidator(), EmailValidator()), "Email") }
    val passwordField = remember { CoreFormFieldState("", listOf(RequiredValidator(), MinLengthValidator(6)), "Password") }
    val formState = remember { CoreFormState(listOf(emailField, passwordField)) }

    BaseScreen(
        topBar = { CoreTopAppBar(title = "Welcome Back", navigationIcon = null) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(CoreTheme.spacing.lg),
            verticalArrangement = Arrangement.Center
        ) {
            Text("Sign In", style = CoreTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(CoreTheme.spacing.xs))
            Text(
                "Demonstrating CoreForm, CoreFormFieldState, and CoreAsyncButton",
                style = CoreTheme.typography.bodyMedium,
                color = CoreTheme.colors.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(CoreTheme.spacing.lg))

            CoreEmailField(
                value = emailField.value,
                onValueChange = emailField::onValueChange,
                error = emailField.error?.asString()
            )
            Spacer(modifier = Modifier.height(CoreTheme.spacing.sm))
            CorePasswordField(
                value = passwordField.value,
                onValueChange = passwordField::onValueChange,
                error = passwordField.error?.asString()
            )
            Spacer(modifier = Modifier.height(CoreTheme.spacing.lg))

            CoreAsyncButton(
                text = "Sign In",
                fullWidth = true,
                onClick = {
                    if (formState.validateAll()) {
                        delay(1000.milliseconds)
                        onLoginSuccess()
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateTo: (String) -> Unit
) {
    BaseScreen(
        topBar = { CoreTopAppBar(title = "Compose Core Showcase", navigationIcon = null) },
        bottomBar = {
            CoreNavigationBar(
                items = listOf(
                    CoreNavigationItemData("home", "Home", Icons.Default.Home),
                    CoreNavigationItemData("search", "Search", Icons.Default.Search),
                    CoreNavigationItemData("list", "List", Icons.Default.List),
                    CoreNavigationItemData("profile", "Profile", Icons.Default.AccountCircle)
                ),
                selectedRouteKey = "home",
                onItemSelected = { route -> onNavigateTo(route) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(CoreTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(CoreTheme.spacing.md)
        ) {
            CoreElevatedCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Widgets,
                        contentDescription = null,
                        tint = CoreTheme.colors.primary,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(CoreTheme.spacing.md))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Component Showcase Catalog", style = CoreTheme.typography.titleMedium)
                        Text("Visual catalog of all 30+ reusable core controls", style = CoreTheme.typography.bodySmall)
                    }
                }
                Spacer(modifier = Modifier.height(CoreTheme.spacing.md))
                CoreButton(
                    text = "Open Component Showcase",
                    fullWidth = true,
                    onClick = { onNavigateTo("showcase") }
                )
            }

            val demos = listOf(
                "form" to "Forms & Validation Demo",
                "dialog" to "Dialogs System Demo",
                "bottom_sheet" to "Bottom Sheets Demo",
                "theme" to "Theme & Design Tokens Demo",
                "offline" to "Network Status & Offline Demo",
                "permission" to "Permission Handler Demo",
                "media" to "Photo & File Pickers Demo"
            )

            demos.forEach { (route, title) ->
                CoreClickableCard(onClick = { onNavigateTo(route) }) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(title, style = CoreTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                        CoreBadge(count = 1)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(onBack: () -> Unit) {
    val (query, setQuery) = rememberDebouncedSearch(debounceMs = 300) { debounced ->
        // Handle search query updates
    }
    val allItems = listOf("Buttons", "TextFields", "Cards", "Dialogs", "BottomSheets", "Shimmer", "Paging", "Navigation")
    val filtered = remember(query) {
        if (query.isBlank()) allItems else allItems.filter { it.contains(query, ignoreCase = true) }
    }

    BaseScreen(
        topBar = { CoreTopAppBar(title = "Search Demo", onNavigationClick = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(CoreTheme.spacing.md)
        ) {
            CoreSearchBar(query = query, onQueryChange = setQuery, placeholder = "Search features...")
            Spacer(modifier = Modifier.height(CoreTheme.spacing.md))
            LazyColumn {
                items(filtered) { item ->
                    CoreOutlinedCard(modifier = Modifier.padding(vertical = CoreTheme.spacing.xxs)) {
                        Text(item, style = CoreTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(onBack: () -> Unit) {
    var itemsList by remember { mutableStateOf((1..15).map { "Item #$it - Swipe left to delete" }) }

    BaseScreen(
        topBar = { CoreTopAppBar(title = "Swipeable List Demo", onNavigationClick = onBack) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(CoreTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(CoreTheme.spacing.sm)
        ) {
            items(itemsList, key = { it }) { item ->
                CoreSwipeableItem(
                    onDismiss = { itemsList = itemsList - item }
                ) {
                    CoreOutlinedCard {
                        Text(item, style = CoreTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormDemoScreen(onBack: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var agreed by remember { mutableStateOf(false) }

    BaseScreen(
        topBar = { CoreTopAppBar(title = "Form Architecture Demo", onNavigationClick = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(CoreTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(CoreTheme.spacing.md)
        ) {
            CoreOutlinedTextField(value = name, onValueChange = { name = it }, label = "Full Name")
            CoreEmailField(value = email, onValueChange = { email = it })
            CorePhoneField(value = phone, onValueChange = { phone = it })
            CoreNumberField(value = age, onValueChange = { age = it }, label = "Age")
            CoreCheckbox(checked = agreed, onCheckedChange = { agreed = it }, label = "I agree to Terms & Conditions")
            CoreButton(text = "Submit Form", fullWidth = true, onClick = {})
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDemoScreen(
    currentTheme: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit,
    onBack: () -> Unit
) {
    BaseScreen(
        topBar = { CoreTopAppBar(title = "Settings Demo", onNavigationClick = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(CoreTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(CoreTheme.spacing.md)
        ) {
            Text("Theme Mode", style = CoreTheme.typography.titleMedium)
            CoreRadioButton(selected = currentTheme == ThemeMode.System, onClick = { onThemeChange(ThemeMode.System) }, label = "System Default")
            CoreRadioButton(selected = currentTheme == ThemeMode.Light, onClick = { onThemeChange(ThemeMode.Light) }, label = "Light Mode")
            CoreRadioButton(selected = currentTheme == ThemeMode.Dark, onClick = { onThemeChange(ThemeMode.Dark) }, label = "Dark Mode")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDemoScreen(onBack: () -> Unit) {
    var isSensitiveHidden by remember { mutableStateOf(true) }

    BaseScreen(
        topBar = { CoreTopAppBar(title = "Profile & Security Demo", onNavigationClick = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(CoreTheme.spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CoreAvatar(initials = "VK", size = 84.dp, status = AvatarStatus.Online)
            Spacer(modifier = Modifier.height(CoreTheme.spacing.md))
            Text("Viral Kalathiya", style = CoreTheme.typography.headlineSmall)
            Text("Lead Android Architect", style = CoreTheme.typography.bodyMedium, color = CoreTheme.colors.onSurfaceVariant)

            Spacer(modifier = Modifier.height(CoreTheme.spacing.xl))

            CoreOutlinedCard {
                Text("Sensitive Financial Balance", style = CoreTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(CoreTheme.spacing.xs))
                SensitiveContent(isSensitive = isSensitiveHidden) {
                    Text("$124,580.00 USD", style = CoreTheme.typography.headlineMedium, color = CoreTheme.colors.primary)
                }
                Spacer(modifier = Modifier.height(CoreTheme.spacing.sm))
                CoreOutlinedButton(
                    text = if (isSensitiveHidden) "Reveal Balance" else "Hide Balance",
                    onClick = { isSensitiveHidden = !isSensitiveHidden }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogDemoScreen(onBack: () -> Unit) {
    var dialogType by remember { mutableStateOf<String?>(null) }

    BaseScreen(
        topBar = { CoreTopAppBar(title = "Dialogs Demo", onNavigationClick = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(CoreTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(CoreTheme.spacing.sm)
        ) {
            CoreButton(text = "Show Alert Dialog", fullWidth = true, onClick = { dialogType = "alert" })
            CoreButton(text = "Show Confirm Dialog", fullWidth = true, onClick = { dialogType = "confirm" })
            CoreButton(text = "Show Success Dialog", fullWidth = true, onClick = { dialogType = "success" })
            CoreButton(text = "Show Error Dialog", fullWidth = true, onClick = { dialogType = "error" })
        }
    }

    when (dialogType) {
        "alert" -> CoreAlertDialog(visible = true, title = "Notice", message = "This is a simple alert.", onDismissRequest = { dialogType = null })
        "confirm" -> CoreConfirmDialog(visible = true, title = "Confirm", message = "Proceed with operation?", onConfirm = { dialogType = null }, onDismiss = { dialogType = null })
        "success" -> CoreSuccessDialog(visible = true, message = "Operation completed successfully!", onDismiss = { dialogType = null })
        "error" -> CoreErrorDialog(visible = true, message = "Operation encountered an error.", onDismiss = { dialogType = null })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetDemoScreen(onBack: () -> Unit) {
    var showSheet by remember { mutableStateOf(false) }

    BaseScreen(
        topBar = { CoreTopAppBar(title = "Bottom Sheet Demo", onNavigationClick = onBack) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            CoreButton(text = "Open Bottom Sheet", onClick = { showSheet = true })
        }
    }

    CoreModalBottomSheet(
        visible = showSheet,
        onDismissRequest = { showSheet = false }
    ) {
        Text("Modal Bottom Sheet", style = CoreTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(CoreTheme.spacing.sm))
        Text("Clean, production-ready bottom sheet integration.", style = CoreTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(CoreTheme.spacing.lg))
        CoreButton(text = "Dismiss", fullWidth = true, onClick = { showSheet = false })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeDemoScreen(onBack: () -> Unit) {
    BaseScreen(
        topBar = { CoreTopAppBar(title = "Design Tokens Demo", onNavigationClick = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(CoreTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(CoreTheme.spacing.md)
        ) {
            Text("Colors Palette", style = CoreTheme.typography.titleLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(CoreTheme.spacing.sm)) {
                Box(Modifier.size(48.dp).background(CoreTheme.colors.primary, RoundedCornerShape(CoreRadius.small)))
                Box(Modifier.size(48.dp).background(CoreTheme.colors.secondary, RoundedCornerShape(CoreRadius.small)))
                Box(Modifier.size(48.dp).background(CoreTheme.colors.success, RoundedCornerShape(CoreRadius.small)))
                Box(Modifier.size(48.dp).background(CoreTheme.colors.error, RoundedCornerShape(CoreRadius.small)))
                Box(Modifier.size(48.dp).background(CoreTheme.colors.warning, RoundedCornerShape(CoreRadius.small)))
            }

            Spacer(modifier = Modifier.height(CoreTheme.spacing.sm))
            Text("Typography Scale", style = CoreTheme.typography.titleLarge)
            Text("Display Large", style = CoreTheme.typography.displaySmall)
            Text("Headline Medium", style = CoreTheme.typography.headlineMedium)
            Text("Title Medium", style = CoreTheme.typography.titleMedium)
            Text("Body Medium", style = CoreTheme.typography.bodyMedium)
            Text("Caption Text", style = CoreTheme.typography.caption)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfflineDemoScreen(onBack: () -> Unit) {
    var isSimulatedOnline by remember { mutableStateOf(false) }

    BaseScreen(
        topBar = { CoreTopAppBar(title = "Offline & Network Banner Demo", onNavigationClick = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            CoreNetworkStatusBanner(isOnline = isSimulatedOnline)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(CoreTheme.spacing.md),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CoreSwitch(
                    checked = isSimulatedOnline,
                    onCheckedChange = { isSimulatedOnline = it },
                    label = "Simulate Network State",
                    subLabel = if (isSimulatedOnline) "Currently Online" else "Currently Offline"
                )
                if (!isSimulatedOnline) {
                    Spacer(modifier = Modifier.height(CoreTheme.spacing.lg))
                    CoreNoInternetView(onRetry = { isSimulatedOnline = true })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionDemoScreen(onBack: () -> Unit) {
    val cameraPermission = rememberCorePermissionState(permission = android.Manifest.permission.CAMERA)

    BaseScreen(
        topBar = { CoreTopAppBar(title = "Permission Handler Demo", onNavigationClick = onBack) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            CorePermissionHandler(
                permissionState = cameraPermission,
                rationaleMessage = "Camera access is needed to capture photos."
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = CoreTheme.colors.success, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(CoreTheme.spacing.xs))
                    Text("Camera Permission Granted!", style = CoreTheme.typography.titleMedium)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaDemoScreen(onBack: () -> Unit) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }

    val pickImage = rememberImagePicker { uri -> selectedImageUri = uri }
    val pickFile = rememberFilePicker("*/*") { uri -> selectedFileUri = uri }

    BaseScreen(
        topBar = { CoreTopAppBar(title = "Media & File Pickers Demo", onNavigationClick = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(CoreTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(CoreTheme.spacing.md)
        ) {
            CoreButton(text = "Pick Single Image", fullWidth = true, onClick = pickImage)
            if (selectedImageUri != null) {
                Text("Selected Image: $selectedImageUri", style = CoreTheme.typography.bodySmall)
            }

            CoreOutlinedButton(text = "Pick Document File", fullWidth = true, onClick = pickFile)
            if (selectedFileUri != null) {
                Text("Selected File: $selectedFileUri", style = CoreTheme.typography.bodySmall)
            }
        }
    }
}

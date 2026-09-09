package com.vc.composesample.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import com.vc.composecore.animation.CoreAnimatedVisibility
import com.vc.composecore.animation.CoreShimmerBox
import com.vc.composecore.animation.CoreShimmerList
import com.vc.composecore.animation.CoreShimmerText
import com.vc.composecore.components.appbar.CoreTopAppBar
import com.vc.composecore.components.badge.CoreBadge
import com.vc.composecore.components.badge.CoreNotificationBadge
import com.vc.composecore.components.badge.CoreStatusBadge
import com.vc.composecore.components.button.CoreAsyncButton
import com.vc.composecore.components.button.CoreButton
import com.vc.composecore.components.button.CoreDangerButton
import com.vc.composecore.components.button.CoreFloatingActionButton
import com.vc.composecore.components.button.CoreIconButton
import com.vc.composecore.components.button.CoreLoadingButton
import com.vc.composecore.components.button.CoreOutlinedButton
import com.vc.composecore.components.button.CoreTextButton
import com.vc.composecore.components.card.CoreCard
import com.vc.composecore.components.card.CoreClickableCard
import com.vc.composecore.components.card.CoreElevatedCard
import com.vc.composecore.components.card.CoreOutlinedCard
import com.vc.composecore.components.chip.CoreAssistChip
import com.vc.composecore.components.chip.CoreChip
import com.vc.composecore.components.chip.CoreFilterChip
import com.vc.composecore.components.chip.CoreInputChip
import com.vc.composecore.components.chip.CoreSuggestionChip
import com.vc.composecore.components.dialog.CoreAlertDialog
import com.vc.composecore.components.dialog.CoreConfirmDialog
import com.vc.composecore.components.dialog.CoreErrorDialog
import com.vc.composecore.components.dialog.CoreLoadingDialog
import com.vc.composecore.components.dialog.CoreSuccessDialog
import com.vc.composecore.components.divider.CoreDivider
import com.vc.composecore.components.divider.CoreListSeparator
import com.vc.composecore.components.dropdown.CoreDropdown
import com.vc.composecore.components.image.AvatarStatus
import com.vc.composecore.components.image.CoreAvatar
import com.vc.composecore.components.image.CoreAvatarGroup
import com.vc.composecore.components.loading.CoreCircularProgress
import com.vc.composecore.components.loading.CoreLinearProgress
import com.vc.composecore.components.otp.CoreOtpField
import com.vc.composecore.components.picker.CoreDatePicker
import com.vc.composecore.components.picker.CoreTimePicker
import com.vc.composecore.components.search.CoreSearchBar
import com.vc.composecore.components.selection.CoreCheckbox
import com.vc.composecore.components.selection.CoreRadioButton
import com.vc.composecore.components.selection.CoreSwitch
import com.vc.composecore.components.selection.CoreTriStateCheckbox
import com.vc.composecore.components.sheet.CoreModalBottomSheet
import com.vc.composecore.components.snackbar.CoreSnackbarHost
import com.vc.composecore.components.tab.CoreTabData
import com.vc.composecore.components.tab.CoreTabRow
import com.vc.composecore.components.textfield.CoreDateField
import com.vc.composecore.components.textfield.CoreDropdownField
import com.vc.composecore.components.textfield.CoreEmailField
import com.vc.composecore.components.textfield.CoreNumberField
import com.vc.composecore.components.textfield.CoreOutlinedTextField
import com.vc.composecore.components.textfield.CorePasswordField
import com.vc.composecore.components.textfield.CorePhoneField
import com.vc.composecore.components.textfield.CoreSearchField
import com.vc.composecore.components.textfield.CoreSimpleEmailField
import com.vc.composecore.components.textfield.CoreSimplePasswordField
import com.vc.composecore.components.textfield.CoreSimpleTextField
import com.vc.composecore.components.textfield.CoreTextField
import com.vc.composecore.components.tooltip.CorePlainTooltip
import com.vc.composecore.components.tooltip.CoreRichTooltip
import com.vc.composecore.state.UiEffect
import com.vc.composecore.theme.CoreTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComponentShowcaseScreen(
    onBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Dialog state
    var showAlertDialog by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showLoadingDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }

    // Text field state
    var textValue by remember { mutableStateOf("Input text") }
    var passwordValue by remember { mutableStateOf("secret123") }
    var otpValue by remember { mutableStateOf("123456") }
    var emailValue by remember { mutableStateOf("user@domain.com") }
    var phoneValue by remember { mutableStateOf("+1 555 123 4567") }
    var numberValue by remember { mutableStateOf("42") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedDateText by remember { mutableStateOf("Oct 24, 2026") }

    // Selection state
    var checkboxState by remember { mutableStateOf(true) }
    var triState by remember { mutableStateOf(ToggleableState.Indeterminate) }
    var radioSelected by remember { mutableIntStateOf(0) }
    var switchState by remember { mutableStateOf(true) }

    // Dropdown / Tab
    val dropdownOptions = listOf("Standard Plan", "Professional Plan", "Enterprise Plan")
    var selectedDropdown by remember { mutableStateOf(dropdownOptions[0]) }
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            CoreTopAppBar(
                title = "Component Showcase",
                subtitle = "Complete Jetpack Compose UI Catalog",
                navigationIcon = Icons.Default.ArrowBack,
                onNavigationClick = onBack
            )
        },
        snackbarHost = { CoreSnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(CoreTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(CoreTheme.spacing.lg)
        ) {
            // 1. BUTTONS SECTION
            ShowcaseSection(title = "1. Buttons") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(CoreTheme.spacing.sm)
                ) {
                    CoreButton(text = "Primary", onClick = {
                        scope.launch { snackbarHostState.showSnackbar("Primary clicked") }
                    })
                    CoreOutlinedButton(text = "Outlined", onClick = {})
                    CoreTextButton(text = "Text Button", onClick = {})
                    CoreDangerButton(text = "Danger", onClick = {})
                    CoreLoadingButton(text = "Loading", isLoading = true, onClick = {})
                    CoreAsyncButton(
                        text = "Async (1s)",
                        onClick = { delay(1000) }
                    )
                    CoreIconButton(
                        icon = Icons.Default.Star,
                        contentDescription = "Favorite",
                        onClick = {}
                    )
                }
            }

            // 2. TEXT FIELDS SECTION
            ShowcaseSection(title = "2. Text Fields & OTP") {
                CoreOutlinedTextField(
                    value = textValue,
                    onValueChange = { textValue = it },
                    label = "Outlined Text Field",
                    helperText = "Helper information text"
                )
                Spacer(modifier = Modifier.height(CoreTheme.spacing.xs))
                CoreSimpleEmailField(
                    value = emailValue,
                    onValueChange = { emailValue = it },
                    label = "Email Address",
                    placeholder = "Enter your email",
                    isRequired = true
                )
                Spacer(modifier = Modifier.height(CoreTheme.spacing.xs))
                CoreSimplePasswordField(
                    value = passwordValue,
                    onValueChange = { passwordValue = it }
                )
                Spacer(modifier = Modifier.height(CoreTheme.spacing.xs))
                CoreEmailField(value = emailValue, onValueChange = { emailValue = it })
                Spacer(modifier = Modifier.height(CoreTheme.spacing.xs))
                CorePhoneField(value = phoneValue, onValueChange = { phoneValue = it })
                Spacer(modifier = Modifier.height(CoreTheme.spacing.xs))
                CoreNumberField(value = numberValue, onValueChange = { numberValue = it }, label = "Number Input")
                Spacer(modifier = Modifier.height(CoreTheme.spacing.xs))
                CoreDateField(value = selectedDateText, onClick = { showDatePicker = true })
                Spacer(modifier = Modifier.height(CoreTheme.spacing.sm))
                Text("OTP / PIN Input:", style = CoreTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(CoreTheme.spacing.xxs))
                CoreOtpField(otpValue = otpValue, onOtpChange = { otpValue = it }, length = 6)
            }

            // 3. SEARCH & CHIPS SECTION
            ShowcaseSection(title = "3. Search & Chips") {
                CoreSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Search components..."
                )
                Spacer(modifier = Modifier.height(CoreTheme.spacing.sm))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(CoreTheme.spacing.sm)
                ) {
                    CoreChip(label = "Standard Chip", onClick = {})
                    CoreFilterChip(selected = true, onClick = {}, label = "Active Filter")
                    CoreFilterChip(selected = false, onClick = {}, label = "Inactive")
                    CoreInputChip(selected = false, onClick = {}, label = "Input Chip", onDismiss = {})
                    CoreSuggestionChip(label = "Suggestion", onClick = {})
                }
            }

            // 4. SELECTION CONTROLS
            ShowcaseSection(title = "4. Selection Controls") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CoreCheckbox(
                        checked = checkboxState,
                        onCheckedChange = { checkboxState = it },
                        label = "Checkbox Active"
                    )
                    Spacer(modifier = Modifier.width(CoreTheme.spacing.md))
                    CoreTriStateCheckbox(
                        state = triState,
                        onClick = {
                            triState = when (triState) {
                                ToggleableState.On -> ToggleableState.Off
                                ToggleableState.Off -> ToggleableState.Indeterminate
                                ToggleableState.Indeterminate -> ToggleableState.On
                            }
                        },
                        label = "Tri-State"
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CoreRadioButton(
                        selected = radioSelected == 0,
                        onClick = { radioSelected = 0 },
                        label = "Option A"
                    )
                    Spacer(modifier = Modifier.width(CoreTheme.spacing.md))
                    CoreRadioButton(
                        selected = radioSelected == 1,
                        onClick = { radioSelected = 1 },
                        label = "Option B"
                    )
                }
                CoreSwitch(
                    checked = switchState,
                    onCheckedChange = { switchState = it },
                    label = "Switch Preference",
                    subLabel = "Toggle background synchronization"
                )
            }

            // 5. CARDS SECTION
            ShowcaseSection(title = "5. Cards") {
                CoreCard {
                    Text("Standard CoreCard", style = CoreTheme.typography.titleMedium)
                    Text("Elevation level 1 surface card with automatic padding.", style = CoreTheme.typography.bodySmall)
                }
                Spacer(modifier = Modifier.height(CoreTheme.spacing.xs))
                CoreOutlinedCard {
                    Text("CoreOutlinedCard", style = CoreTheme.typography.titleMedium)
                    Text("Subtle hairline border card styling.", style = CoreTheme.typography.bodySmall)
                }
                Spacer(modifier = Modifier.height(CoreTheme.spacing.xs))
                CoreElevatedCard {
                    Text("CoreElevatedCard", style = CoreTheme.typography.titleMedium)
                    Text("Pronounced elevation level 2 card.", style = CoreTheme.typography.bodySmall)
                }
                Spacer(modifier = Modifier.height(CoreTheme.spacing.xs))
                CoreClickableCard(onClick = {
                    scope.launch { snackbarHostState.showSnackbar("Clicked Card!") }
                }) {
                    Text("CoreClickableCard (Tap Me)", style = CoreTheme.typography.titleMedium)
                    Text("Includes ripple feedback and elevation state.", style = CoreTheme.typography.bodySmall)
                }
            }

            // 6. TABS & DROPDOWN
            ShowcaseSection(title = "6. Tabs & Dropdown") {
                CoreTabRow(
                    selectedTabIndex = selectedTab,
                    tabs = listOf(
                        CoreTabData("Overview", icon = Icons.Default.Info),
                        CoreTabData("Analytics", icon = Icons.Default.Star, badgeCount = 3),
                        CoreTabData("Settings", icon = Icons.Default.Lock)
                    ),
                    onTabSelected = { selectedTab = it }
                )
                Spacer(modifier = Modifier.height(CoreTheme.spacing.md))
                CoreDropdown(
                    items = dropdownOptions,
                    selectedItem = selectedDropdown,
                    onItemSelected = { selectedDropdown = it },
                    labelProvider = { it }
                )
            }

            // 7. DIALOGS & SHEETS TRIGGER
            ShowcaseSection(title = "7. Dialogs & Bottom Sheet") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(CoreTheme.spacing.sm)
                ) {
                    CoreButton(text = "Alert Dialog", onClick = { showAlertDialog = true })
                    CoreButton(text = "Confirm Dialog", onClick = { showConfirmDialog = true })
                    CoreButton(text = "Success Dialog", onClick = { showSuccessDialog = true })
                    CoreButton(text = "Error Dialog", onClick = { showErrorDialog = true })
                    CoreButton(text = "Loading Dialog", onClick = {
                        showLoadingDialog = true
                        scope.launch {
                            delay(1500)
                            showLoadingDialog = false
                        }
                    })
                    CoreButton(text = "Bottom Sheet", onClick = { showBottomSheet = true })
                }
            }

            // 8. BADGES, TOOLTIPS, DIVIDERS
            ShowcaseSection(title = "8. Badges, Tooltips & Dividers") {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(CoreTheme.spacing.md)
                ) {
                    CoreBadge(count = 5)
                    CoreBadge(count = 142)
                    CoreNotificationBadge()
                    CoreStatusBadge(label = "Verified")
                    CorePlainTooltip(tooltipText = "Accessible helpful tooltip") {
                        Icon(Icons.Default.Help, contentDescription = "Help")
                    }
                }
                Spacer(modifier = Modifier.height(CoreTheme.spacing.sm))
                CoreDivider()
                Spacer(modifier = Modifier.height(CoreTheme.spacing.sm))
                CoreListSeparator()
            }

            // 9. AVATARS
            ShowcaseSection(title = "9. Avatars & Group") {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(CoreTheme.spacing.md)
                ) {
                    CoreAvatar(initials = "VK", status = AvatarStatus.Online)
                    CoreAvatar(initials = "JD", status = AvatarStatus.Busy)
                    CoreAvatar(status = AvatarStatus.Away)
                    CoreAvatarGroup(
                        avatars = listOf(
                            null to "AB",
                            null to "CD",
                            null to "EF",
                            null to "GH",
                            null to "IJ"
                        )
                    )
                }
            }

            // 10. LOADERS & SHIMMER
            ShowcaseSection(title = "10. Loaders & Shimmer") {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(CoreTheme.spacing.md)
                ) {
                    CoreCircularProgress()
                    CoreCircularProgress(size = 24.dp)
                }
                Spacer(modifier = Modifier.height(CoreTheme.spacing.sm))
                CoreLinearProgress()
                Spacer(modifier = Modifier.height(CoreTheme.spacing.sm))
                Text("Shimmer Placeholders:", style = CoreTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(CoreTheme.spacing.xs))
                CoreShimmerList(itemCount = 2)
            }
        }
    }

    // Interactive Dialogs rendering
    CoreAlertDialog(
        visible = showAlertDialog,
        title = "Information Alert",
        message = "This is a state-driven CoreAlertDialog.",
        onDismissRequest = { showAlertDialog = false }
    )

    CoreConfirmDialog(
        visible = showConfirmDialog,
        title = "Confirm Deletion?",
        message = "Are you sure you want to permanently delete this item?",
        confirmText = "Delete",
        isDanger = true,
        onConfirm = {
            showConfirmDialog = false
            scope.launch { snackbarHostState.showSnackbar("Item Deleted") }
        },
        onDismiss = { showConfirmDialog = false }
    )

    CoreSuccessDialog(
        visible = showSuccessDialog,
        message = "Your settings have been saved successfully!",
        onDismiss = { showSuccessDialog = false }
    )

    CoreErrorDialog(
        visible = showErrorDialog,
        message = "Unable to process the request due to a network timeout.",
        onDismiss = { showErrorDialog = false }
    )

    CoreLoadingDialog(
        visible = showLoadingDialog,
        message = "Saving changes..."
    )

    CoreDatePicker(
        visible = showDatePicker,
        onDateSelected = { millis ->
            if (millis != null) {
                selectedDateText = "Selected Date: $millis"
            }
        },
        onDismiss = { showDatePicker = false }
    )

    CoreModalBottomSheet(
        visible = showBottomSheet,
        onDismissRequest = { showBottomSheet = false }
    ) {
        Text("Modal Bottom Sheet Content", style = CoreTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(CoreTheme.spacing.xs))
        Text("Supports drag handle, partial and full expansion states.", style = CoreTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(CoreTheme.spacing.md))
        CoreButton(
            text = "Close Bottom Sheet",
            onClick = { showBottomSheet = false },
            fullWidth = true
        )
        Spacer(modifier = Modifier.height(CoreTheme.spacing.lg))
    }
}

@Composable
private fun ShowcaseSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = CoreTheme.typography.titleMedium,
            color = CoreTheme.colors.primary
        )
        Spacer(modifier = Modifier.height(CoreTheme.spacing.xs))
        CoreCard(contentPadding = CoreTheme.spacing.md) {
            content()
        }
    }
}

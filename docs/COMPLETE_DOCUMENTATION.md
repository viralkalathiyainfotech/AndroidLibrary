# AndroidCoreLibrary: The Complete Master Guide

A production-grade, architectural foundation library for Android applications engineered in Kotlin.

This guide is the complete, single-source-of-truth manual for **both** the XML/View-based foundation (`:core`) and the Jetpack Compose foundation (`:compose-core`).

---

## Table of Contents

1. [Quick Installation](#1-quick-installation)
2. [Module Selection: `:core` vs `:compose-core`](#2-module-selection-core-vs-compose-core)
3. [Core Module Guide (`:core` - View / XML Architecture)](#3-core-module-guide-core---view--xml-architecture)
   - [3.1 Application Initialization](#31-application-initialization)
   - [3.2 BaseActivity & BaseFragment](#32-baseactivity--basefragment)
   - [3.3 BaseViewModel & Event Architecture](#33-baseviewmodel--event-architecture)
   - [3.4 Networking Engine (Simple & Authenticated)](#34-networking-engine-simple--authenticated)
   - [3.5 Offline-First Database (Room & BaseDao)](#35-offline-first-database-room--basedao)
   - [3.6 Jetpack DataStore Preferences](#36-jetpack-datastore-preferences)
   - [3.7 Runtime Permission Manager](#37-runtime-permission-manager)
   - [3.8 Multipart & Image/File Upload Helper](#38-multipart--imagefile-upload-helper)
   - [3.9 Developer Ergonomics (Debounce, Extensions, Dialogs)](#39-developer-ergonomics-debounce-extensions-dialogs)
4. [Compose Core Module Guide (`:compose-core` - Jetpack Compose)](#4-compose-core-module-guide-compose-core---jetpack-compose)
   - [4.1 MVI Architecture Contracts (ViewState, UiAction, UiEffect)](#41-mvi-architecture-contracts-viewstate-uiaction-uieffect)
   - [4.2 BaseComposeActivity & BaseComposeViewModel](#42-basecomposeactivity--basecomposeviewmodel)
   - [4.3 Base Screens & CoreStateScreen](#43-base-screens--corestatescreen)
   - [4.4 Design System & Core Tokens (Spacing, Radii, Colors)](#44-design-system--core-tokens-spacing-radii-colors)
   - [4.5 UI Component Library (23+ Production Components)](#45-ui-component-library-23-production-components)
   - [4.6 Modern Form Architecture & Declarative Validation](#46-modern-form-architecture--declarative-validation)
   - [4.7 Lists, Swipe-to-Dismiss & Paging 3 Integration](#47-lists-swipe-to-dismiss--paging-3-integration)
   - [4.8 Compose Permissions & Media Picker](#48-compose-permissions--media-picker)
   - [4.9 Responsive Layouts & Window Size Classes](#49-responsive-layouts--window-size-classes)
   - [4.10 Type-Safe Navigation Architecture](#410-type-safe-navigation-architecture)
   - [4.11 Resource Decoupling (CoreText & CoreDrawable)](#411-resource-decoupling-coretext--coredrawable)
5. [Complete Real-World Recipes](#5-complete-real-world-recipes)
   - [Recipe 1: Full Authentication Flow (XML vs Compose)](#recipe-1-full-authentication-flow-xml-vs-compose)
   - [Recipe 2: Offline-First Synchronized List](#recipe-2-offline-first-synchronized-list)
   - [Recipe 3: Modern Registration Form with Field Validation](#recipe-3-modern-registration-form-with-field-validation)
6. [Troubleshooting & ProGuard](#6-troubleshooting--proguard)

---

## 1. Quick Installation

### Step 1: Add JitPack Repository
In your root `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

### Step 2: Add Module Dependencies
In your app's `build.gradle.kts`:

```kotlin
dependencies {
    // For XML / ViewBinding projects:
    implementation("com.github.viralkalathiyainfotech.AndroidLibrary:core:2.1.0")

    // For Jetpack Compose projects:
    implementation("com.github.viralkalathiyainfotech.AndroidLibrary:compose-core:compose-1.0.0")
}
```

---

## 2. Module Selection: `:core` vs `:compose-core`

| Feature | `:core` Module | `:compose-core` Module |
|---|---|---|
| **UI Framework** | Android Views & ViewBinding | Jetpack Compose & Material 3 |
| **Architecture Pattern** | MVVM with `repeatOnLifecycle` | Pure MVI (Unidirectional Data Flow) |
| **State Handling** | `StateFlow` & `SharedFlow<UiEvent>` | `StateFlow<ViewState>` & `SharedFlow<UiEffect>` |
| **Networking & Database** | Bundled Retrofit, OkHttp, Room, DataStore | Inherits `:core` data layer automatically |
| **Best Used For** | Traditional XML apps, legacy migrations | Modern Compose apps, multiplatform design systems |

---

## 3. Core Module Guide (`:core` - View / XML Architecture)

### 3.1 Application Initialization
In your `Application` class, initialize the global configuration:

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        CoreLibrary.initialize(
            context = this,
            config = CoreConfig(
                baseUrl = "https://api.example.com/",
                enableLogging = BuildConfig.DEBUG,
                enableNetworkLogging = BuildConfig.DEBUG,
                defaultTimeout = 30L
            )
        )
    }
}
```

---

### 3.2 BaseActivity & BaseFragment

`BaseActivity` eliminates boilerplate for ViewBinding inflation, toolbar management, dialogs, and safe lifecycle flow observation.

```kotlin
class ProfileActivity : BaseActivity<ActivityProfileBinding>() {

    private val viewModel by viewModels<ProfileViewModel>()

    // 1. Inflate ViewBinding cleanly
    override fun inflateBinding(): ActivityProfileBinding =
        ActivityProfileBinding.inflate(layoutInflater)

    // 2. Setup static views or toolbars
    override fun setupViews() {
        setupToolbar(binding.toolbar, title = "My Profile", displayHomeAsUp = true)
    }

    // 3. Setup click listeners with debouncing
    override fun setupListeners() {
        binding.btnSave.setOnDebouncedClickListener {
            hideKeyboard()
            val name = binding.etName.textValue()
            viewModel.updateProfile(name)
        }
    }

    // 4. Observe ViewModel data streams
    override fun observeData() {
        // Automatically handles loading spinner, network errors, and toasts:
        observeBaseEvents(viewModel)

        // Observe custom StateFlow
        collectLifecycleFlow(viewModel.profileData) { profile ->
            binding.tvUsername.text = profile.username
        }
    }
}
```

#### `BaseFragment` Lifecycle-Safe ViewBinding:
`BaseFragment` guarantees that the view binding reference is cleared during `onDestroyView()` to completely avoid Android Fragment memory leaks:

```kotlin
class OrdersFragment : BaseFragment<FragmentOrdersBinding>() {

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentOrdersBinding {
        return FragmentOrdersBinding.inflate(inflater, container, false)
    }

    override fun setupViews() {
        // Safe to access binding
    }
}
```

---

### 3.3 BaseViewModel & Event Architecture

`BaseViewModel` provides coroutine execution within `viewModelScope` with automatic loading states, centralized error mapping, and single-shot UI events.

#### Methods Provided by `BaseViewModel`:

| Method | Purpose |
|---|---|
| `launchApi(call, onSuccess, onError)` | **Single-line** API caller with auto loading dialog, IO thread dispatch, and auto retry. |
| `launchSafe(showLoading, onError) { ... }` | Executes a general coroutine safely with automatic error catching. |
| `sendEvent(UiEvent)` | Sends single-shot UI events (Navigation, Toast, Snackbar, Custom). |
| `executeApiCall(apiCall, onSuccess, onError)` | Maps `NetworkResult<T>` directly to success and error callbacks. |

```kotlin
class OrdersViewModel : BaseViewModel() {

    private val apiService = RetrofitProvider.createService<OrderApiService>()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    fun loadOrders() {
        // 1-line API call with automatic loading dialog and error handling!
        launchApi(
            retryCount = 2,
            call = { apiService.getOrders() },
            onSuccess = { orderList ->
                _orders.value = orderList
                sendEvent(UiEvent.ShowToast("Loaded ${orderList.size} orders!"))
            }
        )
    }

    fun cancelOrder(orderId: Long) {
        launchSafe(showLoading = true) {
            val response = safeApiCall { apiService.cancelOrder(orderId) }
            when (response) {
                is NetworkResult.Success -> {
                    sendEvent(UiEvent.ShowSnackbar("Order canceled successfully"))
                    loadOrders() // Refresh
                }
                is NetworkResult.Error -> {
                    sendEvent(UiEvent.ShowSnackbar(response.error.userFriendlyMessage))
                }
                else -> Unit
            }
        }
    }
}
```

---

### 3.4 Networking Engine (Simple & Authenticated)

#### A. Simple API (No Token Needed)
Just 1 line to create the service using the `baseUrl` configured in `CoreLibrary.initialize`:

```kotlin
val apiService = RetrofitProvider.createService<UserApiService>()
```

#### B. Authenticated API (JWT Bearer Token & Automatic 401 Refresh)
When your endpoints require `Authorization: Bearer <token>`:

```kotlin
// 1. Define TokenProvider (reads token from DataStore)
val tokenProvider = object : TokenProvider {
    override fun getAccessToken(): String? = runBlocking {
        dataStoreManager.getString("jwt_token")
    }
}

// 2. Define RefreshTokenProvider (re-authenticates automatically on HTTP 401)
val refreshTokenProvider = object : RefreshTokenProvider {
    override fun refreshToken(): String? {
        val newTokens = authApiService.refreshTokenSync()
        return newTokens?.accessToken
    }
}

// 3. Build OkHttpClient
val okHttpClient = OkHttpProvider.builder()
    .tokenProvider(tokenProvider, refreshTokenProvider)
    .logging(BuildConfig.DEBUG)
    .connectTimeout(30)
    .readTimeout(30)
    .build()

// 4. Create Service
val secureService = RetrofitProvider.createService<UserApiService>(
    baseUrl = "https://api.myproductionapp.com/",
    okHttpClient = okHttpClient
)
```

---

### 3.5 Offline-First Database (Room & BaseDao)

The library provides `BaseDao<T>` with standard CRUD, conflict strategies, and `DatabaseProvider` to construct Room databases effortlessly:

```kotlin
// 1. Entity
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val email: String
)

// 2. Dao with BaseDao
@Dao
interface UserDao : BaseDao<UserEntity> {
    @Query("SELECT * FROM users ORDER BY name ASC")
    fun getAllUsersFlow(): Flow<List<UserEntity>>
}

// 3. Room Database
@Database(entities = [UserEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}

// 4. Build Instance with DatabaseProvider
val database = DatabaseProvider.builder(
    context = context,
    klass = AppDatabase::class.java,
    databaseName = "app_database.db"
).build()
```

---

### 3.6 Jetpack DataStore Preferences

A type-safe, asynchronous key-value persistence wrapper over Android Jetpack DataStore:

```kotlin
val dataStoreManager = DataStoreManager(context)

// Saving values (Suspend functions)
dataStoreManager.putString("user_email", "user@example.com")
dataStoreManager.putInt("theme_mode", 2)
dataStoreManager.putBoolean("is_logged_in", true)

// Reading values as reactive Flows
val emailFlow: Flow<String?> = dataStoreManager.getStringFlow("user_email")
val isLoggedInFlow: Flow<Boolean> = dataStoreManager.getBooleanFlow("is_logged_in", defaultValue = false)

// Clear all preferences
dataStoreManager.clearAll()
```

---

### 3.7 Runtime Permission Manager

Modern permission management without overriding `onRequestPermissionsResult`:

```kotlin
class CameraActivity : BaseActivity<ActivityCameraBinding>() {

    private lateinit var permissionManager: PermissionManager

    override fun setupViews() {
        permissionManager = PermissionManager(this)
    }

    override fun setupListeners() {
        binding.btnScan.setOnClickListener {
            permissionManager.request(
                Manifest.permission.CAMERA,
                onGranted = { openScanner() },
                onDenied = { showToast("Camera permission was denied") },
                onPermanentlyDenied = {
                    permissionManager.openAppSettings(this)
                }
            )
        }
    }
}
```

---

### 3.8 Multipart & Image/File Upload Helper

Converts Android `Uri`, files, or byte arrays into `MultipartBody.Part` with automatic MIME-type detection:

```kotlin
// In ViewModel or Repository:
fun uploadAvatar(imageUri: Uri, userId: Long) {
    launchSafe(showLoading = true) {
        val filePart = MultipartHelper.createFilePart(
            context = context,
            partName = "avatar",
            uri = imageUri
        )
        val userIdPart = MultipartHelper.createStringPart("user_id", userId.toString())

        val result = safeApiCall { apiService.uploadProfile(userIdPart, filePart) }
        // Handle result...
    }
}
```

---

### 3.9 Developer Ergonomics

```kotlin
// 1. Debounced Click Listener (Prevents double-click bugs)
binding.btnSubmit.setOnDebouncedClickListener(debounceTimeMs = 600L) {
    viewModel.submit()
}

// 2. Debounced Search Input (Wait 400ms after user stops typing)
binding.etSearch.onDebouncedQueryChange(lifecycleScope, waitMs = 400L) { query ->
    viewModel.search(query)
}

// 3. String & Validation Extensions
"user@mail.com".isValidEmail()        // true
"+1234567890".isValidPhone()          // true
binding.etInput.textValue()            // trim().toString()
binding.etInput.isEmpty()              // boolean

// 4. Quick Material Dialogs
showConfirmDialog(
    title = "Delete Order",
    message = "Are you sure you want to delete this order?",
    positiveText = "Delete",
    isDanger = true,
    onPositive = { viewModel.deleteOrder() }
)
```

---

## 4. Compose Core Module Guide (`:compose-core` - Jetpack Compose)

`:compose-core` provides a full, production-ready Jetpack Compose foundation adhering to **Pure MVI (Model-View-Intent)** architecture.

---

### 4.1 MVI Architecture Contracts

```
┌──────────────┐         UiAction (User Intent)         ┌──────────────────────┐
│  Compose UI  ├───────────────────────────────────────►│ BaseComposeViewModel │
│              │◄───────────────────────────────────────┤                      │
└──────────────┘   ViewState (StateFlow)                └──────────────────────┘
                   UiEffect  (One-Shot SharedFlow)
```

- **`ViewState`**: Marker interface for immutable screen state.
- **`UiAction`**: User intentions sent from UI to ViewModel (`Refresh`, `Submit`, `SelectUser`).
- **`UiEffect`**: Single-shot events that must not be re-triggered on screen rotation (`Navigate`, `ShowSnackbar`).
- **`CollectEffect`**: Lifecycle-aware Composable that collects `UiEffect` cleanly.

```kotlin
// 1. ViewState
data class LoginViewState(
    val email: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false
) : ViewState

// 2. UiAction
sealed interface LoginAction : UiAction {
    data class UpdateEmail(val value: String) : LoginAction
    data object Submit : LoginAction
}

// 3. UiEffect
sealed interface LoginEffect : UiEffect {
    data class ShowToast(val message: String) : LoginEffect
    data object NavigateToDashboard : LoginEffect
}
```

---

### 4.2 BaseComposeActivity & BaseComposeViewModel

```kotlin
// ViewModel
class LoginViewModel : BaseComposeViewModel<LoginViewState, LoginAction, LoginEffect>(LoginViewState()) {

    override fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.UpdateEmail -> updateState { copy(email = action.value) }
            LoginAction.Submit -> performLogin()
        }
    }

    private fun performLogin() {
        launchWithState {
            updateState { copy(isLoading = true) }
            delay(1000)
            updateState { copy(isLoading = false, isSuccess = true) }
            sendEffect(LoginEffect.NavigateToDashboard)
        }
    }
}

// Activity
class LoginComposeActivity : BaseComposeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoreTheme {
                LoginScreen()
            }
        }
    }
}
```

---

### 4.3 Base Screens & CoreStateScreen

`CoreStateScreen` is a declarative layout switcher handling **Loading Skeletons**, **Empty States**, **Error Retry Views**, and **Content**:

```kotlin
@Composable
fun ProductsScreen(viewModel: ProductsViewModel = viewModel()) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()

    CoreStateScreen(
        state = state.screenState, // ScreenState.Loading, Success, Empty, Error
        onRetry = { viewModel.onAction(ProductsAction.Retry) }
    ) { products ->
        CoreLazyColumn(items = products) { product ->
            ProductCard(product)
        }
    }
}
```

---

### 4.4 Design System & Core Tokens

Design tokens ensure consistent spacing, corner curves, elevation, and typography:

```kotlin
// Spacing Tokens
CoreSpacing.xxs  // 2.dp
CoreSpacing.xs   // 4.dp
CoreSpacing.sm   // 8.dp
CoreSpacing.md   // 16.dp
CoreSpacing.lg   // 24.dp
CoreSpacing.xl   // 32.dp

// Radius Tokens
CoreRadius.small  // 4.dp
CoreRadius.medium // 8.dp
CoreRadius.large  // 16.dp
CoreRadius.full   // 999.dp (Pill)

// Accessing Theme Tokens
CoreTheme.colors.primary
CoreTheme.typography.titleMedium
CoreTheme.spacing.md
```

---

### 4.5 UI Component Library (23+ Production Components)

#### A. Modern Simple Text Fields (External Top Label + Placeholder + Unbroken Border)

```kotlin
// 1. Simple Email Field (Matching modern web/mobile design)
CoreSimpleEmailField(
    value = email,
    onValueChange = { email = it },
    label = "Email Address",
    placeholder = "Enter your email",
    isRequired = true,
    error = if (isEmailInvalid) "Invalid email" else null
)

// 2. Simple Password Field (With built-in eye toggle)
CoreSimplePasswordField(
    value = password,
    onValueChange = { password = it },
    label = "Password",
    placeholder = "Enter your password",
    isRequired = true
)

// 3. OTP Verification Input
CoreOtpField(
    otpValue = otp,
    onOtpChange = { otp = it },
    length = 6
)
```

#### B. Buttons & Actions
```kotlin
// Button with interactive loading spinner
CoreButton(
    text = "Confirm Order",
    onClick = { viewModel.confirm() },
    isLoading = state.isLoading,
    enabled = !state.isLoading
)

// Danger Button
CoreDangerButton(
    text = "Delete Account",
    onClick = { showDeleteDialog = true }
)
```

#### C. Dialogs & Overlays
```kotlin
CoreConfirmDialog(
    visible = showDialog,
    title = "Cancel Subscription",
    message = "Are you sure you want to cancel your subscription?",
    confirmText = "Yes, Cancel",
    isDanger = true,
    onConfirm = { viewModel.cancelSubscription() },
    onDismiss = { showDialog = false }
)
```

---

### 4.6 Modern Form Architecture & Declarative Validation

Form management in Compose without spaghetti code:

```kotlin
@Composable
fun RegistrationForm() {
    val nameField = remember {
        FormFieldState(
            initialValue = "",
            validators = listOf(
                RequiredValidator("Name is required"),
                MinLengthValidator(3, "Must be at least 3 characters")
            )
        )
    }

    val emailField = remember {
        FormFieldState(
            initialValue = "",
            validators = listOf(
                RequiredValidator("Email is required"),
                EmailValidator("Enter a valid email address")
            )
        )
    }

    val form = rememberCoreForm(nameField, emailField)

    Column(modifier = Modifier.padding(CoreSpacing.md)) {
        CoreSimpleTextField(
            value = nameField.value,
            onValueChange = nameField::onValueChange,
            label = "Full Name",
            placeholder = "e.g. John Doe",
            error = nameField.error,
            isRequired = true
        )

        Spacer(modifier = Modifier.height(CoreSpacing.sm))

        CoreSimpleEmailField(
            value = emailField.value,
            onValueChange = emailField::onValueChange,
            error = emailField.error,
            isRequired = true
        )

        Spacer(modifier = Modifier.height(CoreSpacing.lg))

        CoreButton(
            text = "Create Account",
            onClick = {
                if (form.validate()) {
                    // All fields are valid!
                    viewModel.register(nameField.value, emailField.value)
                }
            }
        )
    }
}
```

---

### 4.7 Lists, Swipe-to-Dismiss & Paging 3 Integration

```kotlin
// 1. Swipe to dismiss list
CoreSwipeToDismissBox(
    onDismiss = { viewModel.deleteItem(item) },
    backgroundContent = { /* Red delete background */ }
) {
    ItemCard(item)
}

// 2. Paging 3 Infinite Scroll Column
val pagedItems = viewModel.pagedOrders.collectAsLazyPagingItems()

CorePagingColumn(
    items = pagedItems,
    itemKey = { it.id },
    emptyContent = { CoreEmptyState(title = "No orders found") },
    errorContent = { CoreErrorState(title = "Failed to load orders") }
) { order ->
    OrderRow(order)
}
```

---

### 4.8 Compose Permissions & Media Picker

```kotlin
// 1. Camera & Storage Permissions with Rationale Dialog
val permissionManager = rememberCorePermissionManager(
    permissions = listOf(Manifest.permission.CAMERA),
    onGranted = { openCamera() },
    onDenied = { showToast("Camera permission denied") }
)

CoreButton(text = "Scan QR", onClick = { permissionManager.launch() })

// 2. Media Picker (Camera, Single Image, Multi Image, PDF/Docs)
val mediaPicker = rememberCoreMediaPicker { uris ->
    uris.firstOrNull()?.let { uri -> viewModel.uploadImage(uri) }
}

CoreButton(text = "Choose Photo", onClick = { mediaPicker.pickSingleImage() })
```

---

### 4.9 Responsive Layouts & Window Size Classes

Build adaptive UIs for Phones, Foldables, and Tablets:

```kotlin
val windowSize = rememberCoreWindowSizeClass()

ResponsiveLayout(
    compact = {
        // Phone Layout (Single Column with Bottom Navigation)
        PhoneDashboard()
    },
    medium = {
        // Foldable / Small Tablet (Two Panes)
        TabletDualPaneDashboard()
    },
    expanded = {
        // Desktop / Large Tablet (Navigation Rail + Split Pane)
        DesktopSplitDashboard()
    }
)
```

---

### 4.10 Type-Safe Navigation Architecture

```kotlin
val navController = rememberNavController()

CoreNavHost(
    navController = navController,
    startDestination = "users"
) {
    coreComposable("users") {
        UsersScreen(onUserClick = { userId ->
            navController.navigate("user/$userId")
        })
    }

    coreComposable(
        route = "user/{userId}",
        arguments = listOf(navArgument("userId") { type = NavType.LongType })
    ) { backStackEntry ->
        val userId = backStackEntry.arguments?.getLong("userId") ?: 0L
        UserDetailScreen(userId = userId)
    }
}
```

---

### 4.11 Resource Decoupling (CoreText & CoreDrawable)

Keep your ViewModels 100% independent of Android `Context` for easy unit testing:

```kotlin
// In ViewModel:
val welcomeMessage: CoreText = CoreText.Resource(R.string.welcome_user, "Alex")
val icon: CoreDrawable = CoreDrawable.Resource(R.drawable.ic_check)

// In Compose:
Text(text = welcomeMessage.asString())
```

---

## 5. Complete Real-World Recipes

### Recipe 1: Full Authentication Flow (XML vs Compose)

#### In XML (`LoginActivity.kt`):
```kotlin
class LoginActivity : BaseActivity<ActivityLoginBinding>() {
    private val viewModel by viewModels<LoginViewModel>()

    override fun inflateBinding() = ActivityLoginBinding.inflate(layoutInflater)

    override fun setupListeners() {
        binding.btnLogin.setOnDebouncedClickListener {
            viewModel.login(binding.etEmail.textValue(), binding.etPassword.textValue())
        }
    }

    override fun observeData() {
        observeBaseEvents(viewModel) // Handles loading dialog & errors
    }
}
```

#### In Jetpack Compose (`LoginScreen.kt`):
```kotlin
@Composable
fun LoginScreen(
    viewModel: LoginComposeViewModel = viewModel(),
    onNavigateHome: () -> Unit
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            LoginEffect.NavigateHome -> onNavigateHome()
            is LoginEffect.ShowToast -> { /* show toast */ }
        }
    }

    Column(modifier = Modifier.padding(CoreSpacing.md)) {
        CoreSimpleEmailField(
            value = state.email,
            onValueChange = { viewModel.onAction(LoginAction.UpdateEmail(it)) },
            isRequired = true
        )
        Spacer(modifier = Modifier.height(CoreSpacing.sm))
        CoreSimplePasswordField(
            value = state.password,
            onValueChange = { viewModel.onAction(LoginAction.UpdatePassword(it)) },
            isRequired = true
        )
        Spacer(modifier = Modifier.height(CoreSpacing.lg))
        CoreButton(
            text = "Log In",
            isLoading = state.isLoading,
            onClick = { viewModel.onAction(LoginAction.Submit) }
        )
    }
}
```

---

## 6. Troubleshooting & ProGuard

### ProGuard / R8
Consumer rules are bundled automatically in both `:core` and `:compose-core`. If using R8 full mode, ensure the following in your app's `proguard-rules.pro`:

```proguard
# Keep Retrofit data models
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep ViewBinding generated classes
-keep class * implements androidx.viewbinding.ViewBinding { *; }

# Keep Room database schemas
-keep class * extends androidx.room.RoomDatabase
```

### Dependency Resolution
If JitPack fails to resolve, add the following to `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

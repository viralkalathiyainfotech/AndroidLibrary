# AndroidCoreLibrary

[![JitPack](https://jitpack.io/v/viralkalathiyainfotech/AndroidLibrary.svg)](https://jitpack.io/#viralkalathiyainfotech/AndroidLibrary)
[![Version](https://img.shields.io/badge/version-2.1.0-blue.svg)](https://semver.org)
[![Platform](https://img.shields.io/badge/platform-Android-green.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0+-purple.svg)](https://kotlinlang.org)
[![MinSdk](https://img.shields.io/badge/minSdk-24-orange.svg)](https://developer.android.com)
[![TargetSdk](https://img.shields.io/badge/targetSdk-37-red.svg)](https://developer.android.com)

A production-ready, modular, and reusable Android core library engineered in Kotlin. Designed following **Clean Architecture** and **SOLID** principles, `AndroidCoreLibrary` provides battle-tested foundational building blocks for modern Android applications without application-specific business logic or tight coupling.

> [!TIP]
> 📚 **Complete Documentation & Guides:**
> - **[Master Developer Guide](docs/COMPLETE_DOCUMENTATION.md)** – Comprehensive manual covering `:core` and `:compose-core`, design system, forms, and recipes.
> - **[Networking & API Calls Guide](docs/API_CALL_GUIDE.md)** – Step-by-step guide for Simple 1-line calls, `launchApi()`, custom loading/error states, and `observeData()`.

---

## Table of Contents

1. [Architecture & Philosophy](#1-architecture--philosophy)
2. [Module Structure](#2-module-structure)
3. [Installation & Setup](#3-installation--setup)
4. [Gradle Configuration & Version Catalog](#4-gradle-configuration--version-catalog)
5. [BaseActivity](#5-baseactivity)
6. [BaseFragment](#6-basefragment)
7. [BaseDialog & BaseBottomSheetDialog](#7-basedialog--basebottomsheetdialog)
8. [BaseViewModel & Coroutines](#8-baseviewmodel--coroutines)
9. [Networking Architecture & Retrofit](#9-networking-architecture--retrofit)
10. [Safe API Calls & NetworkResult](#10-safe-api-calls--networkresult)
11. [Authentication & Refresh Token Lifecycle](#11-authentication--refresh-token-lifecycle)
12. [Room Database & BaseDao](#12-room-database--basedao)
13. [Offline-First Repository Pattern](#13-offline-first-repository-pattern)
14. [Jetpack DataStore Preferences](#14-jetpack-datastore-preferences)
15. [RecyclerView & BaseListAdapter](#15-recyclerview--baselistadapter)
16. [Pagination (Paging 3)](#16-pagination-paging-3)
17. [Error Handling & ErrorMapper](#17-error-handling--errormapper)
18. [Network Monitoring](#18-network-monitoring)
19. [Logging & Security](#19-logging--security)
20. [Utility Toolkit](#20-utility-toolkit)
21. [Unit Testing Suite](#21-unit-testing-suite)
22. [ProGuard & R8 Optimization](#22-proguard--r8-optimization)
23. [Standalone Architecture (No ViewModel)](#23-standalone-architecture-no-viewmodel)
24. [Modern Permission Manager](#24-modern-permission-manager)
25. [File / Image Upload & Multipart Helper](#25-file--image-upload--multipart-helper)
26. [Unified Single-Call API Architecture](#26-unified-single-call-api-architecture)
27. [BaseViewModel Single-Line API Architecture](#27-baseviewmodel-single-line-api-architecture)
28. [Developer Ergonomics Toolkit (Navigation, Search, Dialogs, Pagination)](#28-developer-ergonomics-toolkit)
29. [Jetpack Compose Core Library (:compose-core)](#29-jetpack-compose-core-library-compose-core)
    - [Architecture & MVI Unidirectional Data Flow](#compose-mvi-architecture)
    - [Base Components & Screens](#compose-base-components)
    - [Design Tokens & Theme Engine](#compose-theme--design-tokens)
    - [Production UI Components Catalog](#compose-ui-components)
    - [Form Architecture & Declarative Validation](#compose-form-architecture)
    - [Responsive Layouts & Window Size Classes](#compose-responsive--adaptive)
    - [Permissions & Media Picker](#compose-permissions--media)
    - [Compose Navigation Engine](#compose-navigation)
    - [Migration Guide from View-Based Core](#compose-migration-guide)

---

## 1. Architecture & Philosophy

`AndroidCoreLibrary` strictly separates concerns across architectural layers:

```
┌─────────────────────────────────────────────────────────┐
│               PRESENTATION LAYER                        │
│   BaseActivity • BaseFragment • BaseDialog • UI State   │
│   BaseComposeActivity • BaseComposeViewModel • Compose  │
└───────────────────────────┬─────────────────────────────┘
                            │ observes StateFlow & Events
┌───────────────────────────▼─────────────────────────────┐
│                 VIEWMODEL LAYER                         │
│       BaseViewModel • Coroutines • ExceptionHandler     │
│   BaseComposeViewModel • UiAction • UiEvent • UiEffect  │
└───────────────────────────┬─────────────────────────────┘
                            │ requests data streams
┌───────────────────────────▼─────────────────────────────┐
│                REPOSITORY LAYER                         │
│   BaseRepository • networkBoundResource • Offline-First │
└───────────────────────────┬─────────────────────────────┘
                            │ orchestrates sources
              ┌─────────────┴─────────────┐
              ▼                           ▼
┌───────────────────────────┐   ┌─────────────────────────┐
│     REMOTE DATA SOURCE    │   │    LOCAL DATA SOURCE    │
│ Retrofit • OkHttp • Auth  │   │  Room • BaseDao • Flow  │
└───────────────────────────┘   └─────────────────────────┘
```

### Core Tenets:
- **Zero Business Logic in Core**: Generic types (`T`, `VB : ViewBinding`, `ViewState`) throughout.
- **Lifecycle Safety**: Guaranteed cleanup of ViewBinding references in Fragment lifecycles, lifecycle-aware StateFlow collection in Compose with `collectAsStateWithLifecycle()`.
- **Structured Concurrency**: Driven by `viewModelScope` and `repeatOnLifecycle`. No `GlobalScope`.
- **Offline-First Reactive Flow**: Data flows from Local DB Cache $\rightarrow$ Remote Sync $\rightarrow$ DB Update $\rightarrow$ UI State.
- **Interoperability**: Seamless bridging between `:core` (Room, Retrofit, Datastore, NetworkMonitor, AppError) and `:compose-core` (MVI, Material 3, Adaptive UI).

---

## 2. Module Structure

```
AndroidLibrary/
├── gradle/
│   └── libs.versions.toml             # Centralized version catalog
├── core/                              # Standalone Reusable Core Library (:core)
│   ├── build.gradle.kts
│   ├── consumer-rules.pro             # Bundled consumer ProGuard / R8 rules
│   └── src/
│       ├── main/
│       │   ├── java/com/vc/androidcore/
│       │   │   ├── adapter/           # BaseRecyclerAdapter, BaseListAdapter, DiffUtilItemCallback
│       │   │   ├── base/              # BaseActivity, BaseFragment, BaseDialog, BaseBottomSheetDialog, BaseViewModel
│       │   │   ├── config/            # CoreConfig, CoreLibrary
│       │   │   ├── database/          # BaseDao, DatabaseProvider, RoomRepository, converters/
│       │   │   ├── di/                # CoreModule
│       │   │   ├── error/             # AppError, ErrorMapper
│       │   │   ├── logging/           # CoreLogger
│       │   │   ├── network/           # ApiService, RetrofitProvider, OkHttpProvider, NetworkResult,
│       │   │   │                      # SafeApiCall, NetworkMonitor, TokenProvider, interceptors/
│       │   │   ├── pagination/        # BasePagingSource, PaginationState
│       │   │   ├── preferences/       # DataStoreManager, PreferenceManager
│       │   │   ├── repository/        # BaseRepository, RepositoryResult
│       │   │   ├── state/             # UiState, LoadingState, ErrorState, UiEvent
│       │   │   ├── ui/dialog/         # LoadingDialog
│       │   │   └── utils/             # Extensions for View, Context, Date, String, Keyboard, etc.
│       │   └── res/                   # Base drawables, dialog layouts, colors, styles
│       └── test/                      # Comprehensive Unit Test Suite (27 tests)
├── compose-core/                      # Jetpack Compose UI & Architecture Foundation (:compose-core)
│   ├── build.gradle.kts
│   ├── consumer-rules.pro             # ProGuard rules for Compose runtime & models
│   └── src/
│       ├── main/java/com/vc/composecore/
│       │   ├── base/                  # BaseComposeActivity, BaseComposeViewModel, BaseScreens, CoreStateScreen
│       │   ├── state/                 # UiAction, UiEvent, UiEffect, ViewState, ScreenState, CollectEffect
│       │   ├── theme/                 # CoreTokens (spacing, radius, elevation), CoreColors, CoreTheme
│       │   ├── components/            # 23 atomic & composite UI components (buttons, dialogs, sheets, etc.)
│       │   ├── forms/                 # FormFieldState, CoreFormState, 12+ built-in validators
│       │   ├── lists/                 # CoreLazyColumn, CoreLazyRow, SwipeToDismiss, Reorderable
│       │   ├── paging/                # CorePagingColumn, PagingStatusIndicator, PagingLoadStateMapper
│       │   ├── permissions/           # CorePermissionManager, PermissionRationaleDialog
│       │   ├── media/                 # CoreMediaPicker (Camera, Gallery, Multiple, Documents)
│       │   ├── responsive/            # WindowSizeClass, ResponsiveLayout, AdaptiveScaffold, Breakpoints
│       │   ├── navigation/            # CoreNavHost, CoreRoute, SafeArguments, Animated Transitions
│       │   └── resources/             # CoreText, CoreDrawable (decouples ViewModel from Android resources)
│       └── test/                      # Compose Core Unit Tests (ViewModels, Forms, Tokens)
├── compose-sample/                    # Comprehensive Compose Catalog & Feature Showcase (:compose-sample)
│   ├── src/main/java/com/vc/composesample/
│   │   ├── ui/ComponentShowcaseScreen.kt # Interactive visual catalog of all 23 components
│   │   ├── ui/screens/                # Forms, Lists, Paging, Adaptive Layouts, Dialogs
│   │   └── data/                      # Repositories & Mock Data
├── sample/                            # Reference Implementation with XML & MVVM (:sample)
│   ├── src/main/java/com/vc/sample/
│   │   ├── data/                      # UserApiService, AppDatabase, UserDao, UserRepository
│   │   ├── ui/                        # LoginActivity, HomeActivity, UserAdapter, BottomSheet
│   │   └── SampleApplication.kt       # Application setup & DI wiring
├── standalone/                        # Direct API Architecture without ViewModel (:standalone)
│   └── src/main/java/com/vc/standalone/
│       ├── data/                      # StandaloneApiService, StandaloneDatabase, StandaloneUserDao
│       └── ui/                        # StandaloneActivity, StandaloneUserAdapter, UserDetailBottomSheet
└── app/                               # Lightweight application shell (:app)
```

---

## 3. Installation & Setup

### Via JitPack (Recommended)

#### Step 1: Add JitPack repository
In your root `settings.gradle.kts` (or root `build.gradle`):

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

*Or in Groovy (`settings.gradle` / `build.gradle`):*
```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

#### Step 2: Add Dependency
In your app module's `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.viralkalathiyainfotech.AndroidLibrary:core:2.1.0")
}
```

*Or in Groovy (`build.gradle`):*
```groovy
dependencies {
    implementation 'com.github.viralkalathiyainfotech.AndroidLibrary:core:2.1.0'
}
```

---

### Via Local Project Module
1. Copy the `core` folder into your Android project root.
2. In your `settings.gradle.kts`, include `:core`:
   ```kotlin
   include(":core")
   ```
3. In your app module's `build.gradle.kts`, add:
   ```kotlin
   dependencies {
       implementation(project(":core"))
   }
   ```

---

### Application Initialization
Initialize the library in your `Application.onCreate()`:
```kotlin
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        CoreLibrary.initialize(
            context = this,
            config = CoreConfig(
                enableLogging = BuildConfig.DEBUG,
                enableNetworkLogging = BuildConfig.DEBUG,
                defaultTimeout = 30L,
                baseUrl = "https://api.yourdomain.com/"
            )
        )
    }
}
```

---

## 4. Gradle Configuration & Version Catalog

The project utilizes `gradle/libs.versions.toml` to centralize dependency versions:

```toml
[versions]
agp = "9.4.0"
lifecycle = "2.8.7"
coroutines = "1.9.0"
retrofit = "2.11.0"
okhttp = "4.12.0"
room = "2.6.1"
datastore = "1.1.1"
paging = "3.3.2"
coil = "2.7.0"

[libraries]
androidx-lifecycle-viewmodel-ktx = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-ktx", version.ref = "lifecycle" }
retrofit = { group = "com.squareup.retrofit2", name = "retrofit", version.ref = "retrofit" }
room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
datastore-preferences = { group = "androidx.datastore", name = "datastore-preferences", version.ref = "datastore" }
```

---

## 5. BaseActivity

`BaseActivity<VB : ViewBinding>` simplifies boilerplate, manages ViewBinding automatically, and exposes lifecycle-safe coroutine collectors:

```kotlin
class MainActivity : BaseActivity<ActivityMainBinding>() {

    private val viewModel: MainViewModel by viewModels()

    override fun inflateBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun setupUI() {
        setupToolbar(binding.toolbar, title = "Dashboard", displayHomeAsUp = false)
    }

    override fun setupListeners() {
        binding.btnSubmit.setOnDebouncedClickListener {
            hideKeyboard()
            viewModel.submit()
        }
    }

    override fun observeData() {
        // Automatically handles loading spinner, error snackbars, and one-time events
        observeBaseEvents(viewModel)

        // Lifecycle-safe flow collector (only active when Lifecycle is at least STARTED)
        collectLifecycleFlow(viewModel.userData) { user ->
            binding.tvName.text = user.name
        }
    }
}
```

---

## 6. BaseFragment

`BaseFragment<VB : ViewBinding>` guarantees memory safety by clearing `_binding` in `onDestroyView()`:

```kotlin
class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProfileBinding {
        return FragmentProfileBinding.inflate(inflater, container, false)
    }

    override fun setupUI() {
        // UI initialization
    }

    override fun setupListeners() {
        binding.btnSave.setOnDebouncedClickListener {
            showToast("Profile saved!")
        }
    }
}
```

---

## 7. BaseDialog & BaseBottomSheetDialog

### BaseDialog
Customizable modal dialogs with transparent backgrounds and dimension controls:
```kotlin
class ConfirmDialog : BaseDialog<DialogConfirmBinding>() {
    override val widthPercent: Float = 0.85f

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): DialogConfirmBinding {
        return DialogConfirmBinding.inflate(inflater, container, false)
    }

    override fun setupListeners() {
        binding.btnOk.setOnClickListener { dismiss() }
    }
}
```

### BaseBottomSheetDialog
Expanded BottomSheet modal dialogs:
```kotlin
class UserDetailBottomSheet(private val user: User) : BaseBottomSheetDialog<BottomSheetUserDetailBinding>() {
    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): BottomSheetUserDetailBinding {
        return BottomSheetUserDetailBinding.inflate(inflater, container, false)
    }

    override fun setupUI() {
        binding.tvName.text = user.name
    }
}
```

---

## 8. BaseViewModel & Coroutines

Provides structured concurrency, exception handling, and decoupled UI event delivery:

```kotlin
class UserViewModel(private val repository: UserRepository) : BaseViewModel() {

    private val _userState = MutableStateFlow<UiState<User>>(UiState.Idle)
    val userState: StateFlow<UiState<User>> = _userState.asStateFlow()

    fun fetchUser(id: Int) {
        launchSafe(showLoading = true) {
            executeApiCall(
                apiCall = { repository.getUser(id) },
                onSuccess = { user ->
                    _userState.value = UiState.Success(user)
                    sendEvent(UiEvent.ShowToast("User loaded successfully"))
                },
                onError = { appError ->
                    _userState.value = UiState.Error(appError.userMessage)
                }
            )
        }
    }
}
```

---

## 9. Networking Architecture & Retrofit

> [!TIP]
> 📖 **Complete Step-by-Step Guide Available**:  
> For an in-depth walkthrough on simple 1-line calls, `BaseViewModel.launchApi()`, custom screen-level loading, error handling, and `observeData()` event handling, see the [Networking & API Call Complete Guide](docs/API_CALL_GUIDE.md).

### RetrofitProvider
Creates flexible, isolated Retrofit instances:
```kotlin
val retrofit = RetrofitProvider.builder()
    .baseUrl("https://api.example.com/")
    .client(customOkHttpClient)
    .build()

// Or direct interface creation
val apiService = RetrofitProvider.createService<UserApiService>("https://api.example.com/")
```

### OkHttpProvider
Configures timeouts, custom headers, and security:
```kotlin
val client = OkHttpProvider.builder()
    .connectTimeout(20L)
    .readTimeout(20L)
    .tokenProvider(tokenProvider, refreshTokenProvider)
    .headers(mapOf("X-App-Version" to "1.0.0"))
    .logging(BuildConfig.DEBUG)
    .build()
```

---

## 10. Safe API Calls & NetworkResult

`safeApiCall` executes network requests and handles status codes (400, 401, 403, 404, 408, 422, 429, 500, 502, 503) and network failures:

```kotlin
suspend fun fetchUsers(): NetworkResult<List<UserDto>> {
    return safeApiCall { apiService.getUsers() }
}

// Processing results:
when (val result = fetchUsers()) {
    is NetworkResult.Success -> updateUi(result.data)
    is NetworkResult.Error -> showError(result.message, result.appError)
    is NetworkResult.Loading -> showSpinner()
}
```

---

## 11. Authentication & Refresh Token Lifecycle

Authentication is decoupled via `TokenProvider` and `RefreshTokenProvider` interfaces:

```kotlin
val tokenProvider = object : TokenProvider {
    override fun getAccessToken(): String? = dataStore.getString("token")
}

val refreshProvider = object : RefreshTokenProvider {
    override suspend fun refreshToken(): String? {
        // Fetch new token synchronously within mutex
        return newAccessToken
    }

    override fun onTokenExpired() {
        // Notify user to login
    }
}
```

`AuthInterceptor` automatically injects `Authorization: Bearer <token>` and performs thread-safe token refresh on HTTP 401.

---

## 12. Room Database & BaseDao

### BaseDao
Includes ready-to-use CRUD functions:
```kotlin
@Dao
interface UserDao : BaseDao<UserEntity> {
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>
}
```

### DatabaseProvider
Builds Room database instances safely:
```kotlin
val database = DatabaseProvider.builder(
    context = context,
    klass = AppDatabase::class.java,
    databaseName = "app_database.db"
)
.addMigrations(MIGRATION_1_2)
.fallbackToDestructiveMigration(false) // Safe default
.build()
```

---

## 13. Data Architecture & Separation of Concerns

The sample module explicitly separates **Remote API**, **Local Room Database**, and **Combined Offline-First Sync** into clean, independent methods:

### 1. Isolated Remote API Only (No Room DB)
```kotlin
// Standalone network call returning NetworkResult
suspend fun fetchUsersFromRemote(): NetworkResult<List<User>> {
    val result = executeApiCall { apiService.getUsers() }
    return result.map { dtoList -> dtoList.map { it.toDomain() } }
}
```

### 2. Isolated Local Room Database Only (No Network Calls)
```kotlin
// Reactive query from Room
fun getUsersFromLocal(): Flow<List<User>> = userDao.getUsersFlow().map { entities ->
    entities.map { it.toDomain() }
}

// One-shot query from Room
suspend fun getUsersFromLocalOnce(): List<User> = userDao.getUsersOnce().map { it.toDomain() }

// Direct save to Room
suspend fun saveUsersToLocal(users: List<User>) = userDao.insertAll(users.map { it.toEntity() })

// Clear Room
suspend fun clearLocalUsers() = userDao.clearUsers()
```

### 3. Combined Offline-First Pipeline (API ➔ Room DB ➔ UI List)
Demonstrates the full enterprise offline-first pipeline:
1. **Step 1**: Call remote API (`fetchUsersFromRemote()`)
2. **Step 2**: Save response into local Room Database (`saveUsersToLocal()`)
3. **Step 3**: Query persisted data from Room Database (`getUsersFromLocal()`)
4. **Step 4**: Display Room data reactively in the RecyclerView List

```kotlin
fun syncApiToDatabaseAndObserve(): Flow<UiState<List<User>>> = flow {
    emit(UiState.Loading)
    
    // Step 1: Call Remote API
    when (val apiResult = fetchUsersFromRemote()) {
        is NetworkResult.Success -> {
            // Step 2: Save API response into Room Database
            saveUsersToLocal(apiResult.data)

            // Step 3 & 4: Retrieve from Room Database and emit to UI List
            emitAll(getUsersFromLocal().map { UiState.Success(it) })
        }
        is NetworkResult.Error -> {
            // Fallback to cached Room DB data on network error
            val cached = getUsersFromLocalOnce()
            if (cached.isNotEmpty()) emit(UiState.Success(cached))
            else emit(UiState.Error(apiResult.message))
        }
    }
}.flowOn(ioDispatcher)
```

In the sample app UI, interactive toggle buttons allow testing each workflow in isolation:
- **`Sync (API ➔ Room ➔ List)`**: Runs the full 4-step pipeline with real-time status logging.
- **`API Only`**: Bypasses Room and renders API results directly into the list.
- **`Room DB Only`**: Bypasses Network and renders cached Room records directly into the list.
- **`Clear DB`**: Wipes Room records to test empty state and offline fallback.


---

## 14. Jetpack DataStore Preferences

`DataStoreManager` provides type-safe key-value persistence:

```kotlin
val dataStore = DataStoreManager(context)

// Suspend writes
dataStore.putString("jwt_token", token)
dataStore.putInt("user_id", 42)

// Suspend reads
val token = dataStore.getString("jwt_token")

// Reactive flows
val tokenFlow: Flow<String?> = dataStore.getStringFlow("jwt_token")

// Wipe all data
dataStore.clearAll()
```

---

## 15. RecyclerView & BaseListAdapter

### BaseListAdapter
Uses `DiffUtil` and `AsyncListDiffer` to render lists with zero manual index management:

```kotlin
class UserAdapter : BaseListAdapter<User, ItemUserBinding>(
    DiffUtilItemCallback.create(
        areItemsTheSame = { old, new -> old.id == new.id },
        areContentsTheSame = { old, new -> old == new }
    )
) {
    override fun createBinding(parent: ViewGroup): ItemUserBinding {
        return ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun bind(binding: ItemUserBinding, item: User, position: Int) {
        binding.tvName.text = item.name
        binding.ivAvatar.loadImage(item.avatarUrl, isCircle = true)
    }
}
```

---

## 16. Pagination (Paging 3)

`BasePagingSource<Key, Value>` encapsulates paging boilerplate:

```kotlin
val pagingSource = BasePagingSource<Int, User>(
    initialKey = 1,
    fetchData = { page, pageSize -> api.getUsers(page, pageSize) },
    nextKeyProvider = { items, currentPage -> if (items.isEmpty()) null else currentPage + 1 }
)
```

---

## 17. Error Handling & ErrorMapper

Translates any exception (`IOException`, `SocketTimeoutException`, `HttpException`, `JsonSyntaxException`) into structured domain errors:

```kotlin
sealed class AppError {
    data object Network : AppError()
    data object Timeout : AppError()
    data object Unauthorized : AppError()
    data object Forbidden : AppError()
    data object NotFound : AppError()
    data class Server(val code: Int, val message: String?) : AppError()
    data class Validation(val message: String?) : AppError()
    data class Unknown(val message: String?, val throwable: Throwable?) : AppError()
}
```

---

## 18. Network Monitoring

Reactive connectivity tracker via Android's modern `ConnectivityManager.NetworkCallback`:

```kotlin
val networkMonitor = LiveNetworkMonitor(context)

// Real-time StateFlow
lifecycleScope.launch {
    networkMonitor.isOnline.collect { isOnline ->
        if (isOnline) hideOfflineBanner() else showOfflineBanner()
    }
}

// Instant synchronous check
val onlineNow = networkMonitor.isCurrentlyOnline()
```

---

## 19. Logging & Security

`CoreLogger` safely logs events in debug builds while scrubbing sensitive data (passwords, tokens, authorization headers):

```kotlin
CoreLogger.d("User authenticated with token=eyJhbGciOi...")
// Output: User authenticated with token: [REDACTED]
```

---

## 20. Utility Toolkit

| Utility | Description | Example |
|---|---|---|
| **View Extensions** | Visibility & debounced clicks | `view.visible()`, `view.gone()`, `view.setOnDebouncedClickListener { }` |
| **Keyboard Utilities** | Inset-based keyboard controls | `activity.hideKeyboard()`, `activity.showKeyboard(editText)` |
| **Date Utilities** | Modern `java.time` formatting | `DateUtils.formatDate(millis)`, `DateUtils.isToday(millis)` |
| **String Utilities** | Validation & masking | `email.isValidEmail()`, `phone.maskPhone()`, `text.capitalizeFirst()` |
| **Debounce Utilities** | Search query debounce | `editText.onDebouncedQueryChange(scope) { query -> ... }` |
| **Image Loading** | Coil wrapper | `imageView.loadImage(url, isCircle = true)` |
| **Resource Utils** | Compatibility resource access | `context.getColorCompat(R.color.accent)` |

---

## 21. Unit Testing Suite

The library includes an exhaustive JUnit + MockK + Turbine test suite in `core/src/test/`:

- `BaseViewModelTest`: Tests coroutine scopes, loading state emissions, and exception capturing.
- `SafeApiCallTest`: Tests 200 OK, 401 Unauthorized, 500 Internal Server Error, and timeout handling.
- `RepositoryTest`: Tests the offline-first `networkBoundResource` execution, local caching, and fallback.
- `ErrorMapperTest`: Tests exception-to-domain mapping.
- `NetworkResultTest`: Tests state transformations and mapping.
- `DateUtilsTest`: Tests date formatting, comparison, and day counting.
- `StringUtilsTest`: Tests input validation, masking, and null-safety.

Run the test suite using:
```bash
./gradlew :core:testDebugUnitTest
```

---

## 22. ProGuard & R8 Optimization

`core/consumer-rules.pro` is automatically exported with the AAR:
- Preserves Retrofit reflection annotations.
- Preserves ViewBinding inflated constructors.
- Preserves Room entities and DAO queries.
- Preserves OkHttp and Coroutines obfuscation safety.
- Excludes dead code in consumer release builds.

---

## 23. Direct API Calls Without ViewModel (`:standalone`)

The `:standalone` module demonstrates making network calls, database queries, and preference reads/writes directly from a `BaseActivity` **without declaring a ViewModel**, while actively leveraging all `:core` common classes:

```kotlin
class StandaloneActivity : BaseActivity<ActivityStandaloneBinding>() {

    // 1. Direct Retrofit service via Core's RetrofitProvider
    private val apiService = RetrofitProvider.createService<StandaloneApiService>("https://api.example.com/")

    // 2. Direct Room database via Core's DatabaseProvider
    private val database = DatabaseProvider.builder(this, StandaloneDatabase::class.java, "users.db")
        .fallbackToDestructiveMigration(true)
        .build()

    // 3. Direct Preferences via Core's DataStoreManager
    private val dataStore by lazy { DataStoreManager(applicationContext) }

    // 4. Direct Network Connectivity via Core's LiveNetworkMonitor
    private val networkMonitor by lazy { LiveNetworkMonitor(applicationContext) }

    override fun inflateBinding() = ActivityStandaloneBinding.inflate(layoutInflater)

    private fun fetchUsersDirectly() {
        // Pre-check connectivity synchronously
        if (!networkMonitor.isCurrentlyOnline()) {
            showSnackbar("No internet connection")
            return
        }

        // Direct lifecycleScope coroutine execution
        lifecycleScope.launch {
            showLoading("Fetching users directly...")

            // Safe API call handles exceptions, timeouts, and status codes safely
            val result = withContext(Dispatchers.IO) {
                safeApiCall { apiService.getUsers() }
            }

            hideLoading()

            when (result) {
                is NetworkResult.Success -> {
                    // Update UI directly
                    userAdapter.submitList(result.data)
                    showToast("Loaded ${result.data.size} users")
                }
                is NetworkResult.Error -> {
                    // Handle and display error using BaseActivity's error presenter
                    handleAppError(result.appError)
                }
                is NetworkResult.Loading -> {}
            }
        }
    }
}
```

---

## 24. Modern Permission Manager

`BaseActivity` and `BaseFragment` include seamless, boilerplate-free runtime permission requests powered by Android's Activity Result API:

```kotlin
// In any Activity extending BaseActivity or Fragment extending BaseFragment:

// 1. Single permission request
requestPermission(Manifest.permission.CAMERA) { isGranted ->
    if (isGranted) {
        openCamera()
    } else {
        showSnackbar("Camera permission is required", "Settings") {
            openAppSettings() // Redirects to system app settings
        }
    }
}

// 2. Multiple permissions request with granular results
requestPermissions(
    Manifest.permission.CAMERA,
    Manifest.permission.RECORD_AUDIO
) { result ->
    when {
        result.areAllGranted -> startRecording()
        result.hasPermanentlyDenied -> {
            showSnackbar("Permissions permanently denied", "Settings") { openAppSettings() }
        }
        else -> showToast("Permissions denied: ${result.denied.joinToString()}")
    }
}

// 3. Instant synchronous check
val hasCamera = hasPermission(Manifest.permission.CAMERA)
```

---

## 25. File / Image Upload & Multipart Helper

`MultipartHelper` and `ProgressRequestBody` provide a complete toolkit for building multipart payloads from `File`, `Uri`, or `ByteArray` with real-time progress callbacks:

```kotlin
// 1. Create part from local File with progress listener
val filePart = MultipartHelper.createPartFromFile(
    file = imageFile,
    partName = "avatar",
    onProgress = { bytesWritten, totalBytes, percent ->
        progressBar.progress = percent
        tvProgress.text = "Uploading: $percent%"
    }
)

// 2. Create part from content Uri (e.g. from PhotoPicker)
val uriPart = MultipartHelper.createPartFromUri(
    context = this,
    uri = selectedUri,
    partName = "document"
)

// 3. Create part from in-memory ByteArray
val bytePart = MultipartHelper.createPartFromBytes(
    bytes = compressedBytes,
    partName = "photo",
    fileName = "photo.jpg",
    mimeType = "image/jpeg"
)

// 4. Create companion text parameters map
val textParams = MultipartHelper.createPartMap(
    mapOf(
        "userId" to "42",
        "description" to "Profile avatar"
    )
)

// 5. Send with Retrofit
safeApiCall { apiService.uploadProfile(filePart, textParams) }
```

---

## 26. Unified Single-Call API Architecture

Instead of writing 25–40 lines of repetitive coroutine, dialog, dispatching, and error code, make API calls in **3–4 lines** directly from any `BaseActivity` or `BaseFragment`:

### 1. Direct 1-Line API Call (`launchApiCall`)
Automatic network check, loading dialog, `Dispatchers.IO` switching, and standardized error handling:
```kotlin
launchApiCall(
    request = { apiService.getUsers() }
) { users ->
    userAdapter.submitList(users)
}
```

### 2. Auto DTO-to-Domain Mapping (`launchApiCallMapped`)
```kotlin
launchApiCallMapped(
    loadingMessage = "Fetching users...",
    request = { apiService.getUsers() },
    transform = { dtoList -> dtoList.map { it.toDomain() } }
) { domainUsers ->
    userAdapter.submitList(domainUsers)
}
```

### 3. Fluent Kotlin DSL Builder (`executeApi` / `executeApiMapped`)
```kotlin
executeApi<List<UserDto>> {
    request { apiService.getUsers() }
    retry(count = 3)
    loading(message = "Loading users...")
    checkNetwork(check = true, offlineMessage = "Please check internet")
    onSuccess { users ->
        userAdapter.submitList(users)
    }
    onError { error ->
        // Optional custom error handler; defaults to handleAppError(error)
    }
}
```

---

## 27. BaseViewModel Single-Line API Architecture

Make API calls in **1-2 lines** inside any `BaseViewModel` with automatic loading states, error states, and retry support:

```kotlin
class UserViewModel : BaseViewModel() {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    // 1. Direct API call with automatic loadingState & errorState
    fun fetchUsers() = launchApi(
        call = { apiService.getUsers() }
    ) { data ->
        _users.value = data
    }

    // 2. Direct API call with DTO to Domain model mapping & auto-retry
    fun fetchUsersMapped() = launchApiMapped(
        retryCount = 2,
        call = { apiService.getUsers() },
        transform = { dtoList -> dtoList.map { it.toDomain() } }
    ) { domainUsers ->
        _users.value = domainUsers
    }
}
```

---

## 28. Developer Ergonomics Toolkit

A suite of high-productivity extensions eliminating daily boilerplate:

### 1. Instant Search Debounce (`EditText.onSearchQuery`)
```kotlin
binding.etSearch.onSearchQuery(debounceMs = 400L, scope = lifecycleScope) { query ->
    viewModel.search(query)
}
```

### 2. Type-Safe Clean Navigation (`startActivity` / `openActivity`)
```kotlin
// 1. Simplest one-liner (Context, Activity, or Fragment):
startActivity<LoginActivity>()

// 2. Open and finish current screen:
startActivityAndFinish<HomeActivity>()

// 3. Clear task / backstack (e.g. Logout / Splash):
startActivityClearTask<LoginActivity>()

// 4. Pass key-value extras directly:
startActivity<UserDetailActivity>(
    "user_id" to 42,
    "user_name" to "Viral",
    "is_admin" to true
)

// 5. With Intent builder lambda:
startActivity<UserDetailActivity> {
    putExtra("user_id", user.id)
    putExtra("user_name", user.name)
}

// 6. Create Intent only:
val intent = intentOf<LoginActivity>("source" to "notification")

// 7. Finish with Result (Zero boilerplate):
finishWithResultOk("is_updated" to true, "item_id" to 42)
finishWithResultCanceled()

// 8. Property Delegates for Extras & Args (Lazy & Safe):
// In Activity:
private val userId: Int by extra("user_id", -1)
private val userName: String? by extraOrNull("user_name")
// In Fragment:
private val tabId: Int by arg("tab_id", 0)

// 9. Fragment with arguments:
val fragment = newFragment<UserDetailFragment>("user_id" to 42)

// 10. System intents:
openUrl("https://github.com")
shareText("Check out this library!")
dialNumber("+1234567890")
openAppSettings()
openPlayStore()
openWhatsAppChat("+919876543210", "Hello!")
openMapLocation(21.1702, 72.8311, "Surat")
```

### 3. Coil Image Loading Extensions
```kotlin
// Regular loading with placeholder & crossfade
binding.ivProfile.loadImage(user.avatarUrl, placeholderRes = R.drawable.placeholder)

// Circular crop (perfect for avatars)
binding.ivAvatar.loadCircle(user.avatarUrl)

// Rounded corners
binding.ivBanner.loadRounded(bannerUrl, cornerRadiusDp = 12)
```

### 4. Quick Material Dialogs
```kotlin
// Confirmation dialog with actions
showConfirmDialog(
    title = "Delete Record",
    message = "Are you sure you want to delete this record?",
    positiveText = "Delete",
    negativeText = "Cancel",
    onPositive = { deleteItem() }
)

// Alert dialog
showAlertDialog(
    title = "Notice",
    message = "Your session has been updated."
)
```

### 5. RecyclerView Infinite Scroll (`onLoadMore`)
```kotlin
binding.recyclerView.onLoadMore(threshold = 3) { nextPage ->
    viewModel.loadPage(nextPage)
}
```

---

## 29. Jetpack Compose Core Library (`:compose-core`)

The `:compose-core` module is a complete, production-grade Jetpack Compose foundation built on Material 3, modern MVI architecture, and clean separation of concerns. It is designed to be 100% reusable across multiple Android apps without project-specific business logic or hardcoded assets.

### Installation

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

// build.gradle.kts (:app or feature module)
dependencies {
    implementation("com.github.viralkalathiyainfotech.AndroidLibrary:compose-core:compose-1.0.0")
    // Or in multi-module project:
    implementation(project(":compose-core"))
}
```

---

<a name="compose-mvi-architecture"></a>
### 1. Architecture & MVI Unidirectional Data Flow

`:compose-core` adopts a pure **MVI (Model-View-Intent)** architecture with distinct contracts:

```
                  ┌──────────────────────┐
                  │    User Actions /    │
                  │      UiAction        │
                  └──────────┬───────────┘
                             │ onAction(action)
                             ▼
                  ┌──────────────────────┐
                  │ BaseComposeViewModel │
                  └──────┬────────┬──────┘
       _viewState.update │        │ _effect.emit()
                         ▼        ▼
┌──────────────────────────┐    ┌──────────────────────────┐
│   ViewState (StateFlow)   │    │  UiEffect (SharedFlow)   │
│   Immutable Screen State │    │  Single-Shot Events      │
│  (Loading, Content, Data)│    │  (Nav, Toast, SnackBar)  │
└────────────┬─────────────┘    └────────────┬─────────────┘
             │ collectAsStateWithLifecycle   │ CollectEffect
             ▼                               ▼
┌──────────────────────────────────────────────────────────┐
│                      Compose UI                          │
│   BaseStatefulScreen / BaseStatelessScreen / Components  │
└──────────────────────────────────────────────────────────┘
```

#### Contract Interfaces:
- `ViewState`: Marker interface for immutable data representations of the UI.
- `UiAction`: User interactions dispatched from UI to ViewModel (`OnSubmitClicked`, `OnItemSwiped`).
- `UiEvent`: Domain or presentation events.
- `UiEffect`: Single-shot side effects (`NavigateToHome`, `ShowToast`, `OpenUrl`).
- `ScreenState<T>`: Sealed class for standard screen modes (`Initial`, `Loading`, `Success(data)`, `Empty`, `Error(message, throwable)`).

#### Example ViewModel:
```kotlin
data class UsersViewState(
    val users: List<User> = emptyList(),
    val isRefreshing: Boolean = false
) : ViewState

sealed interface UsersAction : UiAction {
    data object Refresh : UsersAction
    data class SelectUser(val user: User) : UsersAction
}

sealed interface UsersEffect : UiEffect {
    data class NavigateToDetails(val userId: Long) : UsersEffect
    data class ShowSnackbar(val message: String) : UsersEffect
}

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val userRepository: UserRepository
) : BaseComposeViewModel<UsersViewState, UsersAction, UsersEffect>(UsersViewState()) {

    init {
        loadUsers()
    }

    override fun onAction(action: UsersAction) {
        when (action) {
            UsersAction.Refresh -> refresh()
            is UsersAction.SelectUser -> sendEffect(UsersEffect.NavigateToDetails(action.user.id))
        }
    }

    private fun loadUsers() {
        launchWithState {
            userRepository.getUsers().collect { result ->
                when (result) {
                    is NetworkResult.Success -> updateState { copy(users = result.data) }
                    is NetworkResult.Error -> sendEffect(UsersEffect.ShowSnackbar(result.error.userFriendlyMessage))
                    else -> Unit
                }
            }
        }
    }
}
```

---

<a name="compose-base-components"></a>
### 2. Base Components & Screens

- **`BaseComposeActivity`**: Base activity handling edge-to-edge layout, system bars, back handling, and theme provisioning automatically.
- **`BaseComposeViewModel`**: Full lifecycle-safe coroutine launchers, state updating with `updateState { copy(...) }`, and effect dispatch with `sendEffect(effect)`.
- **`BaseScreen`**: High-level screen container with customizable top bar, bottom bar, snackbar host, and floating action button.
- **`BaseStatefulScreen` & `BaseStatelessScreen`**: Standardized pattern separating ViewModel-observing containers from pure previewable Composable layouts.
- **`CoreStateScreen`**: Declarative layout switcher rendering loading skeletons, empty states, error retry views, or content based on `ScreenState<T>`.

```kotlin
@Composable
fun UsersScreen(
    viewModel: UsersViewModel = hiltViewModel(),
    onNavigateToDetails: (Long) -> Unit
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()

    // Lifecycle-aware effect collector
    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            is UsersEffect.NavigateToDetails -> onNavigateToDetails(effect.userId)
            is UsersEffect.ShowSnackbar -> { /* show snackbar */ }
        }
    }

    UsersContent(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun UsersContent(
    state: UsersViewState,
    onAction: (UsersAction) -> Unit
) {
    CoreStateScreen(
        state = if (state.users.isEmpty()) ScreenState.Empty() else ScreenState.Success(state.users),
        onRetry = { onAction(UsersAction.Refresh) }
    ) { users ->
        CoreLazyColumn(
            items = users,
            itemKey = { it.id }
        ) { user ->
            UserCard(user = user, onClick = { onAction(UsersAction.SelectUser(user)) })
        }
    }
}
```

---

<a name="compose-theme--design-tokens"></a>
### 3. Design Tokens & Theme Engine

Design tokens enforce a cohesive visual rhythm across spacing, corner radii, elevation, and animation durations:

```kotlin
// Spacing Tokens
CoreSpacing.xxs  // 2.dp
CoreSpacing.xs   // 4.dp
CoreSpacing.sm   // 8.dp
CoreSpacing.md   // 16.dp
CoreSpacing.lg   // 24.dp
CoreSpacing.xl   // 32.dp
CoreSpacing.xxl  // 48.dp

// Corner Radii Tokens
CoreRadius.none  // 0.dp
CoreRadius.small // 4.dp
CoreRadius.medium// 8.dp
CoreRadius.large // 16.dp
CoreRadius.full  // 999.dp (Pill)

// Theme Accessor
CoreTheme.colors.primary
CoreTheme.typography.titleLarge
CoreTheme.spacing.md
```

Wrap your application or screen in `CoreTheme`:
```kotlin
CoreTheme(darkTheme = isSystemInDarkTheme()) {
    // App content
}
```

---

<a name="compose-ui-components"></a>
### 4. Production UI Components Catalog

`:compose-core` ships with 23 atomic and composite UI components:

| Category | Components |
|---|---|
| **Buttons** | `CoreButton`, `CoreOutlinedButton`, `CoreTextButton`, `CoreElevatedButton`, `CoreIconButton`, `CoreFloatingActionButton` |
| **Form Inputs** | `CoreSimpleTextField`, `CoreSimpleEmailField`, `CoreSimplePasswordField`, `CoreSimpleSearchField`, `CoreTextField`, `CoreOutlinedTextField`, `CorePasswordTextField`, `CoreSearchField`, `CoreOtpField` |
| **Selection** | `CoreCheckbox`, `CoreRadioButton`, `CoreSwitch` |
| **Chips** | `CoreChip`, `CoreAssistChip`, `CoreFilterChip`, `CoreInputChip`, `CoreSuggestionChip` |
| **Surfaces & Cards** | `CoreCard`, `CoreElevatedCard`, `CoreOutlinedCard` |
| **Navigation & Bars** | `CoreTopAppBar`, `CoreNavigationBar`, `CoreTabRow`, `CoreScrollableTabRow` |
| **Overlays & Dialogs** | `CoreAlertDialog`, `CoreConfirmDialog`, `CoreLoadingDialog`, `CoreBottomSheet` |
| **Feedback & Status** | `CoreSnackbar`, `CoreCircularProgress`, `CoreLinearProgress`, `CoreSkeletonLoader`, `CoreEmptyState`, `CoreErrorState` |
| **Media & Tooltips** | `CoreAsyncImage`, `CoreAvatar`, `CoreBadge`, `CoreDivider`, `CoreDropdown`, `CoreTooltip` |

```kotlin
// Modern Simple Text Field (Top Label, Placeholder, Unbroken Outline Border)
CoreSimpleEmailField(
    value = email,
    onValueChange = { email = it },
    label = "Email Address",
    placeholder = "Enter your email",
    isRequired = true,
    error = if (isError) "Invalid email address" else null
)

// Interactive Button with Loading State
CoreButton(
    text = "Submit Application",
    onClick = { viewModel.submit() },
    isLoading = state.isLoading,
    enabled = state.isValid
)

// Secure OTP Verification Field
CoreOtpField(
    otpValue = state.otp,
    onOtpChange = { newOtp -> viewModel.updateOtp(newOtp) },
    length = 6,
    isError = state.isOtpInvalid
)

// Confirmation Dialog with Danger Tinting
CoreConfirmDialog(
    visible = showDeleteDialog,
    title = "Delete Account",
    message = "Are you sure? This action cannot be undone.",
    confirmText = "Delete Permanently",
    isDanger = true,
    onConfirm = { viewModel.deleteAccount() },
    onDismiss = { showDeleteDialog = false }
)
```

---

<a name="compose-form-architecture"></a>
### 5. Form Architecture & Declarative Validation

Managing form state and input validation in Compose is declarative and bulletproof:

```kotlin
val emailField = remember {
    FormFieldState(
        initialValue = "",
        validators = listOf(
            RequiredValidator("Email is required"),
            EmailValidator("Enter a valid email address")
        )
    )
}

val passwordField = remember {
    FormFieldState(
        initialValue = "",
        validators = listOf(
            RequiredValidator("Password is required"),
            MinLengthValidator(8, "Password must be at least 8 characters")
        )
    )
}

val formState = rememberCoreForm(emailField, passwordField)

// UI
CoreTextField(
    value = emailField.value,
    onValueChange = emailField::onValueChange,
    error = emailField.error,
    label = "Email Address"
)

CorePasswordTextField(
    value = passwordField.value,
    onValueChange = passwordField::onValueChange,
    error = passwordField.error,
    label = "Password"
)

CoreButton(
    text = "Log In",
    onClick = {
        if (formState.validate()) {
            viewModel.login(emailField.value, passwordField.value)
        }
    }
)
```

#### Built-In Validators:
- `RequiredValidator`
- `EmailValidator`
- `MinLengthValidator` / `MaxLengthValidator`
- `ExactLengthValidator`
- `RegexValidator`
- `NumericValidator`
- `PhoneValidator`
- `UrlValidator`
- `MatchesFieldValidator` (e.g. Confirm Password)
- `CustomValidator`

---

<a name="compose-responsive--adaptive"></a>
### 6. Responsive Layouts & Window Size Classes

Build adaptive multi-device UIs effortlessly with responsive breakpoints:

```kotlin
val windowSizeClass = rememberCoreWindowSizeClass()

ResponsiveLayout(
    compact = {
        // Phone Layout: Single column with bottom navigation
        PhoneContent()
    },
    medium = {
        // Foldable / Small Tablet: Dual-pane or expanded layout
        TabletContent()
    },
    expanded = {
        // Large Tablet / Desktop: Navigation rail with master-detail view
        DesktopSplitContent()
    }
)

// Adaptive Scaffold with automatic BottomBar <-> NavigationRail switching
AdaptiveScaffold(
    topBar = { CoreTopAppBar(title = "Dashboard") },
    navigationItems = navItems,
    selectedItem = selectedRoute,
    onItemSelected = { navigateTo(it) }
) { padding ->
    ScreenContent(modifier = Modifier.padding(padding))
}
```

---

<a name="compose-permissions--media"></a>
### 7. Permissions & Media Picker

Handling runtime permissions and image/document picking without boilerplate:

```kotlin
// Runtime Permissions with automatic Rationale Dialog
val permissionManager = rememberCorePermissionManager(
    permissions = listOf(Manifest.permission.CAMERA),
    onGranted = { openCamera() },
    onDenied = { showDeniedMessage() }
)

CoreButton(
    text = "Scan QR Code",
    onClick = { permissionManager.launch() }
)

// Media Picker (Camera, Gallery, Multi-image, Documents)
val mediaPicker = rememberCoreMediaPicker { uris ->
    uris.firstOrNull()?.let { uri -> viewModel.uploadAvatar(uri) }
}

CoreButton(
    text = "Select Profile Photo",
    onClick = { mediaPicker.pickSingleImage() }
)
```

---

<a name="compose-navigation"></a>
### 8. Compose Navigation Engine

Type-safe navigation routes, arguments, and built-in smooth transitions:

```kotlin
val navController = rememberNavController()

CoreNavHost(
    navController = navController,
    startDestination = "home"
) {
    coreComposable("home") {
        HomeScreen(onNavigateToProfile = { userId ->
            navController.navigate("profile/$userId")
        })
    }

    coreComposable(
        route = "profile/{userId}",
        arguments = listOf(navArgument("userId") { type = NavType.LongType })
    ) { backStackEntry ->
        val userId = backStackEntry.arguments?.getLong("userId") ?: 0L
        ProfileScreen(userId = userId)
    }
}
```

---

<a name="compose-migration-guide"></a>
### 9. Migration Guide from View-Based Core

`:compose-core` seamlessly bridges with the existing `:core` architecture:

1. **Keep Your Business & Data Layers**: Continue using `:core`'s `RetrofitProvider`, `BaseRepository`, `RoomDatabase`, `DataStoreManager`, and `AppError`.
2. **Transition ViewModels to `BaseComposeViewModel`**: Expose a single immutable `ViewState` and handle actions through `onAction(action)`.
3. **Bridge API Results**: Map `NetworkResult<T>` directly to `ScreenState<T>` or update your `ViewState` via `launchWithState {}`.
4. **Decouple Resources**: Use `CoreText` and `CoreDrawable` in ViewModels so view models remain 100% unit-testable without Android framework mocks.

---

## License

```
Copyright 2026 AndroidCoreLibrary

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0
```

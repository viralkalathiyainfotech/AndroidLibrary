# AndroidCoreLibrary

[![JitPack](https://jitpack.io/v/viralkalathiyainfotech/AndroidLibrary.svg)](https://jitpack.io/#viralkalathiyainfotech/AndroidLibrary)
[![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)](https://semver.org)
[![Platform](https://img.shields.io/badge/platform-Android-green.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0+-purple.svg)](https://kotlinlang.org)
[![MinSdk](https://img.shields.io/badge/minSdk-24-orange.svg)](https://developer.android.com)
[![TargetSdk](https://img.shields.io/badge/targetSdk-37-red.svg)](https://developer.android.com)

A production-ready, modular, and reusable Android core library engineered in Kotlin. Designed following **Clean Architecture** and **SOLID** principles, `AndroidCoreLibrary` provides battle-tested foundational building blocks for modern Android applications without application-specific business logic or tight coupling.

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

---

## 1. Architecture & Philosophy

`AndroidCoreLibrary` strictly separates concerns across architectural layers:

```
┌─────────────────────────────────────────────────────────┐
│               PRESENTATION LAYER                        │
│   BaseActivity • BaseFragment • BaseDialog • UI State   │
└───────────────────────────┬─────────────────────────────┘
                            │ observes StateFlow & Events
┌───────────────────────────▼─────────────────────────────┐
│                 VIEWMODEL LAYER                         │
│       BaseViewModel • Coroutines • ExceptionHandler     │
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
- **Zero Business Logic in Core**: Generic types (`T`, `VB : ViewBinding`) throughout.
- **Lifecycle Safety**: Guaranteed cleanup of ViewBinding references in Fragment lifecycles.
- **Structured Concurrency**: Driven by `viewModelScope` and `repeatOnLifecycle`. No `GlobalScope`.
- **Offline-First Reactive Flow**: Data flows from Local DB Cache $\rightarrow$ Remote Sync $\rightarrow$ DB Update $\rightarrow$ UI State.

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
├── sample/                            # Reference Implementation (:sample)
│   ├── src/main/java/com/vc/sample/
│   │   ├── data/                      # UserApiService, AppDatabase, UserDao, UserRepository
│   │   ├── ui/                        # LoginActivity, HomeActivity, UserAdapter, BottomSheet
│   │   └── SampleApplication.kt       # Application setup & DI wiring
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
    implementation("com.github.viralkalathiyainfotech.AndroidLibrary:core:1.0.0")
}
```

*Or in Groovy (`build.gradle`):*
```groovy
dependencies {
    implementation 'com.github.viralkalathiyainfotech.AndroidLibrary:core:1.0.0'
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

## License

```
Copyright 2026 AndroidCoreLibrary

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0
```

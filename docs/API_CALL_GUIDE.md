# Networking & API Call Complete Guide

This guide explains everything about networking in `AndroidCoreLibrary`: from **Simple 1-Line API Calls** to **Authenticated JWT Calls**, **BaseViewModel integration**, and **Activity `observeData` event handling**.

---

## Table of Contents
1. [Simple API vs Authenticated API: What's the Difference?](#1-simple-api-vs-authenticated-api)
2. [Quickstart: Simple API in 3 Steps](#2-quickstart-simple-api-in-3-steps)
3. [How to Call APIs (3 Approaches)](#3-how-to-call-apis)
   - [Approach A: BaseViewModel `launchApi()` (Recommended)](#approach-a-baseviewmodel-launchapi)
   - [Approach B: Screen-Level State with `UiState<T>`](#approach-b-screen-level-state-with-uistatet)
   - [Approach C: Direct API in Activity without ViewModel](#approach-c-direct-api-in-activity)
4. [How to Observe Data & Events in Activity (`observeData`)](#4-how-to-observe-data--events-in-activity)
   - [What `observeBaseEvents(viewModel)` Does](#what-observebaseeventsviewmodel-does)
   - [Custom Screen-Level Progress (No Dialog)](#custom-screen-level-progress)
   - [Custom Error & Success Methods](#custom-error--success-methods)
5. [Advanced: Authenticated APIs (Bearer Token & Auto Refresh)](#5-advanced-authenticated-apis)
6. [Complete End-to-End Example](#6-complete-end-to-end-example)

---

## 1. Simple API vs Authenticated API

| Feature | Simple API (No Token) | Authenticated API (JWT / Bearer Token) |
|---|---|---|
| **Use Case** | Public data, catalogs, weather, news, open endpoints | Login, profile, checkout, user orders, private endpoints |
| **Setup Code** | **1 line** in `Application` class (`CoreLibrary.initialize`) | `OkHttpProvider.builder().tokenProvider(...)` |
| **Service Creation** | `RetrofitProvider.createService<ApiService>()` | `RetrofitProvider.createService<ApiService>(okHttpClient)` |
| **Headers** | None needed | Automatic `Authorization: Bearer <token>` |

---

## 2. Quickstart: Simple API in 3 Steps

If your API does not need a login token or special headers, you can set it up in seconds without any boilerplate.

### Step 1: Initialize in `Application`
Add your `baseUrl` in `Application.onCreate()`:

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // 1. Give the library your base URL
        CoreLibrary.initialize(
            context = this,
            config = CoreConfig(
                baseUrl = "https://jsonplaceholder.typicode.com/",
                enableNetworkLogging = true // Prints requests & responses in Logcat
            )
        )
    }
}
```

### Step 2: Define your Retrofit Interface
Define your data models and standard Retrofit suspend functions:

```kotlin
data class User(
    val id: Int,
    val name: String,
    val email: String
)

interface UserApiService {
    @GET("users")
    suspend fun getUsers(): Response<List<User>>

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Int): Response<User>

    @POST("users")
    suspend fun createUser(@Body user: User): Response<User>
}
```

### Step 3: Create Service Instance (1 Line)
You **do not** need to build `OkHttpClient` or `Retrofit.Builder`. It automatically pulls your `baseUrl` from `CoreLibrary.config`:

```kotlin
val apiService = RetrofitProvider.createService<UserApiService>()
```

---

## 3. How to Call APIs

### Approach A: BaseViewModel `launchApi()` (Recommended)
This is the cleanest, single-line API caller built into `BaseViewModel`. It:
- Automatically shows/hides the loading spinner.
- Dispatches on `Dispatchers.IO` automatically.
- Catches network exceptions and posts friendly errors.
- Supports automatic retry (`retryCount = 2`).

```kotlin
class UserViewModel : BaseViewModel() {

    private val apiService = RetrofitProvider.createService<UserApiService>()

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    fun loadUsers() {
        launchApi(
            call = { apiService.getUsers() },
            onSuccess = { userList ->
                _users.value = userList
                sendEvent(UiEvent.ShowToast("Loaded ${userList.size} users!"))
            }
        )
    }
}
```

---

### Approach B: Screen-Level State with `UiState<T>`
Use this approach when you want **in-screen progress** (e.g. `ProgressBar` or Shimmer inside your layout) instead of the default popup loading dialog, plus custom error and success handling:

```kotlin
class UserViewModel : BaseViewModel() {

    private val apiService = RetrofitProvider.createService<UserApiService>()

    // Screen State: Idle, Loading, Success, Error
    private val _userState = MutableStateFlow<UiState<List<User>>>(UiState.Idle)
    val userState: StateFlow<UiState<List<User>>> = _userState.asStateFlow()

    fun fetchUsers() {
        _userState.value = UiState.Loading // Progress starts

        // showLoading = false disables the default popup dialog
        launchSafe(showLoading = false) {
            val response = safeApiCall { apiService.getUsers() }

            when (response) {
                is NetworkResult.Success -> {
                    _userState.value = UiState.Success(response.data)
                }
                is NetworkResult.Error -> {
                    _userState.value = UiState.Error(response.error.userFriendlyMessage)
                }
                else -> Unit
            }
        }
    }
}
```

---

### Approach C: Direct API in Activity (No ViewModel)
When creating simple screens, one-off dialogs, or utilities where a ViewModel is unnecessary:

```kotlin
class DirectApiActivity : BaseActivity<ActivityDirectBinding>() {

    private val apiService = RetrofitProvider.createService<UserApiService>()

    override fun setupListeners() {
        binding.btnFetch.setOnClickListener {
            lifecycleScope.launch {
                showLoading("Fetching users...")
                
                val result = safeApiCall { apiService.getUsers() }
                
                hideLoading()

                when (result) {
                    is NetworkResult.Success -> {
                        binding.tvResult.text = "Success: ${result.data.size} items"
                    }
                    is NetworkResult.Error -> {
                        showError(result.error.userFriendlyMessage)
                    }
                    else -> Unit
                }
            }
        }
    }

    override fun observeData() {}
}
```

---

## 4. How to Observe Data & Events in Activity

### What `observeBaseEvents(viewModel)` Does
Calling `observeBaseEvents(viewModel)` inside `observeData()` connects the 3 core pipelines with **zero boilerplate**:

```kotlin
class UserActivity : BaseActivity<ActivityUserBinding>() {

    override fun observeData() {
        // 1 line handles: Loading Dialog, App Errors, Toasts, and Navigation!
        observeBaseEvents(viewModel)

        // Observe custom data
        collectLifecycleFlow(viewModel.users) { list ->
            userAdapter.submitList(list)
        }
    }
}
```

Under the hood, `observeBaseEvents` connects:
1. **`viewModel.loadingState`** $\rightarrow$ Automatically shows and hides `LoadingDialog`.
2. **`viewModel.errorState`** $\rightarrow$ Automatically catches HTTP & Network errors and displays `handleAppError(error)`.
3. **`viewModel.uiEvent`** $\rightarrow$ Executes `UiEvent.ShowToast`, `UiEvent.ShowSnackbar`, and `UiEvent.Navigate`.

---

### Custom Screen-Level Progress
To replace the popup dialog with an inline `ProgressBar` or Shimmer:

```kotlin
class UserActivity : BaseActivity<ActivityUserBinding>() {

    override fun observeData() {
        observeBaseEvents(viewModel)
    }

    // Override showLoading to show your layout's ProgressBar:
    override fun showLoading(message: String, isCancelable: Boolean) {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnLoad.isEnabled = false
    }

    override fun hideLoading() {
        binding.progressBar.visibility = View.GONE
        binding.btnLoad.isEnabled = true
    }
}
```

---

### Custom Error & Success Methods
To run custom logic (e.g. custom dialog, red outline, shake animation) when an error or success happens:

#### Handling Custom Error:
```kotlin
override fun handleAppError(error: AppError) {
    // Custom error method
    binding.tvErrorBanner.text = error.userFriendlyMessage
    binding.tvErrorBanner.visibility = View.VISIBLE
    binding.layoutForm.startAnimation(shakeAnimation)
}
```

#### Handling Custom Success via `UiEvent.Custom`:
```kotlin
// In ViewModel:
sendEvent(UiEvent.Custom(user))

// In Activity:
override fun onCustomEvent(payload: Any?) {
    when (payload) {
        is User -> onUserSavedSuccess(payload)
    }
}

private fun onUserSavedSuccess(user: User) {
    showSuccessDialog("Welcome, ${user.name}!")
}
```

---

## 5. Advanced: Authenticated APIs (Token & Refresh)

When endpoints require `Authorization: Bearer <token>`:

```kotlin
// 1. Create TokenProvider (reading token from DataStore/EncryptedPrefs)
val tokenProvider = object : TokenProvider {
    override fun getAccessToken(): String? {
        return runBlocking { dataStoreManager.getString("auth_token") }
    }
}

// 2. Create RefreshTokenProvider (automatically retries on 401 Unauthorized)
val refreshTokenProvider = object : RefreshTokenProvider {
    override fun refreshToken(): String? {
        val newAuth = authApiService.refreshTokenSync()
        return newAuth?.accessToken
    }
}

// 3. Build OkHttpClient with Token & Logging
val okHttpClient = OkHttpProvider.builder()
    .tokenProvider(tokenProvider, refreshTokenProvider)
    .logging(BuildConfig.DEBUG)
    .connectTimeout(30)
    .readTimeout(30)
    .build()

// 4. Create Service with custom client
val authenticatedService = RetrofitProvider.createService<UserApiService>(
    baseUrl = "https://api.myproductionapp.com/",
    okHttpClient = okHttpClient
)
```

---

## 6. Complete End-to-End Example

Here is a full, copy-pasteable working screen:

### The ViewModel:
```kotlin
class ProductsViewModel : BaseViewModel() {

    private val apiService = RetrofitProvider.createService<ProductApiService>()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    fun loadProducts() {
        launchApi(
            call = { apiService.getProducts() },
            onSuccess = { list ->
                _products.value = list
            },
            onError = { appError ->
                // Optional: Custom error logging or fallback
            }
        )
    }
}
```

### The Activity:
```kotlin
class ProductsActivity : BaseActivity<ActivityProductsBinding>() {

    private val viewModel by viewModels<ProductsViewModel>()
    private val adapter = ProductsAdapter()

    override fun inflateBinding(): ActivityProductsBinding =
        ActivityProductsBinding.inflate(layoutInflater)

    override fun setupViews() {
        binding.recyclerView.adapter = adapter
        viewModel.loadProducts()
    }

    override fun setupListeners() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadProducts()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    override fun observeData() {
        // Automatically handles loading spinner and network errors!
        observeBaseEvents(viewModel)

        // Observe product list
        collectLifecycleFlow(viewModel.products) { list ->
            adapter.submitList(list)
            binding.emptyView.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }
    }
}
```

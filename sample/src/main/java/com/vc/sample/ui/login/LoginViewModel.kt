package com.vc.sample.ui.login

import com.vc.androidcore.base.BaseViewModel
import com.vc.androidcore.preferences.DataStoreManager
import com.vc.androidcore.state.UiEvent
import com.vc.androidcore.utils.isValidEmail
import com.vc.sample.ui.home.HomeActivity
import kotlinx.coroutines.delay

/**
 * ViewModel for Login screen extending [BaseViewModel].
 */
class LoginViewModel(
    private val dataStoreManager: DataStoreManager
) : BaseViewModel() {

    fun login(email: String, password: String) {
        if (!email.isValidEmail()) {
            sendEvent(UiEvent.ShowSnackbar("Please enter a valid email address."))
            return
        }

        if (password.length < 6) {
            sendEvent(UiEvent.ShowSnackbar("Password must be at least 6 characters."))
            return
        }

        // Safe coroutine execution with loading spinner
        launchSafe(showLoading = true) {
            // Simulate network authentication roundtrip
            delay(1000)

            // Save auth token to DataStore
            val mockToken = "mock_jwt_token_${System.currentTimeMillis()}"
            dataStoreManager.putString("auth_token", mockToken)
            dataStoreManager.putString("user_email", email)

            sendEvent(UiEvent.ShowToast("Login successful!"))
            sendEvent(UiEvent.Navigate(destination = HomeActivity::class.java, finishCurrent = true))
        }
    }
}

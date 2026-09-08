package com.vc.androidcore.base

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.vc.androidcore.error.AppError
import com.vc.androidcore.state.UiEvent
import com.vc.androidcore.ui.dialog.LoadingDialog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Base activity class providing ViewBinding inflation, lifecycle-aware coroutine collection,
 * loading dialogs, toasts, snackbars, keyboard helpers, and error presentation.
 */
abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    private var _binding: VB? = null
    protected val binding: VB get() = _binding!!

    private var loadingDialog: LoadingDialog? = null

    abstract fun inflateBinding(): VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = inflateBinding()
        setContentView(binding.root)

        loadingDialog = LoadingDialog(this)

        setupUI()
        setupListeners()
        observeData()
    }

    protected open fun setupUI() {}
    protected open fun setupListeners() {}
    protected open fun observeData() {}

    /**
     * Helper to safely collect any [Flow] lifecycle-aware in [Lifecycle.State.STARTED].
     */
    fun <T> collectLifecycleFlow(
        flow: Flow<T>,
        minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
        collector: suspend (T) -> Unit
    ) {
        lifecycleScope.launch {
            repeatOnLifecycle(minActiveState) {
                flow.collect { collector(it) }
            }
        }
    }

    /**
     * Standard observer for [BaseViewModel] events.
     */
    protected fun observeBaseEvents(viewModel: BaseViewModel) {
        collectLifecycleFlow(viewModel.loadingState) { isLoading ->
            if (isLoading) showLoading() else hideLoading()
        }

        collectLifecycleFlow(viewModel.errorState) { error ->
            error?.let { handleAppError(it) }
        }

        collectLifecycleFlow(viewModel.uiEvent) { event ->
            handleUiEvent(event)
        }
    }

    protected open fun handleUiEvent(event: UiEvent) {
        when (event) {
            is UiEvent.ShowToast -> showToast(event.message, event.isLong)
            is UiEvent.ShowSnackbar -> showSnackbar(event.message, event.actionText, event.action)
            is UiEvent.ShowDialog -> showError(event.message)
            is UiEvent.ApiError -> handleAppError(event.error)
            is UiEvent.Navigate -> {
                event.destination?.let { dest ->
                    navigateTo(dest, event.extras, finishCurrent = event.finishCurrent)
                }
            }
            is UiEvent.Logout -> onLogoutRequested()
            is UiEvent.Custom -> onCustomEvent(event.payload)
        }
    }

    protected open fun onLogoutRequested() {}
    protected open fun onCustomEvent(payload: Any?) {}

    // --- Loading Helpers ---
    open fun showLoading(message: String = "Loading...", isCancelable: Boolean = false) {
        if (!isFinishing && !isDestroyed) {
            loadingDialog?.show(message, isCancelable)
        }
    }

    open fun hideLoading() {
        loadingDialog?.dismiss()
    }

    // --- Feedback Helpers ---
    open fun showToast(message: String, isLong: Boolean = false) {
        val duration = if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
        Toast.makeText(this, message, duration).show()
    }

    open fun showSnackbar(
        message: String,
        actionText: String? = null,
        action: (() -> Unit)? = null
    ) {
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
        if (actionText != null && action != null) {
            snackbar.setAction(actionText) { action() }
        }
        snackbar.show()
    }

    open fun showError(message: String) {
        showSnackbar(message)
    }

    open fun handleAppError(error: AppError) {
        showError(error.userMessage)
    }

    // --- Toolbar Helpers ---
    protected fun setupToolbar(
        toolbar: Toolbar,
        title: String? = null,
        displayHomeAsUp: Boolean = true,
        onNavigationClick: (() -> Unit)? = null
    ) {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title?.let { this.title = it }
            setDisplayHomeAsUpEnabled(displayHomeAsUp)
        }
        if (displayHomeAsUp) {
            toolbar.setNavigationOnClickListener {
                onNavigationClick?.invoke() ?: finish()
            }
        }
    }

    // --- Navigation Helpers ---
    fun navigateTo(
        destination: Class<*>,
        extras: Bundle? = null,
        finishCurrent: Boolean = false
    ) {
        val intent = Intent(this, destination).apply {
            extras?.let { putExtras(it) }
        }
        startActivity(intent)
        if (finishCurrent) {
            finish()
        }
    }

    // --- Keyboard Helpers ---
    fun hideKeyboard() {
        val view = currentFocus ?: binding.root
        val controller = WindowInsetsControllerCompat(window, view)
        controller.hide(WindowInsetsCompat.Type.ime())
    }

    fun showKeyboard(view: View) {
        view.requestFocus()
        val controller = WindowInsetsControllerCompat(window, view)
        controller.show(WindowInsetsCompat.Type.ime())
    }

    override fun onDestroy() {
        hideLoading()
        loadingDialog = null
        _binding = null
        super.onDestroy()
    }
}

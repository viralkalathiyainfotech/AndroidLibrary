package com.vc.androidcore.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
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
 * Base [Fragment] providing leak-safe ViewBinding lifecycle management,
 * coroutine collection helpers, loading dialogs, toasts, snackbars, and keyboard controls.
 */
abstract class BaseFragment<VB : ViewBinding> : Fragment() {

    private var _binding: VB? = null
    protected val binding: VB
        get() = _binding ?: throw IllegalStateException(
            "ViewBinding accessed before onCreateView() or after onDestroyView()."
        )

    private var loadingDialog: LoadingDialog? = null

    abstract fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflateBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context?.let { loadingDialog = LoadingDialog(it) }

        setupUI()
        setupListeners()
        observeData()
    }

    protected open fun setupUI() {}
    protected open fun setupListeners() {}
    protected open fun observeData() {}

    /**
     * Safely collects a [Flow] lifecycle-aware within [viewLifecycleOwner].
     */
    fun <T> collectLifecycleFlow(
        flow: Flow<T>,
        minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
        collector: suspend (T) -> Unit
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(minActiveState) {
                flow.collect { collector(it) }
            }
        }
    }

    /**
     * Observes standard [BaseViewModel] streams.
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
            is UiEvent.Logout -> onLogoutRequested()
            else -> {}
        }
    }

    protected open fun onLogoutRequested() {}

    // --- Loading Helpers ---
    open fun showLoading(message: String = "Loading...", isCancelable: Boolean = false) {
        loadingDialog?.show(message, isCancelable)
    }

    open fun hideLoading() {
        loadingDialog?.dismiss()
    }

    // --- Feedback Helpers ---
    open fun showToast(message: String, isLong: Boolean = false) {
        context?.let {
            val duration = if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
            Toast.makeText(it, message, duration).show()
        }
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

    // --- Keyboard Helpers ---
    fun hideKeyboard() {
        val window = activity?.window ?: return
        val view = view ?: return
        val controller = WindowInsetsControllerCompat(window, view)
        controller.hide(WindowInsetsCompat.Type.ime())
    }

    fun showKeyboard(targetView: View) {
        val window = activity?.window ?: return
        targetView.requestFocus()
        val controller = WindowInsetsControllerCompat(window, targetView)
        controller.show(WindowInsetsCompat.Type.ime())
    }

    /**
     * Crucial: Clears binding in onDestroyView() to prevent activity/view memory leaks.
     */
    override fun onDestroyView() {
        hideLoading()
        loadingDialog = null
        _binding = null
        super.onDestroyView()
    }
}

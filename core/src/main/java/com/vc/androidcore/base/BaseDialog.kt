package com.vc.androidcore.base

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.vc.androidcore.ui.dialog.LoadingDialog

/**
 * Base [DialogFragment] with generic ViewBinding, transparent background,
 * configurable sizing, and leak-safe lifecycle handling.
 */
abstract class BaseDialog<VB : ViewBinding> : DialogFragment() {

    private var _binding: VB? = null
    protected val binding: VB get() = _binding!!

    private var loadingDialog: LoadingDialog? = null

    protected open val isDialogCancelable: Boolean = true
    protected open val isCanceledOnTouchOutside: Boolean = true
    protected open val widthPercent: Float = 0.9f
    protected open val heightWrapContent: Boolean = true

    abstract fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.apply {
            requestFeature(Window.FEATURE_NO_TITLE)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        _binding = inflateBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context?.let { loadingDialog = LoadingDialog(it) }

        isCancelable = isDialogCancelable
        dialog?.setCanceledOnTouchOutside(isCanceledOnTouchOutside)

        setupUI()
        setupListeners()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.let { window ->
            val displayMetrics = resources.displayMetrics
            val width = (displayMetrics.widthPixels * widthPercent).toInt()
            val height = if (heightWrapContent) {
                WindowManager.LayoutParams.WRAP_CONTENT
            } else {
                (displayMetrics.heightPixels * 0.8f).toInt()
            }
            window.setLayout(width, height)
        }
    }

    protected open fun setupUI() {}
    protected open fun setupListeners() {}

    fun showLoading(message: String = "Loading...") {
        loadingDialog?.show(message)
    }

    fun hideLoading() {
        loadingDialog?.dismiss()
    }

    override fun onDestroyView() {
        hideLoading()
        loadingDialog = null
        _binding = null
        super.onDestroyView()
    }
}

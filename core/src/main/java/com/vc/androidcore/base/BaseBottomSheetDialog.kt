package com.vc.androidcore.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vc.androidcore.ui.dialog.LoadingDialog

/**
 * Base [BottomSheetDialogFragment] with generic ViewBinding, customizable behavior,
 * and leak-safe lifecycle handling.
 */
abstract class BaseBottomSheetDialog<VB : ViewBinding> : BottomSheetDialogFragment() {

    private var _binding: VB? = null
    protected val binding: VB get() = _binding!!

    private var loadingDialog: LoadingDialog? = null

    protected open val isDialogCancelable: Boolean = true
    protected open val isCanceledOnTouchOutside: Boolean = true
    protected open val isExpandedOnStart: Boolean = true

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

        isCancelable = isDialogCancelable
        dialog?.setCanceledOnTouchOutside(isCanceledOnTouchOutside)

        if (isExpandedOnStart) {
            dialog?.setOnShowListener { dialogInterface ->
                val bottomSheetDialog = dialogInterface as? BottomSheetDialog
                val bottomSheet = bottomSheetDialog?.findViewById<FrameLayout>(
                    com.google.android.material.R.id.design_bottom_sheet
                )
                bottomSheet?.let { sheet ->
                    val behavior = BottomSheetBehavior.from(sheet)
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                    behavior.skipCollapsed = true
                }
            }
        }

        setupUI()
        setupListeners()
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

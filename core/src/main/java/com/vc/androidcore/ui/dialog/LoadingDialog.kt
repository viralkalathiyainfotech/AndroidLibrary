package com.vc.androidcore.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import com.vc.androidcore.R
import com.vc.androidcore.databinding.CoreDialogLoadingBinding
import java.lang.ref.WeakReference

/**
 * Production-ready, leak-safe loading dialog.
 * Uses a [WeakReference] to the host Context and validates activity state before showing/dismissing.
 */
class LoadingDialog(context: Context) {

    private val contextRef: WeakReference<Context> = WeakReference(context)
    private var dialog: Dialog? = null
    private var binding: CoreDialogLoadingBinding? = null

    /**
     * Displays the loading dialog with an optional custom message.
     *
     * @param message Text to display beneath the spinner.
     * @param isCancelable Whether back press / outside touch can dismiss the dialog.
     */
    fun show(message: String = "Loading...", isCancelable: Boolean = false) {
        val currentContext = contextRef.get() ?: return

        // If host is an Activity and is finishing or destroyed, don't show
        if (currentContext is android.app.Activity && (currentContext.isFinishing || currentContext.isDestroyed)) {
            return
        }

        if (dialog == null) {
            val layoutInflater = LayoutInflater.from(currentContext)
            binding = CoreDialogLoadingBinding.inflate(layoutInflater)

            dialog = Dialog(currentContext, R.style.CoreTransparentDialogTheme).apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                setContentView(binding!!.root)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setCanceledOnTouchOutside(false)
                setCancelable(isCancelable)
            }
        }

        binding?.tvLoadingMessage?.text = message
        dialog?.setCancelable(isCancelable)

        if (dialog?.isShowing != true) {
            try {
                dialog?.show()
            } catch (e: Exception) {
                // Catch any WindowManager BadTokenException if activity died concurrently
            }
        }
    }

    /**
     * Updates message while dialog is already showing.
     */
    fun setMessage(message: String) {
        binding?.tvLoadingMessage?.text = message
    }

    /**
     * Safely dismisses the loading dialog.
     */
    fun dismiss() {
        try {
            if (dialog?.isShowing == true) {
                dialog?.dismiss()
            }
        } catch (e: Exception) {
            // Ignored
        } finally {
            dialog = null
            binding = null
        }
    }

    val isShowing: Boolean get() = dialog?.isShowing == true
}

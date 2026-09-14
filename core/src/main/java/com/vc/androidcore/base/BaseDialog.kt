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
import java.lang.reflect.ParameterizedType

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

    /**
     * Automatically inflates the [ViewBinding] via reflection by finding generic type [VB].
     * Subclasses can still override this if custom inflation behavior is required.
     */
    @Suppress("UNCHECKED_CAST")
    protected open fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): VB {
        var currentClass: Class<*>? = javaClass
        while (currentClass != null && currentClass != Any::class.java) {
            val genericSuperclass = currentClass.genericSuperclass
            if (genericSuperclass is ParameterizedType) {
                val bindingType = genericSuperclass.actualTypeArguments.firstOrNull { arg ->
                    arg is Class<*> && ViewBinding::class.java.isAssignableFrom(arg)
                }
                if (bindingType != null) {
                    val bindingClass = bindingType as Class<VB>
                    val inflateMethod3 = runCatching {
                        bindingClass.getMethod(
                            "inflate",
                            LayoutInflater::class.java,
                            ViewGroup::class.java,
                            Boolean::class.javaPrimitiveType
                        )
                    }.getOrNull()

                    if (inflateMethod3 != null) {
                        return inflateMethod3.invoke(null, inflater, container, false) as VB
                    }

                    val inflateMethod1 = bindingClass.getMethod("inflate", LayoutInflater::class.java)
                    return inflateMethod1.invoke(null, inflater) as VB
                }
            }
            currentClass = currentClass.superclass
        }
        error("Unable to automatically inflate ViewBinding for ${javaClass.simpleName}. Please override inflateBinding() manually.")
    }

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

    protected abstract fun setupUI()
    protected abstract fun setupListeners()

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

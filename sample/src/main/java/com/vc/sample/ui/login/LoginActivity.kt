package com.vc.sample.ui.login

import android.os.Bundle
import com.vc.androidcore.base.BaseActivity
import com.vc.androidcore.utils.setOnDebouncedClickListener
import com.vc.androidcore.utils.textValue
import com.vc.sample.SampleApplication
import com.vc.sample.databinding.ActivityLoginBinding

/**
 * Login screen extending [BaseActivity].
 */
class LoginActivity : BaseActivity<ActivityLoginBinding>() {

    private lateinit var viewModel: LoginViewModel

    override fun inflateBinding(): ActivityLoginBinding {
        return ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val app = application as SampleApplication
        viewModel = LoginViewModel(app.dataStoreManager)
        super.onCreate(savedInstanceState)
    }

    override fun setupListeners() {
        binding.btnLogin.setOnDebouncedClickListener {
            hideKeyboard()
            val email = binding.etEmail.textValue()
            val password = binding.etPassword.textValue()
            viewModel.login(email, password)
        }
    }

    override fun observeData() {
        observeBaseEvents(viewModel)
    }
}

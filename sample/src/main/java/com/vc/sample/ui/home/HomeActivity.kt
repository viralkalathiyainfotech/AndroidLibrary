package com.vc.sample.ui.home

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.vc.androidcore.base.BaseActivity
import com.vc.androidcore.state.UiState
import com.vc.androidcore.utils.gone
import com.vc.androidcore.utils.onDebouncedQueryChange
import com.vc.androidcore.utils.setOnDebouncedClickListener
import com.vc.androidcore.utils.setVerticalLayout
import com.vc.androidcore.utils.visible
import com.vc.sample.R
import com.vc.sample.SampleApplication
import com.vc.sample.databinding.ActivityHomeBinding
import com.vc.sample.ui.detail.UserDetailBottomSheet

/**
 * Home screen showcasing:
 * 1. Isolated API Call (API Only)
 * 2. Isolated Room Database Call (Room DB Only)
 * 3. Combined Offline-First Pipeline: API ➔ Room DB ➔ UI List
 * 4. Real-time connectivity status & debounced search
 */
class HomeActivity : BaseActivity<ActivityHomeBinding>() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: UserAdapter

    override fun inflateBinding(): ActivityHomeBinding {
        return ActivityHomeBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val app = application as SampleApplication
        viewModel = HomeViewModel(
            userRepository = app.userRepository,
            networkMonitor = app.networkMonitor,
            dataStoreManager = app.dataStoreManager
        )
        super.onCreate(savedInstanceState)
    }

    override fun setupUI() {
        adapter = UserAdapter()
        binding.rvUsers.setVerticalLayout()
        binding.rvUsers.adapter = adapter
    }

    override fun setupListeners() {
        adapter.onItemClickListener = { user, _ ->
            UserDetailBottomSheet(user).show(supportFragmentManager, "UserDetail")
        }

        // Action 1: Combined Offline-First Pipeline (API ➔ Save to Room ➔ Read from Room ➔ Show in List)
        binding.btnSyncAll.setOnDebouncedClickListener {
            viewModel.loadFromSync()
        }

        // Action 2: Isolated Remote API Call Only (Room DB bypassed)
        binding.btnApiOnly.setOnDebouncedClickListener {
            viewModel.loadFromApiOnly()
        }

        // Action 3: Isolated Local Room DB Query Only (Network bypassed)
        binding.btnRoomOnly.setOnDebouncedClickListener {
            viewModel.loadFromRoomOnly()
        }

        // Action 4: Clear Room Database
        binding.btnClearDb.setOnDebouncedClickListener {
            viewModel.clearRoomDatabase()
        }

        binding.btnLogout.setOnDebouncedClickListener {
            viewModel.logout()
        }

        binding.btnRetry.setOnDebouncedClickListener {
            viewModel.loadFromSync()
        }

        // Debounced search query in Room DB
        binding.etSearch.onDebouncedQueryChange(lifecycleScope, waitMs = 400L) { query ->
            viewModel.search(query)
        }
    }

//    override fun observeData() {
//        // collectLifecycleFlow દ્વારા સેફ રીતે સ્ટેટ સાંભળો
//        collectLifecycleFlow(viewModel.usersState) { state ->
//            when (state) {
//                is UiState.Idle -> {
//                    binding.progressBar.visibility = View.GONE
//                }
//                is UiState.Loading -> {
//                    // 👉 Screen પર પોતાનો Progress Bar / Button Loading
//                    binding.progressBar.visibility = View.VISIBLE
//                    binding.btnLogin.isEnabled = false
//                }
//                is UiState.Success -> {
//                    // 👉 Progress બંધ કરો અને Custom Success Method કોલ કરો
//                    binding.progressBar.visibility = View.GONE
//                    binding.btnLogin.isEnabled = true
//
//                    handleCustomSuccess(state.data) // તમારી Custom Method
//                }
//                is UiState.Error -> {
//                    // 👉 Progress બંધ કરો અને Custom Error Method કોલ કરો
//                    binding.progressBar.visibility = View.GONE
//                    binding.btnLogin.isEnabled = true
//
//                    handleCustomError(state.message) // તમારી Custom Method
//                }
//            }
//        }
//    }


    override fun observeData() {
        observeBaseEvents(viewModel)

        // 1. Observe real-time Network Connectivity
        collectLifecycleFlow(viewModel.isOnline) { isOnline ->
            updateNetworkBanner(isOnline)
        }

        // 2. Observe Live Pipeline Execution Status
        collectLifecycleFlow(viewModel.pipelineStatus) { statusText ->
            binding.tvPipelineStatus.text = statusText
        }

        // 3. Observe Users UiState
        collectLifecycleFlow(viewModel.usersState) { state ->
            when (state) {
                is UiState.Idle -> {
                    binding.pbLoading.gone()
                }
                is UiState.Loading -> {
                    binding.pbLoading.visible()
                    binding.llEmptyState.gone()
                }
                is UiState.Success -> {
                    binding.pbLoading.gone()
                    val users = state.data
                    adapter.submitList(users)

                    if (users.isEmpty()) {
                        binding.llEmptyState.visible()
                        binding.rvUsers.gone()
                    } else {
                        binding.llEmptyState.gone()
                        binding.rvUsers.visible()
                    }
                }
                is UiState.Error -> {
                    binding.pbLoading.gone()
                    showError(state.message)
                    if (adapter.currentList.isEmpty()) {
                        binding.llEmptyState.visible()
                        binding.tvEmptyMessage.text = state.message
                    }
                }
            }
        }
    }

    private fun updateNetworkBanner(isOnline: Boolean) {
        if (isOnline) {
            binding.llNetworkBanner.setBackgroundResource(R.drawable.bg_banner_online)
            binding.viewNetworkDot.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.sample_online)
            binding.tvNetworkStatus.setText(R.string.status_online)
        } else {
            binding.llNetworkBanner.setBackgroundResource(R.drawable.bg_banner_offline)
            binding.viewNetworkDot.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.sample_offline)
            binding.tvNetworkStatus.setText(R.string.status_offline)
        }
    }
}

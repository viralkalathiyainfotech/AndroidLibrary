package com.vc.sample.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import com.vc.androidcore.base.BaseBottomSheetDialog
import com.vc.sample.data.model.User
import com.vc.sample.databinding.BottomSheetUserDetailBinding

/**
 * User detail bottom sheet extending [BaseBottomSheetDialog].
 */
class UserDetailBottomSheet(
    private val user: User
) : BaseBottomSheetDialog<BottomSheetUserDetailBinding>() {

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BottomSheetUserDetailBinding {
        return BottomSheetUserDetailBinding.inflate(inflater, container, false)
    }

    override fun setupUI() {
        binding.tvDetailName.text = user.name
        binding.tvDetailUsername.text = "@${user.username}"
        binding.tvDetailEmail.text = "Email: ${user.email}"
        binding.tvDetailPhone.text = "Phone: ${user.phone}"
        binding.tvDetailWebsite.text = "Website: ${user.website}"
        binding.tvDetailCompany.text = "Company: ${user.companyName}"
    }

    override fun setupListeners() {
        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }
}

package com.vc.standalone.ui.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import com.vc.androidcore.base.BaseBottomSheetDialog
import com.vc.androidcore.utils.loadImage
import com.vc.standalone.data.model.StandaloneUser
import com.vc.standalone.databinding.BottomSheetUserInfoBinding

/**
 * BottomSheet dialog extending [BaseBottomSheetDialog] to display user details.
 */
class UserDetailBottomSheet(
    private val user: StandaloneUser
) : BaseBottomSheetDialog<BottomSheetUserInfoBinding>() {

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BottomSheetUserInfoBinding {
        return BottomSheetUserInfoBinding.inflate(inflater, container, false)
    }

    override fun setupUI() {
        binding.tvSheetName.text = user.name
        binding.tvSheetEmail.text = user.email
        binding.tvSheetPhone.text = "Phone: ${user.phone}"
        binding.tvSheetWebsite.text = "Website: ${user.website}"
        binding.tvSheetAddress.text = "City: ${user.city} • Company: ${user.companyName}"

        binding.ivSheetAvatar.loadImage(
            data = user.avatarUrl,
            isCircle = true
        )
    }

    override fun setupListeners() {
        binding.btnCloseSheet.setOnClickListener {
            dismiss()
        }
    }
}

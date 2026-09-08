package com.vc.sample.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import com.vc.androidcore.adapter.BaseListAdapter
import com.vc.androidcore.adapter.DiffUtilItemCallback
import com.vc.androidcore.utils.loadImage
import com.vc.sample.data.model.User
import com.vc.sample.databinding.ItemUserBinding

/**
 * RecyclerView adapter for displaying users extending [BaseListAdapter].
 */
class UserAdapter : BaseListAdapter<User, ItemUserBinding>(
    DiffUtilItemCallback.create(
        areItemsTheSame = { oldItem, newItem -> oldItem.id == newItem.id },
        areContentsTheSame = { oldItem, newItem -> oldItem == newItem }
    )
) {

    override fun createBinding(parent: ViewGroup): ItemUserBinding {
        return ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun bind(binding: ItemUserBinding, item: User, position: Int) {
        binding.tvUserName.text = item.name
        binding.tvUserEmail.text = item.email
        binding.tvUserPhone.text = item.phone.ifBlank { item.companyName }

        // Load avatar using Coil via extension from AndroidCoreLibrary
        binding.ivAvatar.loadImage(
            data = item.avatarUrl,
            isCircle = true
        )
    }
}

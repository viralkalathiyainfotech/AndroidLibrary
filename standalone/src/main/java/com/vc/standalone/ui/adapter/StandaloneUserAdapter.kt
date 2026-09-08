package com.vc.standalone.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.vc.androidcore.adapter.BaseListAdapter
import com.vc.androidcore.adapter.DiffUtilItemCallback
import com.vc.androidcore.utils.loadImage
import com.vc.standalone.data.model.StandaloneUser
import com.vc.standalone.databinding.ItemStandaloneUserBinding

/**
 * Adapter extending [BaseListAdapter] to render the user list with zero manual diffing boilerplate.
 */
class StandaloneUserAdapter(
    private val onUserClicked: (StandaloneUser) -> Unit
) : BaseListAdapter<StandaloneUser, ItemStandaloneUserBinding>(
    DiffUtilItemCallback.create(
        areItemsTheSame = { oldItem, newItem -> oldItem.id == newItem.id },
        areContentsTheSame = { oldItem, newItem -> oldItem == newItem }
    )
) {

    init {
        onItemClickListener = { user, _ ->
            onUserClicked(user)
        }
    }

    override fun createBinding(parent: ViewGroup): ItemStandaloneUserBinding {
        return ItemStandaloneUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun bind(binding: ItemStandaloneUserBinding, item: StandaloneUser, position: Int) {
        binding.tvName.text = item.name
        binding.tvEmail.text = item.email
        binding.tvCompany.text = "${item.companyName} • ${item.city}"
        binding.ivAvatar.loadImage(
            data = item.avatarUrl,
            isCircle = true
        )
    }
}

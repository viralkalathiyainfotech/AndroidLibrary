package com.vc.androidcore.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/**
 * Generic [ListAdapter] using [DiffUtil] for asynchronous, efficient list diffing.
 *
 * @param T The item model type.
 * @param VB The ViewBinding type for the item layout.
 */
abstract class BaseListAdapter<T : Any, VB : ViewBinding>(
    diffCallback: DiffUtil.ItemCallback<T>
) : ListAdapter<T, BaseListAdapter.BaseViewHolder<VB>>(diffCallback) {

    var onItemClickListener: ((item: T, position: Int) -> Unit)? = null
    var onItemLongClickListener: ((item: T, position: Int) -> Boolean)? = null

    abstract fun createBinding(parent: ViewGroup): VB
    abstract fun bind(binding: VB, item: T, position: Int)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<VB> {
        val binding = createBinding(parent)
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<VB>, position: Int) {
        val item = getItem(position)
        bind(holder.binding, item, position)

        holder.itemView.setOnClickListener {
            val currentPos = holder.bindingAdapterPosition
            if (currentPos != RecyclerView.NO_POSITION && currentPos < currentList.size) {
                onItemClickListener?.invoke(getItem(currentPos), currentPos)
            }
        }

        holder.itemView.setOnLongClickListener {
            val currentPos = holder.bindingAdapterPosition
            if (currentPos != RecyclerView.NO_POSITION && currentPos < currentList.size) {
                onItemLongClickListener?.invoke(getItem(currentPos), currentPos) ?: false
            } else {
                false
            }
        }
    }

    class BaseViewHolder<VB : ViewBinding>(val binding: VB) : RecyclerView.ViewHolder(binding.root)
}

package com.vc.androidcore.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/**
 * Generic [RecyclerView.Adapter] with generic ViewBinding support and list mutation helpers.
 *
 * @param T The item model type.
 * @param VB The ViewBinding type for the item layout.
 */
abstract class BaseRecyclerAdapter<T, VB : ViewBinding> :
    RecyclerView.Adapter<BaseRecyclerAdapter.BaseViewHolder<VB>>() {

    protected val itemList: MutableList<T> = mutableListOf()

    var onItemClickListener: ((item: T, position: Int) -> Unit)? = null
    var onItemLongClickListener: ((item: T, position: Int) -> Boolean)? = null

    abstract fun createBinding(parent: ViewGroup): VB
    abstract fun bind(binding: VB, item: T, position: Int)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<VB> {
        val binding = createBinding(parent)
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<VB>, position: Int) {
        val item = itemList[position]
        bind(holder.binding, item, position)

        holder.itemView.setOnClickListener {
            val currentPos = holder.bindingAdapterPosition
            if (currentPos != RecyclerView.NO_POSITION && currentPos < itemList.size) {
                onItemClickListener?.invoke(itemList[currentPos], currentPos)
            }
        }

        holder.itemView.setOnLongClickListener {
            val currentPos = holder.bindingAdapterPosition
            if (currentPos != RecyclerView.NO_POSITION && currentPos < itemList.size) {
                onItemLongClickListener?.invoke(itemList[currentPos], currentPos) ?: false
            } else {
                false
            }
        }
    }

    override fun getItemCount(): Int = itemList.size

    fun submitList(newItems: List<T>) {
        itemList.clear()
        itemList.addAll(newItems)
        notifyDataSetChanged()
    }

    fun addItem(item: T) {
        itemList.add(item)
        notifyItemInserted(itemList.size - 1)
    }

    fun addItems(newItems: List<T>) {
        val startPos = itemList.size
        itemList.addAll(newItems)
        notifyItemRangeInserted(startPos, newItems.size)
    }

    fun updateItem(position: Int, item: T) {
        if (position in 0 until itemList.size) {
            itemList[position] = item
            notifyItemChanged(position)
        }
    }

    fun removeItem(position: Int) {
        if (position in 0 until itemList.size) {
            itemList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemList.size - position)
        }
    }

    fun clearItems() {
        val count = itemList.size
        itemList.clear()
        notifyItemRangeRemoved(0, count)
    }

    fun getItems(): List<T> = itemList.toList()

    class BaseViewHolder<VB : ViewBinding>(val binding: VB) : RecyclerView.ViewHolder(binding.root)
}

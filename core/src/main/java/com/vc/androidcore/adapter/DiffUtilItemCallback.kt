package com.vc.androidcore.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

/**
 * Factory and builder for creating lightweight [DiffUtil.ItemCallback] instances cleanly.
 */
object DiffUtilItemCallback {

    /**
     * Creates a custom [DiffUtil.ItemCallback] using supplied lambdas.
     */
    fun <T : Any> create(
        areItemsTheSame: (oldItem: T, newItem: T) -> Boolean,
        areContentsTheSame: (oldItem: T, newItem: T) -> Boolean = { old, new -> old == new }
    ): DiffUtil.ItemCallback<T> {
        return object : DiffUtil.ItemCallback<T>() {
            override fun areItemsTheSame(oldItem: T, newItem: T): Boolean =
                areItemsTheSame(oldItem, newItem)

            override fun areContentsTheSame(oldItem: T, newItem: T): Boolean =
                areContentsTheSame(oldItem, newItem)
        }
    }

    /**
     * Creates a default equality-based [DiffUtil.ItemCallback].
     */
    fun <T : Any> default(): DiffUtil.ItemCallback<T> {
        return object : DiffUtil.ItemCallback<T>() {
            override fun areItemsTheSame(oldItem: T, newItem: T): Boolean = oldItem == newItem

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: T, newItem: T): Boolean = oldItem == newItem
        }
    }
}

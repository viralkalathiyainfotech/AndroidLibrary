package com.vc.androidcore.utils

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator

/**
 * Common RecyclerView extension functions.
 */
fun RecyclerView.setVerticalLayout(reverseLayout: Boolean = false) {
    this.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, reverseLayout)
}

fun RecyclerView.setHorizontalLayout(reverseLayout: Boolean = false) {
    this.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, reverseLayout)
}

fun RecyclerView.setGridLayout(spanCount: Int) {
    this.layoutManager = GridLayoutManager(context, spanCount)
}

fun RecyclerView.disableAnimation() {
    (this.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
}

fun RecyclerView.safeScrollToPosition(position: Int) {
    val count = this.adapter?.itemCount ?: 0
    if (position in 0 until count) {
        this.scrollToPosition(position)
    }
}

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

/**
 * Attaches an endless scroll listener to automatically trigger pagination when reaching the bottom threshold.
 *
 * @param threshold Number of items remaining before triggering next page fetch (default: 3).
 * @param startPage Initial starting page count (default: 1).
 * @param onLoadNextPage Lambda receiving the next page number (e.g. 2, 3, 4...).
 */
fun RecyclerView.onLoadMore(
    threshold: Int = 3,
    startPage: Int = 1,
    onLoadNextPage: (nextPage: Int) -> Unit
): RecyclerView.OnScrollListener {
    var currentPage = startPage
    var isLoading = false

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (dy <= 0) return

            val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return
            val totalItemCount = layoutManager.itemCount
            val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

            if (!isLoading && totalItemCount <= (lastVisibleItemPosition + threshold)) {
                isLoading = true
                currentPage++
                onLoadNextPage(currentPage)
            }
        }
    }
    addOnScrollListener(scrollListener)
    return scrollListener
}

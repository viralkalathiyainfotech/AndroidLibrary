package com.vc.androidcore.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState

/**
 * Reusable [PagingSource] implementation simplifying page fetching, pagination calculations,
 * and error capturing.
 *
 * @param Key Pagination key type (e.g. [Int] page index).
 * @param Value Item model type.
 * @param initialKey Starting page key.
 * @param fetchData Suspend lambda fetching a page given the current key and load size.
 * @param nextKeyProvider Computes the next key given the fetched items and current key.
 */
class BasePagingSource<Key : Any, Value : Any>(
    private val initialKey: Key,
    private val fetchData: suspend (key: Key, loadSize: Int) -> List<Value>,
    private val nextKeyProvider: (items: List<Value>, currentKey: Key) -> Key?
) : PagingSource<Key, Value>() {

    override fun getRefreshKey(state: PagingState<Key, Value>): Key? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey ?: initialKey
        }
    }

    override suspend fun load(params: LoadParams<Key>): LoadResult<Key, Value> {
        val currentKey = params.key ?: initialKey
        return try {
            val items = fetchData(currentKey, params.loadSize)
            val nextKey = if (items.isEmpty()) null else nextKeyProvider(items, currentKey)
            LoadResult.Page(
                data = items,
                prevKey = null,
                nextKey = nextKey
            )
        } catch (e: Throwable) {
            LoadResult.Error(e)
        }
    }
}

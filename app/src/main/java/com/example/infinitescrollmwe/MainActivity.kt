package com.example.infinitescrollmwe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.example.infinitescrollmwe.ui.theme.InfiniteScrollMWETheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow

class MainActivity : ComponentActivity() {
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val flow = Pager(PagingConfig(30), 0) {
            DaysMockPagingSource
        }.flow
        setContent {
            InfiniteScrollMWETheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    InfiniteScroll(flow)
                }
            }
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun InfiniteScroll(flow: Flow<PagingData<PageItem>>) {
    val items = flow.collectAsLazyPagingItems()
    LazyColumn(Modifier.fillMaxWidth()) {
        items(
            items,
            key = { it.key }
        ) { item ->
            when (item?.index) {
                null -> {
                    this@LazyColumn.item {
                        Text(text = "Page: ${item?.page}. key: ${item?.key}")
                    }
                }
                else -> {
                    this@LazyColumn.item {
                        Text(text = "Page: ${item.page}. Item: ${item.index}. key: ${item.key}")
                    }
                }
            }
        }
    }
}

data class PageItem(val page: Int, val index: Int?)

val PageItem.key: String
    get() = when (this.index) {
        null -> this.page.toString()
        else -> "${this.page} + ${this.index}"
    }

object DaysMockPagingSource : PagingSource<Int, PageItem>() {

    override fun getRefreshKey(state: PagingState<Int, PageItem>): Int? {
        val anchorPosition = state.anchorPosition
        return if (anchorPosition != null)
            state.closestItemToPosition(anchorPosition)?.page
        else
            null
    }

    @ExperimentalStdlibApi
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PageItem> {
        delay(500)
        val pageKey = params.key
        val res = buildList<PageItem> {
            pageKey?.let {
                add(PageItem(pageKey, null))
                repeat(30) {
                    add(PageItem(pageKey, it))
                }
            }
        }
        return LoadResult.Page(
            res,
            params.key?.minus(1),
            params.key?.plus(1)
        )
    }

}
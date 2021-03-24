package com.example.infinitescrollmwe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.*
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.example.infinitescrollmwe.ui.theme.InfiniteScrollMWETheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val flow = Pager(PagingConfig(10), 0) {
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

@Composable
fun InfiniteScroll(flow: Flow<PagingData<String>>) {
    val items = flow.collectAsLazyPagingItems()
    LazyColumn(Modifier.fillMaxWidth()) {
        items(items) {
            Text(text = it!!)
        }
    }
}


object DaysMockPagingSource : PagingSource<Int, String>() {
    override fun getRefreshKey(state: PagingState<Int, String>): Int? {
        val anchorPosition = state.anchorPosition
        return if (anchorPosition != null)
            state.anchorPosition?.div(10)
        else
            null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, String> {
        delay(500)
        return LoadResult.Page(
            (1..10).map { "PAGE ${params.key}: Index $it" },
            params.key?.minus(1),
            params.key?.plus(1)
        )
    }

}
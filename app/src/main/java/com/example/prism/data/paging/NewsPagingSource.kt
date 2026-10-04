package com.example.prism.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.prism.BuildConfig
import com.example.prism.data.remote.news.NewsApiService
import com.example.prism.data.remote.news.toDomain
import com.example.prism.domain.model.ContentModel
import timber.log.Timber

class NewsPagingSource(
    private val api: NewsApiService
) : PagingSource<Int, ContentModel.NewsContent>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ContentModel.NewsContent> {
        val page = params.key ?: 1
        return try {
            val response = api.searchNews(
                query = "technology OR sports OR food OR business OR health",
                page = page,
                pageSize = params.loadSize.coerceAtMost(20),
                apiKey = BuildConfig.NEWS_API_KEY,
                sortBy = "publishedAt"
            )
            val articles = response.articles.mapNotNull { it.toDomain() }
            Timber.d("NewsPagingSource: Loaded ${articles.size} articles")
            LoadResult.Page(
                data = articles,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (articles.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            Timber.e(e, "NewsPagingSource: Failed page $page")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ContentModel.NewsContent>): Int? =
        state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
}
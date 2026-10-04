package com.example.prism.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.prism.BuildConfig
import com.example.prism.data.remote.food.FoodApiService
import com.example.prism.data.remote.food.toDomain
import com.example.prism.domain.model.ContentModel
import timber.log.Timber

class FoodPagingSource(
    private val api: FoodApiService
) : PagingSource<Int, ContentModel.FoodContent>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ContentModel.FoodContent> {
        val page = params.key ?: 1
        val offset = (page - 1) * params.loadSize

        return try {
            val response = api.searchRecipes(
                apiKey = BuildConfig.FOOD_API_KEY,
                offset = offset,
                number = params.loadSize.coerceAtMost(10)
            )

            val recipes = response.results.map { it.toDomain() }
            Timber.d("FoodPagingSource: Loaded ${recipes.size} recipes (offset=$offset)")

            LoadResult.Page(
                data = recipes,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (recipes.isEmpty() || offset + recipes.size >= response.totalResults) null
                else page + 1
            )
        } catch (e: Exception) {
            Timber.e(e, "FoodPagingSource: Failed to load page $page")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ContentModel.FoodContent>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }
}
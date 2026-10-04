package com.example.prism.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.prism.BuildConfig
import com.example.prism.data.remote.food.FoodApiService
import com.example.prism.data.remote.food.toDomain
import com.example.prism.data.remote.news.NewsApiService
import com.example.prism.data.remote.news.toDomain
import com.example.prism.data.remote.sports.SportsApiService
import com.example.prism.data.remote.sports.toDomain
import com.example.prism.domain.model.ContentModel
import com.example.prism.domain.model.InterestRatio
import timber.log.Timber

class MixedFeedPagingSource(
    private val newsApi: NewsApiService,
    private val sportsApi: SportsApiService,
    private val foodApi: FoodApiService,
    private val ratio: InterestRatio,
    private val heroNewsId: String? = null
) : PagingSource<Int, ContentModel>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ContentModel> {
        val page = params.key ?: 1
        val result = mutableListOf<ContentModel>()

        return try {
            // NEWS
            val newsItems = try {
                newsApi.getTopHeadlines(
                    page = page,
                    pageSize = 10,
                    apiKey = BuildConfig.NEWS_API_KEY
                ).articles
                    .mapNotNull { it.toDomain() }
                    .filter { it.id != heroNewsId }
                    .take(8)
            } catch (e: Exception) {
                Timber.e(e, "News fetch failed")
                emptyList()
            }

            // SPORTS
            val sportsItems = try {
                val events = mutableListOf<ContentModel.SportsContent>()
                for (leagueId in listOf("4328", "4335", "4387")) {
                    val fetched = sportsApi.getNextLeagueEvents(leagueId)
                        .events?.mapNotNull { it.toDomain() } ?: emptyList()
                    events.addAll(fetched)
                    if (events.size >= 4) break
                }
                events.distinctBy { it.id }.take(4)
            } catch (e: Exception) {
                Timber.e(e, "Sports fetch failed")
                emptyList()
            }

            // FOOD
            val foodItems = try {
                foodApi.searchRecipes(
                    apiKey = BuildConfig.FOOD_API_KEY,
                    offset = (page - 1) * 4,
                    number = 4
                ).results.map { it.toDomain() }
            } catch (e: Exception) {
                Timber.e(e, "Food fetch failed")
                emptyList()
            }

            Timber.d("MixedFeed page $page: ${newsItems.size} news, ${sportsItems.size} sports, ${foodItems.size} food")

            val newsQ = ArrayDeque(newsItems)
            val sportsQ = ArrayDeque(sportsItems)
            val foodQ = ArrayDeque(foodItems)

            while (newsQ.isNotEmpty() || sportsQ.isNotEmpty() || foodQ.isNotEmpty()) {
                repeat(2) { if (newsQ.isNotEmpty()) result.add(newsQ.removeFirst()) }
                if (sportsQ.isNotEmpty()) result.add(sportsQ.removeFirst())
                repeat(2) { if (newsQ.isNotEmpty()) result.add(newsQ.removeFirst()) }
                if (foodQ.isNotEmpty()) result.add(foodQ.removeFirst())
                if (newsQ.isEmpty()) {
                    while (sportsQ.isNotEmpty()) result.add(sportsQ.removeFirst())
                    while (foodQ.isNotEmpty()) result.add(foodQ.removeFirst())
                    break
                }
            }

            Timber.d("MixedFeed: total ${result.size} items for page $page")

            LoadResult.Page(
                data = result,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (result.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            Timber.e(e, "MixedFeedPagingSource critical failure page $page")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ContentModel>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }
}
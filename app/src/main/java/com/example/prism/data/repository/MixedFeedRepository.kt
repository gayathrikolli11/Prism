package com.example.prism.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.prism.data.paging.MixedFeedPagingSource
import com.example.prism.data.remote.food.FoodApiService
import com.example.prism.data.remote.news.NewsApiService
import com.example.prism.data.remote.sports.SportsApiService
import com.example.prism.domain.model.ContentModel
import com.example.prism.domain.model.InterestRatio
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MixedFeedRepository @Inject constructor(
    private val newsApi: NewsApiService,
    private val sportsApi: SportsApiService,
    private val foodApi: FoodApiService
) {
    fun getMixedFeed(
        ratio: InterestRatio = InterestRatio.DEFAULT,
        heroNewsId: String? = null
    ): Flow<PagingData<ContentModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 3,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                MixedFeedPagingSource(
                    newsApi = newsApi,
                    sportsApi = sportsApi,
                    foodApi = foodApi,
                    ratio = ratio,
                    heroNewsId = heroNewsId
                )
            }
        ).flow
    }
}
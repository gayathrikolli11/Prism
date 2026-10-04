package com.example.prism.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.prism.data.paging.FoodPagingSource
import com.example.prism.data.remote.food.FoodApiService
import com.example.prism.domain.model.ContentModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FoodRepository @Inject constructor(
    private val api: FoodApiService
) {
    fun getFoodFeed(): Flow<PagingData<ContentModel.FoodContent>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                prefetchDistance = 3,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { FoodPagingSource(api) }
        ).flow
    }
}
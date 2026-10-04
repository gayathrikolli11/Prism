package com.example.prism.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.prism.data.paging.SportsPagingSource
import com.example.prism.data.remote.sports.SportsApiService
import com.example.prism.domain.model.ContentModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SportsRepository @Inject constructor(
    private val api: SportsApiService
) {
    fun getSportsFeed(): Flow<PagingData<ContentModel.SportsContent>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = PREFETCH_DISTANCE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { SportsPagingSource(api) }
        ).flow
    }

    companion object {
        private const val PAGE_SIZE = 20
        private const val PREFETCH_DISTANCE = 3
    }
}
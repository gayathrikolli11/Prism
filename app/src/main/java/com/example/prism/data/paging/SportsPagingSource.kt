package com.example.prism.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.prism.data.remote.sports.SportsApiService
import com.example.prism.data.remote.sports.toDomain
import com.example.prism.domain.model.ContentModel
import timber.log.Timber
class SportsPagingSource(
    private val api: SportsApiService
) : PagingSource<Int, ContentModel.SportsContent>() {

    // Major league IDs from TheSportsDB
    private val leagueIds = listOf(
        "4328", // English Premier League
        "4335", // La Liga
        "4387", // NBA
        "4391", // NFL
    )

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ContentModel.SportsContent> {
        val page = params.key ?: 1
        return try {
            val events = if (page == 1) {
                leagueIds.flatMap { leagueId ->
                    runCatching {
                        api.getNextLeagueEvents(leagueId).events ?: emptyList()
                    }.getOrDefault(emptyList())
                }
            } else {
                leagueIds.flatMap { leagueId ->
                    runCatching {
                        api.getPastLeagueEvents(leagueId).events ?: emptyList()
                    }.getOrDefault(emptyList())
                }
            }

            val content = events.mapNotNull { it.toDomain() }
            Timber.d("SportsPagingSource: Loaded ${content.size} events for page $page")

            LoadResult.Page(
                data = content,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (page >= 2) null else page + 1
            )
        } catch (e: Exception) {
            Timber.e(e, "SportsPagingSource: Failed to load page $page")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ContentModel.SportsContent>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }
}
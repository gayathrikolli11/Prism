package com.example.prism.data.remote.sports

import retrofit2.http.GET
import retrofit2.http.Query

interface SportsApiService {

    @GET("api/v1/json/123/eventsnextleague.php")
    suspend fun getNextLeagueEvents(
        @Query("id") leagueId: String
    ): SportsEventsResponse

    @GET("api/v1/json/123/eventspastleague.php")
    suspend fun getPastLeagueEvents(
        @Query("id") leagueId: String
    ): SportsEventsResponse
}
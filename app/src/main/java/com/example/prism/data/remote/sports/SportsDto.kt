package com.example.prism.data.remote.sports

import com.google.gson.annotations.SerializedName
import com.example.prism.domain.model.ContentModel

data class SportsEventsResponse(
    @SerializedName("events") val events: List<SportsEventDto>?
)

data class SportsEventDto(
    @SerializedName("idEvent") val idEvent: String,
    @SerializedName("strHomeTeam") val homeTeam: String?,
    @SerializedName("strAwayTeam") val awayTeam: String?,
    @SerializedName("intHomeScore") val homeScore: String?,
    @SerializedName("intAwayScore") val awayScore: String?,
    @SerializedName("strStatus") val status: String?,
    @SerializedName("strLeague") val league: String?,
    @SerializedName("dateEvent") val date: String?,
    @SerializedName("strHomeTeamBadge") val homeTeamBadge: String?,
    @SerializedName("strAwayTeamBadge") val awayTeamBadge: String?
)

fun SportsEventDto.toDomain(): ContentModel.SportsContent? {
    val home = homeTeam ?: return null
    val away = awayTeam ?: return null
    return ContentModel.SportsContent(
        id = idEvent,
        homeTeam = home,
        awayTeam = away,
        homeScore = homeScore,
        awayScore = awayScore,
        status = status ?: "Scheduled",
        league = league ?: "",
        date = date ?: "",
        homeTeamBadge = homeTeamBadge,
        awayTeamBadge = awayTeamBadge
    )
}
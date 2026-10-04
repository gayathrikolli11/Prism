package com.example.prism.domain.model

sealed class ContentModel {
    abstract val id: String
    abstract val interest: Interest

    data class NewsContent(
        override val id: String,
        override val interest: Interest = Interest.NEWS,
        val title: String,
        val description: String?,
        val imageUrl: String?,
        val sourceName: String,
        val publishedAt: String,
        val url: String
    ) : ContentModel()

    data class SportsContent(
        override val id: String,
        override val interest: Interest = Interest.SPORTS,
        val homeTeam: String,
        val awayTeam: String,
        val homeScore: String?,
        val awayScore: String?,
        val status: String,
        val league: String,
        val date: String,
        val homeTeamBadge: String?,
        val awayTeamBadge: String?
    ) : ContentModel()

    data class FoodContent(
        override val id: String,
        override val interest: Interest = Interest.FOOD,
        val title: String,
        val imageUrl: String?,
        val cuisineType: String,
        val readyInMinutes: Int,
        val servings: Int,
        val summary: String?,
        val sourceUrl: String?,
        val calories: Int?,
        val dishTypes: List<String>
    ) : ContentModel()
}
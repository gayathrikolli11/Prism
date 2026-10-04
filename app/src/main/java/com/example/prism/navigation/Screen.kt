package com.example.prism.navigation

import kotlinx.serialization.Serializable

@Serializable
object Feed

@Serializable
data class NewsDetail(
    val url: String,
    val title: String,
    val source: String,
    val description: String
)

@Serializable
data class SportsDetail(
    val eventId: String,
    val homeTeam: String,
    val awayTeam: String,
    val homeScore: String,
    val awayScore: String,
    val league: String,
    val status: String,
    val date: String
)

@Serializable
data class FoodDetail(
    val id: String,
    val title: String,
    val imageUrl: String,
    val cuisineType: String,
    val readyInMinutes: Int,
    val servings: Int,
    val sourceUrl: String,
    val calories: Int
)
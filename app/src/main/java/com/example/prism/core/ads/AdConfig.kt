package com.example.prism.core.ads

import com.example.prism.BuildConfig
import com.example.prism.domain.model.Interest

data class AdConfig(
    val adUnitId: String,
    val frequencyItems: Int,
    val style: AdStyle
)

enum class AdStyle {
    EDITORIAL,    // Subtle — blends with news cards
    SCOREBOARD,   // Bold — fits sports feed
    APPETIZING,   // Warm rich style — fits food feed
    STANDARD      // Default for mixed/exploring feed
}

object AdConfigs {
    fun forInterest(interest: Interest): AdConfig = when (interest) {
        Interest.NEWS -> AdConfig(
            adUnitId = BuildConfig.ADMOB_NEWS_AD_UNIT_ID,
            frequencyItems = 6,
            style = AdStyle.EDITORIAL
        )
        Interest.SPORTS -> AdConfig(
            adUnitId = BuildConfig.ADMOB_SPORTS_AD_UNIT_ID,
            frequencyItems = 4,
            style = AdStyle.SCOREBOARD
        )
        Interest.FOOD -> AdConfig(
            adUnitId = BuildConfig.ADMOB_FOOD_AD_UNIT_ID,
            frequencyItems = 5,
            style = AdStyle.APPETIZING
        )
        Interest.NONE -> AdConfig(
            adUnitId = BuildConfig.ADMOB_NEWS_AD_UNIT_ID,
            frequencyItems = 7,
            style = AdStyle.STANDARD
        )
    }
}
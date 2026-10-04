package com.example.prism.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.prism.domain.model.Interest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.interestDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "prism_interests"
)

@Singleton
class InterestDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dominantInterestKey = stringPreferencesKey("dominant_interest")
    private val newsScoreKey = floatPreferencesKey("score_news")
    private val sportsScoreKey = floatPreferencesKey("score_sports")
    private val foodScoreKey = floatPreferencesKey("score_food")

    val dominantInterest: Flow<Interest> = context.interestDataStore.data.map { prefs ->
        val newsScore = prefs[newsScoreKey] ?: 0f
        val sportsScore = prefs[sportsScoreKey] ?: 0f
        val foodScore = prefs[foodScoreKey] ?: 0f
        val totalScore = newsScore + sportsScore + foodScore

        if (totalScore == 0f) return@map Interest.NONE

        val name = prefs[dominantInterestKey] ?: Interest.NONE.name
        runCatching { Interest.valueOf(name) }.getOrDefault(Interest.NONE)
    }

    val interestScores: Flow<Map<Interest, Float>> = context.interestDataStore.data.map { prefs ->
        mapOf(
            Interest.NEWS to (prefs[newsScoreKey] ?: 0f),
            Interest.SPORTS to (prefs[sportsScoreKey] ?: 0f),
            Interest.FOOD to (prefs[foodScoreKey] ?: 0f)
        )
    }

    suspend fun updateScores(scores: Map<Interest, Float>) {
        context.interestDataStore.edit { prefs ->
            prefs[newsScoreKey] = scores[Interest.NEWS] ?: 0f
            prefs[sportsScoreKey] = scores[Interest.SPORTS] ?: 0f
            prefs[foodScoreKey] = scores[Interest.FOOD] ?: 0f
            prefs[dominantInterestKey] = Interest.fromScore(scores).name
        }
    }

    suspend fun updateScoresWithDominant(scores: Map<Interest, Float>, dominant: Interest) {
        context.interestDataStore.edit { prefs ->
            prefs[newsScoreKey] = scores[Interest.NEWS] ?: 0f
            prefs[sportsScoreKey] = scores[Interest.SPORTS] ?: 0f
            prefs[foodScoreKey] = scores[Interest.FOOD] ?: 0f
            prefs[dominantInterestKey] = dominant.name
        }
    }

    suspend fun resetScores() {
        context.interestDataStore.edit { prefs ->
            prefs[newsScoreKey] = 0f
            prefs[sportsScoreKey] = 0f
            prefs[foodScoreKey] = 0f
            prefs[dominantInterestKey] = Interest.NONE.name
        }
    }
}
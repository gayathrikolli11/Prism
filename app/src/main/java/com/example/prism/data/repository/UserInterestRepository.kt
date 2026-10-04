package com.example.prism.data.repository

import com.example.prism.data.local.BehaviorDao
import com.example.prism.data.local.InterestDataStore
import com.example.prism.domain.model.BehaviorEvent
import com.example.prism.domain.model.Interest
import com.example.prism.domain.model.UserBehavior
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.exp

@Singleton
class UserInterestRepository @Inject constructor(
    private val behaviorDao: BehaviorDao,
    private val interestDataStore: InterestDataStore
) {
    val dominantInterest: Flow<Interest> = interestDataStore.dominantInterest
    val interestScores: Flow<Map<Interest, Float>> = interestDataStore.interestScores

    private var lastDominantInterest: Interest = Interest.NONE

    suspend fun recordBehavior(behavior: UserBehavior) {
        recomputeScores()
        Timber.d("UserInterestRepository: Recorded ${behavior.eventType} for ${behavior.interest}")
    }

    private suspend fun recomputeScores() {
        val since = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(SCORING_WINDOW_DAYS)

        val scores = mutableMapOf(
            Interest.NEWS to 0f,
            Interest.SPORTS to 0f,
            Interest.FOOD to 0f
        )

        val stats = behaviorDao.getBehaviorStats(since)

        for (stat in stats) {
            val interest = stat.interest
            if (interest == Interest.NONE) continue

            val eventType = try {
                BehaviorEvent.valueOf(stat.eventType)
            } catch (e: IllegalArgumentException) { continue }

            val approximateAgeMs = TimeUnit.DAYS.toMillis(SCORING_WINDOW_DAYS / 4)
            val score = computeScore(
                eventType = eventType,
                count = stat.count,
                avgDuration = stat.avgDuration,
                ageMs = approximateAgeMs
            )
            scores[interest] = (scores[interest] ?: 0f) + score
        }

        // If all scores are zero (fresh install) reset lastDominantInterest
        val totalScore = scores.values.sum()
        if (totalScore == 0f) lastDominantInterest = Interest.NONE

        val dominant = determineDominantInterest(scores)
        Timber.d("UserInterestRepository: Scores=$scores Dominant=$dominant")
        interestDataStore.updateScoresWithDominant(scores, dominant)
    }

    private fun determineDominantInterest(scores: Map<Interest, Float>): Interest {
        val sorted = scores.entries
            .filter { it.key != Interest.NONE }
            .sortedByDescending { it.value }

        if (sorted.isEmpty()) return Interest.NONE

        val top = sorted[0]
        val second = sorted.getOrNull(1)

        val newDominant = if (top.value >= DOMINANCE_THRESHOLD &&
            (second == null || top.value - second.value >= DOMINANCE_MARGIN)
        ) top.key else Interest.NONE

        return if (newDominant == Interest.NONE && lastDominantInterest != Interest.NONE) {
            lastDominantInterest // Hold previous interest during gap
        } else {
            lastDominantInterest = newDominant
            newDominant
        }
    }

    suspend fun pruneOldBehaviorData() {
        val cutoff = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(PRUNE_AFTER_DAYS)
        behaviorDao.pruneOldEvents(cutoff)
    }

    private fun computeScore(
        eventType: BehaviorEvent,
        count: Int,
        avgDuration: Double,
        ageMs: Long
    ): Float {
        val baseScore = when (eventType) {
            BehaviorEvent.CLICK -> CLICK_SCORE * count
            BehaviorEvent.DWELL -> {
                val dwellSeconds = (avgDuration / 1000).coerceAtMost(MAX_DWELL_SECONDS)
                DWELL_SCORE_PER_SECOND * dwellSeconds * count
            }
            BehaviorEvent.SHARE -> SHARE_SCORE * count
            BehaviorEvent.DISMISS -> DISMISS_SCORE * count
        }
        val ageInDays = ageMs.toDouble() / TimeUnit.DAYS.toMillis(1)
        val decayFactor = exp(-DECAY_RATE * ageInDays)
        return (baseScore * decayFactor).toFloat()
    }

    companion object {
        private const val SCORING_WINDOW_DAYS = 30L
        private const val PRUNE_AFTER_DAYS = 30L
        private const val CLICK_SCORE = 6.0
        private const val DWELL_SCORE_PER_SECOND = 2.0
        private const val MAX_DWELL_SECONDS = 5.0
        private const val SHARE_SCORE = 8.0
        private const val DISMISS_SCORE = -3.0
        private const val DECAY_RATE = 0.02
        private const val DOMINANCE_THRESHOLD = 10f
        private const val DOMINANCE_MARGIN = 5f
    }
}
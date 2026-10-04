package com.example.prism.core.analytics

import com.example.prism.domain.model.Interest
import com.example.prism.domain.model.InterestRatio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class SessionInterestTracker @Inject constructor() {

    private val _sessionScores = MutableStateFlow(
        mapOf(
            Interest.NEWS to 0f,
            Interest.SPORTS to 0f,
            Interest.FOOD to 0f
        )
    )
    val sessionScores: StateFlow<Map<Interest, Float>> = _sessionScores.asStateFlow()

    private val _sessionRatio = MutableStateFlow(InterestRatio.DEFAULT)
    val sessionRatio: StateFlow<InterestRatio> = _sessionRatio.asStateFlow()

    fun recordClick(interest: Interest) {
        if (interest == Interest.NONE) return
        updateScore(interest, CLICK_WEIGHT)
        Timber.d("SessionTracker: Click on $interest — new ratio: ${_sessionRatio.value}")
    }

    fun recordDwell(interest: Interest, durationMs: Long) {
        if (interest == Interest.NONE) return
        val dwellSeconds = (durationMs / 1000f).coerceAtMost(MAX_DWELL_SECONDS)
        updateScore(interest, dwellSeconds * DWELL_WEIGHT_PER_SECOND)
        Timber.d("SessionTracker: Dwell ${durationMs}ms on $interest — new ratio: ${_sessionRatio.value}")
    }

    fun recordDismiss(interest: Interest) {
        if (interest == Interest.NONE) return
        updateScore(interest, DISMISS_WEIGHT)
        Timber.d("SessionTracker: Dismiss on $interest — new ratio: ${_sessionRatio.value}")
    }

    fun seedWithPersistentScores(persistentScores: Map<Interest, Float>) {
        _sessionScores.update { current ->
            mapOf(
                Interest.NEWS to (current[Interest.NEWS] ?: 0f) + (persistentScores[Interest.NEWS] ?: 0f) * PERSISTENT_WEIGHT,
                Interest.SPORTS to (current[Interest.SPORTS] ?: 0f) + (persistentScores[Interest.SPORTS] ?: 0f) * PERSISTENT_WEIGHT,
                Interest.FOOD to (current[Interest.FOOD] ?: 0f) + (persistentScores[Interest.FOOD] ?: 0f) * PERSISTENT_WEIGHT
            )
        }
        recomputeRatio()
    }

    fun resetSession() {
        _sessionScores.value = mapOf(
            Interest.NEWS to 0f,
            Interest.SPORTS to 0f,
            Interest.FOOD to 0f
        )
        _sessionRatio.value = InterestRatio.DEFAULT
    }

    private fun updateScore(interest: Interest, delta: Float) {
        _sessionScores.update { current ->
            current.toMutableMap().apply {
                this[interest] = (this[interest] ?: 0f) + delta
            }
        }
        recomputeRatio()
    }

    private fun recomputeRatio() {
        _sessionRatio.value = InterestRatio.fromScores(_sessionScores.value)
    }

    companion object {
        private const val CLICK_WEIGHT = 8f   // Faster session ratio shift
        private const val DWELL_WEIGHT_PER_SECOND = 2f
        private const val MAX_DWELL_SECONDS = 5f
        private const val DISMISS_WEIGHT = -2f
        private const val PERSISTENT_WEIGHT = 0.5f // Historical scores contribute more // Historical scores contribute 30% to session
    }
}
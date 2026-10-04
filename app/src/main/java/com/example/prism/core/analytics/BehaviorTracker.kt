package com.example.prism.core.analytics

import com.example.prism.domain.model.BehaviorEvent
import com.example.prism.domain.model.Interest
import com.example.prism.domain.model.UserBehavior
import com.example.prism.domain.usecase.TrackBehaviorUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class BehaviorTracker @Inject constructor(
    private val trackBehaviorUseCase: TrackBehaviorUseCase
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Active dwell timers keyed by contentId
    private val dwellJobs = mutableMapOf<String, Job>()
    private val dwellStartTimes = mutableMapOf<String, Long>()

    fun onItemVisible(contentId: String, interest: Interest) {
        if (dwellJobs.containsKey(contentId)) return
        dwellStartTimes[contentId] = System.currentTimeMillis()
        dwellJobs[contentId] = scope.launch {
            delay(MINIMUM_DWELL_MS)
            val duration = System.currentTimeMillis() - (dwellStartTimes[contentId] ?: return@launch)
            recordEvent(
                UserBehavior(
                    contentId = contentId,
                    interest = interest,
                    eventType = BehaviorEvent.DWELL,
                    durationMs = duration
                )
            )
        }
    }

    fun onItemHidden(contentId: String) {
        dwellJobs[contentId]?.cancel()
        dwellJobs.remove(contentId)
        dwellStartTimes.remove(contentId)
    }

    fun onItemClicked(contentId: String, interest: Interest) {
        onItemHidden(contentId)
        recordEvent(
            UserBehavior(
                contentId = contentId,
                interest = interest,
                eventType = BehaviorEvent.CLICK
            )
        )
    }

    fun onItemShared(contentId: String, interest: Interest) {
        recordEvent(
            UserBehavior(
                contentId = contentId,
                interest = interest,
                eventType = BehaviorEvent.SHARE
            )
        )
    }

    fun onItemDismissed(contentId: String, interest: Interest) {
        onItemHidden(contentId)
        recordEvent(
            UserBehavior(
                contentId = contentId,
                interest = interest,
                eventType = BehaviorEvent.DISMISS
            )
        )
    }

    private fun recordEvent(behavior: UserBehavior) {
        scope.launch {
            trackBehaviorUseCase(behavior)
            Timber.d("BehaviorTracker: ${behavior.eventType} on ${behavior.interest} (${behavior.contentId})")
        }
    }

    companion object {
        private const val MINIMUM_DWELL_MS = 1_000L
    }
}
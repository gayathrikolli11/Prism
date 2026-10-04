package com.example.prism.domain.model

data class UserBehavior(
    val contentId: String,
    val interest: Interest,
    val eventType: BehaviorEvent,
    val durationMs: Long = 0L, // For DWELL events — how long they viewed the item
    val timestamp: Long = System.currentTimeMillis()
)

enum class BehaviorEvent {
    CLICK,
    DWELL,
    SHARE,
    DISMISS
}
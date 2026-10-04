package com.example.prism.domain.model

enum class Interest(val displayName: String, val emoji: String) {
    NEWS("News", "📰"),
    SPORTS("Sports", "⚽"),
    FOOD("Food", "🍴"),
    NONE("Exploring", "✨");

    companion object {
        fun fromScore(scores: Map<Interest, Float>): Interest {
            if (scores.values.all { it == 0f }) return NONE
            return scores.maxByOrNull { it.value }?.key ?: NONE
        }
    }
}
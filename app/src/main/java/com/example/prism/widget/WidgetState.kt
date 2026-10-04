package com.example.prism.widget

import com.example.prism.domain.model.Interest

data class WidgetState(
    val dominantInterest: Interest = Interest.NONE,
    val headline: String = "Loading your feed...",
    val subtext: String = "",
    val imageUrl: String? = null,
    val contentId: String? = null
)
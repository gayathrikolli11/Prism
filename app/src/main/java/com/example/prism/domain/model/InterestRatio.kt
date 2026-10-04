package com.example.prism.domain.model

data class InterestRatio(
    val news: Float,
    val sports: Float,
    val food: Float
) {
    companion object {
        val DEFAULT = InterestRatio(news = 0.5f, sports = 0.3f, food = 0.2f)
        private const val MINIMUM_RATIO = 0.05f

        fun fromScores(scores: Map<Interest, Float>): InterestRatio {
            val news = (scores[Interest.NEWS] ?: 0f).coerceAtLeast(0f)
            val sports = (scores[Interest.SPORTS] ?: 0f).coerceAtLeast(0f)
            val food = (scores[Interest.FOOD] ?: 0f).coerceAtLeast(0f)
            val total = news + sports + food

            return if (total == 0f) {
                DEFAULT
            } else {
                val rawNews = news / total
                val rawSports = sports / total
                val rawFood = food / total

                val flooredNews = rawNews.coerceAtLeast(MINIMUM_RATIO)
                val flooredSports = rawSports.coerceAtLeast(MINIMUM_RATIO)
                val flooredFood = rawFood.coerceAtLeast(MINIMUM_RATIO)
                val flooredTotal = flooredNews + flooredSports + flooredFood

                InterestRatio(
                    news = flooredNews / flooredTotal,
                    sports = flooredSports / flooredTotal,
                    food = flooredFood / flooredTotal
                )
            }
        }
    }

    fun itemCountsForPageSize(pageSize: Int): Triple<Int, Int, Int> {
        val newsCount = (pageSize * news).toInt().coerceAtLeast(1)
        val sportsCount = (pageSize * sports).toInt().coerceAtLeast(1)
        val foodCount = (pageSize - newsCount - sportsCount).coerceAtLeast(1)
        return Triple(newsCount, sportsCount, foodCount)
    }

    override fun toString(): String =
        "InterestRatio(news=${(news * 100).toInt()}%, sports=${(sports * 100).toInt()}%, food=${(food * 100).toInt()}%)"
}
package com.example.prism.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.prism.BuildConfig
import com.example.prism.core.ads.AdManager
import com.example.prism.core.analytics.BehaviorTracker
import com.example.prism.core.analytics.SessionInterestTracker
import com.example.prism.data.remote.food.FoodApiService
import com.example.prism.data.remote.food.toDomain
import com.example.prism.data.remote.news.NewsApiService
import com.example.prism.data.remote.news.toDomain
import com.example.prism.data.remote.sports.SportsApiService
import com.example.prism.data.remote.sports.toDomain
import com.example.prism.data.repository.MixedFeedRepository
import com.example.prism.domain.model.ContentModel
import com.example.prism.domain.model.Interest
import com.example.prism.domain.model.InterestRatio
import com.example.prism.domain.usecase.GetDominantInterestUseCase
import com.example.prism.domain.usecase.GetInterestScoresUseCase
import com.example.prism.widget.WidgetStateManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class FeedViewModel @Inject constructor(
    private val mixedFeedRepository: MixedFeedRepository,
    private val newsApi: NewsApiService,
    private val sportsApi: SportsApiService,
    private val foodApi: FoodApiService,
    private val widgetStateManager: WidgetStateManager,
    val adManager: AdManager,
    val behaviorTracker: BehaviorTracker,
    val sessionTracker: SessionInterestTracker,
    getDominantInterestUseCase: GetDominantInterestUseCase,
    getInterestScoresUseCase: GetInterestScoresUseCase
) : ViewModel() {

    private val initialInterest: Interest = runBlocking {
        getDominantInterestUseCase().first()
    }

    val dominantInterest: StateFlow<Interest> = getDominantInterestUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = initialInterest
        )

    val persistentScores: StateFlow<Map<Interest, Float>> = getInterestScoresUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyMap()
        )

    val sessionRatio: StateFlow<InterestRatio> = sessionTracker.sessionRatio

    val heroNews = MutableStateFlow<ContentModel.NewsContent?>(null)
    val heroSports = MutableStateFlow<ContentModel.SportsContent?>(null)
    val heroFood = MutableStateFlow<ContentModel.FoodContent?>(null)

    init {
        viewModelScope.launch {
            persistentScores.collect { scores ->
                if (scores.isNotEmpty()) sessionTracker.seedWithPersistentScores(scores)
            }
        }
        viewModelScope.launch { adManager.preloadAllInterests() }
        fetchHeroContent()
        viewModelScope.launch {
            combine(dominantInterest, heroNews, heroSports, heroFood) {
                    interest, news, sports, food ->
                widgetStateManager.updateFromContent(interest, news, sports, food)
            }.collect {}
        }
    }

    private fun fetchHeroContent() {
        viewModelScope.launch {
            runCatching {
                newsApi.getTopHeadlines(page = 1, pageSize = 5, apiKey = BuildConfig.NEWS_API_KEY)
                    .articles.firstNotNullOfOrNull { it.toDomain() }
            }.onSuccess { heroNews.value = it }
                .onFailure { Timber.e(it, "Hero news fetch failed") }
        }
        viewModelScope.launch {
            runCatching {
                sportsApi.getNextLeagueEvents("4328")
                    .events?.firstNotNullOfOrNull { it.toDomain() }
            }.onSuccess { heroSports.value = it }
                .onFailure { Timber.e(it, "Hero sports fetch failed") }
        }
        viewModelScope.launch {
            runCatching {
                foodApi.searchRecipes(apiKey = BuildConfig.FOOD_API_KEY, number = 1)
                    .results.firstOrNull()?.toDomain()
            }.onSuccess { heroFood.value = it }
                .onFailure { Timber.e(it, "Hero food fetch failed") }
        }
    }

    val contentFeed: Flow<PagingData<ContentModel>> = dominantInterest
        .flatMapLatest { interest ->
            val scores = persistentScores.value
            val ratio = computeRatio(interest, sessionTracker.sessionRatio.value, scores)
            val heroId = heroNews.value?.id
            Timber.d("FeedViewModel: interest=$interest ratio=$ratio")
            adManager.preloadAd(interest)
            @Suppress("UNCHECKED_CAST")
            mixedFeedRepository.getMixedFeed(ratio, heroId) as Flow<PagingData<ContentModel>>
        }
        .cachedIn(viewModelScope)

    private fun computeRatio(
        interest: Interest,
        sessionRatio: InterestRatio,
        scores: Map<Interest, Float>
    ): InterestRatio {
        if (interest == Interest.NONE) return sessionRatio
        val DOMINANT = 0.80f
        val MINORITY = 0.20f
        val others = listOf(Interest.NEWS, Interest.SPORTS, Interest.FOOD).filter { it != interest }
        val otherScores = others.associateWith { (scores[it] ?: 0f).coerceAtLeast(0f) }
        val otherTotal = otherScores.values.sum()
        val ratios = if (otherTotal == 0f) others.associateWith { MINORITY / others.size }
        else others.associateWith { (otherScores[it] ?: 0f) / otherTotal * MINORITY }
        return when (interest) {
            Interest.NEWS -> InterestRatio(news = DOMINANT, sports = ratios[Interest.SPORTS] ?: MINORITY / 2, food = ratios[Interest.FOOD] ?: MINORITY / 2)
            Interest.SPORTS -> InterestRatio(news = ratios[Interest.NEWS] ?: MINORITY / 2, sports = DOMINANT, food = ratios[Interest.FOOD] ?: MINORITY / 2)
            Interest.FOOD -> InterestRatio(news = ratios[Interest.NEWS] ?: MINORITY / 2, sports = ratios[Interest.SPORTS] ?: MINORITY / 2, food = DOMINANT)
            Interest.NONE -> sessionRatio
        }
    }

    fun onContentClicked(interest: Interest) {
        sessionTracker.recordClick(interest)
    }

    fun onContentDwelled(interest: Interest, durationMs: Long) {
        sessionTracker.recordDwell(interest, durationMs)
    }

    fun onContentDismissed(interest: Interest) {
        sessionTracker.recordDismiss(interest)
    }
}
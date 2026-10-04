package com.example.prism.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.prism.core.ads.AdConfig
import com.example.prism.core.ads.AdConfigs
import com.example.prism.domain.model.ContentModel
import com.example.prism.domain.model.Interest
import com.example.prism.ui.components.AdCard
import com.example.prism.ui.components.ErrorScreen
import com.example.prism.ui.components.LoadingScreen
import com.example.prism.ui.components.PagingAppendLoader
import com.example.prism.ui.feed.FeedViewModel
import com.example.prism.ui.food.FoodCard
import com.example.prism.ui.news.NewsCard
import com.example.prism.ui.sports.SportsCard
import com.example.prism.ui.theme.PrismDynamicTheme
import com.google.android.gms.ads.nativead.NativeAd

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onArticleClick: (ContentModel.NewsContent) -> Unit,
    onEventClick: (ContentModel.SportsContent) -> Unit,
    onFoodClick: (ContentModel.FoodContent) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = hiltViewModel()
) {
    val dominantInterest by viewModel.dominantInterest.collectAsStateWithLifecycle()
    val heroNews by viewModel.heroNews.collectAsState()
    val heroSports by viewModel.heroSports.collectAsState()
    val heroFood by viewModel.heroFood.collectAsState()
    val adCache by viewModel.adManager.adCache.collectAsState()
    val adConfig = AdConfigs.forInterest(dominantInterest)
    val currentAd = adCache[dominantInterest] ?: adCache[Interest.NEWS]
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    PrismDynamicTheme(interest = dominantInterest) {
        Scaffold(
            modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "Prism",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${dominantInterest.emoji} ${dominantInterest.displayName} Feed",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        scrolledContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            AnimatedContent(
                targetState = dominantInterest,
                transitionSpec = {
                    (fadeIn(tween(600)) + slideInVertically(tween(600)) { it / 6 })
                        .togetherWith(fadeOut(tween(400)) + slideOutVertically(tween(400)) { -it / 6 })
                },
                label = "layout_transition",
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues)
            ) { interest ->
                when (interest) {
                    Interest.SPORTS -> SportsHomeLayout(
                        viewModel = viewModel,
                        heroSports = heroSports,
                        heroNews = heroNews,
                        adConfig = adConfig,
                        currentAd = currentAd,
                        onEventClick = onEventClick,
                        onArticleClick = onArticleClick,
                        onFoodClick = onFoodClick
                    )
                    Interest.FOOD -> FoodHomeLayout(
                        viewModel = viewModel,
                        heroFood = heroFood,
                        heroNews = heroNews,
                        adConfig = adConfig,
                        currentAd = currentAd,
                        onFoodClick = onFoodClick,
                        onArticleClick = onArticleClick,
                        onEventClick = onEventClick
                    )
                    else -> NewsHomeLayout(
                        viewModel = viewModel,
                        heroNews = heroNews,
                        heroSports = heroSports,
                        heroFood = heroFood,
                        dominantInterest = interest,
                        adConfig = adConfig,
                        currentAd = currentAd,
                        onArticleClick = onArticleClick,
                        onEventClick = onEventClick,
                        onFoodClick = onFoodClick
                    )
                }
            }
        }
    }
}

@Composable
private fun NewsHomeLayout(
    viewModel: FeedViewModel,
    heroNews: ContentModel.NewsContent?,
    heroSports: ContentModel.SportsContent?,
    heroFood: ContentModel.FoodContent?,
    dominantInterest: Interest,
    adConfig: AdConfig,
    currentAd: NativeAd?,
    onArticleClick: (ContentModel.NewsContent) -> Unit,
    onEventClick: (ContentModel.SportsContent) -> Unit,
    onFoodClick: (ContentModel.FoodContent) -> Unit
) {
    val items = viewModel.contentFeed.collectAsLazyPagingItems()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            HeroSection(
                interest = dominantInterest,
                newsContent = heroNews,
                sportsContent = heroSports,
                foodContent = heroFood,
                onNewsClick = { article ->
                    viewModel.behaviorTracker.onItemClicked(article.id, Interest.NEWS)
                    viewModel.onContentClicked(Interest.NEWS)
                    onArticleClick(article)
                },
                onSportsClick = { event ->
                    viewModel.behaviorTracker.onItemClicked(event.id, Interest.SPORTS)
                    viewModel.onContentClicked(Interest.SPORTS)
                    onEventClick(event)
                },
                onFoodClick = { recipe ->
                    viewModel.behaviorTracker.onItemClicked(recipe.id, Interest.FOOD)
                    viewModel.onContentClicked(Interest.FOOD)
                    onFoodClick(recipe)
                }
            )
        }

        if (currentAd != null) {
            item {
                AdCard(
                    headline = currentAd.headline ?: "Sponsored",
                    body = currentAd.body,
                    advertiser = currentAd.advertiser,
                    style = adConfig.style,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        item {
            Text(
                text = "Your Feed",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        when {
            items.loadState.refresh is LoadState.Loading ->
                item { LoadingScreen() }
            items.loadState.refresh is LoadState.Error ->
                item {
                    ErrorScreen(
                        message = (items.loadState.refresh as LoadState.Error)
                            .error.localizedMessage ?: "Couldn't load feed",
                        onRetry = { items.refresh() }
                    )
                }
            else -> {
                items(
                    count = items.itemCount,
                    key = { index -> "${items.peek(index)?.id}_$index" }
                ) { index ->
                    val item = items[index] ?: return@items
                    if (index > 0 && index % adConfig.frequencyItems == 0 && currentAd != null) {
                        AdCard(
                            headline = currentAd.headline ?: "Sponsored",
                            body = currentAd.body,
                            advertiser = currentAd.advertiser,
                            style = adConfig.style,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        when (item) {
                            is ContentModel.NewsContent -> NewsCard(
                                article = item,
                                onClick = {
                                    viewModel.behaviorTracker.onItemClicked(item.id, Interest.NEWS)
                                    viewModel.onContentClicked(Interest.NEWS)
                                    onArticleClick(item)
                                }
                            )
                            is ContentModel.SportsContent -> SportsCard(
                                event = item,
                                onClick = {
                                    viewModel.behaviorTracker.onItemClicked(item.id, Interest.SPORTS)
                                    viewModel.onContentClicked(Interest.SPORTS)
                                    onEventClick(item)
                                }
                            )
                            is ContentModel.FoodContent -> FoodCard(
                                recipe = item,
                                compact = false,
                                onClick = {
                                    viewModel.behaviorTracker.onItemClicked(item.id, Interest.FOOD)
                                    viewModel.onContentClicked(Interest.FOOD)
                                    onFoodClick(item)
                                }
                            )
                        }
                    }
                }
                item { PagingAppendLoader(loadState = items.loadState.append) }
            }
        }
    }
}

@Composable
private fun SportsHomeLayout(
    viewModel: FeedViewModel,
    heroSports: ContentModel.SportsContent?,
    heroNews: ContentModel.NewsContent?,
    adConfig: AdConfig,
    currentAd: NativeAd?,
    onEventClick: (ContentModel.SportsContent) -> Unit,
    onArticleClick: (ContentModel.NewsContent) -> Unit,
    onFoodClick: (ContentModel.FoodContent) -> Unit
) {
    val items = viewModel.contentFeed.collectAsLazyPagingItems()

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item(span = { GridItemSpan(2) }) {
            Column {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "⚽ Featured Match",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                heroSports?.let {
                    SportsHero(event = it, onClick = {
                        viewModel.behaviorTracker.onItemClicked(it.id, Interest.SPORTS)
                        viewModel.onContentClicked(Interest.SPORTS)
                        onEventClick(it)
                    })
                } ?: heroNews?.let {
                    NewsHero(article = it, onClick = {
                        viewModel.behaviorTracker.onItemClicked(it.id, Interest.NEWS)
                        viewModel.onContentClicked(Interest.NEWS)
                        onArticleClick(it)
                    })
                }
            }
        }

        if (currentAd != null) {
            item(span = { GridItemSpan(2) }) {
                AdCard(
                    headline = currentAd.headline ?: "Sponsored",
                    body = currentAd.body,
                    advertiser = currentAd.advertiser,
                    style = adConfig.style
                )
            }
        }

        item(span = { GridItemSpan(2) }) {
            Text(
                text = "Your Feed",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        when {
            items.loadState.refresh is LoadState.Loading ->
                item(span = { GridItemSpan(2) }) { LoadingScreen() }
            items.loadState.refresh is LoadState.Error ->
                item(span = { GridItemSpan(2) }) {
                    ErrorScreen(
                        message = (items.loadState.refresh as LoadState.Error)
                            .error.localizedMessage ?: "Couldn't load feed",
                        onRetry = { items.refresh() }
                    )
                }
            else -> {
                items(
                    count = items.itemCount,
                    key = { index -> "${items.peek(index)?.id}_$index" },
                    span = { index ->
                        val item = items.peek(index)
                        when {
                            index > 0 && index % adConfig.frequencyItems == 0 && currentAd != null -> GridItemSpan(2)
                            item != null && item !is ContentModel.SportsContent -> GridItemSpan(2)
                            else -> GridItemSpan(1)
                        }
                    }
                ) { index ->
                    val item = items[index] ?: return@items
                    when {
                        index > 0 && index % adConfig.frequencyItems == 0 && currentAd != null ->
                            AdCard(
                                headline = currentAd.headline ?: "Sponsored",
                                body = currentAd.body,
                                advertiser = currentAd.advertiser,
                                style = adConfig.style
                            )
                        item is ContentModel.SportsContent -> SportsCard(
                            event = item,
                            onClick = {
                                viewModel.behaviorTracker.onItemClicked(item.id, Interest.SPORTS)
                                viewModel.onContentClicked(Interest.SPORTS)
                                onEventClick(item)
                            }
                        )
                        item is ContentModel.NewsContent -> NewsCard(
                            article = item,
                            onClick = {
                                viewModel.behaviorTracker.onItemClicked(item.id, Interest.NEWS)
                                viewModel.onContentClicked(Interest.NEWS)
                                onArticleClick(item)
                            }
                        )
                        item is ContentModel.FoodContent -> FoodCard(
                            recipe = item,
                            compact = false,
                            onClick = {
                                viewModel.behaviorTracker.onItemClicked(item.id, Interest.FOOD)
                                viewModel.onContentClicked(Interest.FOOD)
                                onFoodClick(item)
                            }
                        )
                    }
                }
                item(span = { GridItemSpan(2) }) {
                    PagingAppendLoader(loadState = items.loadState.append)
                }
            }
        }
    }
}

@Composable
private fun FoodHomeLayout(
    viewModel: FeedViewModel,
    heroFood: ContentModel.FoodContent?,
    heroNews: ContentModel.NewsContent?,
    adConfig: AdConfig,
    currentAd: NativeAd?,
    onFoodClick: (ContentModel.FoodContent) -> Unit,
    onArticleClick: (ContentModel.NewsContent) -> Unit,
    onEventClick: (ContentModel.SportsContent) -> Unit
) {
    val items = viewModel.contentFeed.collectAsLazyPagingItems()

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item(span = { GridItemSpan(2) }) {
            Column {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "🍴 Featured Recipe",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                heroFood?.let {
                    FoodHero(recipe = it, onClick = {
                        viewModel.behaviorTracker.onItemClicked(it.id, Interest.FOOD)
                        viewModel.onContentClicked(Interest.FOOD)
                        onFoodClick(it)
                    })
                } ?: heroNews?.let {
                    NewsHero(article = it, onClick = {
                        viewModel.behaviorTracker.onItemClicked(it.id, Interest.NEWS)
                        viewModel.onContentClicked(Interest.NEWS)
                        onArticleClick(it)
                    })
                }
            }
        }

        if (currentAd != null) {
            item(span = { GridItemSpan(2) }) {
                AdCard(
                    headline = currentAd.headline ?: "Sponsored",
                    body = currentAd.body,
                    advertiser = currentAd.advertiser,
                    style = adConfig.style
                )
            }
        }

        item(span = { GridItemSpan(2) }) {
            Text(
                text = "Your Feed",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        when {
            items.loadState.refresh is LoadState.Loading ->
                item(span = { GridItemSpan(2) }) { LoadingScreen() }
            items.loadState.refresh is LoadState.Error ->
                item(span = { GridItemSpan(2) }) {
                    ErrorScreen(
                        message = (items.loadState.refresh as LoadState.Error)
                            .error.localizedMessage ?: "Couldn't load feed",
                        onRetry = { items.refresh() }
                    )
                }
            else -> {
                items(
                    count = items.itemCount,
                    key = { index -> "${items.peek(index)?.id}_$index" },
                    span = { index ->
                        val item = items.peek(index)
                        when {
                            index > 0 && index % adConfig.frequencyItems == 0 && currentAd != null -> GridItemSpan(2)
                            item != null && item !is ContentModel.FoodContent -> GridItemSpan(2)
                            index % 3 == 0 -> GridItemSpan(2)
                            else -> GridItemSpan(1)
                        }
                    }
                ) { index ->
                    val item = items[index] ?: return@items
                    when {
                        index > 0 && index % adConfig.frequencyItems == 0 && currentAd != null ->
                            AdCard(
                                headline = currentAd.headline ?: "Sponsored",
                                body = currentAd.body,
                                advertiser = currentAd.advertiser,
                                style = adConfig.style
                            )
                        item is ContentModel.FoodContent -> FoodCard(
                            recipe = item,
                            compact = index % 3 != 0,
                            onClick = {
                                viewModel.behaviorTracker.onItemClicked(item.id, Interest.FOOD)
                                viewModel.onContentClicked(Interest.FOOD)
                                onFoodClick(item)
                            }
                        )
                        item is ContentModel.NewsContent -> NewsCard(
                            article = item,
                            onClick = {
                                viewModel.behaviorTracker.onItemClicked(item.id, Interest.NEWS)
                                viewModel.onContentClicked(Interest.NEWS)
                                onArticleClick(item)
                            }
                        )
                        item is ContentModel.SportsContent -> SportsCard(
                            event = item,
                            onClick = {
                                viewModel.behaviorTracker.onItemClicked(item.id, Interest.SPORTS)
                                viewModel.onContentClicked(Interest.SPORTS)
                                onEventClick(item)
                            }
                        )
                    }
                }
                item(span = { GridItemSpan(2) }) {
                    PagingAppendLoader(loadState = items.loadState.append)
                }
            }
        }
    }
}
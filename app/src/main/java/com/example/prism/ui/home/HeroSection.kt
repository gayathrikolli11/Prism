package com.example.prism.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.prism.domain.model.ContentModel
import com.example.prism.domain.model.Interest
@Composable
fun HeroSection(
    interest: Interest,
    newsContent: ContentModel.NewsContent?,
    sportsContent: ContentModel.SportsContent?,
    foodContent: ContentModel.FoodContent?,
    onNewsClick: (ContentModel.NewsContent) -> Unit,
    onSportsClick: (ContentModel.SportsContent) -> Unit,
    onFoodClick: (ContentModel.FoodContent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = when (interest) {
                Interest.NONE -> "✨ Discovering Your Feed"
                Interest.NEWS -> "📰 Top Story"
                Interest.SPORTS -> "⚽ Featured Match"
                Interest.FOOD -> "🍴 Featured Recipe"
            },
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        AnimatedContent(
            targetState = interest,
            transitionSpec = {
                (fadeIn(tween(300)) + slideInVertically(tween(300)) { it / 4 })
                    .togetherWith(fadeOut(tween(200)) + slideOutVertically(tween(200)) { -it / 4 })
            },
            label = "hero_transition",
            modifier = Modifier.padding(horizontal = 16.dp)
        ) { currentInterest ->
            when (currentInterest) {
                Interest.NONE, Interest.NEWS -> {
                    newsContent?.let {
                        NewsHero(article = it, onClick = { onNewsClick(it) })
                    } ?: Box(modifier = Modifier.height(200.dp))
                }
                Interest.SPORTS -> {
                    sportsContent?.let {
                        SportsHero(event = it, onClick = { onSportsClick(it) })
                    } ?: newsContent?.let {
                        NewsHero(article = it, onClick = { onNewsClick(it) })
                    } ?: Box(modifier = Modifier.height(200.dp))
                }
                Interest.FOOD -> {
                    foodContent?.let {
                        FoodHero(recipe = it, onClick = { onFoodClick(it) })
                    } ?: newsContent?.let {
                        NewsHero(article = it, onClick = { onNewsClick(it) })
                    } ?: Box(modifier = Modifier.height(200.dp))
                }
            }
        }
    }
}
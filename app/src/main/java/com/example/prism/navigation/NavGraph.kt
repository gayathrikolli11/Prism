package com.example.prism.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.prism.ui.food.FoodDetailScreen
import com.example.prism.ui.home.HomeScreen
import com.example.prism.ui.news.NewsDetailScreen
import com.example.prism.ui.sports.SportsDetailScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Feed,
        enterTransition = {
            slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) + fadeIn()
        },
        exitTransition = {
            slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300)) + fadeOut()
        },
        popEnterTransition = {
            slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300)) + fadeIn()
        },
        popExitTransition = {
            slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300)) + fadeOut()
        }
    ) {
        composable<Feed> {
            HomeScreen(
                onArticleClick = { article ->
                    navController.navigate(
                        NewsDetail(
                            url = article.url,
                            title = article.title,
                            source = article.sourceName,
                            description = article.description ?: ""
                        )
                    )
                },
                onEventClick = { event ->
                    navController.navigate(
                        SportsDetail(
                            eventId = event.id,
                            homeTeam = event.homeTeam,
                            awayTeam = event.awayTeam,
                            homeScore = event.homeScore ?: "-",
                            awayScore = event.awayScore ?: "-",
                            league = event.league,
                            status = event.status,
                            date = event.date
                        )
                    )
                },
                onFoodClick = { recipe ->
                    navController.navigate(
                        FoodDetail(
                            id = recipe.id,
                            title = recipe.title,
                            imageUrl = recipe.imageUrl ?: "",
                            cuisineType = recipe.cuisineType,
                            readyInMinutes = recipe.readyInMinutes,
                            servings = recipe.servings,
                            sourceUrl = recipe.sourceUrl ?: "",
                            calories = recipe.calories ?: 0
                        )
                    )
                }
            )
        }

        composable<NewsDetail> { backStackEntry ->
            val detail: NewsDetail = backStackEntry.toRoute()
            NewsDetailScreen(
                detail = detail,
                onBack = { navController.popBackStack() }
            )
        }

        composable<SportsDetail> { backStackEntry ->
            val detail: SportsDetail = backStackEntry.toRoute()
            SportsDetailScreen(
                detail = detail,
                onBack = { navController.popBackStack() }
            )
        }

        composable<FoodDetail> { backStackEntry ->
            val detail: FoodDetail = backStackEntry.toRoute()
            FoodDetailScreen(
                detail = detail,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
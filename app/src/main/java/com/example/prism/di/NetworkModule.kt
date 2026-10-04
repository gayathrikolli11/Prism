package com.example.prism.di

import com.example.prism.data.remote.RetrofitInstance
import com.example.prism.data.remote.food.FoodApiService
import com.example.prism.data.remote.news.NewsApiService
import com.example.prism.data.remote.sports.SportsApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideNewsApiService(): NewsApiService = RetrofitInstance.newsApi

    @Provides
    @Singleton
    fun provideFoodApiService(): FoodApiService = RetrofitInstance.foodApi

    @Provides
    @Singleton
    fun provideSportsApiService(): SportsApiService = RetrofitInstance.sportsApi
}
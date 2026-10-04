package com.example.prism.data.remote.food

import retrofit2.http.GET
import retrofit2.http.Query

interface FoodApiService {

    @GET("recipes/complexSearch")
    suspend fun searchRecipes(
        @Query("apiKey") apiKey: String,
        @Query("offset") offset: Int = 0,
        @Query("number") number: Int = 10,
        @Query("addRecipeInformation") addRecipeInformation: Boolean = true,
        @Query("addRecipeNutrition") addRecipeNutrition: Boolean = false,
        @Query("sort") sort: String = "popularity",
        @Query("sortDirection") sortDirection: String = "desc"
    ): RecipeSearchResponse

    @GET("recipes/complexSearch")
    suspend fun searchRecipesByCuisine(
        @Query("apiKey") apiKey: String,
        @Query("cuisine") cuisine: String,
        @Query("offset") offset: Int = 0,
        @Query("number") number: Int = 10,
        @Query("addRecipeInformation") addRecipeInformation: Boolean = true,
        @Query("sort") sort: String = "popularity"
    ): RecipeSearchResponse
}
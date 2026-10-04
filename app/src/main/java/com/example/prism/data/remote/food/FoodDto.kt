package com.example.prism.data.remote.food

import com.google.gson.annotations.SerializedName
import com.example.prism.domain.model.ContentModel

data class RecipeSearchResponse(
    @SerializedName("results") val results: List<RecipeDto>,
    @SerializedName("totalResults") val totalResults: Int,
    @SerializedName("offset") val offset: Int,
    @SerializedName("number") val number: Int
)

data class RecipeDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("image") val image: String?,
    @SerializedName("imageType") val imageType: String?,
    @SerializedName("readyInMinutes") val readyInMinutes: Int?,
    @SerializedName("servings") val servings: Int?,
    @SerializedName("cuisines") val cuisines: List<String>?,
    @SerializedName("dishTypes") val dishTypes: List<String>?,
    @SerializedName("summary") val summary: String?,
    @SerializedName("sourceUrl") val sourceUrl: String?,
    @SerializedName("nutrition") val nutrition: NutritionDto?
)

data class NutritionDto(
    @SerializedName("nutrients") val nutrients: List<NutrientDto>?
)

data class NutrientDto(
    @SerializedName("name") val name: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("unit") val unit: String
)

fun RecipeDto.toDomain(): ContentModel.FoodContent {
    val calories = nutrition?.nutrients
        ?.firstOrNull { it.name.lowercase() == "calories" }
        ?.amount?.toInt()

    val cleanSummary = summary
        ?.replace(Regex("<[^>]*>"), "")
        ?.replace("&amp;", "&")
        ?.replace("&lt;", "<")
        ?.replace("&gt;", ">")
        ?.take(200)

    return ContentModel.FoodContent(
        id = id.toString(),
        title = title,
        imageUrl = image,
        cuisineType = cuisines?.firstOrNull() ?: dishTypes?.firstOrNull() ?: "International",
        readyInMinutes = readyInMinutes ?: 30,
        servings = servings ?: 4,
        summary = cleanSummary,
        sourceUrl = sourceUrl,
        calories = calories,
        dishTypes = dishTypes ?: emptyList()
    )
}
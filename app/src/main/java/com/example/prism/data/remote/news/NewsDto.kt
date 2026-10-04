package com.example.prism.data.remote.news

import com.google.gson.annotations.SerializedName
import com.example.prism.domain.model.ContentModel

data class NewsResponse(
    @SerializedName("status") val status: String,
    @SerializedName("totalResults") val totalResults: Int,
    @SerializedName("articles") val articles: List<NewsArticleDto>
)

data class NewsArticleDto(
    @SerializedName("source") val source: NewsSourceDto,
    @SerializedName("title") val title: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("url") val url: String?,
    @SerializedName("urlToImage") val urlToImage: String?,
    @SerializedName("publishedAt") val publishedAt: String?
)

data class NewsSourceDto(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String
)

fun NewsArticleDto.toDomain(): ContentModel.NewsContent? {
    val validUrl = url ?: return null
    val validTitle = title?.takeIf { it != "[Removed]" } ?: return null
    return ContentModel.NewsContent(
        id = validUrl,
        title = validTitle,
        description = description,
        imageUrl = urlToImage,
        sourceName = source.name,
        publishedAt = publishedAt ?: "",
        url = validUrl
    )
}
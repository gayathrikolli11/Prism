package com.example.prism.widget

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.glance.appwidget.updateAll
import com.example.prism.domain.model.ContentModel
import com.example.prism.domain.model.Interest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

private val Context.widgetDataStore: DataStore<Preferences>
        by preferencesDataStore(name = "prism_widget_state")

@Singleton
class WidgetStateManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val INTEREST_KEY = stringPreferencesKey("widget_interest")
        private val HEADLINE_KEY = stringPreferencesKey("widget_headline")
        private val SUBTEXT_KEY = stringPreferencesKey("widget_subtext")
        private val IMAGE_URL_KEY = stringPreferencesKey("widget_image_url")
        private val CONTENT_ID_KEY = stringPreferencesKey("widget_content_id")
    }

    val widgetState: Flow<WidgetState> = context.widgetDataStore.data.map { prefs ->
        WidgetState(
            dominantInterest = runCatching {
                Interest.valueOf(prefs[INTEREST_KEY] ?: Interest.NONE.name)
            }.getOrDefault(Interest.NONE),
            headline = prefs[HEADLINE_KEY] ?: "Discovering your interests...",
            subtext = prefs[SUBTEXT_KEY] ?: "",
            imageUrl = prefs[IMAGE_URL_KEY],
            contentId = prefs[CONTENT_ID_KEY]
        )
    }

    suspend fun updateFromContent(
        interest: Interest,
        newsContent: ContentModel.NewsContent?,
        sportsContent: ContentModel.SportsContent?,
        foodContent: ContentModel.FoodContent?
    ) {
        val state = when (interest) {
            Interest.NEWS -> newsContent?.let {
                WidgetState(
                    dominantInterest = Interest.NEWS,
                    headline = it.title,
                    subtext = it.sourceName,
                    imageUrl = it.imageUrl,
                    contentId = it.id
                )
            }
            Interest.SPORTS -> sportsContent?.let {
                WidgetState(
                    dominantInterest = Interest.SPORTS,
                    headline = "${it.homeTeam} vs ${it.awayTeam}",
                    subtext = if (it.homeScore != null && it.awayScore != null)
                        "${it.homeScore} – ${it.awayScore} · ${it.league}"
                    else it.league,
                    imageUrl = it.homeTeamBadge,
                    contentId = it.id
                )
            }
            Interest.FOOD -> foodContent?.let {
                WidgetState(
                    dominantInterest = Interest.FOOD,
                    headline = it.title,
                    subtext = "${it.cuisineType} · ${it.readyInMinutes} min",
                    imageUrl = it.imageUrl,
                    contentId = it.id
                )
            }
            Interest.NONE -> newsContent?.let {
                WidgetState(
                    dominantInterest = Interest.NONE,
                    headline = it.title,
                    subtext = "✨ Discovering your feed",
                    imageUrl = it.imageUrl,
                    contentId = it.id
                )
            }
        } ?: WidgetState(
            dominantInterest = interest,
            headline = "Open Prism to get started",
            subtext = "Your personalized feed awaits"
        )

        context.widgetDataStore.edit { prefs ->
            prefs[INTEREST_KEY] = state.dominantInterest.name
            prefs[HEADLINE_KEY] = state.headline
            prefs[SUBTEXT_KEY] = state.subtext
            state.imageUrl?.let { prefs[IMAGE_URL_KEY] = it }
            state.contentId?.let { prefs[CONTENT_ID_KEY] = it }
        }

        PrismWidget().updateAll(context)
        Timber.d("WidgetStateManager: Updated widget for $interest — ${state.headline}")
    }
}
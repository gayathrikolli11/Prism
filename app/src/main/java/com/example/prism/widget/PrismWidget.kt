package com.example.prism.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.prism.MainActivity
import com.example.prism.domain.model.Interest
import kotlinx.coroutines.flow.first


class PrismWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val stateManager = WidgetStateManager(context)
        val state = stateManager.widgetState.first()

        provideContent {
            PrismWidgetContent(state = state)
        }
    }
}

@Composable
private fun PrismWidgetContent(state: WidgetState) {
    val backgroundColor = backgroundColorForInterest(state.dominantInterest)
    val accentColor = accentColorForInterest(state.dominantInterest)

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(backgroundColor)
            .clickable(actionStartActivity<MainActivity>())
            .padding(14.dp)
    ) {
        Column(modifier = GlanceModifier.fillMaxSize()) {

            // Header row — brand + interest label
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PRISM",
                    style = TextStyle(
                        color = ColorProvider(Color.White),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = GlanceModifier.width(6.dp))
                Text(
                    text = "•",
                    style = TextStyle(
                        color = ColorProvider(Color.White.copy(alpha = 0.4f)),
                        fontSize = 12.sp
                    )
                )
                Spacer(modifier = GlanceModifier.width(6.dp))
                Text(
                    text = "${state.dominantInterest.emoji} ${state.dominantInterest.displayName} Feed",
                    style = TextStyle(
                        color = ColorProvider(accentColor),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = GlanceModifier.height(10.dp))

            Row(
                modifier = GlanceModifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = GlanceModifier.defaultWeight(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = state.headline,
                        style = TextStyle(
                            color = ColorProvider(Color.White),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 3
                    )

                    if (state.subtext.isNotBlank()) {
                        Spacer(modifier = GlanceModifier.height(4.dp))
                        Text(
                            text = state.subtext,
                            style = TextStyle(
                                color = ColorProvider(Color.White.copy(alpha = 0.6f)),
                                fontSize = 11.sp
                            ),
                            maxLines = 1
                        )
                    }

                    Spacer(modifier = GlanceModifier.height(8.dp))

                    Text(
                        text = "Tap to open →",
                        style = TextStyle(
                            color = ColorProvider(accentColor),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

private fun backgroundColorForInterest(interest: Interest): Color = when (interest) {
    Interest.NONE -> Color(0xFF1C1C2E)
    Interest.NEWS -> Color(0xFF1E1E1E)
    Interest.SPORTS -> Color(0xFF0A120A)
    Interest.FOOD -> Color(0xFF1A0E06)
}

private fun accentColorForInterest(interest: Interest): Color = when (interest) {
    Interest.NONE -> Color(0xFFA78BFA)
    Interest.NEWS -> Color(0xFFFBBF24)
    Interest.SPORTS -> Color(0xFF22C55E)
    Interest.FOOD -> Color(0xFFEA580C)
}
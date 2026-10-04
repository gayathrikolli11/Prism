package com.example.prism.core.ads

import android.content.Context
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.example.prism.domain.model.Interest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val _adCache = MutableStateFlow<Map<Interest, NativeAd>>(emptyMap())
    val adCache: StateFlow<Map<Interest, NativeAd>> = _adCache

    fun preloadAd(interest: Interest) {
        val config = AdConfigs.forInterest(interest)
        AdLoader.Builder(context, config.adUnitId)
            .forNativeAd { nativeAd ->
                _adCache.value[interest]?.destroy()
                _adCache.value = _adCache.value + (interest to nativeAd)
                Timber.d("AdManager: Native ad loaded for $interest (style=${config.style})")
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    Timber.e("AdManager: Failed to load ad for $interest — ${error.message}")
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT)
                    .build()
            )
            .build()
            .loadAd(AdRequest.Builder().build())
    }

    fun preloadAllInterests() {
        Interest.entries.filter { it != Interest.NONE }.forEach { preloadAd(it) }
        preloadAd(Interest.NONE)
    }

    fun getAdForInterest(interest: Interest): NativeAd? = _adCache.value[interest]

    fun getConfigForInterest(interest: Interest): AdConfig = AdConfigs.forInterest(interest)

    fun destroyAds() {
        _adCache.value.values.forEach { it.destroy() }
        _adCache.value = emptyMap()
    }
}
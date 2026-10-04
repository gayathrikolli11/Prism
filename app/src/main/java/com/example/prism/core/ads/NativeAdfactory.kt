package com.example.prism.core.ads

import android.content.Context
import com.google.android.gms.ads.nativead.NativeAdView
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class NativeAdFactory @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun createNativeAdView(): NativeAdView {
        return NativeAdView(context)
    }
}
package com.example.prism.domain.model

import com.google.android.gms.ads.nativead.NativeAd

sealed class FeedItem {
    data class Content(val content: ContentModel) : FeedItem()
    data class Ad(val nativeAd: NativeAd) : FeedItem()

    companion object {
        const val AD_FREQUENCY = 5
    }
}
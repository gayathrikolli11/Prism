package com.example.prism

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.example.prism.data.local.BehaviorDao
import com.example.prism.data.local.InterestDataStore
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class PrismApp : Application() {

    @Inject lateinit var interestDataStore: InterestDataStore
    @Inject lateinit var behaviorDao: BehaviorDao

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())

        Thread { MobileAds.initialize(this) }.start()
        CoroutineScope(Dispatchers.IO).launch {
            val since = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(30)
            val eventCount = behaviorDao.getTotalEventCount(since)
            if (eventCount == 0) {
                interestDataStore.resetScores()
                Timber.d("DynamicFeedApp: Fresh install detected — reset interest scores")
            }
        }
    }
}
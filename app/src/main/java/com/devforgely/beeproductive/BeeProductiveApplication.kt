package com.devforgely.beeproductive

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.devforgely.beeproductive.data.repository.BillingRepository
import com.devforgely.beeproductive.data.repository.HiveRepository
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class BeeProductiveApplication : Application(), Configuration.Provider {
    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var billingRepository: BillingRepository
    @Inject lateinit var hiveRepository: HiveRepository

    override fun onCreate() {
        super.onCreate()
        billingRepository.connect()
        hiveRepository.observe()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}

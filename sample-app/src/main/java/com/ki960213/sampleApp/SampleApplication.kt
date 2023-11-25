package com.ki960213.sampleApp

import android.app.Application
import com.ki960213.sheathAndroid.SheathApplication

class SampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SheathApplication.run(applicationContext)
    }
}

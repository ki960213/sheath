package com.ki960213.sampleApp

import android.app.Application
import com.ki960213.sheathandroid.SheathApplication

class SampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SheathApplication.run(applicationContext)
    }
}

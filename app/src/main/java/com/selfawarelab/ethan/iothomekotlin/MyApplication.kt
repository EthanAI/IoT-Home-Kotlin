package com.selfawarelab.ethan.iothomekotlin

import android.app.Application
import timber.log.Timber

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        myApplication = this

        Timber.plant(Timber.DebugTree())
    }

    companion object {
        lateinit var myApplication: MyApplication
    }

}

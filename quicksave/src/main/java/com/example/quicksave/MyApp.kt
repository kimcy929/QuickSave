package com.example.quicksave

import android.app.Application
import timber.log.Timber

/**
 * Created by kimcy929 on 1/26/2018.
 */

public class MyApp : Application() {

    companion object {
        lateinit var INSTANCE: MyApp
    }

    override fun onCreate() {

        super.onCreate()

        INSTANCE = this

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
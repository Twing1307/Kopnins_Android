package com.vyapp.doodle

import android.app.Application
import com.vyapp.doodle.data.ScoreRepository

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        ScoreRepository.initialize(this)
    }

}
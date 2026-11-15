package com.ccstudio.mpaz_pro_6

import android.app.Application
import com.ccstudio.mpaz_pro_6.data.AppContainer

class MpazApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}

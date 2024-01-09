package com.kernal.demo.plateid.startup

import android.app.Application
import com.kernal.demo.base.start.AppInitManager

class AppStartUpManager private constructor() : AppInitManager() {
    private val TAG: String = AppStartUpManager::class.java.simpleName

    companion object {
        var mAppStartUpManager: AppStartUpManager? = null
        fun instance(): AppStartUpManager {
            if (mAppStartUpManager == null) {
                mAppStartUpManager = AppStartUpManager()
            }
            return mAppStartUpManager!!
        }
    }

    override fun applicationInit(application: Application) {
//        CrashHandler.instance()?.initCrash(application)
    }

    override fun delayInit(application: Application) {

    }
}
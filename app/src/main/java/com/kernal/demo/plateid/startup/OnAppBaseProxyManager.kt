package com.kernal.demo.plateid.startup

import com.kernal.demo.base.proxy.OnAppBaseProxyLinsener
import com.kernal.demo.plateid.BuildConfig

class OnAppBaseProxyManager : OnAppBaseProxyLinsener {
    override fun onIsProxy(): Boolean {
        return BuildConfig.is_proxy
    }

    override fun onIsDebug(): Boolean {
        return BuildConfig.is_debug
    }

}
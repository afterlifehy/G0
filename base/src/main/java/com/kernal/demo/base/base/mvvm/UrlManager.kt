package com.kernal.demo.base.base.mvvm
import com.kernal.demo.base.BuildConfig

object UrlManager {
    const val DEV_HOST = "http://180.169.37.244/ipms/service/"
    const val FORMAL_HOST = "http://ipms.csnits.com/ipms/service/"

    fun getServerUrl(): String {
        if (BuildConfig.is_dev) {
            return DEV_HOST
        } else {
            return FORMAL_HOST
        }
    }
}
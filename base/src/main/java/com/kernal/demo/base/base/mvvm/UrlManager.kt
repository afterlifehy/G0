package com.kernal.demo.base.base.mvvm
import com.kernal.demo.base.BuildConfig

object UrlManager {
//    const val DEV_HOST = "http://192.168.0.39:8080/ipms/service/"
    const val DEV_HOST = "http://114.94.20.110/ipms/service/"
    const val FORMAL_HOST = "http://ipms.csnits.com/ipms/service/"

    const val File_HOST = "http://114.94.20.110/ipms/file/omor01/"

    fun getServerUrl(): String {
        if (BuildConfig.is_dev) {
            return DEV_HOST
        } else {
            return FORMAL_HOST
        }
    }

    fun getFileServerUrl(): String {
        return File_HOST
    }
}
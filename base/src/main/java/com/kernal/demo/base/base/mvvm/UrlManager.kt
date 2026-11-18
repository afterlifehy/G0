package com.kernal.demo.base.base.mvvm
import com.kernal.demo.base.BuildConfig

object UrlManager {
//    const val DEV_HOST = "http://192.168.0.39:8080/ipms/service/"
    const val RT_DEV_HOST = "http://114.94.20.110/ipms/service/"
    const val RT_FORMAL_HOST = "http://ipms.rentongtek.com/ipms/service/"

    const val DEV_HOST = "http://180.166.5.198:28080/service/parking/"
    const val FORMAL_HOST = "http://roadparking.jtcx.sh.cn/service/parking/"

    const val File_HOST = "http://114.94.20.110/ipms/file/omor01/"

    const val UPLOAD_IMAGE_DEV_HOST = "http://114.94.20.110/ipms/rest/ZJPM0401/"
    const val UPLOAD_IMAGE_FORMAL_HOST = "http://ipms.rentongtek.com/ipms/rest/ZJPM0401/"

    const val DEV_CA = "http://103.36.136.173:1880/possdk/v1"
    const val FORMAL_CA = "https://parkapp.jtcx.sh.cn/ca/possdk/v1"

    fun getRTServerUrl(): String {
        if (BuildConfig.is_dev) {
            return RT_DEV_HOST
        } else {
            return RT_FORMAL_HOST
        }
    }
    fun getCAUrl(): String {
        if (BuildConfig.is_dev) {
            return DEV_CA
        } else {
            return FORMAL_CA
        }
    }

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

    fun getImageUploadServerUrl():String{
        if (BuildConfig.is_dev) {
            return UPLOAD_IMAGE_DEV_HOST
        } else {
            return UPLOAD_IMAGE_FORMAL_HOST
        }
    }
}
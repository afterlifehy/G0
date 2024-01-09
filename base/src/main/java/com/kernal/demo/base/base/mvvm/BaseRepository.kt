package com.kernal.demo.base.base.mvvm

import com.kernal.demo.base.bean.ResResponse
import com.kernal.demo.base.http.RetrofitUtils
import com.kernal.demo.base.request.Api


open class BaseRepository {

    val mServer by lazy {
        RetrofitUtils.getInstance().createCoroutineRetrofit(
            Api::class.java,
            UrlManager.getServerUrl()
        )
    }

    suspend fun <T : Any> apiCall(call: suspend () -> ResResponse<T>): ResResponse<T> {
        return call.invoke()
    }

}
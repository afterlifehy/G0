package com.kernal.demo.base.http.interceptor

import android.annotation.SuppressLint
import android.util.Log
import com.blankj.utilcode.util.TimeUtils
import com.kernal.demo.base.util.LogFileUtil.logToFile
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class LogInterceptor //可以从连几次
    (private val isDebug: Boolean) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        if (isDebug) {
            Log.i("HttpRequest:", "okhttp3:$request") //输出请求前整个url
            //去执行网络请求
        }
        logToFile(currentTime + "    " + request)
        val response: Response = chain.proceed(request)
//        if (isDebug) {
        val mediaType = response.body!!.contentType()
        val content = response.body!!.string()
//            if (isDebug) {
//                String[] url = response.request().url().url().toString().split("/");
//                String method = url[url.length - 1];
//                if (method.contains("?")) {
//                    method = method.split("?")[0];
//                }
//                Log.i("keey", "url:" + method);
//                Log.i("method:", "request:" + request.toString() + "==" + "response body:" + content);//输出返回信息
        Log.i("HttpResponse:", "request:$request==response body:$content") //输出返回信息
//            }
        logToFile(currentTime + "    " + response)
        logToFile(currentTime + "    " + request + "    " + content)
        return response.newBuilder()
            .body(okhttp3.ResponseBody.create(mediaType, content))
            .build();
//        }
//        return response
    }

    val currentTime: String
        get() = TimeUtils.millis2String(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss")
}

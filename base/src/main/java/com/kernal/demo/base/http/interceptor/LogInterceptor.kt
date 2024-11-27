package com.kernal.demo.base.http.interceptor

import android.util.Log
import com.blankj.utilcode.util.TimeUtils
import com.kernal.demo.base.util.LogFileUtil.logToFile
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import java.io.IOException

class LogInterceptor(private val isDebug: Boolean) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()

        // 输出请求信息
        if (isDebug) {
            Log.i("HttpRequest:", "okhttp3:$request") // 输出请求前整个URL
        }
        logToFile(currentTime + "    " + request)

        // 执行请求
        val response: Response = chain.proceed(request)

        // 获取响应体内容并复制
        val responseBody = response.body
        val content: String
        if (responseBody != null) {
            val buffer = Buffer()
            responseBody.source().readAll(buffer)  // 将响应体的内容读取到buffer中
            content = buffer.readUtf8()  // 获取字符串内容
        } else {
            content = ""  // 如果响应体为空，设置为空字符串
        }

        // 输出响应信息
        if (isDebug) {
            Log.i("HttpResponse:", "request:$request==response body:$content") // 输出返回信息
        }
        logToFile(currentTime + "    " + response)
        logToFile(currentTime + "    " + request + "    " + content)

        // 返回一个新的response，并保留原始的响应体内容
        return response.newBuilder()
            .body(okhttp3.ResponseBody.create(responseBody?.contentType(), content)) // 使用复制的响应体
            .build()
    }

    val currentTime: String
        get() = TimeUtils.millis2String(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss")
}

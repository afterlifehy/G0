package com.kernal.demo.base.util

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.Date

object NetTimeUtil {
    fun getNetTime(): Long {
        // 创建 OkHttpClient 实例
        val client = OkHttpClient()

        // 请求百度主页
        val request = Request.Builder()
            .url("http://www.baidu.com")
            .build()
        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val dateHeader = response.header("Date")
                if (dateHeader != null) {
                    val serverTime = Date(dateHeader)
                    return serverTime.time
                } else {
                    return 0L
                }
            } else {
                return 0L
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return 0L
        }
    }
}
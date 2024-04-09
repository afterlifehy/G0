package com.kernal.demo.base.http.interceptor

import android.util.Log
import com.blankj.utilcode.util.ConvertUtils
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.internal.http.promisesBody
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.nio.charset.Charset
import java.util.Locale
import java.util.concurrent.TimeUnit

class ExceptionInterceptor : Interceptor {
    private val UTF8 = Charset.forName("UTF-8")

    override fun intercept(chain: Interceptor.Chain): Response {
        //执行请求，计算请求时间
        val request = chain.request()
        val startNs = System.nanoTime()
        val response = try {
            chain.proceed(request)
        } catch (e: SocketTimeoutException) {
            log("<-- HTTP FAILED: $e")
            val errorMsg = "连接超时，请检查网络"
            throw IOException(errorMsg, e)
        } catch (e: ConnectException) {
            log("<-- HTTP FAILED: $e")
            val errorMsg = "连接服务器错误，请检查网络"
            throw IOException(errorMsg, e)
        } catch (e: UnknownHostException) {
            log("<-- HTTP FAILED: $e")
            val errorMsg = "网络错误，请检查网络"
            throw IOException(errorMsg, e)
        } catch (e: Exception) {
            log("<-- HTTP FAILED: $e")
            throw e
        }
        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
        //响应日志拦截
        return logForResponse(response, tookMs)
    }

    private fun log(message: String) {
        Log.v("G0", message)
    }

    private fun logForResponse(response: Response, tookMs: Long): Response {
        val builder: Response.Builder = response.newBuilder()
        val clone: Response = builder.build()
        var responseBody = clone.body
        try {
            log("<-- " + clone.code + ' ' + clone.message + ' ' + clone.request.url + " (" + tookMs + "ms)")
            val headers = clone.headers
            var i = 0
            val count = headers.size
            while (i < count) {
                log(
                    """
                        
                        ${headers.name(i)}: ${headers.value(i)}
                        """.trimIndent()
                )
                i++
            }
            log(" ")
            if (clone.promisesBody()) {
                if (responseBody == null) return response
                if (isPlaintext(responseBody.contentType())) {
                    val bytes = ConvertUtils.inputStream2Bytes(responseBody.byteStream())
                    val contentType = responseBody.contentType()
                    val body = java.lang.String(bytes, getCharset(contentType)!!)
                    log("\nbody:$body")
                    responseBody = ResponseBody.create(responseBody.contentType(), bytes)
                    return response.newBuilder().body(responseBody).build()
                } else {
                    log("\nbody: maybe [binary body], omitted!")
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            log("<-- END HTTP")
        }
        return response
    }

    private fun isPlaintext(mediaType: MediaType?): Boolean {
        if (mediaType == null) return false
        if (mediaType.type == "text") {
            return true
        }
        var subtype = mediaType.subtype
        if (subtype != null) {
            subtype = subtype.lowercase(Locale.getDefault())
            if (subtype.contains("x-www-form-urlencoded") || subtype.contains("json") || subtype.contains("xml") || subtype.contains("html")) //
                return true
        }
        return false
    }

    private fun getCharset(contentType: MediaType?): Charset? {
        var charset = if (contentType != null) contentType.charset(UTF8) else UTF8
        if (charset == null) charset = UTF8
        return charset
    }
}
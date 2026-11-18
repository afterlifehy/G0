package com.kernal.demo.base.http.interceptor

import android.os.Build
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import com.blankj.utilcode.util.EncryptUtils
import com.kernal.demo.base.base.mvvm.UrlManager
import com.kernal.demo.base.util.Constant
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.Buffer
import org.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.charset.StandardCharsets

class CAInterceptor : Interceptor {
    val log: Logger by lazy { LoggerFactory.getLogger(this::class.java) }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        if (!request.url.toString().startsWith(UrlManager.getServerUrl())) {
            return chain.proceed(request)
        }
        val body = request.body
        val buffer = Buffer()
        body?.writeTo(buffer)

        val parameterStr = buffer.readUtf8()
        val requestBody = parameterStr.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val orgSign = Constant.PASSWORD + "|" + parameterStr
        Log.v("1234", orgSign)
        val sign = EncryptUtils.encryptMD5ToString(orgSign).lowercase()
        Log.v("1234", sign)
        val base64EncodedParams = base64Encode2String(parameterStr)
        if (base64EncodedParams.length < 400) {
            log.info("签名body转base64:  $base64EncodedParams")
        } else {
            log.info("签名body转base64:  ${base64EncodedParams.substring(0, 400)}")
        }

        var time = 1000 * 20
        var flag = true
        var type = "2"//1:申请证书，2：重签 ，3：更新证书
        if (request.method.equals("get", ignoreCase = true)) {
            request = request.newBuilder().addHeader("sign", sign)
                .addHeader("Accept", "application/json").get().build()
        } else {
            request = request.newBuilder().addHeader("sign", sign)
                .addHeader("Accept", "application/json").post(requestBody).build()
        }
        while (flag) {
            Thread.sleep(500)
            time -= 500
            if (time < 0) {
                break
            }
            continue
        }
        log.info("request $request")
        if (!request.url.toUri().path.endsWith("/photo")) {
        }
        return chain.proceed(request)
    }

    fun base64Encode2String(string: String): String {
        return Base64.encodeToString(string.toByteArray(StandardCharsets.UTF_8), Base64.NO_WRAP)
    }
}
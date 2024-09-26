package com.kernal.demo.base.http.interceptor;

import android.util.Log;

import com.kernal.demo.base.util.AppUtil;
import com.kernal.demo.base.util.LogFileUtil;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class LogInterceptor implements Interceptor {
    private boolean isDebug;
    //可以从连几次

    public LogInterceptor(boolean isDebug) {
        this.isDebug = isDebug;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (isDebug) {
            Log.i("HttpRequest:", "okhttp3:" + request.toString());//输出请求前整个url
            //去执行网络请求
        }
        LogFileUtil.INSTANCE.logToFile(AppUtil.INSTANCE.getCurrentTime() + "    " + request);
        Response response = chain.proceed(request);

        if (isDebug) {
            okhttp3.MediaType mediaType = response.body().contentType();

            String content = response.body().string();
            if (isDebug) {
//                String[] url = response.request().url().url().toString().split("/");
//                String method = url[url.length - 1];
//                if (method.contains("?")) {
//                    method = method.split("?")[0];
//                }
//                Log.i("keey", "url:" + method);
//                Log.i("method:", "request:" + request.toString() + "==" + "response body:" + content);//输出返回信息

                Log.i("HttpResponse:", "request:" + request.toString() + "==" + "response body:" + content);//输出返回信息
            }
            LogFileUtil.INSTANCE.logToFile(AppUtil.INSTANCE.getCurrentTime() + "    " + response);
            LogFileUtil.INSTANCE.logToFile(AppUtil.INSTANCE.getCurrentTime() + "    " + request + "    " + content);
            return response.newBuilder()
                    .body(okhttp3.ResponseBody.create(mediaType, content))
                    .build();
        }
        return response;

    }
}

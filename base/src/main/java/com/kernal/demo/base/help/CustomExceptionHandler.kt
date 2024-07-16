package com.kernal.demo.base.help

import android.content.Context
import android.os.Looper
import com.kernal.demo.base.util.ToastUtil
import java.io.PrintWriter
import java.io.StringWriter


class CustomExceptionHandler : Thread.UncaughtExceptionHandler {

    private lateinit var context: Context
    private var defaultExceptionHandler: Thread.UncaughtExceptionHandler? = null
    companion object{
        val INSTANCE: CustomExceptionHandler by lazy { CustomExceptionHandler() }
    }

    fun init(context: Context) {
        this.context = context
        defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        // 创建 StringWriter 和 PrintWriter 来获取堆栈跟踪信息
        val stringWriter = StringWriter()
        e.printStackTrace(PrintWriter(stringWriter))
        val errorReport = stringWriter.toString()
        Thread() {
            run() {
                Looper.prepare()
                ToastUtil.showBottomToast(errorReport)
                Looper.loop()
            }
        }.start()
        Thread.sleep(3000)
    }
}
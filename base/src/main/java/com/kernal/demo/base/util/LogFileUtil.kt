package com.kernal.demo.base.util

import android.annotation.SuppressLint
import android.os.Build
import android.os.Environment
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import com.blankj.utilcode.util.PhoneUtils
import com.blankj.utilcode.util.TimeUtils
import com.kernal.demo.base.BaseApplication
import com.kernal.demo.base.ds.PreferencesDataStore
import com.kernal.demo.base.ds.PreferencesKeys
import kotlinx.coroutines.runBlocking
import me.yokeyword.fragmentation.SupportActivity
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

/**
 * @desc: 日志工具类
 * @author: Uranus
 * @date: 2023/9/22
 * @version: 1.0v
 */
object LogFileUtil {

    const val LOG_DIR_NAME = "G0_logs"
    const val LOG_FILE_NAME = "G0_log"

    @SuppressLint("SimpleDateFormat", "MissingPermission")
    fun logToFile(log: String) {
        var imei = ""
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            } else {
                imei = PhoneUtils.getIMEI()
            }
            val logDir = File(Environment.getExternalStorageDirectory().absolutePath, LOG_DIR_NAME)
            if (!logDir.exists()) {
                logDir.mkdirs()
            }

            // 判断有没有今天的日志文件 没有就创建
            val date = getDaysAgo(0)
            val formatted = SimpleDateFormat("yyyyMMdd").format(date)
            val logFile = File(logDir, LOG_FILE_NAME + "_${imei}_${formatted}.txt")
            if (!logFile.exists()) {
                logFile.createNewFile()
            }
            // 获取时间戳部分的长度，用于后续对齐
            val indent = " ".repeat(42) // 4 是为了包括时间戳和后面的空格

            // 手动处理长行的换行和对齐
            val maxLineLength = 200 // 假设每行最多200个字符（根据你的需求调整）
            val sb = StringBuilder()

//            var currentLine = log
//            log.forEach { char ->
//                if (currentLine.length < maxLineLength) {
//                    currentLine += char
//                } else {
//                    sb.append(currentLine).append("\n")
//                    currentLine = "$indent$char"
//                }
//            }
//            sb.append(currentLine)
            // 构建日志内容，手动处理每行长度
            var startIndex = 0
            var newLog = log.replace(Regex("""photo=[^&]*"""), "photo=[REDACTED]")
            while (startIndex < newLog.length) {
                // 计算当前行的结束位置
                val endIndex = if (startIndex + maxLineLength < newLog.length) {
                    startIndex + maxLineLength
                } else {
                    newLog.length
                }
                // 截取当前行的内容
                val line = newLog.substring(startIndex, endIndex)
                // 如果是第一行，添加时间戳；否则添加缩进
                if (startIndex == 0) {
                    sb.append("$line")
                } else {
                    sb.append("$indent$line")
                }
                // 更新下一个行的起始位置
                startIndex = endIndex
                // 如果还没到日志的结尾，添加换行符
                if (startIndex < newLog.length) {
                    sb.append("\n")
                }
            }

            val fos = FileOutputStream(logFile, true)
            val osw = OutputStreamWriter(fos)
            val bw = BufferedWriter(osw)
            bw.write(sb.toString())
            bw.newLine()
            bw.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * 删除日志文件
     */
    @SuppressLint("SimpleDateFormat")
    fun delLog() {
        val dirFile = File(Environment.getExternalStorageDirectory().absolutePath, LOG_DIR_NAME)
        val files = dirFile.listFiles()
        if (files == null || files.isEmpty()) {
            return
        }
        for (file in files) {
            // 文件创建日期信息
            val createTime = file.name.replace(LOG_FILE_NAME + "_", "")
                .replace(".txt", "000000")
                .replace(".zip", "000000")

            val createTimeMills = TimeUtils.string2Millis(createTime, "yyyyMMddHHmmss")
            // 要删除的条件信息
            val daysAgo = getDaysAgo(7)
            val formatted = SimpleDateFormat("yyyyMMdd").format(daysAgo)

            val daysAgoMills = TimeUtils.string2Millis(formatted + "000000", "yyyyMMddHHmmss")
            if (daysAgoMills > createTimeMills) {
                // 删除文件
                file.delete()
            }
        }
    }

    /**
     * @desc:给定一个整数 返回一个Date对象
     * @author: Uranus
     * @date: 2023/9/22 14:51
     * @version: 1.0v
     */
    fun getDaysAgo(daysAgo: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)

        return calendar.time
    }
}
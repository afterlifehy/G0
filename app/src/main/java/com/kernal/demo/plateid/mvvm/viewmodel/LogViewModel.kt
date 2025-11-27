package com.kernal.demo.plateid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.kernal.demo.base.BaseApplication
import com.kernal.demo.base.base.mvvm.BaseViewModel
import com.kernal.demo.base.base.mvvm.ErrorMessage
import com.kernal.demo.base.util.Constant
import com.kernal.demo.plateid.mvvm.repository.LoginRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import retrofit2.http.Part
import java.io.File

class LogViewModel: BaseViewModel() {
    val mLoginRepository by lazy {
        LoginRepository()
    }

    val logFileListLiveData = MutableLiveData<MutableList<File>>()
    val upLoadLogLiveData = MutableLiveData<Any>()

    fun logFileList() {
        val logFileList: MutableList<File> = ArrayList()
        val logDir = File(BaseApplication.instance().getExternalFilesDir(null), Constant.LOG_DIR_NAME)
        if (logDir.exists() && logDir.isDirectory) {
            val files = logDir.listFiles()
            if (files != null && files.isNotEmpty()) {
                logFileList.addAll(files)
                logFileListLiveData.value = logFileList
            } else {
            }
        } else {
        }
    }

    fun logFileUpload(@Part file: MultipartBody.Part) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mLoginRepository.logFileUpload(file)
            }
            executeResponse(response, {
                upLoadLogLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status, api = "logFileUpload"))
            })
        }
    }
}
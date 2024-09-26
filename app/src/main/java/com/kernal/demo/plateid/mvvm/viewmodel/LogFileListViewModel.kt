package com.kernal.demo.plateid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.kernal.demo.base.base.mvvm.BaseViewModel
import com.kernal.demo.base.base.mvvm.ErrorMessage
import com.kernal.demo.plateid.mvvm.repository.LoginRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import retrofit2.http.Part
import java.io.File

class LogFileListViewModel: BaseViewModel() {
    val mLoginRepository by lazy {
        LoginRepository()
    }

    val logFileUploadLiveData = MutableLiveData<Any>()

    fun logFileUpload(@Part file: MultipartBody.Part) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mLoginRepository.logFileUpload(file)
            }
            executeResponse(response, {
                logFileUploadLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
            })
        }
    }
}
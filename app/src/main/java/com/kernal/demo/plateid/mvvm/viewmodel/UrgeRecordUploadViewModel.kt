package com.kernal.demo.plateid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.kernal.demo.base.base.mvvm.BaseViewModel
import com.kernal.demo.base.base.mvvm.ErrorMessage
import com.kernal.demo.base.bean.UploadImageBean
import com.kernal.demo.plateid.mvvm.repository.OrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UrgeRecordUploadViewModel : BaseViewModel() {

    val mOrderRepository by lazy {
        OrderRepository()
    }

    val urgepayLiveData = MutableLiveData<Any>()
    val urgeNoticeLiveData = MutableLiveData<Any>()
    val uploadFileForAppLiveData = MutableLiveData<UploadImageBean>()
    fun urgepay(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mOrderRepository.urgepay(param)
            }
            executeResponse(response, {
                urgepayLiveData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.message, code = response.code, api = "urgepay"))
            })
        }
    }

    fun urgeNotice(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mOrderRepository.urgeNotice(param)
            }
            executeResponse(response, {
                urgeNoticeLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status, api = "urgeNotice"))
            })
        }
    }

    fun uploadFileForApp(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mOrderRepository.uploadFileForApp(param)
            }
            executeResponse(response, {
                uploadFileForAppLiveData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.message, code = response.code, api = "uploadFileForApp"))
            })
        }
    }
}
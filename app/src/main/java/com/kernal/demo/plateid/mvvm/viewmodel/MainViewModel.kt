package com.kernal.demo.plateid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.kernal.demo.base.base.mvvm.BaseViewModel
import com.kernal.demo.base.base.mvvm.ErrorMessage
import com.kernal.demo.base.bean.UpdateBean
import com.kernal.demo.plateid.mvvm.repository.MineRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainViewModel : BaseViewModel() {
    val mMineRepository by lazy {
        MineRepository()
    }

    val checkUpdateLiveDate = MutableLiveData<UpdateBean>()
    val locationUploadLiveData = MutableLiveData<Any>()

    fun checkUpdate(param: Map<String, Any?>) {
        checkUpdateLiveDate.value = UpdateBean("0", "0", "21321312", "1.0.1")
//        launch {
//            val response = withContext(Dispatchers.IO) {
//                mMineRepository.checkUpdate(param)
//            }
//            executeResponse(response, {
//                checkUpdateLiveDate.value = response.attr
//            }, {
//                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
//            })
//        }
    }

    fun locationUpload(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mMineRepository.locationUpload(param)
            }
            executeResponse(response, {
                locationUploadLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
            })
        }
    }
}
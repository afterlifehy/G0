package com.kernal.demo.plateid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.kernal.demo.base.base.mvvm.BaseViewModel
import com.kernal.demo.base.base.mvvm.ErrorMessage
import com.kernal.demo.base.bean.DebtUploadBean
import com.kernal.demo.base.bean.PicInquiryBean
import com.kernal.demo.plateid.mvvm.repository.OrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OrderDetailViewModel: BaseViewModel() {
    val mOrderRepository by lazy {
        OrderRepository()
    }

    val picInquiryLiveData = MutableLiveData<PicInquiryBean>()
    val debtUploadLiveData = MutableLiveData<DebtUploadBean>()

    fun picInquiry(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mOrderRepository.picInquiry(param)
            }
            executeResponse(response, {
                picInquiryLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
            })
        }
    }

    fun debtUpload(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mOrderRepository.debtUpload(param)
            }
            executeResponse(response, {
                debtUploadLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
            })
        }
    }
}
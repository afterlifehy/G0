package com.kernal.demo.plateid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.kernal.demo.base.base.mvvm.BaseViewModel
import com.kernal.demo.base.base.mvvm.ErrorMessage
import com.kernal.demo.base.bean.DebtUploadBean
import com.kernal.demo.base.bean.OrderResultBean
import com.kernal.demo.plateid.mvvm.repository.OrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OrderInquiryViewModel : BaseViewModel() {
    val mOrderRepository by lazy {
        OrderRepository()
    }

    val orderInquiryLiveData = MutableLiveData<OrderResultBean>()
    val debtUploadLiveData = MutableLiveData<DebtUploadBean>()

    fun orderInquiry(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mOrderRepository.orderInquiry(param)
            }
            executeResponse(response, {
                orderInquiryLiveData.value = response.attr
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
package com.kernal.demo.plateid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.kernal.demo.base.base.mvvm.BaseViewModel
import com.kernal.demo.base.base.mvvm.ErrorMessage
import com.kernal.demo.base.bean.DebtUploadBean
import com.kernal.demo.base.bean.EndOrderInfoBean
import com.kernal.demo.base.bean.PayQRBean
import com.kernal.demo.base.bean.PayResultBean
import com.kernal.demo.plateid.mvvm.repository.OrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OrderInfoViewModel : BaseViewModel() {

    val mOrderRepository by lazy {
        OrderRepository()
    }

    val endOrderInfoLiveData = MutableLiveData<EndOrderInfoBean>()
    val endOrderQRLiveData = MutableLiveData<PayQRBean>()
    val payResultInquiryLiveData = MutableLiveData<PayResultBean>()
    val debtUploadLiveData = MutableLiveData<DebtUploadBean>()
    fun endOrderInfo(param: Map<String, Any?>) {
        launch {

            val response = withContext(Dispatchers.IO) {
                mOrderRepository.endOrderInfo(param)
            }
            executeResponse(response, {
                endOrderInfoLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
            })
        }
    }

    fun endOrderQR(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mOrderRepository.endOrderQR(param)
            }
            executeResponse(response, {
                endOrderQRLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
            })
        }
    }

    fun payResultInquiry(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mOrderRepository.payResultInquiry(param)
            }
            executeResponse(response, {
                payResultInquiryLiveData.value = response.attr
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
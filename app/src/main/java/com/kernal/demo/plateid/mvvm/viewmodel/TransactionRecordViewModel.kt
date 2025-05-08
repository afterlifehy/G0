package com.kernal.demo.plateid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.kernal.demo.base.base.mvvm.BaseViewModel
import com.kernal.demo.base.base.mvvm.ErrorMessage
import com.kernal.demo.base.bean.DebtCollectionResultBean
import com.kernal.demo.base.bean.TicketPrintResultBean
import com.kernal.demo.plateid.mvvm.repository.OrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TransactionRecordViewModel : BaseViewModel() {

    val mOrderRepository by lazy {
        OrderRepository()
    }

    val inquiryTransactionByOrderNoLiveData = MutableLiveData<TicketPrintResultBean>()
    val debtInquiryLiveData = MutableLiveData<DebtCollectionResultBean>()

    fun debtInquiry(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mOrderRepository.debtInquiry(param)
            }
            executeResponse(response, {
                debtInquiryLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
            })
        }
    }
    fun inquiryTransactionByOrderNo(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mOrderRepository.inquiryTransactionByOrderNo(param)
            }
            executeResponse(response, {
                inquiryTransactionByOrderNoLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
            })
        }
    }
}
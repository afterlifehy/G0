package ja.insepector.bxapp.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import ja.insepector.base.base.mvvm.BaseViewModel
import ja.insepector.base.base.mvvm.ErrorMessage
import ja.insepector.base.bean.TransactionResultBean
import ja.insepector.bxapp.mvvm.repository.OrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TransactionInquiryViewModel : BaseViewModel() {
    val mOrderRepository by lazy {
        OrderRepository()
    }

    val transactionInquiryLiveData = MutableLiveData<Boolean>()
    val notificationInquiryLiveData = MutableLiveData<Any>()
    val payResultLiveData = MutableLiveData<Any>()

    fun transactionInquiry(param: Map<String, Any?>) {
        transactionInquiryLiveData.value = true
//        launch {
//            val response = withContext(Dispatchers.IO) {
//                mOrderRepository.transactionInquiry(param)
//            }
//            executeResponse(response, {
//                transactionInquiryLiveData.value = response.attr
//            }, {
//                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
//            })
//        }
    }

    fun notificationInquiry(param: Map<String, Any?>) {
//        launch {
//            val response = withContext(Dispatchers.IO) {
//                mOrderRepository.notificationInquiry(param)
//            }
//            executeResponse(response, {
//                notificationInquiryLiveData.value = response.attr
//            }, {
//                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
//            })
//        }
    }

    fun payResult(param: Map<String, Any?>) {
//        launch {
//            val response = withContext(Dispatchers.IO) {
//                mOrderRepository.payResult(param)
//            }
//            executeResponse(response, {
//                payResultLiveData.value = response.attr
//            }, {
//                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
//            })
//        }
    }
}
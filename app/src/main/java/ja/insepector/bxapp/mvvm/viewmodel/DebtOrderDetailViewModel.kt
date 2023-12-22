package ja.insepector.bxapp.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import ja.insepector.base.base.mvvm.BaseViewModel
import ja.insepector.base.base.mvvm.ErrorMessage
import ja.insepector.base.bean.PicInquiryBean
import ja.insepector.bxapp.mvvm.repository.OrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DebtOrderDetailViewModel : BaseViewModel() {
    val mOrderRepository by lazy {
        OrderRepository()
    }

    val debtPayLiveData = MutableLiveData<Any>()
    val payResultLiveData = MutableLiveData<Any>()
    val picInquiryLiveData = MutableLiveData<PicInquiryBean>()

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

    fun debtPay(param: Map<String, Any?>) {
        debtPayLiveData.value = true
//        launch {
//            val response = withContext(Dispatchers.IO) {
//                mOrderRepository.debtPay(param)
//            }
//            executeResponse(response, {
//                debtPayLiveData.value = response.attr
//            }, {
//                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
//            })
//        }
    }

    fun payResult(param: Map<String, Any?>) {
        payResultLiveData.value = true
//        launch {
//            val response = withContext(Dispatchers.IO) {
//                mOrderRepository.payResult(param)
//            }
//            executeResponse(response, {
//                payResultLiveData.value = response.attr
//            }, {
//                traverseErrorMsg(ErrorMessage(msg = "", code = response.status))
//            })
//        }
    }
}
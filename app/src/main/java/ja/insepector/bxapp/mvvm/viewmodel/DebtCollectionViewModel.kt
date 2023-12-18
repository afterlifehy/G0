package ja.insepector.bxapp.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import ja.insepector.base.base.mvvm.BaseViewModel
import ja.insepector.base.base.mvvm.ErrorMessage
import ja.insepector.bxapp.mvvm.repository.OrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DebtCollectionViewModel : BaseViewModel() {
    val mOrderRepository by lazy {
        OrderRepository()
    }

    val debtInquiryLiveData = MutableLiveData<Any>()
    val debtPayLiveData = MutableLiveData<Any>()

    fun debtInquiry(param: Map<String, Any?>) {
        debtInquiryLiveData.value = true
//        launch {
//            val response = withContext(Dispatchers.IO) {
//                mOrderRepository.debtInquiry(param)
//            }
//            executeResponse(response, {
//                debtInquiryLiveData.value = response.attr
//            }, {
//                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
//            })
//        }
    }
}
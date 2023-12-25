package ja.insepector.bxapp.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import ja.insepector.base.base.mvvm.BaseViewModel
import ja.insepector.base.base.mvvm.ErrorMessage
import ja.insepector.base.bean.OrderBean
import ja.insepector.base.bean.OrderResultBean
import ja.insepector.bxapp.mvvm.repository.OrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OrderInquiryViewModel : BaseViewModel() {
    val mOrderRepository by lazy {
        OrderRepository()
    }

    val orderInquiryLiveData = MutableLiveData<OrderResultBean>()
    val debtUploadLiveData = MutableLiveData<Any>()

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
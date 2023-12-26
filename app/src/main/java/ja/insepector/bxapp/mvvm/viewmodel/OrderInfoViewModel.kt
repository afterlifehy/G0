package ja.insepector.bxapp.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import ja.insepector.base.base.mvvm.BaseViewModel
import ja.insepector.base.base.mvvm.ErrorMessage
import ja.insepector.base.bean.EndOrderInfoBean
import ja.insepector.base.bean.PayQRBean
import ja.insepector.bxapp.mvvm.repository.OrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OrderInfoViewModel : BaseViewModel() {

    val mOrderRepository by lazy {
        OrderRepository()
    }

    val endOrderInfoLiveData = MutableLiveData<EndOrderInfoBean>()
    val endOrderQRLiveData = MutableLiveData<PayQRBean>()

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
}
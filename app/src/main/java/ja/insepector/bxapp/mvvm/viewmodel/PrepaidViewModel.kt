package ja.insepector.bxapp.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import ja.insepector.base.base.mvvm.BaseViewModel
import ja.insepector.base.base.mvvm.ErrorMessage
import ja.insepector.base.bean.PrePayFeeInquiryBean
import ja.insepector.bxapp.mvvm.repository.OrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PrepaidViewModel: BaseViewModel() {
    val mOrderRepository by lazy {
        OrderRepository()
    }

    val prePayFeeInquiryLiveData = MutableLiveData<PrePayFeeInquiryBean>()

    fun prePayFeeInquiry(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mOrderRepository.prePayFeeInquiry(param)
            }
            executeResponse(response, {
                prePayFeeInquiryLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
            })
        }
    }
}
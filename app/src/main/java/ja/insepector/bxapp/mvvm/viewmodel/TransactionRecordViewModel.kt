package ja.insepector.bxapp.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import ja.insepector.base.base.mvvm.BaseViewModel
import ja.insepector.base.base.mvvm.ErrorMessage
import ja.insepector.base.bean.TransactionBean
import ja.insepector.base.bean.TransactionResultBean
import ja.insepector.bxapp.mvvm.repository.OrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TransactionRecordViewModel : BaseViewModel() {

    val mOrderRepository by lazy {
        OrderRepository()
    }

    val transactionInquiryByOrderLiveData = MutableLiveData<TransactionResultBean>()
    val notificationInquiryLiveData = MutableLiveData<Any>()

    fun transactionInquiryByOrder(param: Map<String, Any?>) {
        val list: MutableList<TransactionBean> = ArrayList()
        list.add(
            TransactionBean(
                "沪A36N81",
                "2023-06-25 08:10:52",
                "",
                "20230625JAZ02100075601",
                "100",
                "JAZ-021-008",
                "50",
                "2023-06-25 08:01:43",
                "20230625JAZ02100075601"
            )
        )
        list.add(
            TransactionBean(
                "沪A36N81",
                "2023-06-25 08:10:52",
                "",
                "20230625JAZ02100075601",
                "100",
                "JAZ-021-008",
                "50",
                "2023-06-25 08:01:43",
                "20230625JAZ02100075601"
            )
        )
        val transactionResultBean = TransactionResultBean(list)
        transactionInquiryByOrderLiveData.value = transactionResultBean
//        launch {
//            val response = withContext(Dispatchers.IO) {
//                mOrderRepository.transactionInquiryByOrder(param)
//            }
//            executeResponse(response, {
//                transactionInquiryByOrderLiveData.value = response.attr
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
}
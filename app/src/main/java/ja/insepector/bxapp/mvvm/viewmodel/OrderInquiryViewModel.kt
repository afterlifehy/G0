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

    fun orderInquiry(param: Map<String, Any?>) {
        val orderList: MutableList<OrderBean> = ArrayList()
        orderList.add(OrderBean("45","沪DRX357","100","2023-06-25 12:22:51","","1","JAZ-021-006","2023-06-25 10:11:45","111","45"))
        orderList.add(OrderBean("0","沪DRX357","1000","2023-06-25 12:22:51","","2","JAZ-021-006","2023-06-25 10:11:45","111","0"))
        orderList.add(OrderBean("45","沪DRX357","1100","2023-06-25 12:22:51","","3","JAZ-021-006","2023-06-25 10:11:45","111","0"))
        orderList.add(OrderBean("45","沪DRX357","5000","2023-06-25 12:22:51","","4","JAZ-021-006","2023-06-25 10:11:45","111","45"))
        orderList.add(OrderBean("45","沪DRX357","10000","2023-06-25 12:22:51","","5","JAZ-021-006","2023-06-25 10:11:45","111","0"))
        orderList.add(OrderBean("45","沪DRX357","20000","2023-06-25 12:22:51","","6","JAZ-021-006","2023-06-25 10:11:45","111","0"))
        orderList.add(OrderBean("45","沪DRX357","50000","2023-06-25 12:22:51","","7","JAZ-021-006","2023-06-25 10:11:45","111","0"))
        orderList.add(OrderBean("45","沪DRX357","100000","2023-06-25 12:22:51","","8","JAZ-021-006","2023-06-25 10:11:45","111","0"))
        orderInquiryLiveData.value = OrderResultBean(orderList)
//        launch {
//            val response = withContext(Dispatchers.IO) {
//                mOrderRepository.orderInquiry(param)
//            }
//            executeResponse(response, {
//                orderInquiryLiveData.value = response.attr
//            }, {
//                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
//            })
//        }
    }
}
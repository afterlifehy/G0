package com.kernal.demo.plateid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.kernal.demo.base.base.mvvm.BaseViewModel
import com.kernal.demo.plateid.mvvm.repository.OrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.kernal.demo.base.base.mvvm.ErrorMessage
import com.kernal.demo.base.bean.UrgeBean

class UrgeListViewModel : BaseViewModel() {
    val mOrderRepository by lazy {
        OrderRepository()
    }

    val urgepaylistLiveData = MutableLiveData<List<UrgeBean>>()


    fun urgepaylist(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mOrderRepository.urgepaylist(param)
            }
            executeResponse(response, {
                urgepaylistLiveData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.message, code = response.code, api = "urgepaylist"))
            })
        }
    }

}
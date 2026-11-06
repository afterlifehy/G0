package com.kernal.demo.plateid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.kernal.demo.base.base.mvvm.BaseViewModel
import com.kernal.demo.base.bean.FreeParkDetailBean
import com.kernal.demo.plateid.mvvm.repository.OrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.kernal.demo.base.base.mvvm.ErrorMessage

class FreeParkDetailViewModel: BaseViewModel() {
    val mOrderRepository by lazy {
        OrderRepository()
    }

    val freeParkDetailLiveData = MutableLiveData<List<FreeParkDetailBean>>()
    fun freeParkDetail(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mOrderRepository.freeParkDetail(param)
            }
            executeResponse(response, {
                freeParkDetailLiveData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.message, code = response.code, api = "freeParkDetail"))
            })
        }
    }
}
package com.kernal.demo.plateid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.kernal.demo.base.base.mvvm.BaseViewModel
import com.kernal.demo.base.base.mvvm.ErrorMessage
import com.kernal.demo.base.bean.IncomeCountingBean
import com.kernal.demo.plateid.mvvm.repository.IncomeCountingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class IncomeCountingViewModel: BaseViewModel() {

    val mIncomeCountingRepository by lazy {
        IncomeCountingRepository()
    }

    val incomeCountingLiveData = MutableLiveData<IncomeCountingBean>()

    fun incomeCounting(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mIncomeCountingRepository.incomeCounting(param)
            }
            executeResponse(response, {
                incomeCountingLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
            })
        }
    }
}
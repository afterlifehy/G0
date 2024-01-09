package com.kernal.demo.plateid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.kernal.demo.base.base.mvvm.BaseViewModel
import com.kernal.demo.base.base.mvvm.ErrorMessage
import com.kernal.demo.base.bean.FeeRateResultBean
import com.kernal.demo.plateid.mvvm.repository.MineRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FeeRateFragmentViewModel : BaseViewModel() {

    val mMineRepository by lazy {
        MineRepository()
    }
    val feeRateLiveData = MutableLiveData<FeeRateResultBean>()

    fun feeRate(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mMineRepository.feeRate(param)
            }
            executeResponse(response, {
                feeRateLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
            })
        }
    }
}
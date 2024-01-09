package com.kernal.demo.plateid.mvvm.repository

import com.kernal.demo.base.base.mvvm.BaseRepository
import com.kernal.demo.base.bean.HttpWrapper
import com.kernal.demo.base.bean.IncomeCountingBean

class IncomeCountingRepository : BaseRepository() {

    /**
     * 营收盘点
     */
    suspend fun incomeCounting(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<IncomeCountingBean> {
        return mServer.incomeCounting(param)
    }
}
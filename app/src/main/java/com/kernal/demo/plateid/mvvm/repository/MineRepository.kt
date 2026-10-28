package com.kernal.demo.plateid.mvvm.repository

import com.kernal.demo.base.base.mvvm.BaseRepository
import com.kernal.demo.base.bean.FeeRateResultBean
import com.kernal.demo.base.bean.HttpWrapper
import com.kernal.demo.base.bean.IncomeCountingBean
import com.kernal.demo.base.bean.UpdateBean

class MineRepository : BaseRepository() {
    /**
     * 版本更新查询
     */
    suspend fun checkUpdate(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<UpdateBean> {
        return mRTServer.checkUpdate(param)
    }

    /**
     * 营收打印
     */
    suspend fun incomeCounting(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<IncomeCountingBean> {
        return mRTServer.incomeCounting(param)
    }

    /**
     * 费率
     */
    suspend fun feeRate(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<FeeRateResultBean> {
        return mRTServer.feeRate(param)
    }

    /**
     * 上传经纬度
     */
    suspend fun locationUpload(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any> {
        return mRTServer.locationUpload(param)
    }
}
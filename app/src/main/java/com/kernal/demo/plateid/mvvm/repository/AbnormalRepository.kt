package com.kernal.demo.plateid.mvvm.repository

import com.kernal.demo.base.base.mvvm.BaseRepository
import com.kernal.demo.base.bean.HttpWrapper
import com.kernal.demo.base.bean.OrderNoBean

class AbnormalRepository : BaseRepository() {
    /**
     * 异常上报
     */
    suspend fun abnormalReport(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any> {
        return mServer.abnormalReport(param)
    }

    /**
     * 图片上传
     */
    suspend fun picUpload(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any> {
        return mServer.picUpload(param)
    }

    /**
     * 根据parkingNo查询orderNo
     */
    suspend fun inquiryOrderNoByParkingNo(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<OrderNoBean> {
        return mServer.inquiryOrderNoByParkingNo(param)
    }
}
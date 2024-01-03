package ja.insepector.bxapp.mvvm.repository

import ja.insepector.base.base.mvvm.BaseRepository
import ja.insepector.base.bean.HttpWrapper
import ja.insepector.base.bean.OrderNoBean

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
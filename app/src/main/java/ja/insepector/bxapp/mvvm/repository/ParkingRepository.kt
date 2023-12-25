package ja.insepector.bxapp.mvvm.repository

import ja.insepector.base.base.mvvm.BaseRepository
import ja.insepector.base.bean.HttpWrapper
import ja.insepector.base.bean.ParkingLotResultBean
import ja.insepector.base.bean.ParkingSpaceBean
import ja.insepector.base.bean.PlaceOederResultBean
import ja.insepector.base.bean.TicketPrintBean

//import ja.insepector.base.bean.ParkingLotResultBean
//import ja.insepector.base.bean.ParkingSpaceBean
//import ja.insepector.base.bean.PayResultBean
//import ja.insepector.base.bean.QRPayBean

class ParkingRepository : BaseRepository() {

    /**
     * 停车场泊位列表
     */
    suspend fun getParkingLotList(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<ParkingLotResultBean> {
        return mServer.getParkingLotList(param)
    }

    /**
     * 场内停车费查询
     */
    suspend fun parkingSpace(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<ParkingSpaceBean> {
        return mServer.parkingSpace(param)
    }

    /**
     * 下单
     */
    suspend fun placeOrder(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<PlaceOederResultBean> {
        return mServer.placeOrder(param)
    }

    /**
     * 结单
     */
    suspend fun endOrder(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any> {
        return mServer.endOrder(param)
    }

    /**
     * 图片上传
     */
    suspend fun picUpload(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any> {
        return mServer.picUpload(param)
    }

    /**
     *  场内支付
     */
    suspend fun ticketPrint(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<TicketPrintBean> {
        return mServer.ticketPrint(param)
    }

//    /**
//     *  支付结果
//     */
//    suspend fun payResult(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<PayResultBean> {
//        return mServer.payResult(param)
//    }
}
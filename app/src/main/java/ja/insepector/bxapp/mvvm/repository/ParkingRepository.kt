package ja.insepector.bxapp.mvvm.repository

import ja.insepector.base.base.mvvm.BaseRepository
import ja.insepector.base.bean.DebtUploadBean
import ja.insepector.base.bean.HttpWrapper
import ja.insepector.base.bean.ParkingLotResultBean
import ja.insepector.base.bean.ParkingSpaceBean
import ja.insepector.base.bean.PlaceOederResultBean
import ja.insepector.base.bean.TicketPrintBean
import ja.insepector.base.bean.TicketPrintResultBean

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

    /**
     *  根据订单查交易
     */
    suspend fun inquiryTransactionByOrderNo(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<TicketPrintResultBean> {
        return mServer.inquiryTransactionByOrderNo(param)
    }

    /**
     * 欠费上传
     */
    suspend fun debtUpload(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<DebtUploadBean> {
        return mServer.debtUpload(param)
    }
//    /**
//     *  支付结果
//     */
//    suspend fun payResult(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<PayResultBean> {
//        return mServer.payResult(param)
//    }
}
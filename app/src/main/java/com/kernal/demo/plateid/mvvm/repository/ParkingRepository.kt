package com.kernal.demo.plateid.mvvm.repository

import com.kernal.demo.base.base.mvvm.BaseRepository
import com.kernal.demo.base.bean.DebtUploadBean
import com.kernal.demo.base.bean.HttpWrapper
import com.kernal.demo.base.bean.ParkingLotResultBean
import com.kernal.demo.base.bean.ParkingSpaceBean
import com.kernal.demo.base.bean.PayQRBean
import com.kernal.demo.base.bean.PlaceOrderResultBean
import com.kernal.demo.base.bean.TicketPrintBean
import com.kernal.demo.base.bean.TicketPrintResultBean

class ParkingRepository : BaseRepository() {

    /**
     * 停车场泊位列表
     */
    suspend fun getParkingLotList(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<ParkingLotResultBean> {
        return mRTServer.getParkingLotList(param)
    }

    /**
     * 场内停车费查询
     */
    suspend fun parkingSpace(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<ParkingSpaceBean> {
        return mRTServer.parkingSpace(param)
    }

    /**
     * 下单
     */
    suspend fun placeOrder(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<PlaceOrderResultBean> {
        return mRTServer.placeOrder(param)
    }

    /**
     * 结单
     */
    suspend fun endOrder(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any> {
        return mRTServer.endOrder(param)
    }

    /**
     * 图片上传
     */
    suspend fun picUpload(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any> {
        return mRTServer.picUpload(param)
    }

    /**
     *  场内支付
     */
    suspend fun ticketPrint(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<TicketPrintBean> {
        return mRTServer.ticketPrint(param)
    }

    /**
     *  根据订单查交易
     */
    suspend fun inquiryTransactionByOrderNo(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<TicketPrintResultBean> {
        return mRTServer.inquiryTransactionByOrderNo(param)
    }

    /**
     * 欠费上传
     */
    suspend fun debtUpload(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<DebtUploadBean> {
        return mRTServer.debtUpload(param)
    }
//    /**
//     *  支付结果
//     */
//    suspend fun payResult(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<PayResultBean> {
//        return mServer.payResult(param)
//    }

    /**
     * 场内支付
     */
    suspend fun onsitePayQR(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<PayQRBean> {
        return mRTServer.onsitePayQR(param)
    }

    /**
     * 查询支付结果
     */
    suspend fun payResultInquiry(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<TicketPrintBean> {
        return mRTServer.payResultInquiry(param)
    }
}
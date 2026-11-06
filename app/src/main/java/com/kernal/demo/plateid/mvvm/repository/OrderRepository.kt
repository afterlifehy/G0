package com.kernal.demo.plateid.mvvm.repository

import com.blankj.utilcode.util.EncryptUtils
import com.kernal.demo.base.base.mvvm.BaseRepository
import com.kernal.demo.base.bean.DebtCollectionResultBean
import com.kernal.demo.base.bean.DebtUploadBean
import com.kernal.demo.base.bean.EndOrderInfoBean
import com.kernal.demo.base.bean.FreeParkBean
import com.kernal.demo.base.bean.FreeParkDetailBean
import com.kernal.demo.base.bean.HttpWrapper
import com.kernal.demo.base.bean.HttpWrapper2
import com.kernal.demo.base.bean.OrderResultBean
import com.kernal.demo.base.bean.OweMoneyBean
import com.kernal.demo.base.bean.PicInquiryBean
import com.kernal.demo.base.bean.PayQRBean
import com.kernal.demo.base.bean.QueryPayBean
import com.kernal.demo.base.bean.TicketPrintBean
import com.kernal.demo.base.bean.TicketPrintResultBean
import com.kernal.demo.base.bean.TransactionResultBean
import com.kernal.demo.base.bean.UploadImageBean
import com.kernal.demo.base.bean.UrgeBean
import com.kernal.demo.base.bean.UrgeDetailBean
import com.kernal.demo.base.util.Constant
import retrofit2.http.Body

class OrderRepository : BaseRepository() {

    /**
     * 订单查询
     */
    suspend fun orderInquiry(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<OrderResultBean> {
        return mRTServer.orderInquiry(param)
    }

    /**
     * 交易查询
     */
    suspend fun transactionInquiry(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<TransactionResultBean> {
        return mRTServer.transactionInquiry(param)
    }

    /**
     * 欠费查询
     */
    suspend fun debtInquiry(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<DebtCollectionResultBean> {
        return mRTServer.debtInquiry(param)
    }

    /**
     * 图片查询
     */
    suspend fun picInquiry(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<PicInquiryBean> {
        return mRTServer.picInquiry(param)
    }

    /**
     * 预支付查询
     */
    suspend fun prePayFeeInquiry(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<PayQRBean> {
        return mRTServer.prePayFeeInquiry(param)
    }

    /**
     * 离场订单查询
     */
    suspend fun endOrderInfo(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<EndOrderInfoBean> {
        return mRTServer.endOrderInfo(param)
    }

    /**
     * 欠费上传
     */
    suspend fun debtUpload(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<DebtUploadBean> {
        return mRTServer.debtUpload(param)
    }

    /**
     * 票据打印
     */
    suspend fun ticketPrint(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<TicketPrintBean> {
        return mRTServer.ticketPrint(param)
    }

    /**
     * 查询支付结果
     */
    suspend fun payResultInquiry(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<TicketPrintBean> {
        return mRTServer.payResultInquiry(param)
    }

    /**
     *  根据订单查交易
     */
    suspend fun inquiryTransactionByOrderNo(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<TicketPrintResultBean> {
        return mRTServer.inquiryTransactionByOrderNo(param)
    }

    /**
     *  离场支付二维码
     */
    suspend fun endOrderQR(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<PayQRBean> {
        return mRTServer.endOrderQR(param)
    }

    /**
     *  追缴二维码
     */
    suspend fun debtPayQr(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<PayQRBean> {
        return mRTServer.debtPayQr(param)
    }

    /**
     * 欠费催缴单查询
     */
    suspend fun urgepaylist(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper2<List<UrgeBean>> {
        val nonce = EncryptUtils.encryptMD5ToString(Math.random().toString()).lowercase()
        val curTime = (System.currentTimeMillis() / 1000).toString()
        val checkSum = EncryptUtils.encryptSHA1ToString(Constant.PASSWORD + nonce + curTime).lowercase()
        val options: Map<String, String> = mapOf(
            "nonce" to nonce,
            "curTime" to curTime,
            "checkSum" to checkSum,
            "appId" to Constant.APP_ID
        )
        return mServer.urgepaylist(param, options)
    }

    /**
     * 催缴告知书详情
     */
    suspend fun urgepaydetail(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper2<UrgeDetailBean> {
        val nonce = EncryptUtils.encryptMD5ToString(Math.random().toString()).lowercase()
        val curTime = (System.currentTimeMillis() / 1000).toString()
        val checkSum = EncryptUtils.encryptSHA1ToString(Constant.PASSWORD + nonce + curTime).lowercase()
        val options: Map<String, String> = mapOf(
            "nonce" to nonce,
            "curTime" to curTime,
            "checkSum" to checkSum,
            "appId" to Constant.APP_ID
        )
        return mServer.urgepaydetail(param, options)
    }

    /**
     * 欠费支付请求
     */
    suspend fun payowemoney(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper2<OweMoneyBean> {
        val nonce = EncryptUtils.encryptMD5ToString(Math.random().toString()).lowercase()
        val curTime = (System.currentTimeMillis() / 1000).toString()
        val checkSum = EncryptUtils.encryptSHA1ToString(Constant.PASSWORD + nonce + curTime).lowercase()
        val options: Map<String, String> = mapOf(
            "nonce" to nonce,
            "curTime" to curTime,
            "checkSum" to checkSum,
            "appId" to Constant.APP_ID
        )
        return mServer.payowemoney(param, options)
    }

    /**
     * 催缴告知书支付二维码
     */
    suspend fun consumeUrgePay(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper2<PayQRBean> {
        val nonce = EncryptUtils.encryptMD5ToString(Math.random().toString()).lowercase()
        val curTime = (System.currentTimeMillis() / 1000).toString()
        val checkSum = EncryptUtils.encryptSHA1ToString(Constant.PASSWORD + nonce + curTime).lowercase()
        val options: Map<String, String> = mapOf(
            "nonce" to nonce,
            "curTime" to curTime,
            "checkSum" to checkSum,
            "appId" to Constant.APP_ID
        )
        return mServer.consumeUrgePay(param, options)
    }

    /**
     * 催缴告知书二维码
     */
    suspend fun urgepayQrcode(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper2<PayQRBean> {
        val nonce = EncryptUtils.encryptMD5ToString(Math.random().toString()).lowercase()
        val curTime = (System.currentTimeMillis() / 1000).toString()
        val checkSum = EncryptUtils.encryptSHA1ToString(Constant.PASSWORD + nonce + curTime).lowercase()
        val options: Map<String, String> = mapOf(
            "nonce" to nonce,
            "curTime" to curTime,
            "checkSum" to checkSum,
            "appId" to Constant.APP_ID
        )
        return mServer.urgepayQrcode(param, options)
    }

    /**
     * 支付结果查询
     */
    suspend fun querypay(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper2<QueryPayBean> {
        val nonce = EncryptUtils.encryptMD5ToString(Math.random().toString()).lowercase()
        val curTime = (System.currentTimeMillis() / 1000).toString()
        val checkSum = EncryptUtils.encryptSHA1ToString(Constant.PASSWORD + nonce + curTime).lowercase()
        val options: Map<String, String> = mapOf(
            "nonce" to nonce,
            "curTime" to curTime,
            "checkSum" to checkSum,
            "appId" to Constant.APP_ID
        )
        return mServer.querypay(param, options)
    }

    /**
     * 欠费催缴告知书打印通知
     */
    suspend fun urgepayprint(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper2<Any> {
        val nonce = EncryptUtils.encryptMD5ToString(Math.random().toString()).lowercase()
        val curTime = (System.currentTimeMillis() / 1000).toString()
        val checkSum = EncryptUtils.encryptSHA1ToString(Constant.PASSWORD + nonce + curTime).lowercase()
        val options: Map<String, String> = mapOf(
            "nonce" to nonce,
            "curTime" to curTime,
            "checkSum" to checkSum,
            "appId" to Constant.APP_ID
        )
        return mServer.urgepayprint(param, options)
    }

    /**
     * 欠费催缴告知书记录上传
     */
    suspend fun urgepay(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper2<Any> {
        val nonce = EncryptUtils.encryptMD5ToString(Math.random().toString()).lowercase()
        val curTime = (System.currentTimeMillis() / 1000).toString()
        val checkSum = EncryptUtils.encryptSHA1ToString(Constant.PASSWORD + nonce + curTime).lowercase()
        val options: Map<String, String> = mapOf(
            "nonce" to nonce,
            "curTime" to curTime,
            "checkSum" to checkSum,
            "appId" to Constant.APP_ID
        )
        return mServer.urgepay(param, options)
    }

    /**
     * 欠费催缴单通知
     */
    suspend fun urgeBeanNotice(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any> {
        return mRTServer.urgeBeanNotice(param)
    }

    /**
     * 欠费催缴单欠费通知
     */
    suspend fun urgeDetailNotice(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any> {
        return mRTServer.urgeDetailNotice(param)
    }

    /**
     * 欠费催缴单支付通知
     */
    suspend fun urgePayNotice(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any> {
        return mRTServer.urgePayNotice(param)
    }

    /**
     * 欠费催缴单支付结果通知
     */
    suspend fun urgePayResultNotice(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any> {
        return mRTServer.urgePayResultNotice(param)
    }

    /**
     * 欠费催缴单打印通知
     */
    suspend fun urgePrintNotice(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any> {
        return mRTServer.urgePrintNotice(param)
    }

    /**
     * 欠费催缴单催缴通知
     */
    suspend fun urgeNotice(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any> {
        return mRTServer.urgeNotice(param)
    }
    /**
     * 图片上传我们平台
     */
    suspend fun uploadFileForApp(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper2<UploadImageBean>{
        return mImageUploadServer.uploadFileForApp(param)
    }

    /**
     * 免费停车记录
     */
    suspend fun freePark(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper2<FreeParkBean> {
        val nonce = EncryptUtils.encryptMD5ToString(Math.random().toString()).lowercase()
        val curTime = (System.currentTimeMillis() / 1000).toString()
        val checkSum = EncryptUtils.encryptSHA1ToString(Constant.PASSWORD + nonce + curTime).lowercase()
        val options: Map<String, String> = mapOf(
            "nonce" to nonce,
            "curTime" to curTime,
            "checkSum" to checkSum,
            "appId" to Constant.APP_ID
        )
        return mServer.freePark(param, options)
    }

    /**
     * 免费停车记录
     */
    suspend fun freeParkDetail(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper2<List<FreeParkDetailBean>> {
        val nonce = EncryptUtils.encryptMD5ToString(Math.random().toString()).lowercase()
        val curTime = (System.currentTimeMillis() / 1000).toString()
        val checkSum = EncryptUtils.encryptSHA1ToString(Constant.PASSWORD + nonce + curTime).lowercase()
        val options: Map<String, String> = mapOf(
            "nonce" to nonce,
            "curTime" to curTime,
            "checkSum" to checkSum,
            "appId" to Constant.APP_ID
        )
        return mServer.freeParkDetail(param, options)
    }
}
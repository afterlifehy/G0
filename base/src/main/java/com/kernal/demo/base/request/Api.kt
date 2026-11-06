package com.kernal.demo.base.request

import com.kernal.demo.base.bean.*
import okhttp3.MultipartBody
import retrofit2.http.*


interface Api {
    /**
     * 签到
     */
    @POST("S_G0_01")
    suspend fun login(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<LoginBean>

    /**
     * 停车场泊位列表
     */
    @POST("S_VO2_02")
    suspend fun getParkingLotList(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<ParkingLotResultBean>

    /**
     * 下单
     */
    @POST("S_G0_02")
    suspend fun placeOrder(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<PlaceOrderResultBean>

    /**
     * 结单
     */
    @POST("S_G0_03")
    suspend fun endOrder(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any>

    /**
     * 预支付数据查询
     */
    @POST("S_G0_04_1")
    suspend fun prePayFeeInquiry(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<PayQRBean>

    /**
     * 支付结果查询
     */
    @POST("S_G0_05_1")
    suspend fun payResultInquiry(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<TicketPrintBean>

    /**
     * 欠费上传
     */
    @POST("S_G0_06")
    suspend fun debtUpload(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<DebtUploadBean>

    /**
     * 签退
     */
    @POST("S_G0_07")
    suspend fun logout(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any>

    /**
     * 图片上传
     */
    @POST("S_G0_08")
    suspend fun picUpload(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any>

    /**
     * 异常上报
     */
    @POST("S_G0_10")
    suspend fun abnormalReport(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any>

    /**
     * 订单查询
     */
    @POST("S_G0_11")
    suspend fun orderInquiry(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<OrderResultBean>

    /**
     * 交易查询
     */
    @POST("S_G0_12")
    suspend fun transactionInquiry(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<TransactionResultBean>

    /**
     * 营收盘点
     */
    @POST("S_G0_13")
    suspend fun incomeCounting(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<IncomeCountingBean>

    /**
     * 费率查询
     */
    @POST("S_G0_14")
    suspend fun feeRate(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<FeeRateResultBean>

    /**
     * 版本查询
     */
    @POST("S_V_01")
    suspend fun checkUpdate(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<UpdateBean>

    /**
     * 泊位订单查询
     */
    @POST("S_G0_16_1")
    suspend fun parkingSpace(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<ParkingSpaceBean>

    /**
     * 欠费查询
     */
    @POST("S_G0_17")
    suspend fun debtInquiry(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<DebtCollectionResultBean>

    /**
     * 泊位订单查询
     */
    @POST("S_G0_18")
    suspend fun picInquiry(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<PicInquiryBean>

    /**
     * 票据查询
     */
    @POST("S_G0_19")
    suspend fun ticketPrint(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<TicketPrintBean>

    /**
     * 离场订单查询
     */
    @POST("S_G0_20")
    suspend fun endOrderInfo(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<EndOrderInfoBean>

    /**
     * 考勤排班
     */
    @POST("S_VO2_20")
    suspend fun checkOnWork(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any>

    /**
     * 根据orderNo查交易
     */
    @POST("S_G0_21")
    suspend fun inquiryTransactionByOrderNo(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<TicketPrintResultBean>

    /**
     * 根据泊位号查询orderNo
     */
    @POST("S_G0_22")
    suspend fun inquiryOrderNoByParkingNo(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<OrderNoBean>

    /**
     * 场内支付二维码
     */
    @POST("S_G0_25_1")
    suspend fun onsitePayQR(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<PayQRBean>

    /**
     * 离场支付二维码
     */
    @POST("S_OR_0112")
    suspend fun endOrderQR(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<PayQRBean>

    /**
     * 追缴二维码
     */
    @POST("S_G0_27_1")
    suspend fun debtPayQr(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<PayQRBean>

    /**
     * 轨迹上传
     */
    @POST("S_VO3_02")
    suspend fun locationUpload(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any>

    /**
     * 日志上传
     */
    @Multipart
    @POST("upload")
    suspend fun logFileUpload(@Part file: MultipartBody.Part): HttpWrapper<Any>

    /**
     * 修改密码
     */
    @POST("S_V_03")
    suspend fun editPw(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any>

    /**
     * 查询是为初始密码
     */
    @POST("S_V_05")
    suspend fun queryPwStatus(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<QueryPwStatusBean>

    /**
     * 欠费催缴单查询
     */
    @POST("urgepaylist")
    suspend fun urgepaylist(
        @Body param: @JvmSuppressWildcards Map<String, Any?>, @QueryMap options: @JvmSuppressWildcards Map<String, String>
    ): HttpWrapper2<List<UrgeBean>>

    /**
     * 催缴告知书详情
     */
    @POST("urgepaydetail")
    suspend fun urgepaydetail(
        @Body param: @JvmSuppressWildcards Map<String, Any?>,
        @QueryMap options: @JvmSuppressWildcards Map<String, String>
    ): HttpWrapper2<UrgeDetailBean>

    /**
     * 欠费支付请求
     */
    @POST("payowemoney")
    suspend fun payowemoney(
        @Body param: @JvmSuppressWildcards Map<String, Any?>,
        @QueryMap options: @JvmSuppressWildcards Map<String, String>
    ): HttpWrapper2<OweMoneyBean>

    /**
     * 催缴告知书支付二维码
     */
    @POST("consume/urgepay")
    suspend fun consumeUrgePay(
        @Body param: @JvmSuppressWildcards Map<String, Any?>,
        @QueryMap options: @JvmSuppressWildcards Map<String, String>
    ): HttpWrapper2<PayQRBean>

    /**
     * 催缴告知书二维码
     */
    @POST("urgepay/qrcode")
    suspend fun urgepayQrcode(
        @Body param: @JvmSuppressWildcards Map<String, Any?>,
        @QueryMap options: @JvmSuppressWildcards Map<String, String>
    ): HttpWrapper2<PayQRBean>

    /**
     * 支付结果查询
     */
    @POST("querypay")
    suspend fun querypay(
        @Body param: @JvmSuppressWildcards Map<String, Any?>,
        @QueryMap options: @JvmSuppressWildcards Map<String, String>
    ): HttpWrapper2<QueryPayBean>

    /**
     * 欠费催缴告知书打印通知
     */
    @POST("urgepayprint")
    suspend fun urgepayprint(
        @Body param: @JvmSuppressWildcards Map<String, Any?>,
        @QueryMap options: @JvmSuppressWildcards Map<String, String>
    ): HttpWrapper2<Any>

    /**
     * 欠费单催缴记录上传
     */
    @POST("urgepay")
    suspend fun urgepay(
        @Body param: @JvmSuppressWildcards Map<String, Any?>, @QueryMap options: @JvmSuppressWildcards Map<String, String>
    ): HttpWrapper2<Any>

    /**
     * 欠费催缴单通知
     */
    @POST("S_UR_01")
    suspend fun urgeBeanNotice(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any>

    /**
     * 欠费催缴单欠费通知
     */
    @POST("S_UR_02")
    suspend fun urgeDetailNotice(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any>

    /**
     * 欠费催缴单支付通知
     */
    @POST("S_UR_04")
    suspend fun urgePayNotice(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any>

    /**
     * 欠费催缴单支付结果通知
     */
    @POST("S_UR_05")
    suspend fun urgePayResultNotice(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any>

    /**
     * 欠费催缴单打印通知
     */
    @POST("S_UR_06")
    suspend fun urgePrintNotice(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any>

    /**
     * 欠费催缴单催缴通知
     */
    @POST("S_UR_07")
    suspend fun urgeNotice(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any>

    /**
     * 催缴告知书二维码
     */
    @POST("freepark")
    suspend fun freePark(
        @Body param: @JvmSuppressWildcards Map<String, Any?>, @QueryMap options: @JvmSuppressWildcards Map<String, String>
    ): HttpWrapper2<FreeParkBean>

    /**
     * 免费停车记录查询
     */
    @POST("freepark/detail")
    suspend fun freeParkDetail(
        @Body param: @JvmSuppressWildcards Map<String, Any?>, @QueryMap options: @JvmSuppressWildcards Map<String, String>
    ): HttpWrapper2<List<FreeParkDetailBean>>

    /**
     * 图片上传我们平台
     */
    @POST("uploadFileForApp")
    suspend fun uploadFileForApp(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper2<UploadImageBean>
}
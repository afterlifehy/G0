package ja.insepector.base.request

import ja.insepector.base.bean.*
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
    suspend fun placeOrder(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any>

    /**
     * 结单
     */
    @POST("S_G0_03")
    suspend fun endOrder(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any>

    /**
     * 预支付数据查询
     */
    @POST("S_G0_04")
    suspend fun prePayFeeInquiry(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<PrePayFeeInquiryBean>

    /**
     * 签退
     */
    @POST("S_G0_07")
    suspend fun logout(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any>
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
    @POST("S_G0_15")
    suspend fun checkUpdate(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<UpdateBean>

    /**
     * 泊位订单查询
     */
    @POST("S_G0_16")
    suspend fun parkingSpace(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<ParkingSpaceResultBean>

    /**
     * 泊位订单查询
     */
    @POST("S_G0_18")
    suspend fun picInquiry(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<PicInquiryBean>

    /**
     * 考勤排班
     */
    @POST("S_VO2_20")
    suspend fun checkOnWork(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any>

    /**
     * 欠费查询
     */
    @POST("S_G0_17")
    suspend fun debtInquiry(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<DebtCollectionResultBean>

    /**
     * 离场订单查询
     */
    @POST("S_G0_20")
    suspend fun endOrderInfo(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<EndOrderInfoBean>

}
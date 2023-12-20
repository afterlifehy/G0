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
     * 考勤排班
     */
    @POST("S_VO2_20")
    suspend fun checkOnWork(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any>

    /**
     * 欠费查询
     */
    @POST("S_CP_0108")
    suspend fun debtInquiry(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<DebtCollectionResultBean>

}
package com.kernal.demo.base.bean

data class QueryPayBean(
    val description: String,
    val orderId: String,
    val payMoney: Int,
    val payStatus: Int,
    val payTime: Long,
    val payType: Int
)
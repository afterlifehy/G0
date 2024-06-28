package com.kernal.demo.base.bean

data class PayResultBean(
    var businessCname: String = "",
    var carLicense: String = "",
    var endTime: String = "",
    var oweCount: Int = 0,
    var payMoney: String = "",
    var phone: String = "",
    var remark: String = "",
    var roadName: String = "",
    var startTime: String = "",
    var tradeNo: String = ""
)

data class PayResultPrintResultBean(
    var result: ArrayList<PayResultBean>
)
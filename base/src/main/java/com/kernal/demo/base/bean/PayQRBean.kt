package com.kernal.demo.base.bean

data class PayQRBean(
    var totalAmount: Double = 0.0,
    var qrCode: String = "",
    var qrcode:String,
    var tradeNo: String = "",
    var qr_code: String = "",
    var amount: Double = 0.0,
    var payUrl: String = "",
    val userId:String
)
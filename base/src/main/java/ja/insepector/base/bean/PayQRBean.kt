package ja.insepector.base.bean

data class PayQRBean(
    var totalAmount: Double = 0.0,
    var qrCode: String = "",
    var tradeNo: String = ""
)
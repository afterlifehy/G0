package ja.insepector.base.bean

data class EndOrderInfoBean(
    val carLicense: String = "",
    val hasPayed: String = "",
    val havePayMoney: String = "",
    val orderMoney: String = "",
    val parkingHours: Int = 0,
    val realtimeMoney: String = ""
)
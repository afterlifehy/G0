package ja.insepector.base.bean

data class FeeRateResultBean(
    val result: List<FeeRateBean>
)

data class FeeRateBean(
    val blackEnd: String = "",
    val blackStart: String = "",
    val dateType: Int = 0,
    val period: String = "",
    val unitPrice: String = "",
    val whiteEnd: String = "",
    val whiteStart: String = "",
    var firstHourMoney: String = ""
)
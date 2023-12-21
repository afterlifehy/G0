package ja.insepector.base.bean

data class ParkingSpaceResultBean(
    val result: ParkingSpaceBean
)

data class ParkingSpaceBean(
    val carLicense: String = "",
    val havePayMoney: String = "",
    val historyCount: Int = 0,
    val historySum: Int = 0,
    val orderNo: String = "",
    val parkingNo: String = "",
    val realtimeMoney: String = "",
    val startTime: String = "",
    val streetNo: String = "",
    var timeOut: String = ""
)
package ja.insepector.base.bean

data class ParkingSpaceResultBean(
    val result: ParkingSpaceBean
)

data class ParkingSpaceBean(
    val carLicense: String,
    val havePayMoney: Int,
    val parkingNo: String,
    val startTime: String,
    val streetNo: String
)
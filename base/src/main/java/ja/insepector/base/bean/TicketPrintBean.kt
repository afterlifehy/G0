package ja.insepector.base.bean

data class TicketPrintBean(
    val businessCname: String = "",
    val carLicense: String = "",
    val endTime: String = "",
    val oweCount: Int = 0,
    val payMoney: String = "",
    val phone: String = "",
    val remark: String = "",
    val roadName: String = "",
    val startTime: String = "",
    val tradeNo: String = ""
)
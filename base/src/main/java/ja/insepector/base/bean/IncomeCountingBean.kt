package ja.insepector.base.bean

data class IncomeCountingBean(
    var loginName: String = "",
    var range: String = "",
    val list1: List<TodayIncomeBean>,
    val list2: List<RangeIncomeBean>
)

data class TodayIncomeBean(
    val onlineMoney: String = "",
    val orderCount: Int = 0,
    val oweCount: Int = 0,
    val oweMoney: String = "",
    val partPayCount: Int = 0,
    val passMoney: String = "",
    val payMoney: String = "",
    val refusePayCount: Int = 0
)

data class RangeIncomeBean(
    val orderCount: Int = 0,
    val payMoney: String = ""
)
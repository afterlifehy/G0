package ja.insepector.base.bean

data class UpdateBean(
    var force: String,
    var state: String,
    var url: String,
    var version: String
)
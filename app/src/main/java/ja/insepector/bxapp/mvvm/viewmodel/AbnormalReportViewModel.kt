package ja.insepector.bxapp.mvvm.viewmodel

import ja.insepector.base.base.mvvm.BaseViewModel
import ja.insepector.bxapp.mvvm.repository.AbnormalRepository

class AbnormalReportViewModel: BaseViewModel() {
    val mAbnormalRepository by lazy {
        AbnormalRepository()
    }

//    val abnormalReportLiveData = MutableLiveData<AbnormalReportResultBean>()
//
//    fun abnormalReport(param: Map<String, Any?>) {
//        launch {
//            val response = withContext(Dispatchers.IO) {
//                mAbnormalRepository.abnormalReport(param)
//            }
//            executeResponse(response, {
//                abnormalReportLiveData.value = response.attr
//            }, {
//                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
//            })
//        }
//    }
}
package ja.insepector.bxapp.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import ja.insepector.base.base.mvvm.BaseViewModel
import ja.insepector.base.base.mvvm.ErrorMessage
import ja.insepector.bxapp.mvvm.repository.AbnormalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AbnormalReportViewModel: BaseViewModel() {
    val mAbnormalRepository by lazy {
        AbnormalRepository()
    }

    val abnormalReportLiveData = MutableLiveData<Any>()

    fun abnormalReport(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mAbnormalRepository.abnormalReport(param)
            }
            executeResponse(response, {
                abnormalReportLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
            })
        }
    }
}
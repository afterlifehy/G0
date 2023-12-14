package ja.insepector.bxapp.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import ja.insepector.base.base.mvvm.BaseViewModel
import ja.insepector.base.bean.UpdateBean
import ja.insepector.bxapp.mvvm.repository.MineRepository

class MineViewModel: BaseViewModel() {
    val mMineRepository by lazy {
        MineRepository()
    }

    val checkUpdateLiveDate = MutableLiveData<UpdateBean>()

    fun checkUpdate(param: Map<String, Any?>) {
        checkUpdateLiveDate.value = UpdateBean("0","0","21321312","1.0.1")
//        launch {
//            val response = withContext(Dispatchers.IO) {
//                mMineRepository.checkUpdate(param)
//            }
//            executeResponse(response, {
//                checkUpdateLiveDate.value = response.attr
//            }, {
//                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
//            })
//        }
    }
}
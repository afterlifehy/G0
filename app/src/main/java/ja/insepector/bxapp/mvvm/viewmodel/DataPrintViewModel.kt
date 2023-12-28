package ja.insepector.bxapp.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import ja.insepector.base.base.mvvm.BaseViewModel
import ja.insepector.base.base.mvvm.ErrorMessage
import ja.insepector.base.bean.IncomeCountingBean
import ja.insepector.bxapp.mvvm.repository.IncomeCountingRepository
import ja.insepector.bxapp.mvvm.repository.MineRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DataPrintViewModel: BaseViewModel() {
    val mMineRepository by lazy {
        MineRepository()
    }

    val incomeCountingLiveData = MutableLiveData<IncomeCountingBean>()

    fun incomeCounting(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mMineRepository.incomeCounting(param)
            }
            executeResponse(response, {
                incomeCountingLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
            })
        }
    }
}
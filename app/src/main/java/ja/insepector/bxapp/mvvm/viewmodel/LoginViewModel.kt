package ja.insepector.bxapp.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import ja.insepector.base.base.mvvm.BaseViewModel
import ja.insepector.base.base.mvvm.ErrorMessage
import ja.insepector.base.bean.LoginBean
import ja.insepector.base.bean.Street
import ja.insepector.base.bean.UpdateBean
import ja.insepector.bxapp.mvvm.repository.LoginRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginViewModel : BaseViewModel() {

    val mLoginRepository by lazy {
        LoginRepository()
    }

    val loginLiveData = MutableLiveData<LoginBean>()
    val checkUpdateLiveDate = MutableLiveData<UpdateBean>()

    fun login(param: Map<String, Any?>) {
        val streetList: MutableList<Street> = ArrayList()
        streetList.add(Street("qwe-123", "昌平路", false, false))
        streetList.add(Street("qwe-124", "昌平路1", false, false))
        streetList.add(Street("qwe-125", "昌平路2", false, false))
        loginLiveData.value = LoginBean("123", "123", "123", streetList, "3213213")
//        launch {
//            val response = withContext(Dispatchers.IO) {
//                mLoginRepository.login(param)
//            }
//            executeResponse(response, {
//                loginLiveData.value = response.attr
//            }, {
//                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
//            })
//        }
    }

    fun checkUpdate(param: Map<String, Any?>) {
        checkUpdateLiveDate.value = UpdateBean("0","0","21321312","1.0.1")
//        launch {
//            val response = withContext(Dispatchers.IO) {
//                mLoginRepository.checkUpdate(param)
//            }
//            executeResponse(response, {
//                checkUpdateLiveDate.value = response.attr
//            }, {
//                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
//            })
//        }
    }

}
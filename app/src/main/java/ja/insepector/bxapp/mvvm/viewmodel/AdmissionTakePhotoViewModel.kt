package ja.insepector.bxapp.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import ja.insepector.base.base.mvvm.BaseViewModel
import ja.insepector.base.base.mvvm.ErrorMessage
import ja.insepector.base.bean.PlaceOederResultBean
import ja.insepector.bxapp.mvvm.repository.ParkingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AdmissionTakePhotoViewModel: BaseViewModel() {

    val mParkingRepository by lazy {
        ParkingRepository()
    }
    val placeOrderLiveData = MutableLiveData<PlaceOederResultBean>()
    val picUploadLiveData = MutableLiveData<Any>()

    fun placeOrder(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mParkingRepository.placeOrder(param)
            }
            executeResponse(response, {
                placeOrderLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
            })
        }
    }

    fun picUpload(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mParkingRepository.picUpload(param)
            }
            executeResponse(response, {
                picUploadLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
            })
        }
    }
}
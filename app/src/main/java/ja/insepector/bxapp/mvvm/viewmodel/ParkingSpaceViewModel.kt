package ja.insepector.bxapp.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import ja.insepector.base.base.mvvm.BaseViewModel
import ja.insepector.base.base.mvvm.ErrorMessage
import ja.insepector.base.bean.ParkingSpaceBean
import ja.insepector.base.bean.ParkingSpaceResultBean
import ja.insepector.bxapp.mvvm.repository.ParkingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ParkingSpaceViewModel: BaseViewModel() {
    val mParkingRepository by lazy {
        ParkingRepository()
    }

    val parkingSpaceLiveData = MutableLiveData<ParkingSpaceResultBean>()
    val insidePayLiveData = MutableLiveData<Any>()
    val payResultLiveData = MutableLiveData<Any>()

    fun parkingSpace(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mParkingRepository.parkingSpace(param)
            }
            executeResponse(response, {
                parkingSpaceLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
            })
        }
    }

    fun insidePay(param: Map<String, Any?>) {
//        launch {
//            val response = withContext(Dispatchers.IO) {
//                mParkingRepository.insidePay(param)
//            }
//            executeResponse(response, {
//                insidePayLiveData.value = response.attr
//            }, {
//                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
//            })
//        }
    }

    fun payResult(param: Map<String, Any?>) {
//        launch {
//            val response = withContext(Dispatchers.IO) {
//                mParkingRepository.payResult(param)
//            }
//            executeResponse(response, {
//                payResultLiveData.value = response.attr
//            }, {
//                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
//            })
//        }
    }
}
package ja.insepector.bxapp.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import ja.insepector.base.base.mvvm.BaseViewModel
import ja.insepector.bxapp.mvvm.repository.ParkingRepository

class ParkingSpaceViewModel: BaseViewModel() {
    val mParkingRepository by lazy {
        ParkingRepository()
    }

    val parkingSpaceFeeLiveData = MutableLiveData<Any>()
    val insidePayLiveData = MutableLiveData<Any>()
    val payResultLiveData = MutableLiveData<Any>()

    fun parkingSpaceFee(param: Map<String, Any?>) {
        parkingSpaceFeeLiveData.value = true
//        launch {
//            val response = withContext(Dispatchers.IO) {
//                mParkingRepository.parkingSpaceFee(param)
//            }
//            executeResponse(response, {
//                parkingSpaceFeeLiveData.value = response.attr
//            }, {
//                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
//            })
//        }
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
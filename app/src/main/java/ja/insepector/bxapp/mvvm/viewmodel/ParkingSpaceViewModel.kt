package ja.insepector.bxapp.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import ja.insepector.base.base.mvvm.BaseViewModel
import ja.insepector.base.base.mvvm.ErrorMessage
import ja.insepector.base.bean.ParkingSpaceBean
import ja.insepector.base.bean.TicketPrintBean
import ja.insepector.bxapp.mvvm.repository.ParkingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ParkingSpaceViewModel : BaseViewModel() {
    val mParkingRepository by lazy {
        ParkingRepository()
    }

    val parkingSpaceLiveData = MutableLiveData<ParkingSpaceBean>()
    val endOrderLiveData = MutableLiveData<Any>()
    val picUploadLiveData = MutableLiveData<Any>()
    val ticketPrintLiveData = MutableLiveData<TicketPrintBean>()

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

    fun endOrder(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mParkingRepository.endOrder(param)
            }
            executeResponse(response, {
                endOrderLiveData.value = response.attr
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

    fun ticketPrint(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mParkingRepository.ticketPrint(param)
            }
            executeResponse(response, {
                ticketPrintLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
            })
        }
    }
}
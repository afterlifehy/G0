package ja.insepector.bxapp.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.alibaba.fastjson.JSONObject
import ja.insepector.base.base.mvvm.BaseViewModel
import ja.insepector.base.base.mvvm.ErrorMessage
import ja.insepector.base.bean.ParkingLotResultBean
import ja.insepector.bxapp.mvvm.repository.ParkingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ParkingLotViewModel : BaseViewModel() {
    val mParkingRepository by lazy {
        ParkingRepository()
    }

    val parkingLotListLiveData = MutableLiveData<ParkingLotResultBean>()

    fun getParkingLotList(param: Map<String, Any?>) {
        val json =
            "{\"result\":[{\"deadLine\":\"1704072933000\",\"carColor\":\"9\",\"orderNo\":\"20231201JAZ026001000066\",\"parkingNo\":\"JAZ-026-001\",\"state\":\"02\",\"cleared\":\"02\",\"carLicense\":\"沪ABQ2327\"},{\"carColor\":\"5\",\"orderNo\":\"20231130JAZ026002000136\",\"parkingNo\":\"JAZ-026-002\",\"state\":\"02\",\"cleared\":\"02\",\"carLicense\":\"新A9DD05\"},{\"carColor\":\"5\",\"orderNo\":\"20231130JAZ026003000282\",\"parkingNo\":\"JAZ-026-003\",\"state\":\"02\",\"cleared\":\"02\",\"carLicense\":\"默00000\"},{\"carColor\":\"5\",\"orderNo\":\"20231201JAZ026004000080\",\"parkingNo\":\"JAZ-026-004\",\"state\":\"02\",\"cleared\":\"02\",\"carLicense\":\"沪EVG397\"},{\"carColor\":\"5\",\"orderNo\":\"\",\"parkingNo\":\"JAZ-026-005\",\"state\":\"01\",\"cleared\":\"02\",\"carLicense\":\"\"},{\"carColor\":\"9\",\"orderNo\":\"20231201JAZ026006000061\",\"parkingNo\":\"JAZ-026-006\",\"state\":\"02\",\"cleared\":\"02\",\"carLicense\":\"沪ADK1150\"},{\"carColor\":\"5\",\"orderNo\":\"20231201JAZ026007000069\",\"parkingNo\":\"JAZ-026-007\",\"state\":\"02\",\"cleared\":\"02\",\"carLicense\":\"沪A70C91\"},{\"carColor\":\"9\",\"orderNo\":\"20231201JAZ026008000072\",\"parkingNo\":\"JAZ-026-008\",\"state\":\"02\",\"cleared\":\"02\",\"carLicense\":\"沪AAF0255\"},{\"carColor\":\"5\",\"orderNo\":\"20231130JAZ026009000236\",\"parkingNo\":\"JAZ-026-009\",\"state\":\"02\",\"cleared\":\"02\",\"carLicense\":\"沪NK9831\"},{\"carColor\":\"5\",\"orderNo\":\"20231201JAZ026010000074\",\"parkingNo\":\"JAZ-026-010\",\"state\":\"02\",\"cleared\":\"02\",\"carLicense\":\"沪FHU182\"},{\"carColor\":\"5\",\"orderNo\":\"20231201JAZ026011000076\",\"parkingNo\":\"JAZ-026-011\",\"state\":\"02\",\"cleared\":\"02\",\"carLicense\":\"沪A1U820\"},{\"carColor\":\"5\",\"orderNo\":\"20231201JAZ026012000083\",\"parkingNo\":\"JAZ-026-012\",\"state\":\"02\",\"cleared\":\"02\",\"carLicense\":\"沪GE0663\"}]}"
        parkingLotListLiveData.value = JSONObject.parseObject(json, ParkingLotResultBean::class.java)
//        launch {
//            val response = withContext(Dispatchers.IO) {
//                mParkingRepository.getParkingLotList(param)
//            }
//            executeResponse(response, {
//                parkingLotListLiveData.value = response.attr
//            }, {
//                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
//            })
//        }
    }
}
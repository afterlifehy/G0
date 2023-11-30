package ja.insepector.bxapp.startup

import ja.insepector.base.start.BaseStartUpManager
import ja.insepector.base.start.StartUpKey
import ja.insepector.bxapp.AppApplication
import com.xj.anchortask.library.AnchorTask

class AnchorTaskOne : AnchorTask(StartUpKey.TASK_NAME_TWO) {
    override fun isRunOnMainThread(): Boolean {
        return false
    }

    override fun run() {
        BaseStartUpManager.instance().delayInit(AppApplication.instance())
        AppStartUpManager.instance().delayInit(AppApplication.instance())
    }
}
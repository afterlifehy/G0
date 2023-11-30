package ja.insepector.bxapp.startup

import ja.insepector.base.start.BaseStartUpManager
import ja.insepector.base.start.StartUpKey
import ja.insepector.bxapp.AppApplication
import com.xj.anchortask.library.AnchorTask

class AnchorTaskZero : AnchorTask(StartUpKey.TASK_NAME_ONE) {
    override fun isRunOnMainThread(): Boolean {
        return false
    }

    override fun run() {
        BaseStartUpManager.instance().applicationInit(AppApplication.instance())
        AppStartUpManager.instance().applicationInit(AppApplication.instance())
    }
}
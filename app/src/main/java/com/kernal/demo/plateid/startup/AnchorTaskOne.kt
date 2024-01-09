package com.kernal.demo.plateid.startup

import com.kernal.demo.base.start.BaseStartUpManager
import com.kernal.demo.base.start.StartUpKey
import com.kernal.demo.plateid.AppApplication
import com.xj.anchortask.library.AnchorTask

class AnchorTaskOne : AnchorTask(StartUpKey.TASK_NAME_TWO) {
    override fun isRunOnMainThread(): Boolean {
        return false
    }

    override fun run() {
        BaseStartUpManager.instance().delayInit(com.kernal.demo.plateid.AppApplication.instance())
        AppStartUpManager.instance().delayInit(com.kernal.demo.plateid.AppApplication.instance())
    }
}
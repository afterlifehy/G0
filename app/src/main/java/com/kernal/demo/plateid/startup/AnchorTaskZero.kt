package com.kernal.demo.plateid.startup

import com.kernal.demo.base.start.BaseStartUpManager
import com.kernal.demo.base.start.StartUpKey
import com.kernal.demo.plateid.AppApplication
import com.xj.anchortask.library.AnchorTask

class AnchorTaskZero : AnchorTask(StartUpKey.TASK_NAME_ONE) {
    override fun isRunOnMainThread(): Boolean {
        return false
    }

    override fun run() {
        BaseStartUpManager.instance().applicationInit(com.kernal.demo.plateid.AppApplication.instance())
        AppStartUpManager.instance().applicationInit(com.kernal.demo.plateid.AppApplication.instance())
    }
}
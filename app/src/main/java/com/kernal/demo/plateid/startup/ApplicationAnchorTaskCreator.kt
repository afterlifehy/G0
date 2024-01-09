package com.kernal.demo.plateid.startup

import com.kernal.demo.base.start.StartUpKey
import com.xj.anchortask.library.AnchorTask
import com.xj.anchortask.library.IAnchorTaskCreator

class ApplicationAnchorTaskCreator : IAnchorTaskCreator {
    override fun createTask(taskName: String): AnchorTask? {
        when (taskName) {
            StartUpKey.TASK_NAME_ONE -> {
                return AnchorTaskZero()
            }
            StartUpKey.TASK_NAME_TWO -> {
                return AnchorTaskOne()

            }
        }
        return null
    }
}
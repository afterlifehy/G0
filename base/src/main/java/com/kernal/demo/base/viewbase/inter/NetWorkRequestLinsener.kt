package com.kernal.demo.base.viewbase.inter

import com.kernal.demo.base.bean.NetWorkRequestData

interface NetWorkRequestLinsener {
    /**
     * 网络超时之类的错误
     */
    fun onNetWorkRequestError(errror: NetWorkRequestData)

    /**
     * 无网络错误
     */
    fun onNoNetWorkErrror(errror: NetWorkRequestData)
}
package com.kernal.demo.plateid.mvvm.repository

import com.kernal.demo.base.base.mvvm.BaseRepository
import com.kernal.demo.base.bean.HttpWrapper
import com.kernal.demo.base.bean.LoginBean
import com.kernal.demo.base.bean.UpdateBean

class LoginRepository : BaseRepository() {

    /**
     * 登录
     */
    suspend fun login(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<LoginBean> {
        return mServer.login(param)
    }

    /**
     * 版本更新查询
     */
    suspend fun checkUpdate(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<UpdateBean> {
        return mServer.checkUpdate(param)
    }

    /**
     * 考勤排班
     */
    suspend fun checkOnWork(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any> {
        return mServer.checkOnWork(param)
    }
}
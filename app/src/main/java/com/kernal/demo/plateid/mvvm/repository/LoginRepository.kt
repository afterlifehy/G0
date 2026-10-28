package com.kernal.demo.plateid.mvvm.repository

import com.kernal.demo.base.base.mvvm.BaseRepository
import com.kernal.demo.base.bean.HttpWrapper
import com.kernal.demo.base.bean.LoginBean
import com.kernal.demo.base.bean.QueryPwStatusBean
import com.kernal.demo.base.bean.UpdateBean
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Part

class LoginRepository : BaseRepository() {

    /**
     * 登录
     */
    suspend fun login(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<LoginBean> {
        return mRTServer.login(param)
    }

    /**
     * 版本更新查询
     */
    suspend fun checkUpdate(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<UpdateBean> {
        return mRTServer.checkUpdate(param)
    }

    /**
     * 考勤排班
     */
    suspend fun checkOnWork(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any> {
        return mRTServer.checkOnWork(param)
    }

    /**
     * 日志上传
     */
    suspend fun logFileUpload(@Part file: MultipartBody.Part): HttpWrapper<Any> {
        return mFileServer.logFileUpload(file)
    }

    /**
     * 修改密码
     */
    suspend fun editPw(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any> {
        return mRTServer.editPw(param)
    }

    /**
     * 查询是为初始密码
     */
    suspend fun queryPwStatus(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<QueryPwStatusBean>{
        return mRTServer.queryPwStatus(param)
    }
}
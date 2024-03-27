package com.kernal.demo.base

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.alibaba.android.arouter.launcher.ARouter
import com.kernal.demo.base.help.ActivityCacheManager
import com.kernal.demo.base.http.OnAddOkhttpInterceptor
import com.kernal.demo.base.proxy.OnAppBaseProxyLinsener
import com.tencent.bugly.crashreport.CrashReport
import okhttp3.OkHttpClient
import kotlin.properties.Delegates


abstract class BaseApplication : Application(), Application.ActivityLifecycleCallbacks,
    OnAddOkhttpInterceptor {
    private var mOnAppBaseProxyLinsener: OnAppBaseProxyLinsener? = null

    companion object {
        var baseApplication: BaseApplication by Delegates.notNull()
        private lateinit var client: OkHttpClient

        @JvmStatic
        fun instance() = BaseApplication.Companion.baseApplication
    }

    override fun onCreate() {
        super.onCreate()
        BaseApplication.Companion.baseApplication = this
        registerActivityLifecycleCallbacks(this)
//        SkinCompatManager.withoutActivity(this)
//            .addInflater(SkinAppCompatViewInflater()) // 基础控件换肤初始化
//            .addInflater(SkinMaterialViewInflater()) // material design 控件换肤初始化[可选]
//            .addInflater(SkinConstraintViewInflater()) // ConstraintLayout 控件换肤初始化[可选]
//            .addInflater(SkinCardViewInflater()) // CardView v7 控件换肤初始化[可选]
//            .setSkinStatusBarColorEnable(false) // 关闭状态栏换肤，默认打开[可选]
//            .setSkinWindowBackgroundEnable(false) // 关闭windowBackground换肤，默认打开[可选]
//            .loadSkin()
        initClient()
        initArouter()
        CrashReport.initCrashReport(this@BaseApplication,"5f44c9799e",false)
    }

    fun initArouter() {
        Thread {
            if (BuildConfig.is_debug) {
                ARouter.openLog()
                ARouter.openDebug()
            }
            ARouter.init(BaseApplication.Companion.instance())
        }.start()
    }

    private fun initClient() {
        Thread {
            BaseApplication.Companion.client = OkHttpClient.Builder().build()
        }.start()
    }

    /**
     * 用来获取子类和父类直接的交互
     */
    fun setOnAppBaseProxyLinsener(mOnAppBaseProxyLinsener: OnAppBaseProxyLinsener?) {
        this.mOnAppBaseProxyLinsener = mOnAppBaseProxyLinsener
    }

    fun getOnAppBaseProxyLinsener(): OnAppBaseProxyLinsener? {
        return mOnAppBaseProxyLinsener
    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        ActivityCacheManager.instance().removeActivity(activity)
        activity.finish()
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        ActivityCacheManager.instance().addActivity(activity)
    }

    override fun onActivityResumed(activity: Activity) {
    }

}
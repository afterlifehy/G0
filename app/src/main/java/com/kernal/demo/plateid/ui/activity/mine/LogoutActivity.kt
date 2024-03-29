package com.kernal.demo.plateid.ui.activity.mine

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.viewbinding.ViewBinding
import com.alibaba.fastjson.JSONObject
import com.baidu.location.LocationClientOption
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.PhoneUtils
import com.blankj.utilcode.util.TimeUtils
import com.kernal.demo.base.BaseApplication
import com.kernal.demo.base.arouter.ARouterMap
import com.kernal.demo.base.dialog.DialogHelp
import com.kernal.demo.base.ds.PreferencesDataStore
import com.kernal.demo.base.ds.PreferencesKeys
import com.kernal.demo.base.ext.i18N
import com.kernal.demo.base.ext.i18n
import com.kernal.demo.base.util.ToastUtil
import com.kernal.demo.base.viewbase.VbBaseActivity
import com.kernal.demo.common.realm.RealmUtil
import com.kernal.demo.plateid.R
import com.kernal.demo.plateid.databinding.ActivityLogOutBinding
import com.kernal.demo.plateid.mvvm.viewmodel.LogoutViewModel
import com.tbruyelle.rxpermissions3.RxPermissions
import com.kernal.demo.base.ext.startArouter
import com.kernal.demo.common.util.BaiduLocationUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class LogoutActivity : VbBaseActivity<LogoutViewModel, ActivityLogOutBinding>(), OnClickListener {
    private var job: Job? = null
    lateinit var baiduLocationUtil: BaiduLocationUtil
    var lat = 0.00
    var lon = 0.00
    var locationEnable = 0
    var loginName = ""

    @SuppressLint("MissingPermission", "CheckResult")
    override fun initView() {
        binding.layoutToolbar.tvTitle.text = i18n(com.kernal.demo.base.R.string.签退)
        job = GlobalScope.launch(Dispatchers.IO) {
            while (true) {
                val time = TimeUtils.millis2String(System.currentTimeMillis(), "HH:mm:ss")
                withContext(Dispatchers.Main) {
                    binding.rtvHour1.text = time.split(":")[0][0].toString()
                    binding.rtvHour2.text = time.split(":")[0][1].toString()
                    binding.rtvMinute1.text = time.split(":")[1][0].toString()
                    binding.rtvMinute2.text = time.split(":")[1][1].toString()
                    binding.rtvSec1.text = time.split(":")[2][0].toString()
                    binding.rtvSec2.text = time.split(":")[2][1].toString()
                }
                delay(1000)
            }
        }

        var rxPermissions = RxPermissions(this@LogoutActivity)
        rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION).subscribe {
            if (it) {
                baiduLocationUtil = BaiduLocationUtil()
                baiduLocationUtil.initBaiduLocation()
                val callback = object : BaiduLocationUtil.BaiduLocationCallBack {
                    override fun locationChange(
                        lon: Double,
                        lat: Double,
                        location: LocationClientOption?,
                        isSuccess: Boolean,
                        address: String?
                    ) {
                        if (isSuccess) {
                            this@LogoutActivity.lat = lat
                            this@LogoutActivity.lon = lon
                            locationEnable = 1
                        } else {
                            locationEnable = -1
                        }
                    }

                }
                baiduLocationUtil.setBaiduLocationCallBack(callback)
                baiduLocationUtil.startLocation()
            }
        }
    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
        binding.tvLogout.setOnClickListener(this)
    }

    override fun initData() {
        runBlocking {
            loginName = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.loginName)
            val workingHour = RealmUtil.instance?.findCurrentWorkingHour(loginName)
            if (workingHour != null) {
                binding.tvWorkingHours.text = TimeUtils.millis2String(workingHour.time, "yyyy-MM-dd HH:mm:ss")
            }
        }
    }

    @SuppressLint("MissingPermission", "CheckResult")
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }

            R.id.tv_logout -> {
                var rxPermissions = RxPermissions(this@LogoutActivity)
                rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE).subscribe {
                    if (it) {
                        DialogHelp.Builder().setTitle(i18N(com.kernal.demo.base.R.string.确认签退))
                            .setLeftMsg(i18N(com.kernal.demo.base.R.string.取消))
                            .setRightMsg(i18N(com.kernal.demo.base.R.string.确定)).setCancelable(true)
                            .setOnButtonClickLinsener(object : DialogHelp.OnButtonClickLinsener {
                                override fun onLeftClickLinsener(msg: String) {
                                }

                                override fun onRightClickLinsener(msg: String) {
                                    if (locationEnable != -1) {
                                        showProgressDialog(20000)
                                        runBlocking {
                                            val simId =
                                                PreferencesDataStore(BaseApplication.baseApplication).getString(PreferencesKeys.simId)
                                            val loginName =
                                                PreferencesDataStore(BaseApplication.baseApplication).getString(PreferencesKeys.loginName)
                                            val param = HashMap<String, Any>()
                                            val jsonobject = JSONObject()
                                            jsonobject["simId"] = simId
                                            jsonobject["loginName"] = loginName
                                            jsonobject["longitude"] = lon
                                            jsonobject["latitude"] = lat
                                            jsonobject["simId"] = PhoneUtils.getIMSI()
                                            jsonobject["imei"] = PhoneUtils.getIMEI()
                                            jsonobject["version"] = AppUtils.getAppVersionName()
                                            param["attr"] = jsonobject
                                            mViewModel.logout(param)
                                        }
                                    } else {
                                        ToastUtil.showMiddleToast(i18N(com.kernal.demo.base.R.string.请打开位置信息))
                                    }
                                }

                            }).build(this@LogoutActivity).showDailog()
                    }
                }
            }
        }
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            logoutLiveData.observe(this@LogoutActivity) {
                dismissProgressDialog()
                startArouter(ARouterMap.DATA_PRINT, data = Bundle().apply {
                    putString(ARouterMap.DATA_PRINT_LOGIN_NAME, loginName)
                })
                ToastUtil.showMiddleToast(i18N(com.kernal.demo.base.R.string.签退成功))
            }
            errMsg.observe(this@LogoutActivity) {
                dismissProgressDialog()
                ToastUtil.showMiddleToast(it.msg)
            }
            mException.observe(this@LogoutActivity) {
                dismissProgressDialog()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        GlobalScope.launch(Dispatchers.IO) {
            job?.cancelAndJoin()
        }
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityLogOutBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override val isFullScreen: Boolean
        get() = true

    override fun marginStatusBarView(): View {
        return binding.layoutToolbar.ablToolbar
    }

    override fun providerVMClass(): Class<LogoutViewModel> {
        return LogoutViewModel::class.java
    }

}
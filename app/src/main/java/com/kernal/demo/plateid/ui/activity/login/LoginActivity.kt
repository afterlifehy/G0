package com.kernal.demo.plateid.ui.activity.login

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSONObject
import com.baidu.location.LocationClientOption
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.PhoneUtils
import com.kernal.demo.base.BaseApplication
import com.kernal.demo.base.arouter.ARouterMap
import com.kernal.demo.base.bean.UpdateBean
import com.kernal.demo.base.ds.PreferencesDataStore
import com.kernal.demo.base.ds.PreferencesKeys
import com.kernal.demo.base.ext.i18N
import com.kernal.demo.base.ext.startAct
import com.kernal.demo.base.util.ToastUtil
import com.kernal.demo.base.viewbase.VbBaseActivity
import com.kernal.demo.common.event.BaiduLocationEvent
import com.kernal.demo.common.event.BaiduLocationLoginEvent
import com.kernal.demo.common.util.BaiduLocationUtil
import com.kernal.demo.common.util.Constant
import com.kernal.demo.plateid.R
import com.kernal.demo.plateid.databinding.ActivityLoginBinding
import com.kernal.demo.plateid.mvvm.viewmodel.LoginViewModel
import com.kernal.demo.plateid.util.UpdateUtil
import com.tbruyelle.rxpermissions3.RxPermissions
import kotlinx.coroutines.runBlocking
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@Route(path = ARouterMap.LOGIN)
class LoginActivity : VbBaseActivity<LoginViewModel, ActivityLoginBinding>(), OnClickListener {
    var baiduLocationUtil: BaiduLocationUtil? = null
    var lat = 121.445345
    var lon = 31.238665
    var updateBean: UpdateBean? = null
    var locationEnable = 0

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(baiduLocationLoginEvent: BaiduLocationLoginEvent) {
        startBadiMapLocation()
    }

    @SuppressLint("CheckResult", "MissingPermission")
    override fun initView() {
        var rxPermissions = RxPermissions(this@LoginActivity)
        rxPermissions.request(
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.REQUEST_INSTALL_PACKAGES
        ).subscribe {
            if (rxPermissions.isGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
                startBadiMapLocation()
                baiduLocationUtil?.startLocation()
            }
        }
        binding.tvVersion.text = "v" + AppUtils.getAppVersionName()
    }

    override fun initListener() {
        binding.tvForgetPw.setOnClickListener(this)
        binding.etAccount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if (binding.etPw.text.isNotEmpty() && p0!!.isNotEmpty()) {
                    binding.rtvLogin.delegate.setBackgroundColor(
                        ContextCompat.getColor(
                            BaseApplication.instance(),
                            com.kernal.demo.base.R.color.color_ff04a091
                        )
                    )
                    ClickUtils.applySingleDebouncing(binding.rtvLogin, 3000, this@LoginActivity)
                } else {
                    binding.rtvLogin.delegate.setBackgroundColor(
                        ContextCompat.getColor(
                            BaseApplication.instance(),
                            com.kernal.demo.base.R.color.color_9904a091
                        )
                    )
                    binding.rtvLogin.setOnClickListener(null)
                }
                binding.rtvLogin.delegate.init()
            }

        })
        binding.etAccount.setOnEditorActionListener { textView, i, keyEvent ->
            binding.etPw.requestFocus()
        }
        binding.etPw.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if (binding.etAccount.text.isNotEmpty() && p0!!.isNotEmpty()) {
                    binding.rtvLogin.delegate.setBackgroundColor(
                        ContextCompat.getColor(
                            BaseApplication.instance(),
                            com.kernal.demo.base.R.color.color_ff04a091
                        )
                    )
                    ClickUtils.applySingleDebouncing(binding.rtvLogin, 3000, this@LoginActivity)
                } else {
                    binding.rtvLogin.delegate.setBackgroundColor(
                        ContextCompat.getColor(
                            BaseApplication.instance(),
                            com.kernal.demo.base.R.color.color_9904a091
                        )
                    )
                    binding.rtvLogin.setOnClickListener(null)
                }
                binding.rtvLogin.delegate.init()
            }

        })
    }

    override fun initData() {
        val param = HashMap<String, Any>()
        val jsonobject = JSONObject()
        jsonobject["version"] = AppUtils.getAppVersionCode()
        jsonobject["softType"] = "30"
        param["attr"] = jsonobject
        mViewModel.checkUpdate(param)
    }

    fun startBadiMapLocation() {
        baiduLocationUtil = BaiduLocationUtil.getInstance(1000)
        baiduLocationUtil?.initBaiduLocation()
        val callback = object : BaiduLocationUtil.BaiduLocationCallBack {
            override fun locationChange(
                lon: Double,
                lat: Double,
                location: LocationClientOption?,
                isSuccess: Boolean,
                address: String?
            ) {
                if (isSuccess) {
                    this@LoginActivity.lat = lat
                    this@LoginActivity.lon = lon
                    runBlocking {
                        PreferencesDataStore(BaseApplication.instance()).putDouble(PreferencesKeys.lon, lon)
                        PreferencesDataStore(BaseApplication.instance()).putDouble(PreferencesKeys.lat, lat)
                    }
                    locationEnable = 1
                } else {
                    locationEnable = -1
                }
            }

        }
        baiduLocationUtil?.setBaiduLocationCallBack(callback)
    }

    @SuppressLint("CheckResult", "MissingPermission")
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_forgetPw -> {

            }

            R.id.rtv_login -> {
                var rxPermissions = RxPermissions(this@LoginActivity)
                if (locationEnable == 1) {
                    rxPermissions.request(Manifest.permission.READ_PHONE_STATE).subscribe {
                        if (it) {
                            login()
                        } else {
                            ToastUtil.showBottomToast(i18N(com.kernal.demo.base.R.string.请授权电话权限))
                        }
                    }
                } else {
                    if (rxPermissions.isGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        ToastUtil.showBottomToast(i18N(com.kernal.demo.base.R.string.未获取到位置信息))
                    } else {
                        rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE).subscribe {
                            if (it) {
                                startBadiMapLocation()
                                baiduLocationUtil?.startLocation()
                            } else if (!rxPermissions.isGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
                                ToastUtil.showBottomToast(i18N(com.kernal.demo.base.R.string.请打开位置信息))
                            } else if (!rxPermissions.isGranted(Manifest.permission.READ_PHONE_STATE)) {
                                ToastUtil.showBottomToast(i18N(com.kernal.demo.base.R.string.请授权电话权限))
                            }
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun login() {
        showProgressDialog(20000)
        val param = HashMap<String, Any>()
        val jsonobject = JSONObject()
        jsonobject["loginName"] = binding.etAccount.text.toString()
        jsonobject["passWord"] = binding.etPw.text.toString()
        jsonobject["longitude"] = lon.toString()
        jsonobject["latitude"] = lat.toString()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            jsonobject["simId"] = PhoneUtils.getIMSI()
        } else {
            jsonobject["simId"] = (getSystemService(TELEPHONY_SERVICE) as TelephonyManager).simSerialNumber
        }
        jsonobject["imei"] = PhoneUtils.getIMEI()
        jsonobject["version"] = AppUtils.getAppVersionName()
        param["attr"] = jsonobject
        mViewModel.login(param)
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            loginLiveData.observe(this@LoginActivity) {
                dismissProgressDialog()
                startAct<StreetChooseActivity>(data = Bundle().apply {
                    putParcelable(ARouterMap.LOGIN_INFO, it)
                })
            }
            checkUpdateLiveDate.observe(this@LoginActivity) {
                updateBean = it
                if (updateBean?.state == "0") {
                    UpdateUtil.instance?.checkNewVersion(updateBean!!, object : UpdateUtil.UpdateInterface {
                        override fun requestionPermission() {
                            requestPermissions()
                        }

                        override fun install(path: String) {

                        }
                    })
                }
            }
            errMsg.observe(this@LoginActivity) {
                dismissProgressDialog()
                ToastUtil.showBottomToast(it.msg)
            }
            mException.observe(this@LoginActivity) {
                dismissProgressDialog()
            }
        }
    }

    @SuppressLint("CheckResult")
    fun requestPermissions() {
        var rxPermissions = RxPermissions(this@LoginActivity)
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe {
            if (it) {
                UpdateUtil.instance?.downloadFileAndInstall(object : UpdateUtil.UpdateInterface {
                    override fun requestionPermission() {

                    }

                    override fun install(path: String) {
                        AppUtils.installApp(path)
                    }
                })
            } else {

            }
        }
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun providerVMClass(): Class<LoginViewModel> {
        return LoginViewModel::class.java
    }

    override val isFullScreen: Boolean
        get() = false

    override fun isRegEventBus(): Boolean {
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
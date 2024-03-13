package com.kernal.demo.plateid.ui.activity.login

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.OnClickListener
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSONObject
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.PhoneUtils
import com.kernal.demo.base.BaseApplication
import com.kernal.demo.base.arouter.ARouterMap
import com.kernal.demo.base.ext.i18N
import com.kernal.demo.base.util.ToastUtil
import com.kernal.demo.base.viewbase.VbBaseActivity
import com.tbruyelle.rxpermissions3.RxPermissions
import com.kernal.demo.base.bean.UpdateBean
import com.kernal.demo.base.ext.startAct
import com.kernal.demo.plateid.BuildConfig
import com.kernal.demo.plateid.R
import com.kernal.demo.plateid.databinding.ActivityLoginBinding
import com.kernal.demo.plateid.mvvm.viewmodel.LoginViewModel
import com.kernal.demo.plateid.util.UpdateUtil
import java.io.File

@Route(path = ARouterMap.LOGIN)
class LoginActivity : VbBaseActivity<LoginViewModel, ActivityLoginBinding>(), OnClickListener {
    var locationManager: LocationManager? = null
    var lat = 121.445345
    var lon = 31.238665
    var updateBean: UpdateBean? = null
    var locationEnable = 0

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
                locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val provider = LocationManager.NETWORK_PROVIDER
                locationManager?.requestLocationUpdates(provider, 1000, 1f, object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        lat = location.latitude
                        lon = location.longitude
                        locationEnable = 1
                    }

                    override fun onProviderDisabled(provider: String) {
                        locationEnable = -1
                        ToastUtil.showMiddleToast(i18N(com.kernal.demo.base.R.string.请打开位置信息))
                    }

                    override fun onProviderEnabled(provider: String) {
                        locationEnable = 1
                    }
                })
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
                    binding.rtvLogin.setOnClickListener(this@LoginActivity)
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
                    binding.rtvLogin.setOnClickListener(this@LoginActivity)
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
        jsonobject["softType"] = "14"
        param["attr"] = jsonobject
        mViewModel.checkUpdate(param)
    }

    @SuppressLint("CheckResult", "MissingPermission")
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_forgetPw -> {

            }

            R.id.rtv_login -> {
                var rxPermissions = RxPermissions(this@LoginActivity)
                rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE).subscribe {
                    if (it) {
                        if (locationManager == null) {
                            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                            val provider = LocationManager.NETWORK_PROVIDER
                            locationManager?.requestLocationUpdates(provider, 1000, 1f, object : LocationListener {
                                override fun onLocationChanged(location: Location) {
                                    lat = location.latitude
                                    lon = location.longitude
                                    locationEnable = 1
                                }

                                override fun onProviderDisabled(provider: String) {
                                    locationEnable = -1
                                    ToastUtil.showMiddleToast(i18N(com.kernal.demo.base.R.string.请打开位置信息))
                                }

                                override fun onProviderEnabled(provider: String) {
                                    locationEnable = 1
                                }
                            })
                        }
                        if (locationEnable != -1) {
                            showProgressDialog(20000)
                            val param = HashMap<String, Any>()
                            val jsonobject = JSONObject()
                            jsonobject["loginName"] = binding.etAccount.text.toString()
                            jsonobject["passWord"] = binding.etPw.text.toString()
                            jsonobject["longitude"] = lon
                            jsonobject["latitude"] = lat
                            jsonobject["simId"] = PhoneUtils.getIMSI()
                            jsonobject["imei"] = PhoneUtils.getIMEI()
                            jsonobject["version"] = AppUtils.getAppVersionName()
                            param["attr"] = jsonobject
                            mViewModel.login(param)
                        } else {
                            ToastUtil.showMiddleToast(i18N(com.kernal.demo.base.R.string.请打开位置信息))
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("NewApi")
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
                ToastUtil.showMiddleToast(it.msg)
            }
            mException.observe(this@LoginActivity) {
                dismissProgressDialog()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("CheckResult")
    fun requestPermissions() {
        var rxPermissions = RxPermissions(this@LoginActivity)
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe {
            if (it) {
                UpdateUtil.instance?.downloadFileAndInstall(object : UpdateUtil.UpdateInterface {
                    override fun requestionPermission() {

                    }

                    override fun install(path: String) {
                        if (packageManager.canRequestPackageInstalls()) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                val contentUri =
                                    FileProvider.getUriForFile(this@LoginActivity, "com.kernal.demo.plateid.fileprovider", File(path))
                                intent.setDataAndType(contentUri, "application/vnd.android.package-archive")
                            } else {
                                intent.setDataAndType(Uri.fromFile(File(path)), "application/vnd.android.package-archive")
                            }
                        } else {
                            AppUtils.installApp(path)
                        }
                    }

                })
            } else {

            }
        }
    }

    val requestInstallPackageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            UpdateUtil.instance?.downloadFileAndInstall(object : UpdateUtil.UpdateInterface {
                override fun requestionPermission() {

                }

                override fun install(path: String) {
                }

            })
        } else {

        }
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override fun providerVMClass(): Class<LoginViewModel> {
        return LoginViewModel::class.java
    }

    override val isFullScreen: Boolean
        get() = false

}
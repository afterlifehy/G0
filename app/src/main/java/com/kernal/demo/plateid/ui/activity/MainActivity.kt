package com.kernal.demo.plateid.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.View.OnClickListener
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSONObject
import com.baidu.location.LocationClientOption
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.TimeUtils
import com.hyperai.hyperlpr3.HyperLPR3
import com.hyperai.hyperlpr3.bean.HyperLPRParameter
import com.kernal.demo.base.BaseApplication
import com.kernal.demo.base.arouter.ARouterMap
import com.kernal.demo.base.dialog.DialogHelp
import com.kernal.demo.base.ext.i18N
import com.kernal.demo.base.help.ActivityCacheManager
import com.kernal.demo.base.util.ToastUtil
import com.kernal.demo.base.viewbase.VbBaseActivity
import com.kernal.demo.common.realm.RealmUtil
import com.kernal.demo.common.util.AppUtil
import com.kernal.demo.common.util.BluePrint
import com.kernal.demo.plateid.R
import com.kernal.demo.plateid.databinding.ActivityMainBinding
import com.kernal.demo.plateid.mvvm.viewmodel.MainViewModel
import com.tbruyelle.rxpermissions3.RxPermissions
import com.kernal.demo.base.bean.BlueToothDeviceBean
import com.kernal.demo.base.ds.PreferencesDataStore
import com.kernal.demo.base.ds.PreferencesKeys
import com.kernal.demo.base.ext.startAct
import com.kernal.demo.base.ext.startArouter
import com.kernal.demo.common.event.BaiduLocationEvent
import com.kernal.demo.common.event.RefreshIsPrintEvent
import com.kernal.demo.common.util.BaiduLocationUtil
import com.kernal.demo.common.util.Constant
import com.kernal.demo.plateid.ui.activity.abnormal.AbnormalReportActivity
import com.kernal.demo.plateid.ui.activity.income.IncomeCountingActivity
import com.kernal.demo.plateid.ui.activity.login.LoginActivity
import com.kernal.demo.plateid.ui.activity.login.StreetChooseActivity
import com.kernal.demo.plateid.ui.activity.mine.LogoutActivity
import com.kernal.demo.plateid.ui.activity.order.OrderMainActivity
import com.kernal.demo.plateid.ui.activity.parking.ParkingLotActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@Route(path = ARouterMap.MAIN)
class MainActivity : VbBaseActivity<MainViewModel, ActivityMainBinding>(), OnClickListener {
    lateinit var baiduLocationUtil: BaiduLocationUtil
    var lat = 0.00
    var lon = 0.00
    var loginName = ""

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(baiduLocationEvent: BaiduLocationEvent) {
        startBadiMapLocation()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        // super.onSaveInstanceState(outState)
    }

    override fun initView() {
        delete2DayPic()
        initHyperLPR()
        runBlocking {
            loginName = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.loginName)
        }
        PermissionUtils.permission(Manifest.permission.ACCESS_FINE_LOCATION)
            .callback(object : PermissionUtils.FullCallback {
                override fun onGranted(granted: MutableList<String>) {
                    startBadiMapLocation()
                }

                override fun onDenied(deniedForever: MutableList<String>, denied: MutableList<String>) {
                    ToastUtil.showBottomToast(i18N(com.kernal.demo.base.R.string.请打开位置信息))
                }

            }).request()
        repeatCheckLocation {
            runOnUiThread {
                if (PermissionUtils.isGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    if (baiduLocationUtil == null) {
                        startBadiMapLocation()
                    }
                    locationUpload()
                } else {
                    PermissionUtils.permission(Manifest.permission.ACCESS_FINE_LOCATION)
                        .callback(object : PermissionUtils.FullCallback {
                            override fun onGranted(granted: MutableList<String>) {
                                startBadiMapLocation()
                                locationUpload()
                            }

                            override fun onDenied(deniedForever: MutableList<String>, denied: MutableList<String>) {
                                ToastUtil.showBottomToast(i18N(com.kernal.demo.base.R.string.请打开位置信息))
                            }

                        }).request()
                }
            }
        }
    }

    fun startBadiMapLocation() {
        baiduLocationUtil = BaiduLocationUtil.getInstance(1000 * 60 * 5)
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
                    this@MainActivity.lat = lat
                    this@MainActivity.lon = lon
                    runBlocking {
                        PreferencesDataStore(BaseApplication.instance()).putDouble(PreferencesKeys.lat, lat)
                        PreferencesDataStore(BaseApplication.instance()).putDouble(PreferencesKeys.lon, lon)
                    }
                }
            }
        }
        baiduLocationUtil.setBaiduLocationCallBack(callback)
    }

    fun delete2DayPic() {
        val path = PathUtils.getExternalAppPicturesPath()
        if (FileUtils.createOrExistsDir(path)) {
            val list = FileUtils.listFilesInDir(path)
            for (i in list) {
                if (i.name.contains("_")) {
                    val createTime = TimeUtils.string2Millis(i.name.substring(0, 8), "yyyyMMdd")
                    if (System.currentTimeMillis() - createTime > 2 * 24 * 60 * 60 * 1000) {
                        i.delete()
                    }
                } else {
                    i.delete()
                }
            }
        }
    }

    fun locationUpload() {
        if (loginName.isNotEmpty()) {
            runBlocking {
                val longitude = PreferencesDataStore(BaseApplication.instance()).getDouble(PreferencesKeys.lon)
                val latitude = PreferencesDataStore(BaseApplication.instance()).getDouble(PreferencesKeys.lat)
                val param = HashMap<String, Any>()
                val jsonobject = JSONObject()
                jsonobject["loginName"] = loginName
                jsonobject["longitude"] = lon.takeIf { it != 0.0 }?.toString() ?: longitude.toString()
                jsonobject["latitude"] = lat.takeIf { it != 0.0 }?.toString() ?: latitude.toString()
                param["attr"] = jsonobject
                mViewModel.locationUpload(param)
            }
        }
    }

    override fun initListener() {
        val views = arrayOf(
            binding.ivHead,
            binding.llParkingLot,
            binding.flIncomeCounting,
            binding.flOrder,
            binding.flBerthAbnormal,
            binding.flLogout
        )
        ClickUtils.applySingleDebouncing(views, 1000, this)
    }

    @SuppressLint("SetTextI18n")
    override fun initData() {
//        connectBluePrint()
    }

    @SuppressLint("CheckResult", "MissingPermission")
    fun connectBluePrint() {
        BluePrint.instance?.disConnect()
        if (RealmUtil.instance?.findCurrentDeviceList()!!.isNotEmpty()) {
            Thread {
                val device = RealmUtil.instance?.findCurrentDeviceList()!![0]
                if (device != null) {
                    val printResult = BluePrint.instance?.connet(device.address)
                    if (printResult != 0) {
                        runOnUiThread {
                            DialogHelp.Builder().setTitle(i18N(com.kernal.demo.base.R.string.打印机连接失败需要手动连接))
                                .setLeftMsg(i18N(com.kernal.demo.base.R.string.取消))
                                .setRightMsg(i18N(com.kernal.demo.base.R.string.去连接)).setCancelable(true)
                                .setOnButtonClickLinsener(object : DialogHelp.OnButtonClickLinsener {
                                    override fun onLeftClickListener(msg: String) {
                                    }

                                    override fun onRightClickListener(msg: String) {
                                        if (ActivityCacheManager.instance().getCurrentActivity() !is LoginActivity &&
                                            ActivityCacheManager.instance().getCurrentActivity() !is StreetChooseActivity
                                        ) {
                                            startArouter(ARouterMap.MINE, data = Bundle().apply {
                                                putInt(ARouterMap.MINE_BLUE_PRINT, 1)
                                            })
                                        }
                                    }

                                }).build(ActivityCacheManager.instance().getCurrentActivity()).showDailog()
                        }
                    }
                }
            }.start()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                var rxPermissions = RxPermissions(this@MainActivity)
                rxPermissions.request(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN).subscribe {
                    if (it) {
                        BluePrint.instance?.disConnect()
                        val printList = BluePrint.instance?.blueToothDevice!!
                        if (printList.size == 1) {
                            Thread {
                                val device = printList[0]
                                var connectResult = BluePrint.instance?.connet(device.address)
                                if (connectResult == 0) {
                                    RealmUtil.instance?.deleteAllDevice()
                                    RealmUtil.instance?.addRealm(BlueToothDeviceBean(device.address, device.name))
                                }
                            }.start()
                        } else if (printList.size > 1) {
                            multipleDevice()
                        } else {
                            DialogHelp.Builder().setTitle(i18N(com.kernal.demo.base.R.string.未检测到已配对的打印设备))
                                .setLeftMsg(i18N(com.kernal.demo.base.R.string.取消))
                                .setRightMsg(i18N(com.kernal.demo.base.R.string.去配对)).setCancelable(true)
                                .setOnButtonClickLinsener(object : DialogHelp.OnButtonClickLinsener {
                                    override fun onLeftClickListener(msg: String) {
                                    }

                                    override fun onRightClickListener(msg: String) {
                                        val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
                                        startActivity(intent)
                                    }

                                }).build(this@MainActivity).showDailog()
                        }
                    }
                }
            } else {
                val printList = BluePrint.instance?.blueToothDevice!!
                if (printList.size == 1) {
                    Thread {
                        val device = printList[0]
                        var connectResult = BluePrint.instance?.connet(device.address)
                        if (connectResult == 0) {
                            RealmUtil.instance?.deleteAllDevice()
                            RealmUtil.instance?.addRealm(BlueToothDeviceBean(device.address, device.name))
                        }
                    }.start()
                } else if (printList.size > 1) {
                    multipleDevice()
                } else {
                    DialogHelp.Builder().setTitle(i18N(com.kernal.demo.base.R.string.未检测到已配对的打印设备))
                        .setLeftMsg(i18N(com.kernal.demo.base.R.string.取消))
                        .setRightMsg(i18N(com.kernal.demo.base.R.string.去配对)).setCancelable(true)
                        .setOnButtonClickLinsener(object : DialogHelp.OnButtonClickLinsener {
                            override fun onLeftClickListener(msg: String) {
                            }

                            override fun onRightClickListener(msg: String) {
                                val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
                                startActivity(intent)
                            }

                        }).build(this@MainActivity).showDailog()
                }
            }
        }
    }

    fun multipleDevice() {
        DialogHelp.Builder().setTitle(i18N(com.kernal.demo.base.R.string.检测到存在多台打印设备需手动连接))
            .setLeftMsg(i18N(com.kernal.demo.base.R.string.取消))
            .setRightMsg(i18N(com.kernal.demo.base.R.string.去连接)).setCancelable(true)
            .setOnButtonClickLinsener(object : DialogHelp.OnButtonClickLinsener {
                override fun onLeftClickListener(msg: String) {
                }

                override fun onRightClickListener(msg: String) {
                    if (ActivityCacheManager.instance().getCurrentActivity() !is LoginActivity &&
                        ActivityCacheManager.instance().getCurrentActivity() !is StreetChooseActivity
                    ) {
                        startArouter(ARouterMap.MINE, data = Bundle().apply {
                            putInt(ARouterMap.MINE_BLUE_PRINT, 1)
                        })
                    }
                }

            }).build(this@MainActivity).showDailog()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_head -> {
                startArouter(ARouterMap.MINE, data = Bundle().apply {
                    putInt(ARouterMap.MINE_BLUE_PRINT, 0)
                })
            }

            R.id.ll_parkingLot -> {
                startAct<ParkingLotActivity>()
            }

            R.id.fl_incomeCounting -> {
                startAct<IncomeCountingActivity>()
            }

            R.id.fl_order -> {
                startAct<OrderMainActivity>()
            }

            R.id.fl_berthAbnormal -> {
                startAct<AbnormalReportActivity>()
            }

            R.id.fl_logout -> {
                startAct<LogoutActivity>()
            }
        }
    }

    private fun initHyperLPR() {
        // 车牌识别算法配置参数
        val parameter = HyperLPRParameter()
            .setDetLevel(HyperLPR3.DETECT_LEVEL_LOW)
            .setMaxNum(1)
            .setRecConfidenceThreshold(0.85f)
        // 初始化(仅执行一次生效)
        HyperLPR3.getInstance().init(BaseApplication.instance(), parameter)
    }

    @SuppressLint("NewApi")
    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
        }
    }

    fun repeatCheckLocation(action: () -> Unit) {
        runBlocking {
            PreferencesDataStore(BaseApplication.instance()).putBoolean(PreferencesKeys.isUpdateLocation, true)
            GlobalScope.launch {
                while (PreferencesDataStore(BaseApplication.instance()).getBoolean(PreferencesKeys.isUpdateLocation)) {
                    delay(1000 * 60 * 5)
                    action.invoke()
                }
            }
        }
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun providerVMClass(): Class<MainViewModel> {
        return MainViewModel::class.java
    }

    override val isFullScreen: Boolean
        get() = false

    override fun isRegEventBus(): Boolean {
        return true
    }

    override fun onBackPressedSupport() {
        if (AppUtil.isFastClick(1000)) {
            ActivityCacheManager.instance().getAllActivity().forEach {
                if (!it.isFinishing) {
                    it.finish()
                }
            }
        } else {
            ToastUtil.showBottomToast(i18N(com.kernal.demo.base.R.string.再按一次退出程序))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        baiduLocationUtil.stopLocation()
//        baiduLocationUtil.unregisterListener()
    }
}
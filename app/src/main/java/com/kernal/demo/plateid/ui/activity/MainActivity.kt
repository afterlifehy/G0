package com.kernal.demo.plateid.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.View.OnClickListener
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.PathUtils
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
import com.kernal.demo.base.ext.startAct
import com.kernal.demo.base.ext.startArouter
import com.kernal.demo.plateid.ui.activity.abnormal.AbnormalReportActivity
import com.kernal.demo.plateid.ui.activity.income.IncomeCountingActivity
import com.kernal.demo.plateid.ui.activity.login.LoginActivity
import com.kernal.demo.plateid.ui.activity.login.StreetChooseActivity
import com.kernal.demo.plateid.ui.activity.mine.LogoutActivity
import com.kernal.demo.plateid.ui.activity.order.OrderMainActivity
import com.kernal.demo.plateid.ui.activity.parking.ParkingLotActivity
import com.kernal.demo.plateid.util.UpdateUtil

@Route(path = ARouterMap.MAIN)
class MainActivity : VbBaseActivity<MainViewModel, ActivityMainBinding>(), OnClickListener {

    override fun onSaveInstanceState(outState: Bundle) {
        // super.onSaveInstanceState(outState)
    }

    override fun initView() {
        delete7DayPic()
        initHyperLPR()
    }

    fun delete7DayPic() {
        val path = PathUtils.getExternalAppPicturesPath()
        if (FileUtils.createOrExistsDir(path)) {
            val list = FileUtils.listFilesInDir(path)
            for (i in list) {
//                val createTime = TimeUtils.string2Millis(i.name.split("_")[1] + i.name.split("_")[2], "yyyyMMddHHmmss")
//                if (System.currentTimeMillis() - createTime > 7 * 24 * 60 * 60 * 1000) {
                i.delete()
//                }
            }
        }
    }

    override fun initListener() {
        binding.ivHead.setOnClickListener(this)
        binding.llParkingLot.setOnClickListener(this)
        binding.flIncomeCounting.setOnClickListener(this)
        binding.flOrder.setOnClickListener(this)
        binding.flBerthAbnormal.setOnClickListener(this)
        binding.flLogout.setOnClickListener(this)
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
                                    override fun onLeftClickLinsener(msg: String) {
                                    }

                                    override fun onRightClickLinsener(msg: String) {
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
                                    override fun onLeftClickLinsener(msg: String) {
                                    }

                                    override fun onRightClickLinsener(msg: String) {
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
                            override fun onLeftClickLinsener(msg: String) {
                            }

                            override fun onRightClickLinsener(msg: String) {
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
                override fun onLeftClickLinsener(msg: String) {
                }

                override fun onRightClickLinsener(msg: String) {
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

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("CheckResult")
    fun requestPermissions() {
        var rxPermissions = RxPermissions(this@MainActivity)
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe {
            if (it) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (packageManager.canRequestPackageInstalls()) {
                        UpdateUtil.instance?.downloadFileAndInstall(object : UpdateUtil.UpdateInterface {
                            override fun requestionPermission() {

                            }

                            override fun install(path: String) {
                            }

                        })
                    } else {
                        val uri = Uri.parse("package:${AppUtils.getAppPackageName()}")
                        val intent =
                            Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, uri)
                        requestInstallPackageLauncher.launch(intent)
                    }
                } else {
                    UpdateUtil.instance?.downloadFileAndInstall(object : UpdateUtil.UpdateInterface {
                        override fun requestionPermission() {

                        }

                        override fun install(path: String) {
                        }

                    })
                }
            } else {

            }
        }
    }

    val requestInstallPackageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            UpdateUtil.instance?.downloadFileAndInstall(object :UpdateUtil.UpdateInterface {
                override fun requestionPermission() {

                }

                override fun install(path: String) {
                }

            })
        } else {

        }
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {

    }

    override fun providerVMClass(): Class<MainViewModel> {
        return MainViewModel::class.java
    }

    override val isFullScreen: Boolean
        get() = false

    override fun onBackPressedSupport() {
        if (AppUtil.isFastClick(1000)) {
            ActivityCacheManager.instance().getAllActivity().forEach {
                if (!it.isFinishing) {
                    it.finish()
                }
            }
        } else {
            ToastUtil.showMiddleToast(i18N(com.kernal.demo.base.R.string.再按一次退出程序))
        }
    }
}
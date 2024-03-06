package com.kernal.demo.plateid.ui.activity.mine

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.view.View
import android.view.View.OnClickListener
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSONObject
import com.blankj.utilcode.util.TimeUtils
import com.tbruyelle.rxpermissions3.RxPermissions
import com.kernal.demo.base.BaseApplication
import com.kernal.demo.base.arouter.ARouterMap
import com.kernal.demo.base.bean.DataPrintBean
import com.kernal.demo.base.bean.IncomeCountingBean
import com.kernal.demo.base.ds.PreferencesDataStore
import com.kernal.demo.base.ds.PreferencesKeys
import com.kernal.demo.base.ext.i18N
import com.kernal.demo.base.ext.i18n
import com.kernal.demo.base.ext.startArouter
import com.kernal.demo.base.help.ActivityCacheManager
import com.kernal.demo.base.util.ToastUtil
import com.kernal.demo.base.viewbase.VbBaseActivity
import com.kernal.demo.plateid.R
import com.kernal.demo.plateid.adapter.DataPrintAdapter
import com.kernal.demo.plateid.databinding.ActivityDataPrintBinding
import com.kernal.demo.plateid.mvvm.viewmodel.DataPrintViewModel
import com.kernal.demo.plateid.ui.activity.login.LoginActivity
import com.kernal.demo.common.realm.RealmUtil
import com.kernal.demo.common.util.BluePrint
import com.kernal.demo.common.util.GlideUtils
import kotlinx.coroutines.runBlocking

@Route(path = ARouterMap.DATA_PRINT)
class DataPrintActivity : VbBaseActivity<DataPrintViewModel, ActivityDataPrintBinding>(), OnClickListener {
    var dataPrintAdapter: DataPrintAdapter? = null
    var dataPrintList: MutableList<DataPrintBean> = ArrayList()
    var startDate = ""
    var endDate = ""
    var loginName = ""
    var incomeCountingBean: IncomeCountingBean? = null

    override fun initView() {
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, com.kernal.demo.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = i18N(com.kernal.demo.base.R.string.数据打印)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.kernal.demo.base.R.color.white))

        binding.rvPrintInfo.setHasFixedSize(true)
        binding.rvPrintInfo.layoutManager = LinearLayoutManager(this)
        dataPrintAdapter = DataPrintAdapter(dataPrintList)
        binding.rvPrintInfo.adapter = dataPrintAdapter
    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
        binding.rtvNoPrint.setOnClickListener(this)
        binding.rtvPrint.setOnClickListener(this)
    }

    override fun initData() {
        runBlocking {
            loginName = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.loginName)
        }
        endDate = TimeUtils.millis2String(System.currentTimeMillis(), "yyyy-MM-dd")
        startDate = endDate.substring(0, 8) + "01"

        dataPrintList.add(DataPrintBean(1, "① 订单总数"))
        dataPrintList.add(DataPrintBean(2, "② 拒付费订单数"))
        dataPrintList.add(DataPrintBean(3, "③ 部分付费订单数"))
        dataPrintList.add(DataPrintBean(4, "④ 被追缴金额"))
        dataPrintList.add(DataPrintBean(5, "⑤ 追缴他人金额"))
        dataPrintList.add(DataPrintBean(6, "⑥ 停车人APP金额"))
        dataPrintList.add(DataPrintBean(7, "⑦ 总营收（含④和⑥，不含⑤）"))
        dataPrintAdapter?.setList(dataPrintList)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back,
            R.id.rtv_noPrint -> {
                onBackPressedSupport()
            }

            R.id.rtv_print -> {
                showProgressDialog(20000)
                val param = HashMap<String, Any>()
                val jsonobject = JSONObject()
                jsonobject["startDate"] = startDate
                jsonobject["endDate"] = endDate
                jsonobject["loginName"] = loginName
                jsonobject["searchRange"] = "0"
                param["attr"] = jsonobject
                mViewModel.incomeCounting(param)
            }
        }
    }

    @SuppressLint("CheckResult")
    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            incomeCountingLiveData.observe(this@DataPrintActivity) {
                dismissProgressDialog()
                incomeCountingBean = it
                incomeCountingBean?.loginName = loginName
                incomeCountingBean?.range = ""
                if (incomeCountingBean?.list1 != null) {
                    incomeCountingBean?.list1!![0].oweCount = -1
                    for (i in dataPrintList) {
                        when (i.id) {
                            1 -> {
                                if (!i.ischeck) {
                                    incomeCountingBean?.list1!![0].orderCount = -1
                                }
                            }

                            2 -> {
                                if (!i.ischeck) {
                                    incomeCountingBean?.list1!![0].refusePayCount = -1
                                }
                            }

                            3 -> {
                                if (!i.ischeck) {
                                    incomeCountingBean?.list1!![0].partPayCount = -1
                                }
                            }

                            4 -> {
                                if (!i.ischeck) {
                                    incomeCountingBean?.list1!![0].passMoney = ""
                                }
                            }

                            5 -> {
                                if (!i.ischeck) {
                                    incomeCountingBean?.list1!![0].oweMoney = ""
                                }
                            }

                            6 -> {
                                if (!i.ischeck) {
                                    incomeCountingBean?.list1!![0].onlineMoney = ""
                                }
                            }

                            7 -> {
                                if (!i.ischeck) {
                                    incomeCountingBean?.list1!![0].payMoney = ""
                                }
                            }
                        }
                    }
                    var str = "receipt,"
                    var rxPermissions = RxPermissions(this@DataPrintActivity)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        rxPermissions.request(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN).subscribe {
                            if (it) {
                                val printList = BluePrint.instance?.blueToothDevice!!
                                if (printList.size == 1) {
                                    Thread {
                                        val device = printList[0]
                                        var connectResult = BluePrint.instance?.connet(device.address)
                                        if (connectResult == 0) {
                                            runOnUiThread {
                                                ToastUtil.showMiddleToast(i18n(com.kernal.demo.base.R.string.开始打印))
                                            }
                                            BluePrint.instance?.zkblueprint(str + JSONObject.toJSONString(incomeCountingBean))
                                        }
                                    }.start()
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
                                    runOnUiThread {
                                        ToastUtil.showMiddleToast(i18n(com.kernal.demo.base.R.string.开始打印))
                                    }
                                    BluePrint.instance?.zkblueprint(str + JSONObject.toJSONString(incomeCountingBean))
                                }
                            }.start()
                        }
                    }
                }
                runBlocking {
                    PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.simId, "")
                    PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.phone, "")
                    PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.name, "")
                    PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.loginName, "")
                }
                RealmUtil.instance?.deleteAllStreet()
                onBackPressedSupport()
            }
            errMsg.observe(this@DataPrintActivity) {
                dismissProgressDialog()
                ToastUtil.showMiddleToast(it.msg)
            }
            mException.observe(this@DataPrintActivity) {
                dismissProgressDialog()
            }
        }
    }

    override fun providerVMClass(): Class<DataPrintViewModel>? {
        return DataPrintViewModel::class.java
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityDataPrintBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override val isFullScreen: Boolean
        get() = true

    override fun marginStatusBarView(): View {
        return binding.layoutToolbar.ablToolbar
    }

    override fun onBackPressedSupport() {
        startArouter(ARouterMap.LOGIN)
        for (i in ActivityCacheManager.instance().getAllActivity()) {
            if (i !is LoginActivity) {
                i.finish()
            }
        }
    }
}
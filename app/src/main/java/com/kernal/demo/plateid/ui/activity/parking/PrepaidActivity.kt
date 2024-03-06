package com.kernal.demo.plateid.ui.activity.parking

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.ArrayMap
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSONObject
import com.tbruyelle.rxpermissions3.RxPermissions
import com.kernal.demo.base.BaseApplication
import com.kernal.demo.base.arouter.ARouterMap
import com.kernal.demo.base.bean.PayResultBean
import com.kernal.demo.base.bean.PrintInfoBean
import com.kernal.demo.base.ds.PreferencesDataStore
import com.kernal.demo.base.ds.PreferencesKeys
import com.kernal.demo.base.ext.hide
import com.kernal.demo.base.ext.i18N
import com.kernal.demo.base.ext.i18n
import com.kernal.demo.base.ext.show
import com.kernal.demo.base.util.ToastUtil
import com.kernal.demo.base.viewbase.VbBaseActivity
import com.kernal.demo.plateid.R
import com.kernal.demo.plateid.databinding.ActivityPrepaidBinding
import com.kernal.demo.plateid.dialog.PaymentQrDialog
import com.kernal.demo.plateid.mvvm.viewmodel.PrepaidViewModel
import com.kernal.demo.common.event.RefreshParkingSpaceEvent
import com.kernal.demo.common.util.AppUtil
import com.kernal.demo.common.util.BluePrint
import com.kernal.demo.common.util.Constant
import com.kernal.demo.common.util.GlideUtils
import kotlinx.coroutines.runBlocking
import org.greenrobot.eventbus.EventBus

@Route(path = ARouterMap.PREPAID)
class PrepaidActivity : VbBaseActivity<PrepaidViewModel, ActivityPrepaidBinding>(), OnClickListener {
    var timeDuration = 1.0
    var paymentQrDialog: PaymentQrDialog? = null

    var minAmount = 1.0
    var parkingNo = ""
    var carLicense = ""
    var orderNo = ""
    var carColor = ""

    var simId = ""
    var loginName = ""

    var count = 0
    var handler = Handler(Looper.getMainLooper())
    var tradeNo = ""
    var plateLogoColorMap: MutableMap<String, Int> = ArrayMap()

    init {
        plateLogoColorMap[Constant.BLACK] = com.kernal.demo.base.R.color.black
        plateLogoColorMap[Constant.WHITE] = com.kernal.demo.base.R.color.white
        plateLogoColorMap[Constant.GREY] = com.kernal.demo.base.R.color.white
        plateLogoColorMap[Constant.RED] = com.kernal.demo.base.R.color.white
        plateLogoColorMap[Constant.BLUE] = com.kernal.demo.base.R.color.color_ff0046de
        plateLogoColorMap[Constant.YELLOW] = com.kernal.demo.base.R.color.color_fffda027
        plateLogoColorMap[Constant.ORANGE] = com.kernal.demo.base.R.color.white
        plateLogoColorMap[Constant.BROWN] = com.kernal.demo.base.R.color.white
        plateLogoColorMap[Constant.GREEN] = com.kernal.demo.base.R.color.color_ff09a95f
        plateLogoColorMap[Constant.PURPLE] = com.kernal.demo.base.R.color.white
        plateLogoColorMap[Constant.CYAN] = com.kernal.demo.base.R.color.white
        plateLogoColorMap[Constant.PINK] = com.kernal.demo.base.R.color.white
        plateLogoColorMap[Constant.TRANSPARENT] = com.kernal.demo.base.R.color.white
        plateLogoColorMap[Constant.OTHERS] = com.kernal.demo.base.R.color.white
    }

    override fun initView() {
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, com.kernal.demo.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.kernal.demo.base.R.color.white))

        minAmount = intent.getDoubleExtra(ARouterMap.PREPAID_MIN_AMOUNT, 1.0)
        carLicense = intent.getStringExtra(ARouterMap.PREPAID_CARLICENSE).toString()
        parkingNo = intent.getStringExtra(ARouterMap.PREPAID_PARKING_NO).toString()
        orderNo = intent.getStringExtra(ARouterMap.PREPAID_ORDER_NO).toString()
        carColor = intent.getStringExtra(ARouterMap.PREPAID_CAR_COLOR).toString()
        if (minAmount == 1.0) {
            binding.layoutToolbar.tvTitle.text = i18N(com.kernal.demo.base.R.string.预支付)
        } else {
            binding.layoutToolbar.tvTitle.text = i18N(com.kernal.demo.base.R.string.续费)
        }

        binding.tvPlate.text = carLicense
        binding.tvParkingNo.text = parkingNo
        if (carColor == Constant.YELLOW_GREEN) {
            binding.llCarColor.show()
            binding.rtvCarColor.delegate.setBackgroundColor(
                ContextCompat.getColor(
                    BaseApplication.instance(),
                    com.kernal.demo.base.R.color.transparent
                )
            )
            binding.rtvCarColor.delegate.init()
        } else {
            binding.llCarColor.hide()
            binding.rtvCarColor.delegate.setBackgroundColor(
                ContextCompat.getColor(
                    BaseApplication.instance(),
                    plateLogoColorMap[carColor]!!
                )
            )
            if (plateLogoColorMap[carColor]!! == com.kernal.demo.base.R.color.white) {
                binding.rtvCarColor.delegate.setStrokeWidth(1)
                binding.rtvCarColor.delegate.setTextColor(
                    ContextCompat.getColor(
                        BaseApplication.instance(),
                        com.kernal.demo.base.R.color.black
                    )
                )
            } else {
                binding.rtvCarColor.delegate.setStrokeWidth(0)
                binding.rtvCarColor.delegate.setTextColor(
                    ContextCompat.getColor(
                        BaseApplication.instance(),
                        com.kernal.demo.base.R.color.white
                    )
                )
            }
            binding.rtvCarColor.delegate.init()
        }
    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
        binding.rflAdd.setOnClickListener(this)
        binding.rflMinus.setOnClickListener(this)
        binding.rflScanPay.setOnClickListener(this)
        binding.etTimeDuration.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val value = s.toString()
                if (value.contains(".")) {
                    val splitInput = value.split(".")
                    if (splitInput.size > 1 && splitInput[1].length > 1) {
                        s?.delete(s.length - 1, s.length)
                    }
                    if (value.endsWith(".") && value.length > 1) {
                        timeDuration = value.replace(".", "").toDouble()
                    } else if (value.endsWith(".") && value.length <= 1) {
                        timeDuration = minAmount - 0.5
                    } else {
                        timeDuration = value.toDouble()
                    }
                } else if (value.length > 0) {
                    timeDuration = value.toDouble()
                } else {
                    timeDuration = 0.0
                }
                if (timeDuration > 999) {
                    timeDuration = 999.0
                    binding.etTimeDuration.setText(timeDuration.toString())
                    binding.etTimeDuration.setSelection(timeDuration.toString().length)
                }
            }

        })
    }

    override fun initData() {
        runBlocking {
            simId = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.simId)
            loginName = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.loginName)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }

            R.id.rfl_add -> {
                if (timeDuration == 999.0) {
                    return
                }
                if (timeDuration < minAmount) {
                    timeDuration = minAmount
                } else {
                    timeDuration += 0.5
                }
                binding.etTimeDuration.setText(timeDuration.toString())
                binding.etTimeDuration.setSelection(timeDuration.toString().length)
            }

            R.id.rfl_minus -> {
                if (timeDuration <= minAmount) {
                    timeDuration = minAmount
                } else {
                    timeDuration -= 0.5
                }
                binding.etTimeDuration.setText(timeDuration.toString())
                binding.etTimeDuration.setSelection(timeDuration.toString().length)
            }

            R.id.rfl_scanPay -> {
                if (timeDuration >= minAmount) {
                    val param = HashMap<String, Any>()
                    val jsonobject = JSONObject()
                    jsonobject["parkingNo"] = parkingNo
                    jsonobject["orderNo"] = orderNo
                    jsonobject["loginName"] = loginName
                    jsonobject["simId"] = simId
                    jsonobject["parkingHours"] = timeDuration.toString()
                    jsonobject["orderType"] = "1"
                    param["attr"] = jsonobject
                    mViewModel.prePayFeeInquiry(param)
                } else {
                    ToastUtil.showMiddleToast("时长过短")
                    return
                }
            }
        }
    }

    @SuppressLint("CheckResult")
    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            prePayFeeInquiryLiveData.observe(this@PrepaidActivity) {
                dismissProgressDialog()
                tradeNo = it.tradeNo
                paymentQrDialog = PaymentQrDialog(it.qrCode, AppUtil.keepNDecimals(it.totalAmount.toString(), 2))
                paymentQrDialog?.show()
                paymentQrDialog?.setOnDismissListener { handler.removeCallbacks(runnable) }
                count = 0
                handler.postDelayed(runnable, 2000)
            }
            payResultInquiryLiveData.observe(this@PrepaidActivity) {
                dismissProgressDialog()
                if (it != null) {
                    handler.removeCallbacks(runnable)
                    ToastUtil.showMiddleToast(i18N(com.kernal.demo.base.R.string.支付成功))
                    if (paymentQrDialog != null) {
                        paymentQrDialog?.dismiss()
                    }
                    val payResultBean = it
                    var rxPermissions = RxPermissions(this@PrepaidActivity)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        rxPermissions.request(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN).subscribe {
                            if (it) {
                                startPrint(payResultBean)
                            }
                        }
                    } else {
                        startPrint(payResultBean)
                    }
                    EventBus.getDefault().post(RefreshParkingSpaceEvent())
                    onBackPressedSupport()
                }
            }
            errMsg.observe(this@PrepaidActivity) {
                dismissProgressDialog()
                ToastUtil.showMiddleToast(it.msg)
            }
            mException.observe(this@PrepaidActivity) {
                dismissProgressDialog()
            }
        }
    }

    val runnable = object : Runnable {
        override fun run() {
            if (count < 60) {
                checkPayResult()
                count++
                handler.postDelayed(this, 3000)
            }
        }
    }

    fun checkPayResult() {
        val param = HashMap<String, Any>()
        val jsonobject = JSONObject()
        jsonobject["simId"] = simId
        jsonobject["tradeNo"] = tradeNo
        param["attr"] = jsonobject
        mViewModel.payResultInquiry(param)
    }

    fun startPrint(it: PayResultBean) {
        val payMoney = it.payMoney
        val printInfo = PrintInfoBean(
            roadId = it.roadName,
            plateId = it.carLicense,
            payMoney = String.format("%.2f", payMoney.toFloat()),
            orderId = it.tradeNo,
            phone = it.phone,
            startTime = it.startTime,
            leftTime = it.endTime,
            remark = it.remark,
            company = it.businessCname,
            oweCount = it.oweCount
        )
        val printList = BluePrint.instance?.blueToothDevice!!
        if (printList.size == 1) {
            Thread {
                val device = printList[0]
                var connectResult = BluePrint.instance?.connet(device.address)
                if (connectResult == 0) {
                    runOnUiThread {
                        ToastUtil.showMiddleToast(i18n(com.kernal.demo.base.R.string.开始打印))
                    }
                    BluePrint.instance?.zkblueprint(JSONObject.toJSONString(printInfo))
                }
            }.start()
        }
    }

    override fun providerVMClass(): Class<PrepaidViewModel> {
        return PrepaidViewModel::class.java
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityPrepaidBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override val isFullScreen: Boolean
        get() = true

    override fun marginStatusBarView(): View {
        return binding.layoutToolbar.ablToolbar
    }

    override fun onStop() {
        super.onStop()
        if (handler != null) {
            handler.removeCallbacks(runnable)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (handler != null) {
            handler.removeCallbacks(runnable)
        }
    }
}
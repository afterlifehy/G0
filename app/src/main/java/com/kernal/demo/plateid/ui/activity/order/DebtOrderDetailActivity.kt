package com.kernal.demo.plateid.ui.activity.order

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.View.OnClickListener
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSONObject
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.TimeUtils
import com.kernal.demo.base.BaseApplication
import com.kernal.demo.base.arouter.ARouterMap
import com.kernal.demo.base.bean.DebtCollectionBean
import com.kernal.demo.base.ds.PreferencesDataStore
import com.kernal.demo.base.ds.PreferencesKeys
import com.kernal.demo.base.ext.i18N
import com.kernal.demo.base.util.ToastUtil
import com.kernal.demo.base.viewbase.VbBaseActivity
import com.kernal.demo.common.event.RefreshDebtOrderListEvent
import com.kernal.demo.common.util.AppUtil
import com.kernal.demo.common.util.GlideUtils
import com.kernal.demo.plateid.R
import com.kernal.demo.plateid.databinding.ActivityDebtOrderDetailBinding
import com.kernal.demo.plateid.dialog.PaymentQrDialog
import com.kernal.demo.plateid.mvvm.viewmodel.DebtOrderDetailViewModel
import com.tbruyelle.rxpermissions3.RxPermissions
import com.zrq.spanbuilder.TextStyle
import com.kernal.demo.base.bean.PayResultBean
import com.kernal.demo.base.bean.PrintInfoBean
import com.kernal.demo.base.ext.i18n
import com.kernal.demo.base.ext.startArouter
import com.kernal.demo.common.util.BluePrint
import kotlinx.coroutines.runBlocking
import org.greenrobot.eventbus.EventBus

@Route(path = ARouterMap.DEBT_ORDER_DETAIL)
class DebtOrderDetailActivity : VbBaseActivity<DebtOrderDetailViewModel, ActivityDebtOrderDetailBinding>(), OnClickListener {
    val colors = intArrayOf(com.kernal.demo.base.R.color.color_fff70f0f, com.kernal.demo.base.R.color.color_fff70f0f)
    val sizes = intArrayOf(24, 16)
    val styles = arrayOf(TextStyle.BOLD, TextStyle.NORMAL)
    val colors2 = intArrayOf(com.kernal.demo.base.R.color.color_ff666666, com.kernal.demo.base.R.color.color_ff1a1a1a)
    val sizes2 = intArrayOf(19, 19)
    var paymentQrDialog: PaymentQrDialog? = null
    var tradeNo = ""
    var debtCollectionBean: DebtCollectionBean? = null
    var simId = ""
    var loginName = ""
    var count = 0
    var handler = Handler(Looper.getMainLooper())
    var picList: MutableList<String> = ArrayList()

    @SuppressLint("NewApi")
    override fun initView() {
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, com.kernal.demo.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = i18N(com.kernal.demo.base.R.string.欠费订单详情)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.kernal.demo.base.R.color.white))

        debtCollectionBean = intent.getParcelableExtra(ARouterMap.DEBT_ORDER) as? DebtCollectionBean
        binding.tvPlate.text = debtCollectionBean!!.carLicense
        val strings1 = arrayOf("未付：${AppUtil.keepNDecimal(debtCollectionBean!!.oweMoney / 100.00, 2)}", "元")
        binding.tvArrearsAmount.text = AppUtil.getSpan(strings1, sizes, colors)
        val strings2 = arrayOf(i18N(com.kernal.demo.base.R.string.订单) + "：", debtCollectionBean!!.orderNo)
        binding.tvOrderNo.text = AppUtil.getSpan(strings2, sizes2, colors2)
        val strings3 = arrayOf(i18N(com.kernal.demo.base.R.string.泊位) + "：", debtCollectionBean!!.parkingNo)
        binding.tvBerth.text = AppUtil.getSpan(strings3, sizes2, colors2)
        val strings4 = arrayOf(i18N(com.kernal.demo.base.R.string.路段) + "：", debtCollectionBean!!.streetName)
        binding.tvStreet.text = AppUtil.getSpan(strings4, sizes2, colors2)
        val strings5 = arrayOf(i18N(com.kernal.demo.base.R.string.入场) + "：", debtCollectionBean!!.startTime)
        binding.tvStartTime.text = AppUtil.getSpan(strings5, sizes2, colors2)
        val strings6 = arrayOf(i18N(com.kernal.demo.base.R.string.出场) + "：", debtCollectionBean!!.endTime)
        binding.tvEndTime.text = AppUtil.getSpan(strings6, sizes2, colors2)

        val lp = binding.llPic.layoutParams
        lp.height = (ScreenUtils.getScreenWidth() - SizeUtils.dp2px(68f)) / 3
        binding.llPic.layoutParams = lp
    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
        binding.layoutToolbar.ivRight.setOnClickListener(this)
        binding.rivPic1.setOnClickListener(this)
        binding.rivPic2.setOnClickListener(this)
        binding.rivPic3.setOnClickListener(this)
        binding.rflPay.setOnClickListener(this)
    }

    override fun initData() {
        showProgressDialog(20000)
        val param = HashMap<String, Any>()
        val jsonobject = JSONObject()
        jsonobject["orderNo"] = debtCollectionBean?.orderNo
        param["attr"] = jsonobject
        mViewModel.picInquiry(param)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }

            R.id.iv_right -> {

            }

            R.id.riv_pic1 -> {
                startArouter(ARouterMap.PREVIEW_IMAGE, data = Bundle().apply {
                    putInt(ARouterMap.IMG_INDEX, 0)
                    putStringArrayList(ARouterMap.IMG_LIST, picList as ArrayList<String>)
                })
            }

            R.id.riv_pic2 -> {
                startArouter(ARouterMap.PREVIEW_IMAGE, data = Bundle().apply {
                    putInt(ARouterMap.IMG_INDEX, 1)
                    putStringArrayList(ARouterMap.IMG_LIST, picList as ArrayList<String>)
                })
            }

            R.id.riv_pic3 -> {
                startArouter(ARouterMap.PREVIEW_IMAGE, data = Bundle().apply {
                    putInt(ARouterMap.IMG_INDEX, 2)
                    putStringArrayList(ARouterMap.IMG_LIST, picList as ArrayList<String>)
                })
            }

            R.id.rfl_pay -> {
                showProgressDialog(20000)
                runBlocking {
                    simId = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.simId)
                    loginName = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.loginName)
                    val param = HashMap<String, Any>()
                    val jsonobject = JSONObject()
                    jsonobject["loginName"] = loginName
                    jsonobject["carLicense"] = debtCollectionBean?.carLicense
                    jsonobject["districtId"] = debtCollectionBean?.districtId
                    jsonobject["businessId"] = debtCollectionBean?.orderNo
                    jsonobject["simId"] = simId
                    jsonobject["channel"] = "pos"
                    jsonobject["orderId"] = debtCollectionBean?.oweOrderId
                    jsonobject["parkingTime"] = debtCollectionBean?.parkingTime
                    jsonobject["arrivedTime"] = debtCollectionBean?.startTime!!.replace("-", "").replace(":", "").replace(" ", "")
                    jsonobject["leftTime"] = debtCollectionBean?.endTime!!.replace("-", "").replace(":", "").replace(" ", "")
                    jsonobject["roadName"] = debtCollectionBean?.streetName
                    jsonobject["dueMoney"] = debtCollectionBean?.dueMoney.toString()
                    jsonobject["oweMoney"] = debtCollectionBean?.oweMoney.toString()
                    jsonobject["paidMoney"] = debtCollectionBean?.paidMoney.toString()
                    param["attr"] = jsonobject
                    mViewModel.debtPayQr(param)
                }
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

    val runnable = object : Runnable {
        override fun run() {
            if (count < 60) {
                checkPayResult()
                count++
                handler.postDelayed(this, 3000)
            }
        }
    }

    @SuppressLint("CheckResult")
    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            picInquiryLiveData.observe(this@DebtOrderDetailActivity) {
                dismissProgressDialog()
                picList.add(it.inPicture11)
                picList.add(it.inPicture10)
                picList.add(it.inPicture20)
                GlideUtils.instance?.loadImage(binding.rivPic1, picList[0], com.kernal.demo.common.R.mipmap.ic_placeholder)
                GlideUtils.instance?.loadImage(binding.rivPic2, picList[1], com.kernal.demo.common.R.mipmap.ic_placeholder)
                GlideUtils.instance?.loadImage(binding.rivPic3, picList[2], com.kernal.demo.common.R.mipmap.ic_placeholder)
            }
            debtPayQrLiveData.observe(this@DebtOrderDetailActivity) {
                dismissProgressDialog()
                tradeNo = it.tradeNo
                paymentQrDialog = PaymentQrDialog(it.qr_code, AppUtil.keepNDecimals((it.amount / 100).toString(), 2))
                paymentQrDialog?.show()
                paymentQrDialog?.setOnDismissListener { handler.removeCallbacks(runnable) }
                count = 0
                handler.postDelayed(runnable, 2000)
            }
            payResultInquiryLiveData.observe(this@DebtOrderDetailActivity) {
                dismissProgressDialog()
                handler.removeCallbacks(runnable)
                ToastUtil.showMiddleToast(i18N(com.kernal.demo.base.R.string.支付成功))
                if (paymentQrDialog != null) {
                    paymentQrDialog?.dismiss()
                }
                val payResultBean = it
                var rxPermissions = RxPermissions(this@DebtOrderDetailActivity)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    rxPermissions.request(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN).subscribe {
                        if (it) {
                            startPrint(payResultBean)
                        }
                    }
                } else {
                    startPrint(it)
                }
                EventBus.getDefault().post(RefreshDebtOrderListEvent())
                onBackPressedSupport()
            }
            errMsg.observe(this@DebtOrderDetailActivity) {
                dismissProgressDialog()
                ToastUtil.showMiddleToast(it.msg)
            }
            mException.observe(this@DebtOrderDetailActivity) {
                dismissProgressDialog()
            }
        }
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

    override fun getVbBindingView(): ViewBinding {
        return ActivityDebtOrderDetailBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override val isFullScreen: Boolean
        get() = true

    override fun marginStatusBarView(): View? {
        return binding.layoutToolbar.ablToolbar
    }

    override fun providerVMClass(): Class<DebtOrderDetailViewModel> {
        return DebtOrderDetailViewModel::class.java
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
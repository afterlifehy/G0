package ja.insepector.bxapp.ui.activity.order

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
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
import ja.insepector.base.BaseApplication
import ja.insepector.base.arouter.ARouterMap
import ja.insepector.base.bean.DebtCollectionBean
import ja.insepector.base.ds.PreferencesDataStore
import ja.insepector.base.ds.PreferencesKeys
import ja.insepector.base.ext.i18N
import ja.insepector.base.util.ToastUtil
import ja.insepector.base.viewbase.VbBaseActivity
import ja.insepector.common.event.RefreshDebtOrderListEvent
import ja.insepector.common.util.AppUtil
import ja.insepector.common.util.GlideUtils
import ja.insepector.bxapp.R
import ja.insepector.bxapp.databinding.ActivityDebtOrderDetailBinding
import ja.insepector.bxapp.dialog.PaymentQrDialog
import ja.insepector.bxapp.mvvm.viewmodel.DebtOrderDetailViewModel
import com.tbruyelle.rxpermissions3.RxPermissions
import com.zrq.spanbuilder.TextStyle
import ja.insepector.base.ext.startArouter
import kotlinx.coroutines.runBlocking
import org.greenrobot.eventbus.EventBus

@Route(path = ARouterMap.DEBT_ORDER_DETAIL)
class DebtOrderDetailActivity : VbBaseActivity<DebtOrderDetailViewModel, ActivityDebtOrderDetailBinding>(), OnClickListener {
    val colors = intArrayOf(ja.insepector.base.R.color.color_fff70f0f, ja.insepector.base.R.color.color_fff70f0f)
    val sizes = intArrayOf(24, 16)
    val styles = arrayOf(TextStyle.BOLD, TextStyle.NORMAL)
    val colors2 = intArrayOf(ja.insepector.base.R.color.color_ff666666, ja.insepector.base.R.color.color_ff1a1a1a)
    val sizes2 = intArrayOf(19, 19)
    var paymentQrDialog: PaymentQrDialog? = null
    var qr = ""
    var tradeNo = ""
    var debtCollectionBean: DebtCollectionBean? = null
    var token = ""
    var count = 0
    var handler = Handler(Looper.getMainLooper())
    var picList: MutableList<String> = ArrayList()

    @SuppressLint("NewApi")
    override fun initView() {
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, ja.insepector.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = i18N(ja.insepector.base.R.string.欠费订单详情)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), ja.insepector.base.R.color.white))

        debtCollectionBean = intent.getParcelableExtra(ARouterMap.DEBT_ORDER) as? DebtCollectionBean
        binding.tvPlate.text = debtCollectionBean!!.carLicense
        val strings1 = arrayOf("${AppUtil.keepNDecimal(debtCollectionBean!!.oweMoney / 100.00, 2)}", "元")
        binding.tvArrearsAmount.text = AppUtil.getSpan(strings1, sizes, colors)
        val strings2 = arrayOf(i18N(ja.insepector.base.R.string.订单) + "：", debtCollectionBean!!.orderNo)
        binding.tvOrderNo.text = AppUtil.getSpan(strings2, sizes2, colors2)
        val strings3 = arrayOf(i18N(ja.insepector.base.R.string.泊位) + "：", debtCollectionBean!!.parkingNo)
        binding.tvBerth.text = AppUtil.getSpan(strings3, sizes2, colors2)
        val strings4 = arrayOf(i18N(ja.insepector.base.R.string.路段) + "：", debtCollectionBean!!.streetName)
        binding.tvStreet.text = AppUtil.getSpan(strings4, sizes2, colors2)
        val strings5 = arrayOf(i18N(ja.insepector.base.R.string.入场) + "：", debtCollectionBean!!.startTime)
        binding.tvStartTime.text = AppUtil.getSpan(strings5, sizes2, colors2)
        val strings6 = arrayOf(i18N(ja.insepector.base.R.string.出场) + "：", debtCollectionBean!!.endTime)
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
                    token = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.simId)
                    val param = HashMap<String, Any>()
                    val jsonobject = JSONObject()
                    jsonobject["token"] = token
//                    TODO()
//                    jsonobject["oweOrderId"] = debtCollectionBean!!.oweOrderId
//                    jsonobject["oweMoney"] = debtCollectionBean!!.oweMoney
//                    jsonobject["orderNo"] = debtCollectionBean!!.orderNo
//                    jsonobject["startTime"] = debtCollectionBean!!.startTime
//                    jsonobject["endTime"] = debtCollectionBean!!.endTime
//                    jsonobject["streetName"] = debtCollectionBean!!.streetName
//                    jsonobject["streetNo"] = debtCollectionBean!!.streetNo
//                    jsonobject["districtId"] = debtCollectionBean!!.districtId
//                    jsonobject["carLicense"] = debtCollectionBean!!.carLicense
//                    jsonobject["parkingNo"] = debtCollectionBean!!.parkingNo
//                    jsonobject["parkingTime"] = debtCollectionBean!!.parkingTime
//                    jsonobject["companyName"] = debtCollectionBean!!.companyName
//                    jsonobject["companyPhone"] = debtCollectionBean!!.companyPhone
                    param["attr"] = jsonobject
                    mViewModel.debtPay(param)
                }
            }
        }
    }

    fun checkPayResult() {
        val param = HashMap<String, Any>()
        val jsonobject = JSONObject()
        jsonobject["token"] = token
        jsonobject["tradeNo"] = tradeNo
        param["attr"] = jsonobject
        mViewModel.payResult(param)
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
                GlideUtils.instance?.loadImage(binding.rivPic1, picList[0], ja.insepector.common.R.mipmap.ic_placeholder)
                GlideUtils.instance?.loadImage(binding.rivPic2, picList[1], ja.insepector.common.R.mipmap.ic_placeholder)
                GlideUtils.instance?.loadImage(binding.rivPic3, picList[2], ja.insepector.common.R.mipmap.ic_placeholder)
            }
            debtPayLiveData.observe(this@DebtOrderDetailActivity) {
                dismissProgressDialog()
                tradeNo = "12345678"
                qr = "www.baidu.com"
                paymentQrDialog = PaymentQrDialog(qr,0)
                paymentQrDialog?.show()
                paymentQrDialog?.setOnDismissListener(object : DialogInterface.OnDismissListener {
                    override fun onDismiss(p0: DialogInterface?) {
                        handler.removeCallbacks(runnable)
                    }
                })
                count = 0
                //TODO(post)
                handler.postDelayed(runnable, 2000)
            }
            payResultLiveData.observe(this@DebtOrderDetailActivity) {
                dismissProgressDialog()
                handler.removeCallbacks(runnable)
                ToastUtil.showMiddleToast(i18N(ja.insepector.base.R.string.支付成功))
                if (paymentQrDialog != null) {
                    paymentQrDialog?.dismiss()
                }
                val payResultBean = it
                var rxPermissions = RxPermissions(this@DebtOrderDetailActivity)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    rxPermissions.request(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN).subscribe {
                        if (it) {
//                      TODO()
//                            startPrint(payResultBean)
                        }
                    }
                } else {
//                    startPrint(it)
                }
                EventBus.getDefault().post(RefreshDebtOrderListEvent())
                onBackPressedSupport()
            }
            errMsg.observe(this@DebtOrderDetailActivity) {
                dismissProgressDialog()
                ToastUtil.showMiddleToast(it.msg)
            }
        }
    }

//    fun startPrint(it: PayResultBean) {
//        val payMoney = it.payMoney
//        val printInfo = PrintInfoBean(
//            roadId = it.roadName,
//            plateId = it.carLicense,
//            payMoney = String.format("%.2f", payMoney.toFloat()),
//            orderId = debtCollectionBean!!.orderNo,
//            phone = it.phone,
//            startTime = it.startTime,
//            leftTime = it.endTime,
//            remark = it.remark,
//            company = it.businessCname,
//            oweCount = it.oweCount
//        )
//        ToastUtil.showMiddleToast(i18n(ja.insepector.base.R.string.开始打印))
//        Thread {
//            BluePrint.instance?.zkblueprint(JSONObject.toJSONString(printInfo))
//        }.start()
//    }

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

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }
}
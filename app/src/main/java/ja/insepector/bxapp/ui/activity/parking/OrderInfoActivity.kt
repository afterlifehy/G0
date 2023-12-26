package ja.insepector.bxapp.ui.activity.parking

import android.view.View
import android.view.View.OnClickListener
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSONObject
import com.zrq.spanbuilder.TextStyle
import ja.insepector.base.BaseApplication
import ja.insepector.base.arouter.ARouterMap
import ja.insepector.base.bean.EndOrderInfoBean
import ja.insepector.base.ds.PreferencesDataStore
import ja.insepector.base.ds.PreferencesKeys
import ja.insepector.base.ext.i18N
import ja.insepector.base.util.ToastUtil
import ja.insepector.base.viewbase.VbBaseActivity
import ja.insepector.bxapp.R
import ja.insepector.bxapp.databinding.ActivityOrderInfoBinding
import ja.insepector.bxapp.dialog.PaymentQrDialog
import ja.insepector.bxapp.mvvm.viewmodel.OrderInfoViewModel
import ja.insepector.common.event.EndOrderEvent
import ja.insepector.common.util.AppUtil
import ja.insepector.common.util.GlideUtils
import kotlinx.coroutines.runBlocking
import org.greenrobot.eventbus.EventBus

@Route(path = ARouterMap.ORDER_INFO)
class OrderInfoActivity : VbBaseActivity<OrderInfoViewModel, ActivityOrderInfoBinding>(), OnClickListener {
    var paymentQrDialog: PaymentQrDialog? = null
    var qr = ""
    var endOrderBean: EndOrderInfoBean? = null
    var orderNo = ""

    val colors = intArrayOf(ja.insepector.base.R.color.white, ja.insepector.base.R.color.white)
    val sizes = intArrayOf(24, 19)
    val styles = arrayOf(TextStyle.BOLD, TextStyle.NORMAL)

    var simId = ""
    var loginName = ""
    var totalAmount = ""

    override fun initView() {
        binding.layoutToolbar.tvTitle.text = i18N(ja.insepector.base.R.string.订单信息)
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, ja.insepector.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), ja.insepector.base.R.color.white))

        orderNo = intent.getStringExtra(ARouterMap.ORDER_INFO_ORDER_NO).toString()
    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
        binding.rflAppPay.setOnClickListener(this)
        binding.rflRefusePay.setOnClickListener(this)
        binding.rflScanPay.setOnClickListener(this)
    }

    override fun initData() {
        runBlocking {
            simId = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.simId)
            loginName = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.loginName)
        }
        val param = HashMap<String, Any>()
        val jsonobject = JSONObject()
        jsonobject["orderNo"] = orderNo
        param["attr"] = jsonobject
        mViewModel.endOrderInfo(param)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }

            R.id.rfl_appPay -> {
                EventBus.getDefault().post(EndOrderEvent())
                onBackPressedSupport()
            }

            R.id.rfl_refusePay -> {
                EventBus.getDefault().post(EndOrderEvent())
                onBackPressedSupport()
            }

            R.id.rfl_scanPay -> {
                val param = HashMap<String, Any>()
                val jsonobject = JSONObject()
                jsonobject["orderNo"] = orderNo
                jsonobject["totalAmount"] = totalAmount
                jsonobject["loginName"] = loginName
                jsonobject["simId"] = simId
                jsonobject["orderType"] = "2"
                param["attr"] = jsonobject
                mViewModel.endOrderQR(param)
            }
        }
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            endOrderInfoLiveData.observe(this@OrderInfoActivity) {
                dismissProgressDialog()
                endOrderBean = it
                totalAmount = endOrderBean?.realtimeMoney.toString()
                binding.tvCarLicense.text = endOrderBean?.carLicense
                val strings = arrayOf(endOrderBean?.orderMoney.toString(), "元")
                binding.tvOrderAmount.text = AppUtil.getSpan(strings, sizes, colors, styles)
                binding.tvPaidAmount.text = endOrderBean?.havePayMoney
                binding.rtvPayableAmount.text = endOrderBean?.realtimeMoney
            }
            endOrderQRLiveData.observe(this@OrderInfoActivity){
                paymentQrDialog = PaymentQrDialog(qr, "0")
                paymentQrDialog?.show()
            }
            errMsg.observe(this@OrderInfoActivity) {
                dismissProgressDialog()
                ToastUtil.showMiddleToast(it.msg)
            }
        }
    }

    override fun providerVMClass(): Class<OrderInfoViewModel>? {
        return OrderInfoViewModel::class.java
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityOrderInfoBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override val isFullScreen: Boolean
        get() = true

    override fun marginStatusBarView(): View {
        return binding.layoutToolbar.ablToolbar
    }
}
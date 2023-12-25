package ja.insepector.bxapp.ui.activity.parking

import android.view.View
import android.view.View.OnClickListener
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSONObject
import ja.insepector.base.BaseApplication
import ja.insepector.base.arouter.ARouterMap
import ja.insepector.base.ds.PreferencesDataStore
import ja.insepector.base.ds.PreferencesKeys
import ja.insepector.base.ext.i18N
import ja.insepector.base.util.ToastUtil
import ja.insepector.base.viewbase.VbBaseActivity
import ja.insepector.bxapp.R
import ja.insepector.bxapp.databinding.ActivityPrepaidBinding
import ja.insepector.bxapp.dialog.PaymentQrDialog
import ja.insepector.bxapp.mvvm.viewmodel.PrepaidViewModel
import ja.insepector.common.util.GlideUtils
import kotlinx.coroutines.runBlocking

@Route(path = ARouterMap.PREPAID)
class PrepaidActivity : VbBaseActivity<PrepaidViewModel, ActivityPrepaidBinding>(), OnClickListener {
    val sizes = intArrayOf(24, 19)
    val colors = intArrayOf(ja.insepector.base.R.color.color_ff04a091, ja.insepector.base.R.color.color_ff04a091)
    var timeDuration = 1.0
    var paymentQrDialog: PaymentQrDialog? = null
    var qr = "www.baidu.com"

    var minAmount = 1.0
    var parkingNo = ""
    var carLicense = ""
    var orderNo = ""

    var simId = ""
    var loginName = ""

    override fun initView() {
        binding.layoutToolbar.tvTitle.text = i18N(ja.insepector.base.R.string.预支付)
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, ja.insepector.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), ja.insepector.base.R.color.white))

        minAmount = intent.getDoubleExtra(ARouterMap.PREPAID_MIN_AMOUNT, 1.0)
        carLicense = intent.getStringExtra(ARouterMap.PREPAID_CARLICENSE).toString()
        parkingNo = intent.getStringExtra(ARouterMap.PREPAID_PARKING_NO).toString()
        orderNo = intent.getStringExtra(ARouterMap.PREPAID_ORDER_NO).toString()

        binding.tvPlate.text = carLicense
        binding.tvParkingNo.text = parkingNo?.replace("-", "")
    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
        binding.rflAdd.setOnClickListener(this)
        binding.rflMinus.setOnClickListener(this)
        binding.rflScanPay.setOnClickListener(this)
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
                if (timeDuration == 99.0) {
                    return
                }
                timeDuration += 0.5
                binding.tvTimeDuration.text = timeDuration.toString()
            }

            R.id.rfl_minus -> {
                if (timeDuration == minAmount) {
                    return
                }
                timeDuration -= 0.5
                binding.tvTimeDuration.text = timeDuration.toString()
            }

            R.id.rfl_scanPay -> {
//                TODO(时长)
                val param = HashMap<String, Any>()
                val jsonobject = JSONObject()
                jsonobject["parkingNo"] = parkingNo
                jsonobject["orderNo"] = orderNo
                jsonobject["loginName"] = loginName
                jsonobject["simId"] = simId
                jsonobject["orderType"] = "1"
                param["attr"] = jsonobject
                mViewModel.prePayFeeInquiry(param)
            }
        }
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            prePayFeeInquiryLiveData.observe(this@PrepaidActivity) {
                paymentQrDialog = PaymentQrDialog(qr, it.realtimeMoney)
                paymentQrDialog?.show()
                dismissProgressDialog()
            }
            errMsg.observe(this@PrepaidActivity) {
                dismissProgressDialog()
                ToastUtil.showMiddleToast(it.msg)
            }
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
}
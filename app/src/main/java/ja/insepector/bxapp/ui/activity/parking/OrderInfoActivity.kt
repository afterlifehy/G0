package ja.insepector.bxapp.ui.activity.parking

import android.view.View
import android.view.View.OnClickListener
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import ja.insepector.base.BaseApplication
import ja.insepector.base.arouter.ARouterMap
import ja.insepector.base.bean.EndOrderInfoBean
import ja.insepector.base.ext.i18N
import ja.insepector.base.util.ToastUtil
import ja.insepector.base.viewbase.VbBaseActivity
import ja.insepector.bxapp.R
import ja.insepector.bxapp.databinding.ActivityOrderInfoBinding
import ja.insepector.bxapp.dialog.PaymentQrDialog
import ja.insepector.bxapp.mvvm.viewmodel.OrderInfoViewModel
import ja.insepector.common.event.EndOrderEvent
import ja.insepector.common.util.GlideUtils
import org.greenrobot.eventbus.EventBus

@Route(path = ARouterMap.ORDER_INFO)
class OrderInfoActivity : VbBaseActivity<OrderInfoViewModel, ActivityOrderInfoBinding>(), OnClickListener {
    var paymentQrDialog: PaymentQrDialog? = null
    var qr = ""
    var endOrderBean: EndOrderInfoBean? = null

    override fun initView() {
        binding.layoutToolbar.tvTitle.text = i18N(ja.insepector.base.R.string.订单信息)
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, ja.insepector.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), ja.insepector.base.R.color.white))
    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
        binding.rflAppPay.setOnClickListener(this)
        binding.rflRefusePay.setOnClickListener(this)
        binding.rflScanPay.setOnClickListener(this)
    }

    override fun initData() {
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
                paymentQrDialog = PaymentQrDialog(qr)
                paymentQrDialog?.show()
            }
        }
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            endOrderInfoLiveData.observe(this@OrderInfoActivity) {
                dismissProgressDialog()
                endOrderBean = it
                binding.tvCarLicense.text = endOrderBean?.carLicense
                binding.tvOrderAmount.text = endOrderBean?.orderMoney
                binding.tvPaidAmount.text = endOrderBean?.havePayMoney
                binding.rtvPayableAmount.text = endOrderBean?.realtimeMoney
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
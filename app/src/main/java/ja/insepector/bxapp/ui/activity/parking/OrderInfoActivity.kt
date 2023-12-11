package ja.insepector.bxapp.ui.activity.parking

import android.view.View
import android.view.View.OnClickListener
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import ja.insepector.base.arouter.ARouterMap
import ja.insepector.base.viewbase.VbBaseActivity
import ja.insepector.bxapp.R
import ja.insepector.bxapp.databinding.ActivityOrderInfoBinding
import ja.insepector.bxapp.mvvm.viewmodel.OrderInfoViewModel

@Route(path = ARouterMap.ORDER_INFO)
class OrderInfoActivity : VbBaseActivity<OrderInfoViewModel, ActivityOrderInfoBinding>(), OnClickListener {

    override fun initView() {
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

            }

            R.id.rfl_refusePay -> {

            }

            R.id.rfl_scanPay -> {

            }
        }
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
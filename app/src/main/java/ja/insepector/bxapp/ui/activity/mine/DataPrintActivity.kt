package ja.insepector.bxapp.ui.activity.mine

import android.view.View
import android.view.View.OnClickListener
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import ja.insepector.base.BaseApplication
import ja.insepector.base.arouter.ARouterMap
import ja.insepector.base.ext.i18N
import ja.insepector.base.ext.startArouter
import ja.insepector.base.help.ActivityCacheManager
import ja.insepector.base.viewbase.VbBaseActivity
import ja.insepector.bxapp.R
import ja.insepector.bxapp.databinding.ActivityDataPrintBinding
import ja.insepector.bxapp.mvvm.viewmodel.DataPrintViewModel
import ja.insepector.bxapp.ui.activity.login.LoginActivity
import ja.insepector.common.util.GlideUtils

class DataPrintActivity : VbBaseActivity<DataPrintViewModel, ActivityDataPrintBinding>(), OnClickListener {
    override fun initView() {
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, ja.insepector.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = i18N(ja.insepector.base.R.string.数据打印)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), ja.insepector.base.R.color.white))
    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
        binding.rtvNoPrint.setOnClickListener(this)
        binding.rtvPrint.setOnClickListener(this)
    }

    override fun initData() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back,
            R.id.rtv_noPrint -> {
                for (i in ActivityCacheManager.instance().getAllActivity()) {
                    if (i != LoginActivity::class.java) {
                        i.finish()
                    }
                }
                startArouter(ARouterMap.LOGIN)
            }

            R.id.rtv_print -> {
                for (i in ActivityCacheManager.instance().getAllActivity()) {
                    if (i != LoginActivity::class.java) {
                        i.finish()
                    }
                }
                startArouter(ARouterMap.LOGIN)
            }
        }
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
}
package com.kernal.demo.plateid.ui.activity.abnormal

import android.view.View
import android.view.View.OnClickListener
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.kernal.demo.base.BaseApplication
import com.kernal.demo.base.arouter.ARouterMap
import com.kernal.demo.base.ext.i18N
import com.kernal.demo.base.viewbase.VbBaseActivity
import com.kernal.demo.common.util.GlideUtils
import com.kernal.demo.plateid.R
import com.kernal.demo.plateid.databinding.ActivityAbnormalHelpBinding
import com.kernal.demo.plateid.mvvm.viewmodel.AbnormalHelpViewModel

@Route(path = ARouterMap.ABNORMAL_HELP)
class AbnormalHelpActivity : VbBaseActivity<AbnormalHelpViewModel, ActivityAbnormalHelpBinding>(), OnClickListener {

    override fun initView() {
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, com.kernal.demo.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = i18N(com.kernal.demo.base.R.string.异常上报使用帮助)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.kernal.demo.base.R.color.white))
    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
    }

    override fun initData() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }
        }
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityAbnormalHelpBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override val isFullScreen: Boolean
        get() = true

    override fun marginStatusBarView(): View {
        return binding.layoutToolbar.ablToolbar
    }

    override fun providerVMClass(): Class<AbnormalHelpViewModel>? {
        return AbnormalHelpViewModel::class.java
    }
}
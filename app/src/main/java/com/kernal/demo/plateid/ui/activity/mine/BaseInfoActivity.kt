package com.kernal.demo.plateid.ui.activity.mine

import android.view.View
import android.view.View.OnClickListener
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.kernal.demo.base.BaseApplication
import com.kernal.demo.base.arouter.ARouterMap
import com.kernal.demo.base.bean.Street
import com.kernal.demo.base.ext.i18N
import com.kernal.demo.base.util.ToastUtil
import com.kernal.demo.base.viewbase.VbBaseActivity
import com.kernal.demo.common.realm.RealmUtil
import com.kernal.demo.common.util.GlideUtils
import com.kernal.demo.plateid.R
import com.kernal.demo.plateid.databinding.ActivityBaseInfoBinding
import com.kernal.demo.plateid.mvvm.viewmodel.BaseInfoViewModel

@Route(path = ARouterMap.BASE_INFO)
class BaseInfoActivity : VbBaseActivity<BaseInfoViewModel, ActivityBaseInfoBinding>(), OnClickListener {
    var streetList: MutableList<Street> = ArrayList()
    override fun initView() {
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, com.kernal.demo.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = i18N(com.kernal.demo.base.R.string.基本信息)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.kernal.demo.base.R.color.white))

    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
    }

    override fun initData() {
        streetList = RealmUtil.instance?.findCheckedStreetList() as MutableList<Street>
        for (i in streetList) {
            val street = View.inflate(BaseApplication.instance(), R.layout.item_baseinfo_street, null)
            street.findViewById<TextView>(R.id.tv_street).text = i.streetName
            binding.llStreet.addView(street)
        }
        mViewModel.getBaseInfo()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }
        }
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            baseInfoLiveData.observe(this@BaseInfoActivity) {
                binding.tvName.text = it[0]
                binding.tvAccount.text = it[1]
                binding.tvPhoneNum.text = it[2]
            }
            errMsg.observe(this@BaseInfoActivity){
                dismissProgressDialog()
                ToastUtil.showMiddleToast(it.msg)
            }
            mException.observe(this@BaseInfoActivity){
                dismissProgressDialog()
                ToastUtil.showMiddleToast(it.message)
            }
        }
    }

    override fun providerVMClass(): Class<BaseInfoViewModel>? {
        return BaseInfoViewModel::class.java
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityBaseInfoBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override val isFullScreen: Boolean
        get() = true

    override fun marginStatusBarView(): View? {
        return binding.layoutToolbar.ablToolbar
    }
}
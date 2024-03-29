package com.kernal.demo.plateid.ui.activity.mine

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils
import com.kernal.demo.base.BaseApplication
import com.kernal.demo.base.arouter.ARouterMap
import com.kernal.demo.base.bean.Street
import com.kernal.demo.base.ext.hide
import com.kernal.demo.base.ext.i18N
import com.kernal.demo.base.ext.show
import com.kernal.demo.base.viewbase.VbBaseActivity
import com.kernal.demo.common.realm.RealmUtil
import com.kernal.demo.common.util.GlideUtils
import com.kernal.demo.plateid.R
import com.kernal.demo.plateid.adapter.FeeRatePagerAdapter
import com.kernal.demo.plateid.databinding.ActivityFeeRateBinding
import com.kernal.demo.plateid.mvvm.viewmodel.FeeRateViewModel
import com.kernal.demo.plateid.ui.fragment.FeeRateFragment

@Route(path = ARouterMap.FEE_RATE)
class FeeRateActivity : VbBaseActivity<FeeRateViewModel, ActivityFeeRateBinding>(), OnClickListener {
    var tabList: MutableList<Street> = ArrayList()
    var fragmentList: MutableList<Fragment> = ArrayList()

    override fun initView() {
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, com.kernal.demo.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = i18N(com.kernal.demo.base.R.string.费率列表)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.kernal.demo.base.R.color.white))

    }
    
    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
        binding.ivArrowLeft.setOnClickListener(this)
        binding.ivArrowRight.setOnClickListener(this)
        binding.stlStreet.setOnTouchListener { p0, p1 -> true }
    }

    override fun initData() {
        tabList = RealmUtil.instance?.findCheckedStreetList() as MutableList<Street>
        if (tabList.size == 1) {
            binding.ivArrowRight.hide()
        }
        for (i in tabList) {
            val bundle = Bundle()
            val feeRateFragment = FeeRateFragment()
            bundle.putString("streetNo", i.streetNo)
            feeRateFragment.arguments = bundle
            fragmentList.add(feeRateFragment)
        }
        binding.vpFeeRate.adapter = FeeRatePagerAdapter(this@FeeRateActivity, fragmentList, tabList)
        binding.stlStreet.setViewPager(binding.vpFeeRate)
        binding.stlStreet.currentTab = 0
        binding.vpFeeRate.offscreenPageLimit = 2
        for (i in tabList.indices) {
            val tab = (binding.stlStreet.getChildAt(0) as LinearLayout).getChildAt(i) as RelativeLayout
            val tabTxt = tab.getChildAt(0) as TextView
            tabTxt.isSingleLine = false
            tabTxt.maxLines = 2
            val lp = tab.layoutParams
            lp.width = ScreenUtils.getScreenWidth() - SizeUtils.dp2px(72f)
            (binding.stlStreet.getChildAt(0) as LinearLayout).getChildAt(i).requestLayout()
        }
        binding.vpFeeRate.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (tabList.size > 1) {
                    if (position == 0) {
                        binding.ivArrowLeft.hide()
                        binding.ivArrowRight.show()
                    } else if (position == tabList.size - 1) {
                        binding.ivArrowRight.hide()
                        binding.ivArrowLeft.show()
                    } else {
                        binding.ivArrowRight.show()
                        binding.ivArrowLeft.show()
                    }
                }
            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }

            R.id.iv_arrowLeft -> {
                if (binding.vpFeeRate.currentItem > 0) {
                    binding.ivArrowRight.show()
                    binding.vpFeeRate.setCurrentItem(binding.vpFeeRate.currentItem - 1, true)
                } else {
                    binding.ivArrowLeft.hide()
                }
            }

            R.id.iv_arrowRight -> {
                if (binding.vpFeeRate.currentItem < tabList.size - 1) {
                    binding.ivArrowLeft.show()
                    binding.vpFeeRate.setCurrentItem(binding.vpFeeRate.currentItem + 1, true)
                } else {
                    binding.ivArrowRight.hide()
                }
            }
        }
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityFeeRateBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override val isFullScreen: Boolean
        get() = true

    override fun marginStatusBarView(): View {
        return binding.layoutToolbar.ablToolbar
    }
}
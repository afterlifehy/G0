package com.kernal.demo.plateid.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.kernal.demo.base.bean.Street
import com.kernal.demo.common.view.flycotablayout.adapter.SlidingAdapter

/**
 * Created by huy  on 2023/3/1.
 */
class FeeRatePagerAdapter(
    activity: FragmentActivity,
    val fragmentList: List<Fragment>,
    val tabList: MutableList<Street>
) :
    com.kernal.demo.common.view.flycotablayout.adapter.SlidingAdapter(activity) {

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getPageTitle(position: Int): CharSequence {
        return tabList[position].streetName
    }
}
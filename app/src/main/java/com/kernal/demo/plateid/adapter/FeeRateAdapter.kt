package com.kernal.demo.plateid.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils
import com.kernal.demo.base.adapter.BaseBindingAdapter
import com.kernal.demo.base.adapter.VBViewHolder
import com.kernal.demo.base.bean.FeeRateBean
import com.kernal.demo.base.ext.i18n
import com.kernal.demo.plateid.databinding.ItemFeeRateBinding

class FeeRateAdapter(data: MutableList<FeeRateBean>? = null) : BaseBindingAdapter<FeeRateBean, ItemFeeRateBinding>(data) {
    @SuppressLint("SetTextI18n")
    override fun convert(holder: VBViewHolder<ItemFeeRateBinding>, item: FeeRateBean) {
        when (item.dateType) {
            1 -> {
                holder.vb.tvTitle.text = i18n(com.kernal.demo.base.R.string.工作日标准)
            }

            2 -> {
                holder.vb.tvTitle.text = i18n(com.kernal.demo.base.R.string.周末标准)
            }

            3 -> {
                holder.vb.tvTitle.text = i18n(com.kernal.demo.base.R.string.节假日标准)
            }
        }
        holder.vb.rtvDayTimeRange.text = "${item.whiteStart}至${item.whiteEnd}"
        holder.vb.tvStartAmount.text = "${item.firstHourMoney}元"
        holder.vb.tvNextAmount.text = "${item.unitPrice}元"
        holder.vb.tvNightTimeRange.text = "${item.blackStart}至${item.blackEnd},${item.period}元/次"
    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemFeeRateBinding {
        val binding = ItemFeeRateBinding.inflate(inflater)
        binding.rllFeeRate.layoutParams.width = ScreenUtils.getScreenWidth() - SizeUtils.dp2px(26f)
        binding.rllFeeRate.requestLayout()
        return binding
    }
}
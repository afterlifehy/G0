package com.kernal.demo.plateid.adapter

import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import com.kernal.demo.base.adapter.BaseBindingAdapter
import com.kernal.demo.base.adapter.VBViewHolder
import com.kernal.demo.base.bean.DebtCollectionBean
import com.kernal.demo.common.util.AppUtil
import com.kernal.demo.plateid.databinding.ItemDebtCollectionBinding
import com.zrq.spanbuilder.TextStyle

class DebtCollectionAdapter(data: MutableList<DebtCollectionBean>? = null, val onClickListener: OnClickListener) :
    BaseBindingAdapter<DebtCollectionBean, ItemDebtCollectionBinding>(data) {
    val colors = intArrayOf(com.kernal.demo.base.R.color.color_ffe92404, com.kernal.demo.base.R.color.color_ffe92404, com.kernal.demo.base.R.color.color_ffe92404)
    val sizes = intArrayOf(16, 20, 16)
    val styles = arrayOf(TextStyle.NORMAL, TextStyle.BOLD, TextStyle.NORMAL)
    val colors2 = intArrayOf(com.kernal.demo.base.R.color.color_ff666666, com.kernal.demo.base.R.color.color_ff1a1a1a)
    val sizes2 = intArrayOf(19, 19)

    override fun convert(holder: VBViewHolder<ItemDebtCollectionBinding>, item: DebtCollectionBean) {
        holder.vb.tvNum.text = AppUtil.fillZero((data.indexOf(item) + 1).toString())
        holder.vb.tvLicensePlate.text = item.carLicense
        val strings = arrayOf("未付：", "${AppUtil.keepNDecimal(item.oweMoney / 100.00,2)}", "元")
        holder.vb.tvAmount.text = AppUtil.getSpan(strings, sizes, colors, styles)
        val strings2 = arrayOf("入场：", item.startTime)
        holder.vb.tvStartTime.text = AppUtil.getSpan(strings2, sizes2, colors2)
        val strings3 = arrayOf("出场：", item.endTime)
        holder.vb.tvEndTime.text = AppUtil.getSpan(strings3, sizes2, colors2)
        holder.vb.tvAddress.text = item.streetName
        holder.vb.tvNo.text = item.parkingNo

        holder.vb.rrlDebtCollection.tag = item
        holder.vb.rrlDebtCollection.setOnClickListener(onClickListener)
    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemDebtCollectionBinding {
        return ItemDebtCollectionBinding.inflate(inflater)
    }
}
package com.kernal.demo.plateid.adapter

import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import com.kernal.demo.base.adapter.BaseBindingAdapter
import com.kernal.demo.base.adapter.VBViewHolder
import com.kernal.demo.base.bean.OrderBean
import com.kernal.demo.base.ext.i18n
import com.kernal.demo.common.util.AppUtil
import com.kernal.demo.common.util.BigDecimalManager
import com.kernal.demo.plateid.databinding.ItemOrderBinding
import com.zrq.spanbuilder.TextStyle
import com.kernal.demo.base.ext.gone
import com.kernal.demo.base.ext.show

class OrderInquiryAdapter(data: MutableList<OrderBean>? = null, val onClickListener: OnClickListener) :
    BaseBindingAdapter<OrderBean, ItemOrderBinding>(data) {
    val colorsBlue = intArrayOf(
        com.kernal.demo.base.R.color.color_ff04a091,
        com.kernal.demo.base.R.color.color_ff04a091,
        com.kernal.demo.base.R.color.color_ff04a091
    )
    val colorsRed = intArrayOf(
        com.kernal.demo.base.R.color.color_ffe92404,
        com.kernal.demo.base.R.color.color_ffe92404,
        com.kernal.demo.base.R.color.color_ffe92404
    )
    val sizes = intArrayOf(16, 20, 16)
    val styles = arrayOf(TextStyle.NORMAL, TextStyle.BOLD, TextStyle.NORMAL)
    val colors2 = intArrayOf(com.kernal.demo.base.R.color.color_ff666666, com.kernal.demo.base.R.color.color_ff1a1a1a)
    val sizes2 = intArrayOf(19, 19)
    var orderList: MutableList<OrderBean> = ArrayList()

    override fun convert(holder: VBViewHolder<ItemOrderBinding>, item: OrderBean) {
        holder.vb.tvNum.text = AppUtil.fillZero((data.indexOf(item) + 1).toString())
        holder.vb.tvLicensePlate.text = item.carLicense
        if (item.paidAmount.toDouble() > 0.0 || (item.paidAmount.toDouble() == 0.0 && item.amount.toDouble() == 0.0)) {
            holder.vb.cbOrder.gone()
            val strings = arrayOf(
                i18n(com.kernal.demo.base.R.string.已付),
                AppUtil.keepNDecimals(item.paidAmount, 2),
                i18n(com.kernal.demo.base.R.string.元)
            )
            holder.vb.tvAmount.text = AppUtil.getSpan(strings, sizes, colorsBlue, styles)
        } else {
            if (item.isPrinted == "0") {
                holder.vb.cbOrder.show()
            } else {
                holder.vb.cbOrder.gone()
            }
            val strings = arrayOf(
                i18n(com.kernal.demo.base.R.string.欠),
                AppUtil.keepNDecimals(BigDecimalManager.subtractionDoubleToString(item.amount.toDouble(), item.paidAmount.toDouble()), 2),
                i18n(com.kernal.demo.base.R.string.元)
            )
            holder.vb.tvAmount.text = AppUtil.getSpan(strings, sizes, colorsRed, styles)
        }

        val strings2 = arrayOf(i18n(com.kernal.demo.base.R.string.入场) + ":", item.startTime)
        holder.vb.tvStartTime.text = AppUtil.getSpan(strings2, sizes2, colors2)
        val strings3 = arrayOf(i18n(com.kernal.demo.base.R.string.出场) + ":", item.endTime)
        holder.vb.tvEndTime.text = AppUtil.getSpan(strings3, sizes2, colors2)
        holder.vb.tvNo.text = item.parkingNo

        holder.vb.cbOrder.isChecked = false
        holder.vb.cbOrder.tag = item
        holder.vb.cbOrder.setOnCheckedChangeListener(object : OnCheckedChangeListener {
            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                if (p1) {
                    orderList.add(item)
                } else {
                    orderList.remove(item)
                }
            }
        })
        holder.vb.flOrder.tag = item
        holder.vb.flOrder.setOnClickListener(onClickListener)
    }


    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemOrderBinding {
        return ItemOrderBinding.inflate(inflater)
    }

    fun getUploadOrderList(): MutableList<OrderBean> {
        return orderList
    }

    fun clearUploadOrderList() {
        orderList.clear()
    }
}
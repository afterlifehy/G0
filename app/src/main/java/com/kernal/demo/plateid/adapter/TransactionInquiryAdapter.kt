package com.kernal.demo.plateid.adapter

import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import com.kernal.demo.base.adapter.BaseBindingAdapter
import com.kernal.demo.base.adapter.VBViewHolder
import com.kernal.demo.base.bean.TransactionBean
import com.kernal.demo.base.ext.gone
import com.kernal.demo.base.ext.show
import com.kernal.demo.common.util.AppUtil
import com.kernal.demo.plateid.databinding.ItemTransactionQueryBinding
import com.zrq.spanbuilder.TextStyle

class TransactionInquiryAdapter(data: MutableList<TransactionBean>? = null, val onClickListener: OnClickListener) :
    BaseBindingAdapter<TransactionBean, ItemTransactionQueryBinding>(data) {
    val colors = intArrayOf(com.kernal.demo.base.R.color.color_ff04a091, com.kernal.demo.base.R.color.color_ff04a091, com.kernal.demo.base.R.color.color_ff04a091)
    val colors2 = intArrayOf(com.kernal.demo.base.R.color.color_ffe92404, com.kernal.demo.base.R.color.color_ffe92404, com.kernal.demo.base.R.color.color_ffe92404)
    val sizes = intArrayOf(16, 20, 16)
    val styles = arrayOf(TextStyle.NORMAL, TextStyle.BOLD, TextStyle.NORMAL)

    override fun convert(holder: VBViewHolder<ItemTransactionQueryBinding>, item: TransactionBean) {
        holder.vb.tvNum.text = AppUtil.fillZero((data.indexOf(item) + 1).toString())
        holder.vb.tvLicensePlate.text = item.carLicense
        holder.vb.tvStartTime.text = item.startTime
        holder.vb.tvEndTime.text = item.endTime
        holder.vb.tvNo.text = item.parkingNo
        if (item.hasPayed == "1") {
            val strings = arrayOf("已付：", item.payedAmount, "元")
            holder.vb.tvAmount.text = AppUtil.getSpan(strings, sizes, colors, styles)
            holder.vb.flNotification.show()
            holder.vb.flPaymentInquiry.gone()
            holder.vb.flNotification.tag = item
            holder.vb.flNotification.setOnClickListener(onClickListener)
        } else {
            val strings = arrayOf("未付：", item.oweMoney, "元")
            holder.vb.tvAmount.text = AppUtil.getSpan(strings, sizes, colors2, styles)
            holder.vb.flNotification.gone()
            holder.vb.flPaymentInquiry.show()
            holder.vb.flPaymentInquiry.tag = item
            holder.vb.flPaymentInquiry.setOnClickListener(onClickListener)
        }
    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemTransactionQueryBinding {
        return ItemTransactionQueryBinding.inflate(inflater)
    }
}
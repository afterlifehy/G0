package com.kernal.demo.plateid.adapter

import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.aries.ui.view.radius.RadiusTextView
import com.blankj.utilcode.util.ClickUtils
import com.kernal.demo.base.BaseApplication
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
    val colors = intArrayOf(
        com.kernal.demo.base.R.color.color_ff04a091,
        com.kernal.demo.base.R.color.color_ff04a091,
        com.kernal.demo.base.R.color.color_ff04a091
    )
    val colors2 = intArrayOf(
        com.kernal.demo.base.R.color.color_ffe92404,
        com.kernal.demo.base.R.color.color_ffe92404,
        com.kernal.demo.base.R.color.color_ffe92404
    )
    val sizes = intArrayOf(16, 20, 16)
    val styles = arrayOf(TextStyle.NORMAL, TextStyle.BOLD, TextStyle.NORMAL)

    override fun convert(holder: VBViewHolder<ItemTransactionQueryBinding>, item: TransactionBean) {
        holder.vb.tvNum.text = AppUtil.fillZero((data.indexOf(item) + 1).toString())
        holder.vb.tvLicensePlate.text = item.carLicense
        holder.vb.tvStartTime.text = item.startTime
        holder.vb.tvEndTime.text = item.endTime
        holder.vb.tvNo.text = item.parkingNo
        holder.vb.viewLiner.show()
        if (item.hasPayed == "1") {
            if (item.refundMoney > 0.00) {
                val strings = arrayOf("退款：", AppUtil.keepNDecimal(item.refundMoney, 2), "元")
                holder.vb.tvAmount.text = AppUtil.getSpan(strings, sizes, colors2, styles)
                holder.vb.flNotification.gone()
                holder.vb.flPaymentInquiry.gone()
                holder.vb.viewLiner.gone()
                holder.vb.flPaymentInquiry.tag = item
                holder.vb.flPaymentInquiry.setOnClickListener(null)
            } else {
                val strings = arrayOf("已付：", item.payedAmount, "元")
                holder.vb.tvAmount.text = AppUtil.getSpan(strings, sizes, colors, styles)
                holder.vb.flNotification.show()
                holder.vb.flPaymentInquiry.gone()
                holder.vb.flNotification.tag = item
                ClickUtils.applySingleDebouncing(holder.vb.flNotification, 3000, onClickListener)
            }
        } else {
            val strings = arrayOf("未付：", item.oweMoney, "元")
            holder.vb.tvAmount.text = AppUtil.getSpan(strings, sizes, colors2, styles)
            holder.vb.flNotification.gone()
            holder.vb.flPaymentInquiry.show()
            holder.vb.flPaymentInquiry.tag = item
            ClickUtils.applySingleDebouncing(holder.vb.flPaymentInquiry, 3000, onClickListener)
        }
        when (item.orderType) {
            "1" -> {
                showOrderType(
                    holder.vb.rtvOrderType,
                    "预付费",
                    com.kernal.demo.base.R.color.color_ffd6b25a,
                    com.kernal.demo.base.R.color.color_fffef3d5
                )
            }

            "2" -> {
                showOrderType(
                    holder.vb.rtvOrderType,
                    "补缴费",
                    com.kernal.demo.base.R.color.color_ffd6b25a,
                    com.kernal.demo.base.R.color.color_fffef3d5
                )
            }

            "3" -> {
                showOrderType(
                    holder.vb.rtvOrderType,
                    "欠费追缴",
                    com.kernal.demo.base.R.color.color_ffd6b25a,
                    com.kernal.demo.base.R.color.color_fffef3d5
                )
            }

            else -> {
                holder.vb.rtvOrderType.gone()
            }
        }
    }

    fun showOrderType(rtvOrderType: RadiusTextView, content: String, color1: Int, color2: Int) {
        rtvOrderType.show()
        rtvOrderType.text = content
        rtvOrderType.delegate.setTextColor(
            ContextCompat.getColor(
                BaseApplication.instance(),
                color1
            )
        )
        rtvOrderType.delegate.setBackgroundColor(
            ContextCompat.getColor(
                BaseApplication.instance(),
                color2
            )
        )
    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemTransactionQueryBinding {
        return ItemTransactionQueryBinding.inflate(inflater)
    }
}
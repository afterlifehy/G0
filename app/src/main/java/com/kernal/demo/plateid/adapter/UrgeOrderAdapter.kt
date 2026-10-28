package com.kernal.demo.plateid.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.blankj.utilcode.util.TimeUtils
import com.kernal.demo.base.adapter.BaseBindingAdapter
import com.kernal.demo.base.adapter.VBViewHolder
import com.kernal.demo.base.bean.UrgeOrderBean
import com.kernal.demo.base.util.AppUtil
import com.kernal.demo.plateid.databinding.ItemUrgeOrderBinding

class UrgeOrderAdapter(data: MutableList<UrgeOrderBean>? = null, val onclick: (order: UrgeOrderBean) -> Unit) :
    BaseBindingAdapter<UrgeOrderBean, ItemUrgeOrderBinding>(data) {
    override fun convert(holder: VBViewHolder<ItemUrgeOrderBinding>, item: UrgeOrderBean) {
        holder.vb.tvNum.text = AppUtil.fillZero((data.indexOf(item) + 1).toString())
        holder.vb.tvOrderNo.text = item.orderId
        holder.vb.tvStartTime.text = TimeUtils.millis2String(item.arrivedTime, "yyyy-MM-dd HH:mm:ss")
        holder.vb.tvEndTime.text = TimeUtils.millis2String(item.leftTime, "yyyy-MM-dd HH:mm:ss")
        holder.vb.tvStreet.text = "${item.roadId}(${item.roadName})"
        holder.vb.tvOweAmount.text = "欠费：${AppUtil.keepNDecimal(item.oweMoney / 100.0, 2)}元"
    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemUrgeOrderBinding {
        return ItemUrgeOrderBinding.inflate(inflater)
    }
}
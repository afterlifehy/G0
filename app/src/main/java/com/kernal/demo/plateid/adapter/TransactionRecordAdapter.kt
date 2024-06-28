package com.kernal.demo.plateid.adapter

import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import com.kernal.demo.base.adapter.BaseBindingAdapter
import com.kernal.demo.base.adapter.VBViewHolder
import com.kernal.demo.base.bean.PayResultBean
import com.kernal.demo.plateid.databinding.ItemTransactionRecordBinding

class TransactionRecordAdapter(data: MutableList<PayResultBean>? = null, val onClickListener: OnClickListener) :
    BaseBindingAdapter<PayResultBean, ItemTransactionRecordBinding>(data) {
    override fun convert(holder: VBViewHolder<ItemTransactionRecordBinding>, item: PayResultBean) {
        holder.vb.tvOrderNo.text = item.tradeNo
        holder.vb.tvAmount.text = "${item.payMoney}元"
        holder.vb.tvStartTime.text = item.startTime
        holder.vb.tvEndTime.text = item.endTime
        holder.vb.flNotification.tag = item
        holder.vb.flNotification.setOnClickListener(onClickListener)
    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemTransactionRecordBinding {
        return ItemTransactionRecordBinding.inflate(inflater)
    }
}
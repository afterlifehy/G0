package com.kernal.demo.plateid.adapter

import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import com.blankj.utilcode.util.TimeUtils
import com.kernal.demo.base.adapter.BaseBindingAdapter
import com.kernal.demo.base.adapter.VBViewHolder
import com.kernal.demo.base.bean.UrgeBean
import com.kernal.demo.base.util.AppUtil
import com.kernal.demo.plateid.databinding.ItemUrgeBinding

class UrgeAdapter(data: MutableList<UrgeBean>? = null, val onClickListener: OnClickListener) :
    BaseBindingAdapter<UrgeBean, ItemUrgeBinding>(data) {
    var carLicense = ""

    override fun convert(holder: VBViewHolder<ItemUrgeBinding>, item: UrgeBean) {
        holder.vb.tvNum.text = AppUtil.fillZero((data.indexOf(item) + 1).toString())
        holder.vb.tvLicensePlate.text = carLicense
        holder.vb.tvOweAmount.text = "欠费金额：${AppUtil.keepNDecimals((item.oweAmount / 100).toString(), 2)}元"
        holder.vb.tvPrintCount.text = "打印次数：${item.printCount}"
        holder.vb.tvUrgeCount.text = "催缴次数：${item.urgeCount}"
        holder.vb.tvTime.text = "告知书生成时间：${TimeUtils.millis2String(item.dataTime, "yyyy-MM-dd HH:mm:ss")}"

        holder.vb.rrlUrge.tag = item
        holder.vb.rrlUrge.setOnClickListener(onClickListener)
    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemUrgeBinding {
        return ItemUrgeBinding.inflate(inflater)
    }

    fun updateCarLicense(carLicense: String) {
        this.carLicense = carLicense
    }
}
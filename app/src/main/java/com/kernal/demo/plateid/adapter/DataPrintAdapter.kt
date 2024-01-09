package com.kernal.demo.plateid.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.kernal.demo.base.adapter.BaseBindingAdapter
import com.kernal.demo.base.adapter.VBViewHolder
import com.kernal.demo.base.bean.DataPrintBean
import com.kernal.demo.plateid.databinding.ItemDataPrintBinding

class DataPrintAdapter(data: MutableList<DataPrintBean>? = null) :
    BaseBindingAdapter<DataPrintBean, ItemDataPrintBinding>(data) {

    override fun convert(holder: VBViewHolder<ItemDataPrintBinding>, item: DataPrintBean) {
        holder.vb.tvStreet.text = item.receipt
        holder.vb.rlStreet.setOnClickListener {
            holder.vb.cbStreet.isChecked = !item.ischeck
            item.ischeck = holder.vb.cbStreet.isChecked
        }
        holder.vb.cbStreet.setOnClickListener {
            if (holder.vb.cbStreet.isChecked) {
                item.ischeck = true
            } else {
                item.ischeck = false
            }
        }
    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemDataPrintBinding {
        return ItemDataPrintBinding.inflate(inflater)
    }

}
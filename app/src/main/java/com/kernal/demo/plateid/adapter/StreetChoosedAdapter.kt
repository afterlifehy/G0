package com.kernal.demo.plateid.adapter

import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import com.kernal.demo.base.adapter.BaseBindingAdapter
import com.kernal.demo.base.adapter.VBViewHolder
import com.kernal.demo.base.bean.Street
import com.kernal.demo.plateid.databinding.ItemStreetChoosedBinding

class StreetChoosedAdapter(var streetChoosedList: MutableList<Street>, val onClickListener: OnClickListener) :
    BaseBindingAdapter<Street, ItemStreetChoosedBinding>() {

    override fun convert(holder: VBViewHolder<ItemStreetChoosedBinding>, item: Street) {
        holder.vb.tvStreet.text = item.streetName
        holder.vb.rflDelete.tag = item
        holder.vb.rflDelete.setOnClickListener(onClickListener)
    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemStreetChoosedBinding {
        return ItemStreetChoosedBinding.inflate(inflater)
    }
}
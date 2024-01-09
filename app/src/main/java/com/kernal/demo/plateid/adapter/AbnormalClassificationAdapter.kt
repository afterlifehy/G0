package com.kernal.demo.plateid.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import com.kernal.demo.plateid.databinding.ItemParkingChooseStreetBinding
import com.kernal.demo.base.adapter.BaseBindingAdapter
import com.kernal.demo.base.adapter.VBViewHolder

class AbnormalClassificationAdapter(data: MutableList<String>? = null, var currentClassification: String, val callback: ChooseClassificationAdapterCallBack) :
    BaseBindingAdapter<String, ItemParkingChooseStreetBinding>(data) {
    var lastClassificationCB: CheckBox? = null
    var currentClassificationCB: CheckBox? = null
    override fun convert(holder: VBViewHolder<ItemParkingChooseStreetBinding>, item: String) {
        holder.vb.tvStreet.text = item
        if (currentClassification == item) {
            holder.vb.cbStreet.isChecked = true
            currentClassificationCB = holder.vb.cbStreet
        }
        holder.vb.rlStreet.setOnClickListener {
            lastClassificationCB = currentClassificationCB
            lastClassificationCB?.isChecked = false
            currentClassificationCB = holder.vb.cbStreet
            currentClassificationCB?.isChecked = true
            currentClassification = item
            callback.chooseClassification(currentClassification)
        }
        holder.vb.cbStreet.setOnClickListener {
            lastClassificationCB = currentClassificationCB
            lastClassificationCB?.isChecked = false
            currentClassificationCB = holder.vb.cbStreet
            currentClassificationCB?.isChecked = true
            currentClassification = item
            callback.chooseClassification(currentClassification)
        }
    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemParkingChooseStreetBinding {
        return ItemParkingChooseStreetBinding.inflate(inflater)
    }

    interface ChooseClassificationAdapterCallBack {
        fun chooseClassification(classification: String)
    }
}
package com.kernal.demo.plateid.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import com.kernal.demo.base.adapter.BaseBindingAdapter
import com.kernal.demo.base.adapter.VBViewHolder
import com.kernal.demo.base.bean.Street
import com.kernal.demo.plateid.databinding.ItemParkingChooseStreetBinding

class ParkingChooseStreetAdapter(data: MutableList<Street>? = null, var currentStreet: Street, val callback: ChooseStreetAdapterCallBack) :
    BaseBindingAdapter<Street, ItemParkingChooseStreetBinding>(data) {
    var lastStreetCB: CheckBox? = null
    var currentStreetCB: CheckBox? = null
    override fun convert(holder: VBViewHolder<ItemParkingChooseStreetBinding>, item: Street) {
        holder.vb.tvStreet.text = item.streetName
        if (currentStreet.streetNo == item.streetNo) {
            holder.vb.cbStreet.isChecked = true
            currentStreetCB = holder.vb.cbStreet
        }else{
            holder.vb.cbStreet.isChecked = false
        }
        holder.vb.rlStreet.setOnClickListener {
            lastStreetCB = currentStreetCB
            lastStreetCB?.isChecked = false
            currentStreetCB = holder.vb.cbStreet
            currentStreetCB?.isChecked = true
            currentStreet = item
            callback.chooseStreet(currentStreet)
        }
        holder.vb.cbStreet.setOnClickListener {
            lastStreetCB = currentStreetCB
            lastStreetCB?.isChecked = false
            currentStreetCB = holder.vb.cbStreet
            currentStreetCB?.isChecked = true
            currentStreet = item
            callback.chooseStreet(currentStreet)
        }
    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemParkingChooseStreetBinding {
        return ItemParkingChooseStreetBinding.inflate(inflater)
    }

    interface ChooseStreetAdapterCallBack {
        fun chooseStreet(street: Street)
    }
}
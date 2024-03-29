package com.kernal.demo.plateid.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import com.blankj.utilcode.util.SizeUtils
import com.kernal.demo.base.adapter.BaseBindingAdapter
import com.kernal.demo.base.adapter.VBViewHolder
import com.kernal.demo.plateid.databinding.ItemChooseStreetBinding
import com.kernal.demo.base.bean.Street
import com.kernal.demo.base.ext.hide
import com.kernal.demo.base.ext.show
import com.kernal.demo.base.util.ToastUtil

class ChooseStreetAdapter(data: MutableList<Street>? = null, var streetChoosedList: MutableList<Street>) :
    BaseBindingAdapter<Street, ItemChooseStreetBinding>(data) {

    override fun convert(holder: VBViewHolder<ItemChooseStreetBinding>, item: Street) {
        if (item.streetName.isNotEmpty()) {
            holder.vb.rlStreet.show()
            holder.vb.tvStreet.text = item.streetName
            if (streetChoosedList.contains(item)) {
                holder.vb.cbStreet.isChecked = true
            } else {
                holder.vb.cbStreet.isChecked = false
            }

            holder.vb.rlStreet.setOnClickListener {
                holder.vb.cbStreet.isChecked = !item.ischeck
                item.ischeck = holder.vb.cbStreet.isChecked
                if (holder.vb.cbStreet.isChecked) {
                    if (streetChoosedList.size == 5) {
                        holder.vb.cbStreet.isChecked = false
                        ToastUtil.showMiddleToast("最多选择5条路段")
                    } else {
                        streetChoosedList.add(item)
                    }
                } else {
                    streetChoosedList.remove(item)
                }
            }
            holder.vb.cbStreet.setOnClickListener {
                if (holder.vb.cbStreet.isChecked) {
                    item.ischeck = true
                    if (streetChoosedList.size == 5) {
                        holder.vb.cbStreet.isChecked = false
                        ToastUtil.showMiddleToast("最多选择5条路段")
                    } else {
                        streetChoosedList.add(item)
                    }
                } else {
                    item.ischeck = false
                    streetChoosedList.remove(item)
                }
            }
        } else {
            holder.vb.rlStreet.hide()
        }
    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemChooseStreetBinding {
        val lp = ViewGroup.MarginLayoutParams(WindowManager.LayoutParams.MATCH_PARENT, SizeUtils.dp2px(55f))
        val binding = ItemChooseStreetBinding.inflate(inflater)
        binding.root.layoutParams = lp
        return binding
    }

//    fun getCheckedList(): MutableList<Int> {
//        return checkedList
//    }
}
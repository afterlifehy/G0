package com.kernal.demo.plateid.adapter

import android.util.ArrayMap
import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils
import com.kernal.demo.base.adapter.BaseBindingAdapter
import com.kernal.demo.base.adapter.VBViewHolder
import com.kernal.demo.base.ext.hide
import com.kernal.demo.base.ext.show
import com.kernal.demo.common.util.GlideUtils
import com.kernal.demo.plateid.databinding.ItemCollectionPlateColorBinding
import com.kernal.demo.common.util.Constant

class CollectionPlateColorAdapter(val widthType: Int, data: MutableList<String>? = null, val onClickListener: OnClickListener) :
    BaseBindingAdapter<String, ItemCollectionPlateColorBinding>(data) {
    var lastColorPosition = 0
    var checkedColorPosition = 0
    var checkedColor = ""
    var collectioPlateColorMap: MutableMap<String, Int> = ArrayMap()

    init {
        collectioPlateColorMap[Constant.BLUE] = com.kernal.demo.common.R.mipmap.ic_plate_blue
        collectioPlateColorMap[Constant.GREEN] = com.kernal.demo.common.R.mipmap.ic_plate_green
        collectioPlateColorMap[Constant.YELLOW] = com.kernal.demo.common.R.mipmap.ic_plate_yellow
        collectioPlateColorMap[Constant.YELLOW_GREEN] = com.kernal.demo.common.R.mipmap.ic_plate_yellow_green
        collectioPlateColorMap[Constant.WHITE] = com.kernal.demo.common.R.mipmap.ic_plate_white
        collectioPlateColorMap[Constant.BLACK] = com.kernal.demo.common.R.mipmap.ic_plate_black
        collectioPlateColorMap[Constant.OTHERS] = com.kernal.demo.common.R.mipmap.ic_plate_other
    }

    override fun convert(holder: VBViewHolder<ItemCollectionPlateColorBinding>, item: String) {
        GlideUtils.instance?.loadImage(holder.vb.ivColor, collectioPlateColorMap[item]!!)
        if (checkedColor == item) {
            holder.vb.rflStroke.show()
            holder.vb.ivHook.show()
        } else {
            holder.vb.rflStroke.hide()
            holder.vb.ivHook.hide()
        }

        holder.vb.flColor.tag = item
        holder.vb.flColor.setOnClickListener(onClickListener)
    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemCollectionPlateColorBinding {
        val binding = ItemCollectionPlateColorBinding.inflate(inflater)
        var lp = binding.root.layoutParams
        var width = 0
        if (widthType == 1) {
            width = (ScreenUtils.getScreenWidth() - SizeUtils.dp2px(30f)) / 7
        } else if (widthType == 2) {
            width = (ScreenUtils.getScreenWidth() - SizeUtils.dp2px(52f)) / 7
        } else if (widthType == 3) {
            width = (ScreenUtils.getScreenWidth() - SizeUtils.dp2px(38f)) / 7
        }

        if (lp == null) {
            lp = ViewGroup.LayoutParams(width, width)
        } else {
            lp.width = width
            lp.height = width
        }
        binding.root.layoutParams = lp
        return binding
    }

    fun updateColor(color: String, position: Int) {
        lastColorPosition = checkedColorPosition
        checkedColorPosition = position
        checkedColor = color
        notifyItemChanged(lastColorPosition)
        notifyItemChanged(checkedColorPosition)
    }
}
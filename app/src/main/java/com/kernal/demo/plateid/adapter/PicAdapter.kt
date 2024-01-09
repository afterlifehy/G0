package com.kernal.demo.plateid.adapter

import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import com.blankj.utilcode.util.SizeUtils
import com.kernal.demo.base.adapter.BaseBindingAdapter
import com.kernal.demo.base.adapter.VBViewHolder
import com.kernal.demo.plateid.databinding.ItemPicBinding
import com.kernal.demo.common.util.GlideUtils

class PicAdapter(data: MutableList<String>? = null, val onClickListener: OnClickListener) :
    BaseBindingAdapter<String, ItemPicBinding>(data) {
    override fun convert(holder: VBViewHolder<ItemPicBinding>, item: String) {
        val lp = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        lp.bottomMargin = SizeUtils.dp2px(20f)
        holder.vb.rivPic.layoutParams = lp

        GlideUtils.instance?.loadImage(holder.vb.rivPic, item, com.kernal.demo.common.R.mipmap.ic_placeholder_2)
        holder.vb.rivPic.tag = item
        holder.vb.rivPic.setOnClickListener(onClickListener)
    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemPicBinding {
        return ItemPicBinding.inflate(inflater)
    }
}
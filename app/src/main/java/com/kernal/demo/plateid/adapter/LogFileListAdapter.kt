package com.kernal.demo.plateid.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.kernal.demo.base.adapter.BaseBindingAdapter
import com.kernal.demo.base.adapter.VBViewHolder
import com.kernal.demo.plateid.databinding.ItemLogFileBinding
import java.io.File

class LogFileListAdapter(data: MutableList<File>? = null, val listener: (file: File) -> Unit) :
    BaseBindingAdapter<File, ItemLogFileBinding>(data) {
    override fun convert(holder: VBViewHolder<ItemLogFileBinding>, item: File) {
        holder.vb.tvLogFile.text = item.name
        holder.vb.tvLogFile.setOnClickListener {
            listener(item)
        }
    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemLogFileBinding {
        return ItemLogFileBinding.inflate(inflater)
    }
}
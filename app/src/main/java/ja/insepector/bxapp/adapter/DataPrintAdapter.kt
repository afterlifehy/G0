package ja.insepector.bxapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import ja.insepector.base.adapter.BaseBindingAdapter
import ja.insepector.base.adapter.VBViewHolder
import ja.insepector.base.bean.DataPrintBean
import ja.insepector.bxapp.databinding.ItemDataPrintBinding

class DataPrintAdapter(data: MutableList<DataPrintBean>? = null, var dataPrintCheckedList: MutableList<DataPrintBean>) :
    BaseBindingAdapter<DataPrintBean, ItemDataPrintBinding>(data) {

    override fun convert(holder: VBViewHolder<ItemDataPrintBinding>, item: DataPrintBean) {
        holder.vb.tvStreet.text = item.receipt
        if (dataPrintCheckedList.contains(item)) {
            holder.vb.cbStreet.isChecked = true
        } else {
            holder.vb.cbStreet.isChecked = false
        }
        holder.vb.rlStreet.setOnClickListener {
            holder.vb.cbStreet.isChecked = !item.ischeck
            item.ischeck = holder.vb.cbStreet.isChecked
            if (holder.vb.cbStreet.isChecked) {
                dataPrintCheckedList.add(item)
            } else {
                dataPrintCheckedList.remove(item)
            }
        }
        holder.vb.cbStreet.setOnClickListener {
            if (holder.vb.cbStreet.isChecked) {
                dataPrintCheckedList.add(item)
            } else {
                dataPrintCheckedList.remove(item)
            }
        }
    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemDataPrintBinding {
        return ItemDataPrintBinding.inflate(inflater)
    }

}
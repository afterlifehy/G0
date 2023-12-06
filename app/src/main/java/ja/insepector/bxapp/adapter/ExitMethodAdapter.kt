package ja.insepector.bxapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import ja.insepector.base.adapter.BaseBindingAdapter
import ja.insepector.base.adapter.VBViewHolder
import ja.insepector.bxapp.databinding.ItemParkingChooseStreetBinding

class ExitMethodAdapter(data: MutableList<String>? = null, var currentMethod: String, val callback: ChooseExitMethodAdapterCallBack) :
    BaseBindingAdapter<String, ItemParkingChooseStreetBinding>(data) {
    var lastClassificationCB: CheckBox? = null
    var currentClassificationCB: CheckBox? = null
    override fun convert(holder: VBViewHolder<ItemParkingChooseStreetBinding>, item: String) {
        holder.vb.tvStreet.text = item
        if (currentMethod == item) {
            holder.vb.cbStreet.isChecked = true
            currentClassificationCB = holder.vb.cbStreet
        }
        holder.vb.rlStreet.setOnClickListener {
            lastClassificationCB = currentClassificationCB
            lastClassificationCB?.isChecked = false
            currentClassificationCB = holder.vb.cbStreet
            currentClassificationCB?.isChecked = true
            currentMethod = item
            callback.chooseExitMethod(currentMethod)
        }
        holder.vb.cbStreet.setOnClickListener {
            lastClassificationCB = currentClassificationCB
            lastClassificationCB?.isChecked = false
            currentClassificationCB = holder.vb.cbStreet
            currentClassificationCB?.isChecked = true
            currentMethod = item
            callback.chooseExitMethod(currentMethod)
        }
    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemParkingChooseStreetBinding {
        return ItemParkingChooseStreetBinding.inflate(inflater)
    }

    interface ChooseExitMethodAdapterCallBack {
        fun chooseExitMethod(method: String)
    }
}
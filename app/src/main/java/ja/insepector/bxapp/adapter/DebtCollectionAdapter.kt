package ja.insepector.bxapp.adapter

import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import ja.insepector.base.adapter.BaseBindingAdapter
import ja.insepector.base.adapter.VBViewHolder
import ja.insepector.base.bean.DebtCollectionBean
import ja.insepector.common.util.AppUtil
import ja.insepector.bxapp.databinding.ItemDebtCollectionBinding
import com.zrq.spanbuilder.TextStyle

class DebtCollectionAdapter(data: MutableList<Int>? = null, val onClickListener: OnClickListener) :
    BaseBindingAdapter<Int, ItemDebtCollectionBinding>(data) {
    val colors = intArrayOf(ja.insepector.base.R.color.color_ffe92404, ja.insepector.base.R.color.color_ffe92404, ja.insepector.base.R.color.color_ffe92404)
    val sizes = intArrayOf(16, 20, 16)
    val styles = arrayOf(TextStyle.NORMAL, TextStyle.BOLD, TextStyle.NORMAL)
    val colors2 = intArrayOf(ja.insepector.base.R.color.color_ff666666, ja.insepector.base.R.color.color_ff1a1a1a)
    val sizes2 = intArrayOf(19, 19)

    override fun convert(holder: VBViewHolder<ItemDebtCollectionBinding>, item: Int) {
//        holder.vb.tvNum.text = AppUtil.fillZero((data.indexOf(item) + 1).toString())
//        holder.vb.tvLicensePlate.text = item.carLicense
//        val strings = arrayOf("欠：", "${AppUtil.keepNDecimal(item.oweMoney / 100.00,2)}", "元")
//        holder.vb.tvAmount.text = AppUtil.getSpan(strings, sizes, colors, styles)
//        val strings2 = arrayOf("入场：", item.startTime)
//        holder.vb.tvStartTime.text = AppUtil.getSpan(strings2, sizes2, colors2)
//        val strings3 = arrayOf("出场：", item.endTime)
//        holder.vb.tvEndTime.text = AppUtil.getSpan(strings3, sizes2, colors2)
//        holder.vb.tvAddress.text = item.streetName
//        holder.vb.tvNo.text = item.parkingNo

        holder.vb.tvNum.text = AppUtil.fillZero((data.indexOf(item) + 1).toString())
        holder.vb.tvLicensePlate.text = "沪A36N81"
        val strings = arrayOf("欠：", "${AppUtil.keepNDecimal(10000 / 100.00,2)}", "元")
        holder.vb.tvAmount.text = AppUtil.getSpan(strings, sizes, colors, styles)
        val strings2 = arrayOf("入场：", "2023-06-25 10:31:26")
        holder.vb.tvStartTime.text = AppUtil.getSpan(strings2, sizes2, colors2)
        val strings3 = arrayOf("出场：", "2023-06-25 11:27:56 ")
        holder.vb.tvEndTime.text = AppUtil.getSpan(strings3, sizes2, colors2)
        holder.vb.tvAddress.text = "昌平路(西康路-常德路)"
        holder.vb.tvNo.text = "JAZ-021-008"

        holder.vb.rrlDebtCollection.tag = item
        holder.vb.rrlDebtCollection.setOnClickListener(onClickListener)
    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemDebtCollectionBinding {
        return ItemDebtCollectionBinding.inflate(inflater)
    }
}
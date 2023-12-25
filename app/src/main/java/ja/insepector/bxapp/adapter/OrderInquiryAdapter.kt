package ja.insepector.bxapp.adapter

import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import ja.insepector.base.adapter.BaseBindingAdapter
import ja.insepector.base.adapter.VBViewHolder
import ja.insepector.base.bean.OrderBean
import ja.insepector.base.ext.i18n
import ja.insepector.common.util.AppUtil
import ja.insepector.common.util.BigDecimalManager
import ja.insepector.bxapp.databinding.ItemOrderBinding
import com.zrq.spanbuilder.TextStyle
import ja.insepector.base.ext.gone
import ja.insepector.base.ext.show

class OrderInquiryAdapter(data: MutableList<OrderBean>? = null, val onClickListener: OnClickListener) :
    BaseBindingAdapter<OrderBean, ItemOrderBinding>(data) {
    val colorsBlue = intArrayOf(
        ja.insepector.base.R.color.color_ff04a091,
        ja.insepector.base.R.color.color_ff04a091,
        ja.insepector.base.R.color.color_ff04a091
    )
    val colorsRed = intArrayOf(
        ja.insepector.base.R.color.color_ffe92404,
        ja.insepector.base.R.color.color_ffe92404,
        ja.insepector.base.R.color.color_ffe92404
    )
    val sizes = intArrayOf(16, 20, 16)
    val styles = arrayOf(TextStyle.NORMAL, TextStyle.BOLD, TextStyle.NORMAL)
    val colors2 = intArrayOf(ja.insepector.base.R.color.color_ff666666, ja.insepector.base.R.color.color_ff1a1a1a)
    val sizes2 = intArrayOf(19, 19)
    var orderList: MutableList<OrderBean> = ArrayList()


    override fun convert(holder: VBViewHolder<ItemOrderBinding>, item: OrderBean) {
        holder.vb.tvNum.text = AppUtil.fillZero((data.indexOf(item) + 1).toString())
        holder.vb.tvLicensePlate.text = item.carLicense
        if (item.paidAmount.toDouble() > 0.0 || (item.paidAmount.toDouble() == 0.0 && item.amount.toDouble() == 0.0)) {
            holder.vb.cbOrder.gone()
            val strings = arrayOf(
                i18n(ja.insepector.base.R.string.已付),
                AppUtil.keepNDecimals(item.paidAmount, 2),
                i18n(ja.insepector.base.R.string.元)
            )
            holder.vb.tvAmount.text = AppUtil.getSpan(strings, sizes, colorsBlue, styles)
        } else {
            holder.vb.cbOrder.show()
            val strings = arrayOf(
                i18n(ja.insepector.base.R.string.欠),
                AppUtil.keepNDecimals(BigDecimalManager.subtractionDoubleToString(item.amount.toDouble(), item.paidAmount.toDouble()), 2),
                i18n(ja.insepector.base.R.string.元)
            )
            holder.vb.tvAmount.text = AppUtil.getSpan(strings, sizes, colorsRed, styles)
        }

        val strings2 = arrayOf(i18n(ja.insepector.base.R.string.入场) + ":", item.startTime)
        holder.vb.tvStartTime.text = AppUtil.getSpan(strings2, sizes2, colors2)
        val strings3 = arrayOf(i18n(ja.insepector.base.R.string.出场) + ":", item.endTime)
        holder.vb.tvEndTime.text = AppUtil.getSpan(strings3, sizes2, colors2)
        holder.vb.tvNo.text = item.parkingNo

        holder.vb.cbOrder.isChecked = false
        holder.vb.cbOrder.tag = item
        holder.vb.cbOrder.setOnCheckedChangeListener(object : OnCheckedChangeListener {
            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                if (p1) {
                    orderList.add(item)
                } else {
                    orderList.remove(item)
                }
            }
        })
        holder.vb.flOrder.tag = item
        holder.vb.flOrder.setOnClickListener(onClickListener)
    }


    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemOrderBinding {
        return ItemOrderBinding.inflate(inflater)
    }

    fun getUploadOrderList(): MutableList<OrderBean> {
        return orderList
    }

    fun clearUploadOrderList() {
        for (i in orderList) {
            notifyItemChanged(data.indexOf(i))
        }
        orderList.clear()
    }
}
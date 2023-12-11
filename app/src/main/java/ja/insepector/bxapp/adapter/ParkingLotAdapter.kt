package ja.insepector.bxapp.adapter

import android.util.ArrayMap
import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import ja.insepector.base.BaseApplication
import ja.insepector.base.adapter.BaseBindingAdapter
import ja.insepector.base.adapter.VBViewHolder
import ja.insepector.base.bean.ParkingLotBean
import ja.insepector.base.ext.gone
import ja.insepector.base.ext.hide
import ja.insepector.base.ext.i18n
import ja.insepector.base.ext.show
import ja.insepector.bxapp.databinding.ItemParkingLotBinding
import ja.insepector.common.util.AppUtil
import ja.insepector.common.util.Constant

class ParkingLotAdapter(data: MutableList<ParkingLotBean>? = null, val onClickListener: OnClickListener) :
    BaseBindingAdapter<ParkingLotBean, ItemParkingLotBinding>(data) {
    var plateBgMap: MutableMap<String, Int> = ArrayMap()
    var plateTxtColorMap: MutableMap<String, Int> = ArrayMap()
    var colors = intArrayOf(ja.insepector.base.R.color.color_ffeb0000, ja.insepector.base.R.color.black)
    var colors2 = intArrayOf(ja.insepector.base.R.color.black, ja.insepector.base.R.color.color_ffeb0000)
    var sizes = intArrayOf(24, 24)

    init {
        plateBgMap[Constant.BLACK] = ja.insepector.common.R.mipmap.ic_plate_bg_black
        plateBgMap[Constant.WHITE] = ja.insepector.common.R.mipmap.ic_plate_bg_white
        plateBgMap[Constant.GREY] = ja.insepector.common.R.mipmap.ic_plate_bg_white
        plateBgMap[Constant.RED] = ja.insepector.common.R.mipmap.ic_plate_bg_white
        plateBgMap[Constant.BLUE] = ja.insepector.common.R.mipmap.ic_plate_bg_blue
        plateBgMap[Constant.YELLOW] = ja.insepector.common.R.mipmap.ic_plate_bg_yellow
        plateBgMap[Constant.ORANGE] = ja.insepector.common.R.mipmap.ic_plate_bg_white
        plateBgMap[Constant.BROWN] = ja.insepector.common.R.mipmap.ic_plate_bg_white
        plateBgMap[Constant.GREEN] = ja.insepector.common.R.mipmap.ic_plate_bg_green
        plateBgMap[Constant.PURPLE] = ja.insepector.common.R.mipmap.ic_plate_bg_white
        plateBgMap[Constant.CYAN] = ja.insepector.common.R.mipmap.ic_plate_bg_white
        plateBgMap[Constant.PINK] = ja.insepector.common.R.mipmap.ic_plate_bg_white
        plateBgMap[Constant.TRANSPARENT] = ja.insepector.common.R.mipmap.ic_plate_bg_white
        plateBgMap[Constant.OTHERS] = ja.insepector.common.R.mipmap.ic_plate_bg_white

        plateTxtColorMap[Constant.BLACK] = ja.insepector.base.R.color.white
        plateTxtColorMap[Constant.WHITE] = ja.insepector.base.R.color.black
        plateTxtColorMap[Constant.GREY] = ja.insepector.base.R.color.black
        plateTxtColorMap[Constant.RED] = ja.insepector.base.R.color.black
        plateTxtColorMap[Constant.BLUE] = ja.insepector.base.R.color.white
        plateTxtColorMap[Constant.YELLOW] = ja.insepector.base.R.color.black
        plateTxtColorMap[Constant.ORANGE] = ja.insepector.base.R.color.black
        plateTxtColorMap[Constant.BROWN] = ja.insepector.base.R.color.black
        plateTxtColorMap[Constant.GREEN] = ja.insepector.base.R.color.black
        plateTxtColorMap[Constant.PURPLE] = ja.insepector.base.R.color.black
        plateTxtColorMap[Constant.CYAN] = ja.insepector.base.R.color.black
        plateTxtColorMap[Constant.PINK] = ja.insepector.base.R.color.black
        plateTxtColorMap[Constant.TRANSPARENT] = ja.insepector.base.R.color.black
        plateTxtColorMap[Constant.OTHERS] = ja.insepector.base.R.color.black
    }

    override fun convert(holder: VBViewHolder<ItemParkingLotBinding>, item: ParkingLotBean) {
        if (item.state == "01") {
            holder.vb.llParkingLotBg.setBackgroundResource(ja.insepector.common.R.mipmap.ic_parking_bg_grey)
            holder.vb.tvParkingLotNum.setBackgroundResource(ja.insepector.common.R.mipmap.ic_parking_num_bg_grey)
            holder.vb.tvPlate.text = i18n(ja.insepector.base.R.string.空闲)
            holder.vb.tvPlate.setTextColor(ContextCompat.getColor(BaseApplication.instance(), ja.insepector.base.R.color.black))
            holder.vb.tvPlate.background = null
            holder.vb.rflParking.tag = item
            holder.vb.rflParking.setOnClickListener(onClickListener)
        } else {
            if (item.deadLine > System.currentTimeMillis()) {
                holder.vb.llParkingLotBg.setBackgroundResource(ja.insepector.common.R.mipmap.ic_parking_bg_green)
                holder.vb.tvParkingLotNum.setBackgroundResource(ja.insepector.common.R.mipmap.ic_parking_num_bg_green)
            } else {
                holder.vb.llParkingLotBg.setBackgroundResource(ja.insepector.common.R.mipmap.ic_parking_bg_red)
                holder.vb.tvParkingLotNum.setBackgroundResource(ja.insepector.common.R.mipmap.ic_parking_num_bg_red)
            }
            if (item.carColor == "20") {
                holder.vb.llPlate.show()
                holder.vb.tvPlate.gone()
                holder.vb.tvPlate1.text = item.carLicense.substring(0, 2)
                holder.vb.tvPlate2.text = item.carLicense.substring(2, item.carLicense.length)
                holder.vb.tvPlate1.setTextColor(ContextCompat.getColor(BaseApplication.instance(), ja.insepector.base.R.color.black))
                holder.vb.tvPlate2.setTextColor(ContextCompat.getColor(BaseApplication.instance(), ja.insepector.base.R.color.black))
            } else {
                holder.vb.llPlate.hide()
                holder.vb.tvPlate.show()
                if (item.carLicense.contains("WJ")) {
                    val strings = arrayOf("WJ", item.carLicense.substring(2, item.carLicense.length))
                    holder.vb.tvPlate.text = AppUtil.getSpan(strings, sizes, colors)
                } else if (item.carLicense.contains("警")) {
                    val strings = arrayOf(item.carLicense.substring(0, item.carLicense.length - 1), "警")
                    holder.vb.tvPlate.text = AppUtil.getSpan(strings, sizes, colors2)
                } else {
                    holder.vb.tvPlate.text = item.carLicense
                }
                holder.vb.tvPlate.setTextColor(ContextCompat.getColor(BaseApplication.instance(), plateTxtColorMap[item.carColor]!!))
                holder.vb.tvPlate.background = plateBgMap[item.carColor]?.let { ContextCompat.getDrawable(BaseApplication.instance(), it) }
            }
            holder.vb.rflParking.tag = item
            holder.vb.rflParking.setOnClickListener(onClickListener)
        }
        holder.vb.tvParkingLotNum.text = item.parkingNo.substring(item.parkingNo.length - 3, item.parkingNo.length)
    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemParkingLotBinding {
        return ItemParkingLotBinding.inflate(inflater)
    }
}
package com.kernal.demo.plateid.adapter

import android.util.ArrayMap
import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.kernal.demo.base.BaseApplication
import com.kernal.demo.base.adapter.BaseBindingAdapter
import com.kernal.demo.base.adapter.VBViewHolder
import com.kernal.demo.base.bean.ParkingLotBean
import com.kernal.demo.base.ext.gone
import com.kernal.demo.base.ext.hide
import com.kernal.demo.base.ext.i18n
import com.kernal.demo.base.ext.show
import com.kernal.demo.plateid.databinding.ItemParkingLotBinding
import com.kernal.demo.common.util.Constant

class ParkingLotAdapter(data: MutableList<ParkingLotBean>? = null, val onClickListener: OnClickListener) :
    BaseBindingAdapter<ParkingLotBean, ItemParkingLotBinding>(data) {
    var plateBgMap: MutableMap<String, Int> = ArrayMap()
    var plateColorTxtMap: MutableMap<String, String> = ArrayMap()
    var plateLogoColorMap: MutableMap<String, Int> = ArrayMap()
//    var colors = intArrayOf(com.kernal.demo.base.R.color.color_ffeb0000, com.kernal.demo.base.R.color.black)
//    var colors2 = intArrayOf(com.kernal.demo.base.R.color.black, com.kernal.demo.base.R.color.color_ffeb0000)
//    var sizes = intArrayOf(24, 24)

    init {
        plateLogoColorMap[Constant.BLACK] = com.kernal.demo.base.R.color.black
        plateLogoColorMap[Constant.WHITE] = com.kernal.demo.base.R.color.white
        plateLogoColorMap[Constant.GREY] = com.kernal.demo.base.R.color.white
        plateLogoColorMap[Constant.RED] = com.kernal.demo.base.R.color.white
        plateLogoColorMap[Constant.BLUE] = com.kernal.demo.base.R.color.color_ff0046de
        plateLogoColorMap[Constant.YELLOW] = com.kernal.demo.base.R.color.color_fffda027
        plateLogoColorMap[Constant.ORANGE] = com.kernal.demo.base.R.color.white
        plateLogoColorMap[Constant.BROWN] = com.kernal.demo.base.R.color.white
        plateLogoColorMap[Constant.GREEN] = com.kernal.demo.base.R.color.color_ff09a95f
        plateLogoColorMap[Constant.PURPLE] = com.kernal.demo.base.R.color.white
        plateLogoColorMap[Constant.CYAN] = com.kernal.demo.base.R.color.white
        plateLogoColorMap[Constant.PINK] = com.kernal.demo.base.R.color.white
        plateLogoColorMap[Constant.TRANSPARENT] = com.kernal.demo.base.R.color.white
        plateLogoColorMap[Constant.OTHERS] = com.kernal.demo.base.R.color.white
        plateLogoColorMap[Constant.OTHERS_OLD] = com.kernal.demo.base.R.color.white

        plateColorTxtMap[Constant.BLACK] = i18n(com.kernal.demo.base.R.string.黑牌)
        plateColorTxtMap[Constant.WHITE] = i18n(com.kernal.demo.base.R.string.白牌)
        plateColorTxtMap[Constant.GREY] = i18n(com.kernal.demo.base.R.string.白牌)
        plateColorTxtMap[Constant.RED] = i18n(com.kernal.demo.base.R.string.白牌)
        plateColorTxtMap[Constant.BLUE] = i18n(com.kernal.demo.base.R.string.蓝牌)
        plateColorTxtMap[Constant.YELLOW] = i18n(com.kernal.demo.base.R.string.黄牌)
        plateColorTxtMap[Constant.ORANGE] = i18n(com.kernal.demo.base.R.string.白牌)
        plateColorTxtMap[Constant.BROWN] = i18n(com.kernal.demo.base.R.string.白牌)
        plateColorTxtMap[Constant.GREEN] = i18n(com.kernal.demo.base.R.string.绿牌)
        plateColorTxtMap[Constant.PURPLE] = i18n(com.kernal.demo.base.R.string.白牌)
        plateColorTxtMap[Constant.CYAN] = i18n(com.kernal.demo.base.R.string.白牌)
        plateColorTxtMap[Constant.PINK] = i18n(com.kernal.demo.base.R.string.白牌)
        plateColorTxtMap[Constant.TRANSPARENT] = i18n(com.kernal.demo.base.R.string.白牌)
        plateColorTxtMap[Constant.OTHERS] = i18n(com.kernal.demo.base.R.string.临牌)
        plateColorTxtMap[Constant.OTHERS_OLD] = i18n(com.kernal.demo.base.R.string.白牌)
    }

    override fun convert(holder: VBViewHolder<ItemParkingLotBinding>, item: ParkingLotBean) {
        if (item.state == "01") {
            holder.vb.llPlateNum.hide()
            holder.vb.ivCamera.gone()
            holder.vb.llParkingLotBg.setBackgroundColor(
                ContextCompat.getColor(
                    BaseApplication.instance(),
                    com.kernal.demo.base.R.color.color_ffefefef
                )
            )
            holder.vb.rflParkingLotNum.delegate.setBackgroundColor(
                ContextCompat.getColor(
                    BaseApplication.instance(),
                    com.kernal.demo.base.R.color.color_ffaaaaaa
                )
            )
            holder.vb.rflParkingLotNum.delegate.init()
            holder.vb.tvPlate.text = i18n(com.kernal.demo.base.R.string.空闲)
            holder.vb.tvPlate.background = null
            holder.vb.rflParking.tag = item
            holder.vb.rflParking.setOnClickListener(onClickListener)
        } else {
            holder.vb.llPlateNum.show()
            holder.vb.llParkingLotBg.setBackgroundColor(
                ContextCompat.getColor(
                    BaseApplication.instance(),
                    com.kernal.demo.base.R.color.white
                )
            )
            if (item.deadLine > System.currentTimeMillis()) {
                holder.vb.ivCamera.gone()
                holder.vb.rflParkingLotNum.delegate.setBackgroundColor(
                    ContextCompat.getColor(
                        BaseApplication.instance(),
                        com.kernal.demo.base.R.color.color_ff02d28b
                    )
                )
            } else {
                holder.vb.rflParkingLotNum.delegate.setBackgroundColor(
                    ContextCompat.getColor(
                        BaseApplication.instance(),
                        com.kernal.demo.base.R.color.color_fffd4646
                    )
                )
                if (System.currentTimeMillis() - item.orderStartTime > 60 * 60 * 1000) {
                    val num = (System.currentTimeMillis() - item.orderStartTime - 60 * 60 * 1000) / (30 * 60 * 1000)
                    val timeBegin = item.orderStartTime + 60 * 60 * 1000 + (30 * 60 * 1000) * num
                    if (timeBegin > item.thirdPicTime) {
                        holder.vb.ivCamera.show()
                    } else {
                        holder.vb.ivCamera.gone()
                    }
                } else {
                    holder.vb.ivCamera.gone()
                }
            }
            holder.vb.rflParkingLotNum.delegate.init()
            if (item.carColor == Constant.YELLOW_GREEN) {
                holder.vb.llCarColor.show()
                holder.vb.rtvCarColor.text = i18n(com.kernal.demo.base.R.string.黄绿)
                holder.vb.rtvCarColor.delegate.setStrokeWidth(0)
                holder.vb.rtvCarColor.delegate.setBackgroundColor(
                    ContextCompat.getColor(
                        BaseApplication.instance(),
                        com.kernal.demo.base.R.color.transparent
                    )
                )
                holder.vb.rtvCarColor.delegate.init()
            } else {
                holder.vb.llCarColor.hide()
                holder.vb.rtvCarColor.text = plateColorTxtMap[item.carColor]
                holder.vb.rtvCarColor.delegate.setBackgroundColor(
                    ContextCompat.getColor(
                        BaseApplication.instance(),
                        plateLogoColorMap[item.carColor]!!
                    )
                )
                if (plateLogoColorMap[item.carColor]!! == com.kernal.demo.base.R.color.white) {
                    holder.vb.rtvCarColor.delegate.setStrokeWidth(1)
                    holder.vb.rtvCarColor.delegate.setTextColor(
                        ContextCompat.getColor(
                            BaseApplication.instance(),
                            com.kernal.demo.base.R.color.black
                        )
                    )
                } else {
                    holder.vb.rtvCarColor.delegate.setStrokeWidth(0)
                    holder.vb.rtvCarColor.delegate.setTextColor(
                        ContextCompat.getColor(
                            BaseApplication.instance(),
                            com.kernal.demo.base.R.color.white
                        )
                    )
                }
                holder.vb.rtvCarColor.delegate.init()
            }
            holder.vb.tvPlate.text = item.carLicense
            holder.vb.rflParking.tag = item
            holder.vb.rflParking.setOnClickListener(onClickListener)
        }
//        item.parkingNo = "no"
        holder.vb.tvParkingLotNum.text = item.parkingNo.substring(item.parkingNo.length - 3, item.parkingNo.length)
    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemParkingLotBinding {
        return ItemParkingLotBinding.inflate(inflater)
    }
}

//        plateBgMap[Constant.BLACK] = com.kernal.demo.common.R.mipmap.ic_plate_bg_black
//        plateBgMap[Constant.WHITE] = com.kernal.demo.common.R.mipmap.ic_plate_bg_white
//        plateBgMap[Constant.GREY] = com.kernal.demo.common.R.mipmap.ic_plate_bg_white
//        plateBgMap[Constant.RED] = com.kernal.demo.common.R.mipmap.ic_plate_bg_white
//        plateBgMap[Constant.BLUE] = com.kernal.demo.common.R.mipmap.ic_plate_bg_blue
//        plateBgMap[Constant.YELLOW] = com.kernal.demo.common.R.mipmap.ic_plate_bg_yellow
//        plateBgMap[Constant.ORANGE] = com.kernal.demo.common.R.mipmap.ic_plate_bg_white
//        plateBgMap[Constant.BROWN] = com.kernal.demo.common.R.mipmap.ic_plate_bg_white
//        plateBgMap[Constant.GREEN] = com.kernal.demo.common.R.mipmap.ic_plate_bg_green
//        plateBgMap[Constant.PURPLE] = com.kernal.demo.common.R.mipmap.ic_plate_bg_white
//        plateBgMap[Constant.CYAN] = com.kernal.demo.common.R.mipmap.ic_plate_bg_white
//        plateBgMap[Constant.PINK] = com.kernal.demo.common.R.mipmap.ic_plate_bg_white
//        plateBgMap[Constant.TRANSPARENT] = com.kernal.demo.common.R.mipmap.ic_plate_bg_white
//        plateBgMap[Constant.OTHERS] = com.kernal.demo.common.R.mipmap.ic_plate_bg_white
//
//        plateTxtColorMap[Constant.BLACK] = com.kernal.demo.base.R.color.white
//        plateTxtColorMap[Constant.WHITE] = com.kernal.demo.base.R.color.black
//        plateTxtColorMap[Constant.GREY] = com.kernal.demo.base.R.color.black
//        plateTxtColorMap[Constant.RED] = com.kernal.demo.base.R.color.black
//        plateTxtColorMap[Constant.BLUE] = com.kernal.demo.base.R.color.white
//        plateTxtColorMap[Constant.YELLOW] = com.kernal.demo.base.R.color.black
//        plateTxtColorMap[Constant.ORANGE] = com.kernal.demo.base.R.color.black
//        plateTxtColorMap[Constant.BROWN] = com.kernal.demo.base.R.color.black
//        plateTxtColorMap[Constant.GREEN] = com.kernal.demo.base.R.color.black
//        plateTxtColorMap[Constant.PURPLE] = com.kernal.demo.base.R.color.black
//        plateTxtColorMap[Constant.CYAN] = com.kernal.demo.base.R.color.black
//        plateTxtColorMap[Constant.PINK] = com.kernal.demo.base.R.color.black
//        plateTxtColorMap[Constant.TRANSPARENT] = com.kernal.demo.base.R.color.black
//        plateTxtColorMap[Constant.YELLOW_GREEN] = com.kernal.demo.base.R.color.black
//        plateTxtColorMap[Constant.OTHERS] = com.kernal.demo.base.R.color.black
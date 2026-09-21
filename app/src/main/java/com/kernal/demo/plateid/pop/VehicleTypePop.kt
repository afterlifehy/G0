package com.kernal.demo.plateid.pop

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.annotation.RequiresApi
import com.kernal.demo.plateid.R
import com.kernal.demo.common.util.GlideUtils
import com.kernal.demo.plateid.databinding.PopVehicleTypeBinding

/**
 * Created by huy  on 2022/12/7.
 */
class VehicleTypePop(
    val context: Context?,
    var vehicleType: Int,
    var callback: VehicleTypeCallBack
) :
    PopupWindow(context), View.OnClickListener {

    private lateinit var binding: PopVehicleTypeBinding

    init {
        initView()
    }

    private fun initView() {
        binding = PopVehicleTypeBinding.inflate(LayoutInflater.from(context))
        when (vehicleType) {
            1 -> {
                GlideUtils.instance?.loadImage(binding.ivSmall, com.kernal.demo.common.R.mipmap.ic_parking_street_checked)
            }

            2 -> {
                GlideUtils.instance?.loadImage(binding.ivBig, com.kernal.demo.common.R.mipmap.ic_parking_street_checked)
            }
        }
        binding.rllBig.setOnClickListener(this)
        binding.rllSmall.setOnClickListener(this)

        contentView = binding.root
        contentView!!.measure(View.MeasureSpec.EXACTLY, View.MeasureSpec.EXACTLY)
        this.width = ViewGroup.LayoutParams.WRAP_CONTENT
        this.height = ViewGroup.LayoutParams.WRAP_CONTENT
        this.isFocusable = true
        this.isOutsideTouchable = true
        val dw = ColorDrawable(-0)
        //设置弹出窗体的背景
        this.setBackgroundDrawable(dw)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.rll_small -> {
                callback.selectType(1)
                dismiss()
            }

            R.id.rll_big -> {
                callback.selectType(2)
                dismiss()
            }
        }
    }

    interface VehicleTypeCallBack {
        fun selectType(type: Int)
    }


}
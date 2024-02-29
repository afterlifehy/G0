package com.kernal.demo.plateid.dialog

import android.view.Gravity
import android.view.View
import android.view.View.OnClickListener
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils
import com.kernal.demo.base.BaseApplication
import com.kernal.demo.base.bean.Street
import com.kernal.demo.base.dialog.VBBaseLibDialog
import com.kernal.demo.base.ext.gone
import com.kernal.demo.base.help.ActivityCacheManager
import com.kernal.demo.plateid.R
import com.kernal.demo.plateid.adapter.ChooseStreetAdapter
import com.kernal.demo.plateid.databinding.DialogStreetListBinding

class StreetChooseListDialog(
    val streetList: MutableList<Street>,
    var streetChoosedList: MutableList<Street>,
    val callback: StreetChooseCallBack
) :
    VBBaseLibDialog<DialogStreetListBinding>(
        ActivityCacheManager.instance().getCurrentActivity()!!,
        com.kernal.demo.base.R.style.CommonBottomDialogStyle
    ), OnClickListener {

    var chooseStreetAdapter: ChooseStreetAdapter? = null

    init {
        initView()
    }

    private fun initView() {
        streetList.add(Street())
        streetList.add(Street())
        var height = (SizeUtils.dp2px(55f) * (streetList.size + 0.5) + SizeUtils.dp2px(10f)).toInt()
        if (height > ScreenUtils.getAppScreenHeight() - BarUtils.getStatusBarHeight()) {
            height = ScreenUtils.getAppScreenHeight() - BarUtils.getStatusBarHeight()
        }
        setLayout(ScreenUtils.getAppScreenWidth().toFloat(), height.toFloat())
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        binding.tvDialogTitle.gone()
        binding.rvStreet.setHasFixedSize(true)
        binding.rvStreet.layoutManager = LinearLayoutManager(BaseApplication.instance())
        chooseStreetAdapter = ChooseStreetAdapter(streetList, streetChoosedList)
        binding.rvStreet.adapter = chooseStreetAdapter

        binding.rtvOk.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.rtv_ok -> {
                callback.chooseStreets()
                dismiss()
            }
        }
    }

    override fun getVbBindingView(): ViewBinding {
        return DialogStreetListBinding.inflate(layoutInflater)
    }

    override fun getHideInput(): Boolean {
        return true
    }

    override fun getWidth(): Float {
        return WindowManager.LayoutParams.MATCH_PARENT.toFloat()
    }

    override fun getHeight(): Float {
        return WindowManager.LayoutParams.WRAP_CONTENT.toFloat()
    }

    override fun getCanceledOnTouchOutside(): Boolean {
        return true
    }

    override fun getGravity(): Int {
        return Gravity.BOTTOM
    }

    interface StreetChooseCallBack {
        fun chooseStreets()
    }
}
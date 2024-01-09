package com.kernal.demo.plateid.dialog

import android.view.Gravity
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.ScreenUtils
import com.kernal.demo.base.BaseApplication
import com.kernal.demo.base.bean.ExitMethodBean
import com.kernal.demo.base.dialog.VBBaseLibDialog
import com.kernal.demo.base.help.ActivityCacheManager
import com.kernal.demo.plateid.adapter.ExitMethodAdapter
import com.kernal.demo.plateid.databinding.DialogExitMethodBinding

class ExitMethodDialog(
    val methodList: MutableList<ExitMethodBean>,
    var currentMethod: ExitMethodBean?,
    val callBack: ExitMethodCallBack
) :
    VBBaseLibDialog<DialogExitMethodBinding>(ActivityCacheManager.instance().getCurrentActivity()!!) {
    var exitMethodAdapter: ExitMethodAdapter? = null

    init {
        initView()
    }

    private fun initView() {
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        binding.rvExitMethod.setHasFixedSize(true)
        binding.rvExitMethod.layoutManager = LinearLayoutManager(BaseApplication.instance())
        exitMethodAdapter =
            ExitMethodAdapter(
                methodList,
                currentMethod,
                object : ExitMethodAdapter.ChooseExitMethodAdapterCallBack {
                    override fun chooseExitMethod(method: ExitMethodBean) {
                        callBack.chooseExitMethod(method)
                        dismiss()
                    }
                })
        binding.rvExitMethod.adapter = exitMethodAdapter
    }

    override fun getVbBindingView(): ViewBinding {
        return DialogExitMethodBinding.inflate(layoutInflater)
    }

    override fun getHideInput(): Boolean {
        return true
    }

    override fun getWidth(): Float {
        return ScreenUtils.getScreenWidth() * 0.83f
    }

    override fun getHeight(): Float {
        return WindowManager.LayoutParams.WRAP_CONTENT.toFloat()
    }

    override fun getCanceledOnTouchOutside(): Boolean {
        return true
    }

    override fun getGravity(): Int {
        return Gravity.CENTER
    }

    interface ExitMethodCallBack {
        fun chooseExitMethod(method: ExitMethodBean)
    }
}
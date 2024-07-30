package com.kernal.demo.plateid.dialog

import android.content.DialogInterface
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.WindowManager
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.ScreenUtils
import com.kernal.demo.base.dialog.VBBaseLibDialog
import com.kernal.demo.base.ext.gone
import com.kernal.demo.base.help.ActivityCacheManager
import com.kernal.demo.plateid.R
import com.kernal.demo.plateid.databinding.DialogPromptBinding

class PromptDialog(
    val content: String,
    val leftString: String,
    val rightString: String,
    val callBack: PromptCallBack,
    val isSingleButton: Boolean = false,
) :
    VBBaseLibDialog<DialogPromptBinding>(ActivityCacheManager.instance().getCurrentActivity()!!), OnClickListener {

    init {
        initView()
    }

    private fun initView() {
        binding.tvContent.text = content
        binding.rtvLeft.text = leftString
        binding.rtvRight.text = rightString

        if (isSingleButton) {
            binding.rtvLeft.gone()
        }
        binding.rtvLeft.setOnClickListener(this)
        binding.rtvRight.setOnClickListener(this)
        setOnKeyListener(object : DialogInterface.OnKeyListener {
            override fun onKey(dialog: DialogInterface?, keyCode: Int, event: KeyEvent?): Boolean {
                if (keyCode == KeyEvent.KEYCODE_BACK && event?.action == KeyEvent.ACTION_UP) {
                    // 处理返回键事件
                    // 返回true表示消费了该事件，不会关闭对话框
                    return true;
                }
                return false;
            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.rtv_left -> {
                callBack.leftClick()
                dismiss()
            }

            R.id.rtv_right -> {
                callBack.rightClick()
                dismiss()
            }
        }
    }

    override fun getVbBindingView(): ViewBinding? {
        return DialogPromptBinding.inflate(layoutInflater)
    }

    override fun getHideInput(): Boolean {
        return true
    }

    override fun getWidth(): Float {
        return ScreenUtils.getScreenWidth() * 0.9f
    }

    override fun getHeight(): Float {
        return WindowManager.LayoutParams.WRAP_CONTENT.toFloat()
    }

    override fun getCanceledOnTouchOutside(): Boolean {
        return false
    }

    override fun getGravity(): Int {
        return Gravity.CENTER
    }

    interface PromptCallBack {
        fun leftClick()
        fun rightClick()
    }
}
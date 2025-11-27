package com.kernal.demo.plateid.dialog

import android.content.DialogInterface
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import androidx.viewbinding.ViewBinding
import com.kernal.demo.base.dialog.VBBaseLibDialog
import com.kernal.demo.base.help.ActivityCacheManager
import com.kernal.demo.plateid.R
import com.kernal.demo.plateid.databinding.DialogConfirmBinding

class ConfirmDialog(val title: String, val left: () -> Unit, val right: () -> Unit) :
    VBBaseLibDialog<DialogConfirmBinding>(ActivityCacheManager.instance().getCurrentActivity()!!), View.OnClickListener {
    init {
        initView()
    }

    private fun initView() {
        binding.tvTitle.text = title
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
                left()
                dismiss()
            }

            R.id.rtv_right -> {
                right()
                dismiss()
            }
        }
    }

    override fun getVbBindingView(): ViewBinding {
        return DialogConfirmBinding.inflate(layoutInflater)
    }

    override fun getHideInput(): Boolean {
        return true
    }

    override fun getWidth(): Float {
        return WindowManager.LayoutParams.WRAP_CONTENT.toFloat()
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

}
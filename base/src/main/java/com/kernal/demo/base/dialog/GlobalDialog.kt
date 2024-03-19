package com.kernal.demo.base.dialog

import android.content.Context
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.ScreenUtils
import com.kernal.demo.base.R
import com.kernal.demo.base.databinding.DialogContextLayoutBinding
import com.kernal.demo.base.ext.gone

class GlobalDialog(context: Context, mDialogHelp: DialogHelp) :
    VBBaseLibDialog<DialogContextLayoutBinding>(context), View.OnClickListener {
    private var mRootView: View? = null
    private var mDialogHelp: DialogHelp? = null

    init {
        this.mDialogHelp = mDialogHelp
        initView()
    }

    private fun initView() {
        bindShowViewData()

        binding.rtvLeft.setOnClickListener(this)
        binding.rtvRight.setOnClickListener(this)
        ClickUtils.applyGlobalDebouncing(binding.rtvLeft, this)
        ClickUtils.applyGlobalDebouncing(binding.rtvRight, this)
    }

    fun bindShowViewData() {
        mDialogHelp?.let {
            if (!TextUtils.isEmpty(it.title)) {
                binding.tvTitle.text = it.title
            } else {
                binding.tvTitle.gone()
            }
            if (!TextUtils.isEmpty(it.contentMsg)) {
                binding.tvContent.text = it.contentMsg
            } else {
                binding.tvContent.gone()
            }

            if (it.isAloneButton) {
                binding.rtvLeft.gone()
            } else {
                binding.rtvLeft.text = it.leftMsg
            }
            binding.rtvRight.text = it.rightMsg
            setCancelable(it.cancelable)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.rtv_left -> {
                mDialogHelp?.mOnButtonClickLinsener?.onLeftClickLinsener()
                if (mDialogHelp!!.cancelable) {
                    dismiss()
                }

            }

            R.id.rtv_right -> {
                mDialogHelp?.mOnButtonClickLinsener?.onRightClickLinsener()
                if (mDialogHelp!!.cancelable) {
                    dismiss()
                }
            }
        }
    }

    override fun getHideInput(): Boolean {
        return true
    }

    override fun getWidth(): Float {
        return ScreenUtils.getScreenWidth() * 0.85f
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

    override fun getVbBindingView(): ViewBinding {
        return DialogContextLayoutBinding.inflate(layoutInflater)
    }
}
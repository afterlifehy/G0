package com.kernal.demo.plateid.dialog

import android.view.Gravity
import android.view.WindowManager
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.SizeUtils
import com.zrq.spanbuilder.TextStyle
import com.kernal.demo.base.dialog.VBBaseLibDialog
import com.kernal.demo.base.ext.i18N
import com.kernal.demo.base.ext.i18n
import com.kernal.demo.base.help.ActivityCacheManager
import com.kernal.demo.common.util.CodeUtils
import com.kernal.demo.common.util.GlideUtils
import com.kernal.demo.plateid.databinding.DialogPaymentQrBinding
import com.kernal.demo.common.util.AppUtil

class PaymentQrDialog(var qr: String, var amount: String) : VBBaseLibDialog<DialogPaymentQrBinding>(
    ActivityCacheManager.instance().getCurrentActivity()!!,
    com.kernal.demo.base.R.style.CommonBottomDialogStyle
) {
    val sizes = intArrayOf(19, 30, 19)
    val colors = intArrayOf(com.kernal.demo.plateid.R.color.white, com.kernal.demo.plateid.R.color.white, com.kernal.demo.plateid.R.color.white)
    val styles = arrayOf(TextStyle.NORMAL, TextStyle.BOLD, TextStyle.NORMAL)

    init {
        initView()
    }

    private fun initView() {
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        val qrBitmap = com.kernal.demo.common.util.CodeUtils.createImage(qr, SizeUtils.dp2px(184f), SizeUtils.dp2px(184f), null)
        GlideUtils.instance?.loadImage(binding.rivQr, qrBitmap)
        val strings = arrayOf(i18N(com.kernal.demo.base.R.string.支付), amount, i18n(com.kernal.demo.base.R.string.元))
        binding.tvPayAmount.text = AppUtil.getSpan(strings, sizes, colors, styles)
        binding.ivClose.setOnClickListener {
            dismiss()
        }
    }

    override fun getVbBindingView(): ViewBinding? {
        return DialogPaymentQrBinding.inflate(layoutInflater)
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
        return false
    }

    override fun getGravity(): Int {
        return Gravity.BOTTOM
    }
}
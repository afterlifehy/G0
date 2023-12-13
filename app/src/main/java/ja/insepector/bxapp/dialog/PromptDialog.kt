package ja.insepector.bxapp.dialog

import android.view.Gravity
import android.view.View
import android.view.View.OnClickListener
import android.view.WindowManager
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.ScreenUtils
import ja.insepector.base.dialog.VBBaseLibDialog
import ja.insepector.base.help.ActivityCacheManager
import ja.insepector.bxapp.R
import ja.insepector.bxapp.databinding.DialogPromptBinding

class PromptDialog(val content: String, val leftString: String, val rightString: String, val callBack: PromptCallBack) :
    VBBaseLibDialog<DialogPromptBinding>(ActivityCacheManager.instance().getCurrentActivity()!!), OnClickListener {

    init {
        initView()
    }

    private fun initView() {
        binding.tvContent.text = content
        binding.rtvLeft.text = leftString
        binding.rtvRight.text = rightString

        binding.rtvLeft.setOnClickListener(this)
        binding.rtvRight.setOnClickListener(this)
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
        return true
    }

    override fun getGravity(): Int {
        return Gravity.CENTER
    }

    interface PromptCallBack {
        fun leftClick()
        fun rightClick()
    }
}
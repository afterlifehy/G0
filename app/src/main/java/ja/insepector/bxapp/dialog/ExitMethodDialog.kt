package ja.insepector.bxapp.dialog

import android.view.Gravity
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.ScreenUtils
import ja.insepector.base.BaseApplication
import ja.insepector.base.dialog.VBBaseLibDialog
import ja.insepector.base.help.ActivityCacheManager
import ja.insepector.bxapp.adapter.ExitMethodAdapter
import ja.insepector.bxapp.databinding.DialogExitMethodBinding

class ExitMethodDialog(
    val methodList: MutableList<String>,
    var currentMethod: String,
    val callBack: ExitMethodCallBack
) :
    VBBaseLibDialog<DialogExitMethodBinding>(ActivityCacheManager.instance().getCurrentActivity()!!) {
    var exitMethodAdapter: ExitMethodAdapter? = null

    init {
        initView()
    }

    private fun initView() {
        binding.rvExitMethod.setHasFixedSize(true)
        binding.rvExitMethod.layoutManager = LinearLayoutManager(BaseApplication.instance())
        exitMethodAdapter =
            ExitMethodAdapter(
                methodList,
                currentMethod,
                object : ExitMethodAdapter.ChooseExitMethodAdapterCallBack {
                    override fun chooseExitMethod(method: String) {
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
        fun chooseExitMethod(method: String)
    }
}
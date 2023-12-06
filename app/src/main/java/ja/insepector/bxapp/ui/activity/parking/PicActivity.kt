package ja.insepector.bxapp.ui.activity.parking

import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.View.OnClickListener
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import ja.insepector.base.BaseApplication
import ja.insepector.base.ext.i18N
import ja.insepector.base.viewbase.VbBaseActivity
import ja.insepector.bxapp.R
import ja.insepector.bxapp.adapter.PicAdapter
import ja.insepector.bxapp.databinding.ActivityPicBinding
import ja.insepector.bxapp.mvvm.viewmodel.PicViewModel
import ja.insepector.common.util.GlideUtils

class PicActivity : VbBaseActivity<PicViewModel, ActivityPicBinding>(), OnClickListener {
    var picList: MutableList<String> = ArrayList()
    var picAdapter: PicAdapter? = null

    override fun initView() {
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, ja.insepector.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = i18N(ja.insepector.base.R.string.车辆图片)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), ja.insepector.base.R.color.white))

        GlideUtils.instance?.loadImage(binding.layoutNoData.ivNoDataIcon, ja.insepector.common.R.mipmap.ic_no_data)
        binding.layoutNoData.tvNoDataText.text = i18N(ja.insepector.base.R.string.无车辆图片)

        binding.rvPic.setHasFixedSize(true)
        binding.rvPic.layoutManager = LinearLayoutManager(this)
        picAdapter = PicAdapter(picList, this)
        binding.rvPic.adapter = picAdapter
    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
    }

    override fun initData() {
        Handler(Looper.getMainLooper()).postDelayed({
            picList.add("http://58.246.81.3:801/20230903/CNL00301_20230903193808_0_22.jpg")
            picList.add("http://58.246.81.3:801/20230903/CNL00301_20230903161333_1_12.jpg")
            picList.add("http://58.246.81.3:801/20230903/CNL00301_20230903161333_1_12.jpg")
            picAdapter?.setList(picList)
        }, 3000)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }

            R.id.riv_pic -> {

            }
        }
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityPicBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override val isFullScreen: Boolean
        get() = true

    override fun marginStatusBarView(): View {
        return binding.layoutToolbar.ablToolbar
    }
}
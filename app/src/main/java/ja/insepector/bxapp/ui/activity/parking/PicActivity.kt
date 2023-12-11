package ja.insepector.bxapp.ui.activity.parking

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.View.OnClickListener
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import ja.insepector.base.BaseApplication
import ja.insepector.base.arouter.ARouterMap
import ja.insepector.base.ext.gone
import ja.insepector.base.ext.i18N
import ja.insepector.base.ext.startArouter
import ja.insepector.base.viewbase.VbBaseActivity
import ja.insepector.bxapp.R
import ja.insepector.bxapp.adapter.PicAdapter
import ja.insepector.bxapp.databinding.ActivityPicBinding
import ja.insepector.bxapp.mvvm.viewmodel.PicViewModel
import ja.insepector.common.util.GlideUtils

@Route(path = ARouterMap.PIC)
class PicActivity : VbBaseActivity<PicViewModel, ActivityPicBinding>(), OnClickListener {
    var picList: MutableList<String> = ArrayList()
    var picAdapter: PicAdapter? = null

    override fun initView() {
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, ja.insepector.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = i18N(ja.insepector.base.R.string.车辆图片)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), ja.insepector.base.R.color.white))

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
            picList.add("https://n.sinaimg.cn/sinacn10112/384/w2048h1536/20190218/bd7a-htacqww5359098.jpg")
            picList.add("https://p4.itc.cn/q_70/images03/20200723/76f7fd2511a048abbb2e58939b1f9bde.jpeg")
            picList.add("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fsafe-img.xhscdn.com%2Fbw1%2F8ccb3755-b76e-49f1-85ab-60f6c6b161ae%3FimageView2%2F2%2Fw%2F1080%2Fformat%2Fjpg&refer=http%3A%2F%2Fsafe-img.xhscdn.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1704444015&t=45651f5b799fd231a323879ff7abca31")
            picAdapter?.setList(picList)
            binding.tvNoPic.gone()
        }, 3000)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }

            R.id.riv_pic -> {
                val pic = v.tag as String
                startArouter(ARouterMap.PREVIEW_IMAGE,data = Bundle().apply {
                    putInt(ARouterMap.IMG_INDEX, picList.indexOf(pic))
                    putStringArrayList(ARouterMap.IMG_LIST, picList as ArrayList<String>)
                })
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
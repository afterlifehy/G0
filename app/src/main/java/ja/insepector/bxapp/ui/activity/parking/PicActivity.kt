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
import com.alibaba.fastjson.JSONObject
import ja.insepector.base.BaseApplication
import ja.insepector.base.arouter.ARouterMap
import ja.insepector.base.ext.gone
import ja.insepector.base.ext.i18N
import ja.insepector.base.ext.startArouter
import ja.insepector.base.util.ToastUtil
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
    var orderNo = ""

    override fun initView() {
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, ja.insepector.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = i18N(ja.insepector.base.R.string.车辆图片)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), ja.insepector.base.R.color.white))

        orderNo = intent.getStringExtra(ARouterMap.PIC_ORDER_NO).toString()
        binding.rvPic.setHasFixedSize(true)
        binding.rvPic.layoutManager = LinearLayoutManager(this)
        picAdapter = PicAdapter(picList, this)
        binding.rvPic.adapter = picAdapter
    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
    }

    override fun initData() {
        showProgressDialog(20000)
        val param = HashMap<String, Any>()
        val jsonobject = JSONObject()
        jsonobject["orderNo"] = orderNo
        param["attr"] = jsonobject
        mViewModel.picInquiry(param)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }

            R.id.riv_pic -> {
                val pic = v.tag as String
                startArouter(ARouterMap.PREVIEW_IMAGE, data = Bundle().apply {
                    putInt(ARouterMap.IMG_INDEX, picList.indexOf(pic))
                    putStringArrayList(ARouterMap.IMG_LIST, picList as ArrayList<String>)
                })
            }
        }
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            picInquiryLiveData.observe(this@PicActivity) {
                dismissProgressDialog()
                picList.add(it.inPicture11)
                picList.add(it.inPicture10)
                picList.add(it.inPicture20)
                picAdapter?.setList(picList)
                binding.tvNoPic.gone()
            }
            errMsg.observe(this@PicActivity) {
                dismissProgressDialog()
                ToastUtil.showMiddleToast(it.msg)
            }
        }
    }

    override fun providerVMClass(): Class<PicViewModel>? {
        return PicViewModel::class.java
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
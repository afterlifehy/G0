package ja.insepector.bxapp.ui.activity.mine

import android.view.View
import android.view.View.OnClickListener
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import ja.insepector.base.BaseApplication
import ja.insepector.base.arouter.ARouterMap
import ja.insepector.base.bean.DataPrintBean
import ja.insepector.base.ext.i18N
import ja.insepector.base.ext.startArouter
import ja.insepector.base.help.ActivityCacheManager
import ja.insepector.base.viewbase.VbBaseActivity
import ja.insepector.bxapp.R
import ja.insepector.bxapp.adapter.DataPrintAdapter
import ja.insepector.bxapp.databinding.ActivityDataPrintBinding
import ja.insepector.bxapp.mvvm.viewmodel.DataPrintViewModel
import ja.insepector.bxapp.ui.activity.login.LoginActivity
import ja.insepector.common.util.GlideUtils

@Route(path = ARouterMap.DATA_PRINT)
class DataPrintActivity : VbBaseActivity<DataPrintViewModel, ActivityDataPrintBinding>(), OnClickListener {
    var dataPrintAdapter: DataPrintAdapter? = null
    var dataPrintList: MutableList<DataPrintBean> = ArrayList()
    var dataPrintCheckedList: MutableList<DataPrintBean> = ArrayList()

    override fun initView() {
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, ja.insepector.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = i18N(ja.insepector.base.R.string.数据打印)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), ja.insepector.base.R.color.white))

        binding.rvPrintInfo.setHasFixedSize(true)
        binding.rvPrintInfo.layoutManager = LinearLayoutManager(this)
        dataPrintAdapter = DataPrintAdapter(dataPrintList, dataPrintCheckedList)
        binding.rvPrintInfo.adapter = dataPrintAdapter
    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
        binding.rtvNoPrint.setOnClickListener(this)
        binding.rtvPrint.setOnClickListener(this)
    }

    override fun initData() {
        dataPrintList.add(DataPrintBean("① 订单总数"))
        dataPrintList.add(DataPrintBean("② 拒付费订单数"))
        dataPrintList.add(DataPrintBean("③ 部分付费订单数"))
        dataPrintList.add(DataPrintBean("④ 被追缴金额"))
        dataPrintList.add(DataPrintBean("⑤ 追缴他人金额"))
        dataPrintList.add(DataPrintBean("⑥ 停车人APP金额"))
        dataPrintList.add(DataPrintBean("⑦ 总营收（含④和⑥，不含⑤）"))
        dataPrintAdapter?.setList(dataPrintList)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back,
            R.id.rtv_noPrint -> {
                onBackPressedSupport()
            }

            R.id.rtv_print -> {
                onBackPressedSupport()
            }
        }
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityDataPrintBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override val isFullScreen: Boolean
        get() = true

    override fun marginStatusBarView(): View {
        return binding.layoutToolbar.ablToolbar
    }

    override fun onBackPressedSupport() {
        startArouter(ARouterMap.LOGIN)
        for (i in ActivityCacheManager.instance().getAllActivity()) {
            if (i !is LoginActivity) {
                i.finish()
            }
        }
    }
}
package ja.insepector.bxapp.ui.activity.order

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.WindowManager
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSONObject
import com.blankj.utilcode.util.TimeUtils
import ja.insepector.base.BaseApplication
import ja.insepector.base.arouter.ARouterMap
import ja.insepector.base.bean.OrderBean
import ja.insepector.base.dialog.DialogHelp
import ja.insepector.base.ds.PreferencesDataStore
import ja.insepector.base.ds.PreferencesKeys
import ja.insepector.base.ext.gone
import ja.insepector.base.ext.i18N
import ja.insepector.base.ext.show
import ja.insepector.base.ext.startArouter
import ja.insepector.base.help.ActivityCacheManager
import ja.insepector.base.util.ToastUtil
import ja.insepector.base.viewbase.VbBaseActivity
import ja.insepector.common.realm.RealmUtil
import ja.insepector.common.util.GlideUtils
import ja.insepector.common.view.keyboard.KeyboardUtil
import ja.insepector.common.view.keyboard.MyOnTouchListener
import ja.insepector.common.view.keyboard.MyTextWatcher
import ja.insepector.bxapp.R
import ja.insepector.bxapp.adapter.OrderInquiryAdapter
import ja.insepector.bxapp.databinding.ActivityOrderInquiryBinding
import ja.insepector.bxapp.mvvm.viewmodel.OrderInquiryViewModel
import ja.insepector.bxapp.pop.DatePop
import kotlinx.coroutines.runBlocking

@Route(path = ARouterMap.ORDER_INQUIRY)
class OrderInquiryActivity : VbBaseActivity<OrderInquiryViewModel, ActivityOrderInquiryBinding>(), OnClickListener {
    private lateinit var keyboardUtil: KeyboardUtil
    var orderList: MutableList<OrderBean> = ArrayList()
    var orderInquiryAdapter: OrderInquiryAdapter? = null
    var datePop: DatePop? = null
    var pageIndex = 1
    var pageSize = 10
    var startDate = TimeUtils.millis2String(System.currentTimeMillis(), "yyyy-MM-dd")
    var endDate = TimeUtils.millis2String(System.currentTimeMillis(), "yyyy-MM-dd")
    var loginName = ""

    override fun initView() {
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, ja.insepector.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = i18N(ja.insepector.base.R.string.订单查询)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), ja.insepector.base.R.color.white))
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivRight, ja.insepector.common.R.mipmap.ic_calendar)
        binding.layoutToolbar.ivRight.show()

        binding.rvOrders.setHasFixedSize(true)
        binding.rvOrders.layoutManager = LinearLayoutManager(this)
        orderInquiryAdapter = OrderInquiryAdapter(orderList, this)
        binding.rvOrders.adapter = orderInquiryAdapter

        initKeyboard()

    }

    private fun initKeyboard() {
        keyboardUtil = KeyboardUtil(binding.kvKeyBoard) {
            binding.etSearch.requestFocus()
            keyboardUtil.changeKeyboard(true)
            keyboardUtil.setEditText(binding.etSearch)
        }

        binding.etSearch.addTextChangedListener(MyTextWatcher(null, null, true, keyboardUtil))
        binding.etSearch.setOnTouchListener(MyOnTouchListener(true, binding.etSearch, keyboardUtil))
        binding.root.setOnClickListener {
            keyboardUtil.hideKeyboard()
        }
    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
        binding.tvSearch.setOnClickListener(this)
        binding.layoutToolbar.ivRight.setOnClickListener(this)
        binding.root.setOnClickListener(this)
        binding.layoutToolbar.toolbar.setOnClickListener(this)
        binding.rflUpload.setOnClickListener(this)
        binding.srlOrder.setOnRefreshListener {
            pageIndex = 1
            binding.srlOrder.finishRefresh(5000)
            orderList.clear()
            query()
        }
        binding.srlOrder.setOnLoadMoreListener {
            pageIndex++
            query()
            binding.srlOrder.finishLoadMore(5000)
        }
    }

    override fun initData() {
        runBlocking {
            loginName = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.loginName)
            showProgressDialog(20000)
            query()
        }
    }

    private fun query() {
        keyboardUtil.hideKeyboard()
        val searchContent = binding.etSearch.text.toString()
        if (searchContent.isNotEmpty() && (searchContent.length != 7 && searchContent.length != 8)) {
            dismissProgressDialog()
            ToastUtil.showMiddleToast(i18N(ja.insepector.base.R.string.车牌长度只能是7位或8位))
            return
        }
        val param = HashMap<String, Any>()
        val jsonobject = JSONObject()
        jsonobject["loginName"] = loginName
        jsonobject["carLicense"] = searchContent
        jsonobject["startDate"] = startDate
        jsonobject["endDate"] = endDate
        jsonobject["page"] = pageIndex
        jsonobject["size"] = pageSize
        param["attr"] = jsonobject
        mViewModel.orderInquiry(param)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }

            R.id.tv_search -> {
                pageIndex = 1
                showProgressDialog(20000)
                query()
            }

            R.id.iv_right -> {
                datePop = DatePop(BaseApplication.instance(), startDate, endDate, 0, object : DatePop.DateCallBack {
                    override fun selectDate(startTime: String, endTime: String) {
                        startDate = startTime
                        endDate = endTime
                        pageIndex = 1
                        showProgressDialog(20000)
                        query()
                    }

                })
                datePop?.showAsDropDown((v.parent) as Toolbar)
            }

            R.id.toolbar,
            binding.root.id -> {
                keyboardUtil.hideKeyboard()
            }

            R.id.fl_order -> {
                val orderBean = v.tag as OrderBean
                startArouter(ARouterMap.ORDER_DETAIL, data = Bundle().apply {
                    putParcelable(ARouterMap.ORDER, orderBean)
                })
            }

            R.id.rfl_upload -> {
                DialogHelp.Builder().setTitle(i18N(ja.insepector.base.R.string.是否立即上传))
                    .setLeftMsg(i18N(ja.insepector.base.R.string.取消))
                    .setRightMsg(i18N(ja.insepector.base.R.string.确定)).setCancelable(true)
                    .setOnButtonClickLinsener(object : DialogHelp.OnButtonClickLinsener {
                        override fun onLeftClickLinsener(msg: String) {
                        }

                        override fun onRightClickLinsener(msg: String) {
                            val orderList = orderInquiryAdapter?.getUploadOrderList()
                        }

                    }).build(ActivityCacheManager.instance().getCurrentActivity()).showDailog()
            }
        }
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            orderInquiryLiveData.observe(this@OrderInquiryActivity) {
                dismissProgressDialog()
                val tempList = it.result
                if (pageIndex == 1) {
                    if (tempList.isEmpty()) {
                        orderInquiryAdapter?.setNewInstance(null)
                        binding.rvOrders.gone()
                        binding.layoutNoData.root.show()
                        binding.srlOrder.finishRefresh()
                    } else {
                        orderList.clear()
                        orderList.addAll(tempList)
                        orderInquiryAdapter?.setList(orderList)
                        binding.srlOrder.finishRefresh()
                        binding.rvOrders.show()
                        binding.layoutNoData.root.gone()
                    }
                } else {
                    if (tempList.isEmpty()) {
                        pageIndex--
                        binding.srlOrder.finishLoadMoreWithNoMoreData()
                    } else {
                        orderList.addAll(tempList)
                        orderInquiryAdapter?.setList(orderList)
                        binding.srlOrder.finishLoadMore(300)
                    }
                }
            }
            errMsg.observe(this@OrderInquiryActivity) {
                dismissProgressDialog()
                ToastUtil.showMiddleToast(it.msg)
            }
        }
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityOrderInquiryBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override val isFullScreen: Boolean
        get() = true

    override fun providerVMClass(): Class<OrderInquiryViewModel> {
        return OrderInquiryViewModel::class.java
    }

    override fun marginStatusBarView(): View {
        return binding.layoutToolbar.ablToolbar
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (keyboardUtil.isShow()) {
                keyboardUtil.hideKeyboard()
            } else {
                return super.onKeyDown(keyCode, event)
            }
        }
        return false
    }
}
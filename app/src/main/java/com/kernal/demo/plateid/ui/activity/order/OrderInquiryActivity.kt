package com.kernal.demo.plateid.ui.activity.order

import android.content.Intent
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
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.fastjson.JSONObject
import com.blankj.utilcode.util.TimeUtils
import com.kernal.demo.base.BaseApplication
import com.kernal.demo.base.arouter.ARouterMap
import com.kernal.demo.base.bean.OrderBean
import com.kernal.demo.base.dialog.DialogHelp
import com.kernal.demo.base.ds.PreferencesDataStore
import com.kernal.demo.base.ds.PreferencesKeys
import com.kernal.demo.base.ext.gone
import com.kernal.demo.base.ext.i18N
import com.kernal.demo.base.ext.show
import com.kernal.demo.base.ext.startArouter
import com.kernal.demo.base.help.ActivityCacheManager
import com.kernal.demo.base.util.ToastUtil
import com.kernal.demo.base.viewbase.VbBaseActivity
import com.kernal.demo.common.event.RefreshIsPrintEvent
import com.kernal.demo.common.util.GlideUtils
import com.kernal.demo.common.view.keyboard.KeyboardUtil
import com.kernal.demo.common.view.keyboard.MyOnTouchListener
import com.kernal.demo.common.view.keyboard.MyTextWatcher
import com.kernal.demo.plateid.R
import com.kernal.demo.plateid.adapter.OrderInquiryAdapter
import com.kernal.demo.plateid.databinding.ActivityOrderInquiryBinding
import com.kernal.demo.plateid.mvvm.viewmodel.OrderInquiryViewModel
import com.kernal.demo.plateid.pop.DatePop
import kotlinx.coroutines.runBlocking
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

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
    var currentOrder: OrderBean? = null

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(refreshIsPrintEvent: RefreshIsPrintEvent) {
        currentOrder?.isPrinted = "1"
        orderInquiryAdapter?.notifyItemChanged(orderList.indexOf(currentOrder))
    }

    override fun initView() {
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, com.kernal.demo.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = i18N(com.kernal.demo.base.R.string.订单查询)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.kernal.demo.base.R.color.white))
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivRight, com.kernal.demo.common.R.mipmap.ic_calendar)
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
        binding.ivCamera.setOnClickListener(this)
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
            ToastUtil.showMiddleToast(i18N(com.kernal.demo.base.R.string.车牌长度只能是7位或8位))
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
                currentOrder = v.tag as OrderBean
                startArouter(ARouterMap.ORDER_DETAIL, data = Bundle().apply {
                    putParcelable(ARouterMap.ORDER, currentOrder)
                })
            }

            R.id.iv_camera -> {
                ARouter.getInstance().build(ARouterMap.SCAN_PLATE).navigation(this@OrderInquiryActivity, 1)
            }

            R.id.rfl_upload -> {
                val orderList = orderInquiryAdapter?.getUploadOrderList()
                if (orderList?.size == 0) {
                    ToastUtil.showMiddleToast(i18N(com.kernal.demo.base.R.string.请选择需要上传的订单))
                    return
                }
                DialogHelp.Builder().setTitle(i18N(com.kernal.demo.base.R.string.是否立即上传))
                    .setLeftMsg(i18N(com.kernal.demo.base.R.string.取消))
                    .setRightMsg(i18N(com.kernal.demo.base.R.string.确定)).setCancelable(true)
                    .setOnButtonClickLinsener(object : DialogHelp.OnButtonClickLinsener {
                        override fun onLeftClickLinsener(msg: String) {
                        }

                        override fun onRightClickLinsener(msg: String) {
                            showProgressDialog(20000)
                            val param = HashMap<String, Any>()
                            val jsonobject = JSONObject()
                            jsonobject["orderNoList"] = orderList?.joinToString(separator = ",") { it.orderNo }
                            param["attr"] = jsonobject
                            mViewModel.debtUpload(param)
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
                        binding.rflUpload.gone()
                        orderInquiryAdapter?.setNewInstance(null)
                        binding.rvOrders.gone()
                        binding.layoutNoData.root.show()
                        binding.srlOrder.finishRefresh()
                    } else {
                        binding.rflUpload.show()
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
            debtUploadLiveData.observe(this@OrderInquiryActivity) {
                dismissProgressDialog()
                if (it.result) {
                    ToastUtil.showMiddleToast(i18N(com.kernal.demo.base.R.string.上传成功))
                    orderInquiryAdapter?.clearUploadOrderList()
                    pageIndex = 1
                    orderList.clear()
                    query()
                } else {
                    orderInquiryAdapter?.clearUploadOrderList()
                    orderInquiryAdapter?.notifyDataSetChanged()
                    ToastUtil.showMiddleToast(i18N(com.kernal.demo.base.R.string.上传失败))
                }
            }
            errMsg.observe(this@OrderInquiryActivity) {
                dismissProgressDialog()
                ToastUtil.showMiddleToast(it.msg)
            }
            mException.observe(this@OrderInquiryActivity){
                dismissProgressDialog()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                val plate = data?.getStringExtra("plate")
                if (!plate.isNullOrEmpty()) {
                    val plateId = if (plate.contains("新能源")) {
                        plate.substring(plate.length - 8, plate.length)
                    } else {
                        plate.substring(plate.length.minus(7) ?: 0, plate.length)
                    }
                    binding.etSearch.setText(plateId)
                    binding.etSearch.setSelection(plateId.length)
                }
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

    override fun isRegEventBus(): Boolean {
        return true
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
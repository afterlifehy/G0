package com.kernal.demo.plateid.ui.activity.order

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.view.View
import android.view.View.OnClickListener
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSONObject
import com.kernal.demo.base.BaseApplication
import com.kernal.demo.base.arouter.ARouterMap
import com.kernal.demo.base.bean.PrintInfoBean
import com.kernal.demo.base.ds.PreferencesDataStore
import com.kernal.demo.base.ds.PreferencesKeys
import com.kernal.demo.base.ext.gone
import com.kernal.demo.base.ext.i18N
import com.kernal.demo.base.ext.i18n
import com.kernal.demo.base.ext.show
import com.kernal.demo.base.util.ToastUtil
import com.kernal.demo.base.viewbase.VbBaseActivity
import com.kernal.demo.common.util.BluePrint
import com.kernal.demo.common.util.GlideUtils
import com.kernal.demo.plateid.R
import com.kernal.demo.plateid.adapter.TransactionRecordAdapter
import com.kernal.demo.plateid.databinding.ActivityTransactionRecordBinding
import com.kernal.demo.plateid.mvvm.viewmodel.TransactionRecordViewModel
import com.tbruyelle.rxpermissions3.RxPermissions
import com.kernal.demo.base.bean.TicketPrintBean
import kotlinx.coroutines.runBlocking

@Route(path = ARouterMap.TRANSACTION_RECORD)
class TransactionRecordActivity : VbBaseActivity<TransactionRecordViewModel, ActivityTransactionRecordBinding>(), OnClickListener {
    var transactionRecordAdapter: TransactionRecordAdapter? = null
    var transactionRecordList: MutableList<TicketPrintBean> = ArrayList()
    var orderNo = ""
    var simId = ""

    override fun initView() {
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, com.kernal.demo.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = i18N(com.kernal.demo.base.R.string.交易记录信息)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.kernal.demo.base.R.color.white))

        orderNo = intent.getStringExtra(ARouterMap.TRANSACTION_RECORD_ORDER_NO).toString()

        binding.rvTransactionRecord.setHasFixedSize(true)
        binding.rvTransactionRecord.layoutManager = LinearLayoutManager(this@TransactionRecordActivity)
        transactionRecordAdapter = TransactionRecordAdapter(transactionRecordList, this)
        binding.rvTransactionRecord.adapter = transactionRecordAdapter
    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
    }

    override fun initData() {
        runBlocking {
            simId = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.simId)
        }
        query()
    }

    private fun query() {
        showProgressDialog(20000)
        val param = HashMap<String, Any>()
        val jsonobject = JSONObject()
        jsonobject["orderNo"] = orderNo
        jsonobject["simId"] = simId
        param["attr"] = jsonobject
        mViewModel.inquiryTransactionByOrderNo(param)
    }

    @SuppressLint("CheckResult")
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }

            R.id.fl_notification -> {
                val ticketPrintBean = v.tag as TicketPrintBean
                var rxPermissions = RxPermissions(this@TransactionRecordActivity)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    rxPermissions.request(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN).subscribe {
                        if (it) {
                            print(ticketPrintBean)
                        }
                    }
                } else {
                    print(ticketPrintBean)
                }
            }
        }
    }

    fun print(it: TicketPrintBean) {
        ToastUtil.showMiddleToast(i18n(com.kernal.demo.base.R.string.开始打印))
        val payMoney = it.payMoney
        val printInfo = PrintInfoBean(
            roadId = it.roadName,
            plateId = it.carLicense,
            payMoney = String.format("%.2f", payMoney.toFloat()),
            orderId = orderNo,
            phone = it.phone,
            startTime = it.startTime,
            leftTime = it.endTime,
            remark = it.remark,
            company = it.businessCname,
            oweCount = it.oweCount
        )
        Thread {
            BluePrint.instance?.zkblueprint(JSONObject.toJSONString(printInfo))
        }.start()
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            inquiryTransactionByOrderNoLiveData.observe(this@TransactionRecordActivity) {
                transactionRecordList.clear()
                transactionRecordList.addAll(it.result)
                if (transactionRecordList.size > 0) {
                    binding.rvTransactionRecord.show()
                    binding.layoutNoData.root.gone()
                    transactionRecordAdapter?.setList(transactionRecordList)
                } else {
                    binding.rvTransactionRecord.gone()
                    binding.layoutNoData.root.show()
                }
                dismissProgressDialog()
            }
            errMsg.observe(this@TransactionRecordActivity) {
                dismissProgressDialog()
                ToastUtil.showMiddleToast(it.msg)
            }
            mException.observe(this@TransactionRecordActivity){
                dismissProgressDialog()
            }
        }
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityTransactionRecordBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override val isFullScreen: Boolean
        get() = true

    override fun marginStatusBarView(): View {
        return binding.layoutToolbar.ablToolbar
    }

    override fun providerVMClass(): Class<TransactionRecordViewModel> {
        return TransactionRecordViewModel::class.java
    }
}
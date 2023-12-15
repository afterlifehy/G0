package ja.insepector.bxapp.ui.activity.income

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.view.View
import android.view.View.OnClickListener
import androidx.appcompat.widget.Toolbar
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSONObject
import com.blankj.utilcode.constant.TimeConstants
import com.blankj.utilcode.util.TimeUtils
import ja.insepector.base.BaseApplication
import ja.insepector.base.arouter.ARouterMap
import ja.insepector.base.bean.IncomeCountingBean
import ja.insepector.base.ds.PreferencesDataStore
import ja.insepector.base.ds.PreferencesKeys
import ja.insepector.base.ext.i18N
import ja.insepector.base.ext.i18n
import ja.insepector.base.util.ToastUtil
import ja.insepector.base.viewbase.VbBaseActivity
import ja.insepector.common.realm.RealmUtil
import ja.insepector.common.util.BluePrint
import ja.insepector.bxapp.R
import ja.insepector.bxapp.databinding.ActivityIncomeCountingBinding
import ja.insepector.bxapp.mvvm.viewmodel.IncomeCountingViewModel
import ja.insepector.bxapp.pop.DatePop
import com.tbruyelle.rxpermissions3.RxPermissions
import ja.insepector.base.ext.show
import ja.insepector.bxapp.dialog.PromptDialog
import ja.insepector.common.util.GlideUtils
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat

@Route(path = ARouterMap.INCOME_COUNTING)
class IncomeCountingActivity : VbBaseActivity<IncomeCountingViewModel, ActivityIncomeCountingBinding>(), OnClickListener {
    var datePop: DatePop? = null
    var startDate = ""
    var endDate = ""
    var streetNos = ""
    var incomeCountingBean: IncomeCountingBean? = null
    var loginName = ""
    var searchRange = "0"
    var promptDialog: PromptDialog? = null
    var colors = intArrayOf(ja.insepector.base.R.color.color_ff04a091, ja.insepector.base.R.color.color_ff04a091)
    var sizes = intArrayOf(27, 19)
    var colors2 = intArrayOf(ja.insepector.base.R.color.color_ff282828, ja.insepector.base.R.color.color_ff282828)
    var sizes2 = intArrayOf(23, 19)

    override fun initView() {
        binding.layoutToolbar.tvTitle.text = i18N(ja.insepector.base.R.string.营收盘点)
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivRight, ja.insepector.common.R.mipmap.ic_calendar)
        binding.layoutToolbar.ivRight.show()
    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
        binding.layoutToolbar.ivRight.setOnClickListener(this)
        binding.tvTotalIncomeTitle.setOnClickListener(this)
        binding.rtvPrint.setOnClickListener(this)
    }

    override fun initData() {
        runBlocking {
            loginName = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.loginName)
        }
        endDate = TimeUtils.millis2String(System.currentTimeMillis(), "yyyy-MM-dd")
        startDate = endDate.substring(0, 8) + "01"
        val streetList = RealmUtil.instance?.findCheckedStreetList()
        if (streetList != null) {
            streetNos = streetList.joinToString(separator = ",") { it.streetNo }
        }
        getIncomeCounting()
    }

    @SuppressLint("CheckResult")
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }

            R.id.iv_right -> {
                if (datePop == null) {
                    datePop = DatePop(BaseApplication.instance(), startDate, endDate, 1, object : DatePop.DateCallBack {
                        override fun selectDate(startTime: String, endTime: String) {
                            startDate = startTime
                            endDate = endTime
                            val difference = TimeUtils.getTimeSpan(endTime, startTime, SimpleDateFormat("yyyy-MM-dd"), TimeConstants.DAY)
                            if (difference > 90) {
                                ToastUtil.showMiddleToast(i18N(ja.insepector.base.R.string.查询时间间隔不得超过90天))
                                return
                            }
                            binding.rtvDateRange.text = "统计时间：${startDate}~${endDate}"
                            getIncomeCounting()
                        }

                    })
                }
                datePop?.showAsDropDown((v.parent) as Toolbar)
            }

            R.id.tv_totalIncomeTitle -> {
                promptDialog = PromptDialog(
                    i18N(ja.insepector.base.R.string.今日总收费和本月总收入均包含自主缴费金额和被追缴金额且不包含追缴他人金额),
                    "",
                    i18N(ja.insepector.base.R.string.确定),
                    object : PromptDialog.PromptCallBack {
                        override fun leftClick() {

                        }

                        override fun rightClick() {

                        }

                    }, true
                )
                promptDialog?.show()
            }

            R.id.rtv_print -> {
                incomeCountingBean?.loginName = loginName
                if (searchRange == "1") {
                    incomeCountingBean?.range = startDate + "~" + endDate
                }
//                incomeCountingBean?.list1 = todaySummaryList as ArrayList<Summary>
//                incomeCountingBean?.list2 = monthSummaryList as ArrayList<Summary>
                var str = "receipt,"
                var rxPermissions = RxPermissions(this@IncomeCountingActivity)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    rxPermissions.request(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN).subscribe {
                        if (it) {
                            ToastUtil.showMiddleToast(i18n(ja.insepector.base.R.string.开始打印))
                            Thread {
                                BluePrint.instance?.zkblueprint(str + JSONObject.toJSONString(incomeCountingBean))
                            }.start()
                        }
                    }
                } else {
                    ToastUtil.showMiddleToast(i18n(ja.insepector.base.R.string.开始打印))
                    Thread {
                        BluePrint.instance?.zkblueprint(str + JSONObject.toJSONString(incomeCountingBean))
                    }.start()
                }
            }
        }
    }

    fun getIncomeCounting() {
        showProgressDialog(20000)
        val param = HashMap<String, Any>()
        val jsonobject = JSONObject()
        jsonobject["streetNos"] = streetNos
        jsonobject["startDate"] = startDate
        jsonobject["endDate"] = endDate
        jsonobject["loginName"] = loginName
        jsonobject["searchRange"] = searchRange
        param["attr"] = jsonobject
        mViewModel.incomeCounting(param)
    }

    override fun startObserve() {
        mViewModel.apply {
            incomeCountingLiveData.observe(this@IncomeCountingActivity) {
                dismissProgressDialog()
//                incomeCountingBean = it
//                todaySummaryList.clear()
//                todaySummaryList.addAll(incomeCountingBean!!.list1)
//                todaySummaryAdapter?.setList(todaySummaryList)
//                if (searchRange == "1") {
//                    binding.rllMonth.show()
//                    monthSummaryList.clear()
//                    monthSummaryList.addAll(incomeCountingBean!!.list2)
//                    monthSummaryAdapter?.setList(monthSummaryList)
//                }
//                searchRange = "1"
            }
            errMsg.observe(this@IncomeCountingActivity) {
                dismissProgressDialog()
                ToastUtil.showMiddleToast(it.msg)
            }
        }
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityIncomeCountingBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override val isFullScreen: Boolean
        get() = true

    override fun marginStatusBarView(): View {
        return binding.layoutToolbar.ablToolbar
    }

    override fun providerVMClass(): Class<IncomeCountingViewModel> {
        return IncomeCountingViewModel::class.java
    }
}
package com.kernal.demo.plateid.ui.activity.order

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.fastjson.JSONObject
import com.kernal.demo.base.BaseApplication
import com.kernal.demo.base.arouter.ARouterMap
import com.kernal.demo.base.bean.DebtCollectionBean
import com.kernal.demo.base.ds.PreferencesDataStore
import com.kernal.demo.base.ds.PreferencesKeys
import com.kernal.demo.base.ext.gone
import com.kernal.demo.base.ext.i18N
import com.kernal.demo.base.ext.i18n
import com.kernal.demo.base.ext.show
import com.kernal.demo.base.ext.startArouter
import com.kernal.demo.base.util.ToastUtil
import com.kernal.demo.base.viewbase.VbBaseActivity
import com.kernal.demo.common.event.RefreshDebtOrderListEvent
import com.kernal.demo.common.util.GlideUtils
import com.kernal.demo.common.view.keyboard.KeyboardUtil
import com.kernal.demo.common.view.keyboard.MyOnTouchListener
import com.kernal.demo.common.view.keyboard.MyTextWatcher
import com.kernal.demo.plateid.R
import com.kernal.demo.plateid.adapter.DebtCollectionAdapter
import com.kernal.demo.plateid.databinding.ActivityDebtCollectionBinding
import com.kernal.demo.plateid.mvvm.viewmodel.DebtCollectionViewModel
import kotlinx.coroutines.runBlocking
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@Route(path = ARouterMap.DEBT_COLLECTION)
class DebtCollectionActivity : VbBaseActivity<DebtCollectionViewModel, ActivityDebtCollectionBinding>(), OnClickListener {
    private lateinit var keyboardUtil: KeyboardUtil
    var debtCollectionAdapter: DebtCollectionAdapter? = null
    var debtCollectionList: MutableList<DebtCollectionBean> = ArrayList()
    var carLicense = ""
    var simId = ""

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(refreshDebtOrderListEvent: RefreshDebtOrderListEvent) {
        if (carLicense.isEmpty()) {
            return
        }
        query()
    }

    override fun initView() {
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, com.kernal.demo.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = i18N(com.kernal.demo.base.R.string.欠费追缴)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.kernal.demo.base.R.color.white))
        GlideUtils.instance?.loadImage(binding.layoutNoData.ivNoData, com.kernal.demo.common.R.mipmap.ic_no_data_2)
        binding.layoutNoData.tvNoDataTitle.text = i18N(com.kernal.demo.base.R.string.通过车牌号未查询到欠费订单)

        if (intent.getStringExtra(ARouterMap.DEBT_CAR_LICENSE) != null) {
            carLicense = intent.getStringExtra(ARouterMap.DEBT_CAR_LICENSE).toString()
            binding.etSearch.setText(carLicense)
            binding.etSearch.setSelection(carLicense.length)
        }
        binding.rvDebt.setHasFixedSize(true)
        binding.rvDebt.layoutManager = LinearLayoutManager(this)
        debtCollectionAdapter = DebtCollectionAdapter(debtCollectionList, this)
        binding.rvDebt.adapter = debtCollectionAdapter

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
        binding.ivCamera.setOnClickListener(this)
        binding.tvSearch.setOnClickListener(this)
        binding.root.setOnClickListener(this)
        binding.layoutToolbar.toolbar.setOnClickListener(this)
    }

    override fun initData() {
        if (carLicense.isEmpty()) {
            return
        }
        query()
    }

    @SuppressLint("CheckResult")
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }

            R.id.iv_camera -> {
                ARouter.getInstance().build(ARouterMap.SCAN_PLATE).navigation(this@DebtCollectionActivity, 1)
            }

            R.id.tv_search -> {
                carLicense = binding.etSearch.text.toString()
                if (carLicense.isEmpty()) {
                    ToastUtil.showMiddleToast(i18n(com.kernal.demo.base.R.string.请输入车牌号))
                    return
                }
                if (carLicense.length != 7 && carLicense.length != 8) {
                    ToastUtil.showMiddleToast(i18N(com.kernal.demo.base.R.string.车牌长度只能是7位或8位))
                    return
                }
                query()
            }

            R.id.toolbar,
            binding.root.id -> {
                keyboardUtil.hideKeyboard()
            }

            R.id.rrl_debtCollection -> {
                val debtCollectionBean = v.tag as DebtCollectionBean
                startArouter(ARouterMap.DEBT_ORDER_DETAIL, data = Bundle().apply {
                    putParcelable(ARouterMap.DEBT_ORDER, debtCollectionBean)
                })
            }
        }
    }

    fun query() {
        keyboardUtil.hideKeyboard()
        showProgressDialog(20000)
        runBlocking {
            simId = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.simId)
            val param = HashMap<String, Any>()
            val jsonobject = JSONObject()
            jsonobject["simId"] = simId
            jsonobject["plateId"] = carLicense
            param["attr"] = jsonobject
            mViewModel.debtInquiry(param)
        }
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            debtInquiryLiveData.observe(this@DebtCollectionActivity) {
                dismissProgressDialog()
                debtCollectionList.clear()
                debtCollectionList.addAll(it.result)
                if (debtCollectionList.size > 0) {
                    binding.rvDebt.show()
                    binding.layoutNoData.root.gone()
                    debtCollectionAdapter?.setList(debtCollectionList)
                } else {
                    binding.rvDebt.gone()
                    binding.layoutNoData.root.show()
                }
            }
            errMsg.observe(this@DebtCollectionActivity) {
                dismissProgressDialog()
                ToastUtil.showMiddleToast(it.msg)
            }
            mException.observe(this@DebtCollectionActivity){
                dismissProgressDialog()
            }
        }
    }

    override fun providerVMClass(): Class<DebtCollectionViewModel> {
        return DebtCollectionViewModel::class.java
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
            } else if (requestCode == 2) {
//                val plate = data?.getStringExtra("plate")
//                if (!plate.isNullOrEmpty()) {
//                    val plateId = if (plate.contains("新能源")) {
//                        plate.substring(plate.length - 8, plate.length)
//                    } else {
//                        plate.substring(plate.length.minus(7) ?: 0, plate.length)
//                    }
//                    collectionDialog?.setPlate(plateId)
//                    if (plate.startsWith("蓝")) {
//                        collectionDialog?.collectionPlateColorAdapter?.updateColor(Constant.BLUE, 0)
//                    } else if (plate.startsWith("绿")) {
//                        collectionDialog?.collectionPlateColorAdapter?.updateColor(Constant.GREEN, 1)
//                    } else if (plate.startsWith("黄")) {
//                        collectionDialog?.collectionPlateColorAdapter?.updateColor(Constant.YELLOW, 2)
//                    } else if (plate.startsWith("黄绿")) {
//                        collectionDialog?.collectionPlateColorAdapter?.updateColor(Constant.YELLOW_GREEN, 3)
//                    } else if (plate.startsWith("白")) {
//                        collectionDialog?.collectionPlateColorAdapter?.updateColor(Constant.WHITE, 4)
//                    } else if (plate.startsWith("黑")) {
//                        collectionDialog?.collectionPlateColorAdapter?.updateColor(Constant.BLACK, 5)
//                    } else {
//                        collectionDialog?.collectionPlateColorAdapter?.updateColor(Constant.OTHERS, 6)
//                    }
//                }
            }

        }
    }

    override fun isRegEventBus(): Boolean {
        return true
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityDebtCollectionBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override val isFullScreen: Boolean
        get() = true

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
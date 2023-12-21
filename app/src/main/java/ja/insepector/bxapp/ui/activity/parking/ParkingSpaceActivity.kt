package ja.insepector.bxapp.ui.activity.parking

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.View.OnClickListener
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSONObject
import com.blankj.utilcode.util.EncodeUtils
import com.blankj.utilcode.util.ImageUtils
import com.tbruyelle.rxpermissions3.RxPermissions
import ja.insepector.base.BaseApplication
import ja.insepector.base.ds.PreferencesDataStore
import ja.insepector.base.ds.PreferencesKeys
import ja.insepector.base.ext.i18N
import ja.insepector.base.ext.show
import ja.insepector.base.util.ToastUtil
import ja.insepector.base.viewbase.VbBaseActivity
import ja.insepector.common.util.AppUtil
import ja.insepector.common.util.GlideUtils
import ja.insepector.bxapp.R
import ja.insepector.bxapp.databinding.ActivityParkingSpaceBinding
import ja.insepector.bxapp.mvvm.viewmodel.ParkingSpaceViewModel
import com.zrq.spanbuilder.TextStyle
import ja.insepector.base.arouter.ARouterMap
import ja.insepector.base.bean.ParkingSpaceBean
import ja.insepector.base.dialog.DialogHelp
import ja.insepector.base.ext.startArouter
import ja.insepector.bxapp.dialog.ExitMethodDialog
import ja.insepector.common.event.OrderFinishEvent
import kotlinx.coroutines.runBlocking
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@Route(path = ARouterMap.PARKING_SPACE)
class ParkingSpaceActivity : VbBaseActivity<ParkingSpaceViewModel, ActivityParkingSpaceBinding>(), OnClickListener {
    val sizes = intArrayOf(19, 19)
    val colors = intArrayOf(ja.insepector.base.R.color.color_ff666666, ja.insepector.base.R.color.color_ff1a1a1a)
    val colors2 = intArrayOf(ja.insepector.base.R.color.color_ff666666, ja.insepector.base.R.color.color_fff70f0f)
    val styles = arrayOf(TextStyle.NORMAL, TextStyle.BOLD)

    var orderNo = ""
    var carLicense = ""
    var parkingNo = ""
    var parkingSpaceBean: ParkingSpaceBean? = null

    var picBase64 = ""

    var exitMethodDialog: ExitMethodDialog? = null
    var exitMethodList: MutableList<String> = ArrayList()
    var currentMethod = ""

    var type = ""
    var simId = ""

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(orderFinishEvent: OrderFinishEvent) {
        onBackPressedSupport()
    }

    override fun initView() {
        orderNo = intent.getStringExtra(ARouterMap.ORDER_NO).toString()
        carLicense = intent.getStringExtra(ARouterMap.CAR_LICENSE).toString()
        parkingNo = intent.getStringExtra(ARouterMap.PARKING_NO).toString()
        binding.layoutToolbar.tvTitle.text = parkingNo
        binding.tvPlate.text = carLicense

        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, ja.insepector.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = parkingNo
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), ja.insepector.base.R.color.white))
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivRight, ja.insepector.common.R.mipmap.ic_pic)
        binding.layoutToolbar.ivRight.show()

    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
        binding.layoutToolbar.ivRight.setOnClickListener(this)
        binding.rrlArrears.setOnClickListener(this)
        binding.rrlExitMethod.setOnClickListener(this)
        binding.rlCamera.setOnClickListener(this)
        binding.rflNotification.setOnClickListener(this)
        binding.rflReport.setOnClickListener(this)
        binding.rflRenewal.setOnClickListener(this)
        binding.rflFinish.setOnClickListener(this)
    }

    override fun initData() {
        exitMethodList.add(i18N(ja.insepector.base.R.string.收费员不在场欠费驶离))
        exitMethodList.add(i18N(ja.insepector.base.R.string.正常缴费驶离))
        exitMethodList.add(i18N(ja.insepector.base.R.string.当面拒绝驶离))
        exitMethodList.add(i18N(ja.insepector.base.R.string.强制关单))
        exitMethodList.add(i18N(ja.insepector.base.R.string.线上支付))
        exitMethodList.add(i18N(ja.insepector.base.R.string.其他))

        runBlocking {
            simId = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.simId)
            parkingSpaceRequest()
        }
    }

    fun parkingSpaceRequest() {
        showProgressDialog(20000)
        val param = HashMap<String, Any>()
        val jsonobject = JSONObject()
        jsonobject["orderNo"] = orderNo
        jsonobject["simId"] = simId
        param["attr"] = jsonobject
        mViewModel.parkingSpace(param)
    }

    @SuppressLint("CheckResult")
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }

            R.id.iv_right -> {
                startArouter(ARouterMap.PIC)
            }

            R.id.rrl_arrears -> {
                startArouter(ARouterMap.DEBT_COLLECTION, data = Bundle().apply {
                    putString(ARouterMap.DEBT_CAR_LICENSE, carLicense)
                })
            }

            R.id.rrl_exitMethod -> {
                if (exitMethodDialog == null) {
                    exitMethodDialog = ExitMethodDialog(exitMethodList, currentMethod, object : ExitMethodDialog.ExitMethodCallBack {
                        override fun chooseExitMethod(method: String) {
                            currentMethod = method
                            binding.tvExitMethod.text = currentMethod
                        }
                    })
                }
                exitMethodDialog?.show()
            }

            R.id.rl_camera -> {
                var rxPermissions = RxPermissions(this@ParkingSpaceActivity)
                rxPermissions.request(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).subscribe {
                    if (it) {
                        takePhoto()
                    }
                }
            }

            R.id.rfl_notification -> {
                var rxPermissions = RxPermissions(this@ParkingSpaceActivity)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    rxPermissions.request(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN).subscribe {
                        if (it) {
//                            startPrint(payResultBean)
                        }
                    }
                } else {
//                    startPrint(it)
                }
            }

            R.id.rfl_report -> {
                startArouter(ARouterMap.ABNORMAL_REPORT)
            }

            R.id.rfl_renewal -> {
                startArouter(ARouterMap.PREPAID)
            }

            R.id.rfl_finish -> {
                type = AppUtil.fillZero((exitMethodList.indexOf(binding.tvExitMethod.text.toString()) + 1).toString())
                if (type == "02") {
                    startArouter(ARouterMap.ORDER_INFO)
                } else {
                    DialogHelp.Builder().setTitle(i18N(ja.insepector.base.R.string.是否确定结束订单))
                        .setLeftMsg(i18N(ja.insepector.base.R.string.取消))
                        .setRightMsg(i18N(ja.insepector.base.R.string.确定)).setCancelable(true)
                        .setOnButtonClickLinsener(object : DialogHelp.OnButtonClickLinsener {
                            override fun onLeftClickLinsener(msg: String) {
                            }

                            override fun onRightClickLinsener(msg: String) {
                                onBackPressedSupport()
                            }

                        }).build(this@ParkingSpaceActivity).showDailog()
                }
            }
        }
    }

    fun takePhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureLauncher.launch(takePictureIntent)
    }

    val takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as Bitmap
            val file = ImageUtils.save2Album(imageBitmap, Bitmap.CompressFormat.JPEG)
            val bytes = file?.readBytes()
            picBase64 = EncodeUtils.base64Encode2String(bytes)
        }
    }

    @SuppressLint("CheckResult")
    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            parkingSpaceLiveData.observe(this@ParkingSpaceActivity) {
                dismissProgressDialog()
                parkingSpaceBean = it.result
                binding.tvPlate.text = parkingSpaceBean?.carLicense

                val strings = arrayOf(i18N(ja.insepector.base.R.string.开始时间), parkingSpaceBean?.startTime.toString())
                binding.tvStartTime.text = AppUtil.getSpan(strings, sizes, colors)

                val strings2 =
                    arrayOf(
                        i18N(ja.insepector.base.R.string.预付金额),
                        AppUtil.keepNDecimals(parkingSpaceBean?.havePayMoney.toString(), 2) + "元"
                    )
                binding.tvPrepayAmount.text = AppUtil.getSpan(strings2, sizes, colors)

                val strings3 = arrayOf(i18N(ja.insepector.base.R.string.超时时长), parkingSpaceBean?.timeOut.toString())
                binding.tvTimeoutDuration.text = AppUtil.getSpan(strings3, sizes, colors)

                val strings4 = arrayOf(i18N(ja.insepector.base.R.string.待缴费用), "${parkingSpaceBean?.realtimeMoney}元")
                binding.tvPendingFee.text = AppUtil.getSpan(strings4, sizes, colors2, styles)

                binding.tvArrearsNum.text = "${parkingSpaceBean?.historyCount}笔"
                binding.tvArrearsAmount.text = "${parkingSpaceBean?.historySum}元"
            }
            errMsg.observe(this@ParkingSpaceActivity) {
                dismissProgressDialog()
                ToastUtil.showMiddleToast(it.msg)
            }
        }
    }

//    fun startPrint(it: PayResultBean) {
//        val payMoney = it.payMoney
//        val printInfo = PrintInfoBean(
//            roadId = it.roadName,
//            plateId = it.carLicense,
//            payMoney = String.format("%.2f", payMoney.toFloat()),
//            orderId = orderNo,
//            phone = it.phone,
//            startTime = it.startTime,
//            leftTime = it.endTime,
//            remark = it.remark,
//            company = it.businessCname,
//            oweCount = 0
//        )
//        Thread {
//            BluePrint.instance?.zkblueprint(printInfo.toString())
//        }.start()
//    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityParkingSpaceBinding.inflate(layoutInflater)
    }

    override fun isRegEventBus(): Boolean {
        return true
    }

    override fun onReloadData() {
    }

    override val isFullScreen: Boolean
        get() = true

    override fun marginStatusBarView(): View {
        return binding.layoutToolbar.ablToolbar
    }

    override fun providerVMClass(): Class<ParkingSpaceViewModel> {
        return ParkingSpaceViewModel::class.java
    }
}
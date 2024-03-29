package com.kernal.demo.plateid.ui.activity.parking

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.ArrayMap
import android.view.View
import android.view.View.OnClickListener
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSONObject
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.EncodeUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.TimeUtils
import com.tbruyelle.rxpermissions3.RxPermissions
import com.zrq.spanbuilder.TextStyle
import com.kernal.demo.base.BaseApplication
import com.kernal.demo.base.arouter.ARouterMap
import com.kernal.demo.base.bean.ExitMethodBean
import com.kernal.demo.base.bean.ParkingSpaceBean
import com.kernal.demo.base.bean.PrintInfoBean
import com.kernal.demo.base.bean.Street
import com.kernal.demo.base.bean.TicketPrintBean
import com.kernal.demo.base.dialog.DialogHelp
import com.kernal.demo.base.ds.PreferencesDataStore
import com.kernal.demo.base.ds.PreferencesKeys
import com.kernal.demo.base.ext.hide
import com.kernal.demo.base.ext.i18N
import com.kernal.demo.base.ext.show
import com.kernal.demo.base.ext.startArouter
import com.kernal.demo.base.util.ToastUtil
import com.kernal.demo.base.viewbase.VbBaseActivity
import com.kernal.demo.plateid.R
import com.kernal.demo.plateid.databinding.ActivityParkingSpaceBinding
import com.kernal.demo.plateid.dialog.ExitMethodDialog
import com.kernal.demo.plateid.mvvm.viewmodel.ParkingSpaceViewModel
import com.kernal.demo.common.event.AbnormalReportEvent
import com.kernal.demo.common.event.RefreshParkingSpaceEvent
import com.kernal.demo.common.realm.RealmUtil
import com.kernal.demo.common.util.AppUtil
import com.kernal.demo.common.util.BluePrint
import com.kernal.demo.common.util.Constant
import com.kernal.demo.common.util.FileUtil
import com.kernal.demo.common.util.GlideUtils
import com.kernal.demo.common.util.ImageCompressor
import com.kernal.demo.common.util.ImageUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Route(path = ARouterMap.PARKING_SPACE)
class ParkingSpaceActivity : VbBaseActivity<ParkingSpaceViewModel, ActivityParkingSpaceBinding>(), OnClickListener {
    val sizes = intArrayOf(19, 19)
    val colors = intArrayOf(com.kernal.demo.base.R.color.color_ff666666, com.kernal.demo.base.R.color.color_ff1a1a1a)
    val colors2 = intArrayOf(com.kernal.demo.base.R.color.color_ff666666, com.kernal.demo.base.R.color.color_fff70f0f)
    val styles = arrayOf(TextStyle.NORMAL, TextStyle.BOLD)

    var orderNo = ""
    var carLicense = ""
    var carColor = ""
    var parkingSpaceBean: ParkingSpaceBean? = null

    var picBase64 = ""
    var photoType = 20

    var exitMethodDialog: ExitMethodDialog? = null
    var exitMethodList: MutableList<ExitMethodBean> = ArrayList()
    var currentMethod: ExitMethodBean? = null

    var type = ""
    var simId = ""

    var isUpload = false
    var orderList: MutableList<String> = ArrayList()
    var currentStreet: Street? = null
    var plateLogoColorMap: MutableMap<String, Int> = ArrayMap()

    init {
        plateLogoColorMap[Constant.BLACK] = com.kernal.demo.base.R.color.black
        plateLogoColorMap[Constant.WHITE] = com.kernal.demo.base.R.color.white
        plateLogoColorMap[Constant.GREY] = com.kernal.demo.base.R.color.white
        plateLogoColorMap[Constant.RED] = com.kernal.demo.base.R.color.white
        plateLogoColorMap[Constant.BLUE] = com.kernal.demo.base.R.color.color_ff0046de
        plateLogoColorMap[Constant.YELLOW] = com.kernal.demo.base.R.color.color_fffda027
        plateLogoColorMap[Constant.ORANGE] = com.kernal.demo.base.R.color.white
        plateLogoColorMap[Constant.BROWN] = com.kernal.demo.base.R.color.white
        plateLogoColorMap[Constant.GREEN] = com.kernal.demo.base.R.color.color_ff09a95f
        plateLogoColorMap[Constant.PURPLE] = com.kernal.demo.base.R.color.white
        plateLogoColorMap[Constant.CYAN] = com.kernal.demo.base.R.color.white
        plateLogoColorMap[Constant.PINK] = com.kernal.demo.base.R.color.white
        plateLogoColorMap[Constant.TRANSPARENT] = com.kernal.demo.base.R.color.white
        plateLogoColorMap[Constant.OTHERS] = com.kernal.demo.base.R.color.white
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(refreshParkingSpaceEvent: RefreshParkingSpaceEvent) {
        parkingSpaceRequest()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(abnormalReportEvent: AbnormalReportEvent) {
        onBackPressedSupport()
    }

    override fun initView() {
        orderNo = intent.getStringExtra(ARouterMap.ORDER_NO).toString()
        carLicense = intent.getStringExtra(ARouterMap.CAR_LICENSE).toString()
        carColor = intent.getStringExtra(ARouterMap.CAR_COLOR).toString()

        if (carColor == Constant.YELLOW_GREEN) {
            binding.llCarColor.show()
            binding.rtvCarColor.delegate.setStrokeWidth(0)
            binding.rtvCarColor.delegate.setBackgroundColor(
                ContextCompat.getColor(
                    BaseApplication.instance(),
                    com.kernal.demo.base.R.color.transparent
                )
            )
            binding.rtvCarColor.delegate.init()
        } else {
            binding.llCarColor.hide()
            binding.rtvCarColor.delegate.setBackgroundColor(
                ContextCompat.getColor(
                    BaseApplication.instance(),
                    plateLogoColorMap[carColor]!!
                )
            )
            if (plateLogoColorMap[carColor]!! == com.kernal.demo.base.R.color.white) {
                binding.rtvCarColor.delegate.setStrokeWidth(1)
                binding.rtvCarColor.delegate.setTextColor(
                    ContextCompat.getColor(
                        BaseApplication.instance(),
                        com.kernal.demo.base.R.color.black
                    )
                )
            } else {
                binding.rtvCarColor.delegate.setStrokeWidth(0)
                binding.rtvCarColor.delegate.setTextColor(
                    ContextCompat.getColor(
                        BaseApplication.instance(),
                        com.kernal.demo.base.R.color.white
                    )
                )
            }
            binding.rtvCarColor.delegate.init()
        }
        binding.tvPlate.text = carLicense
        orderList.add(orderNo)

        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, com.kernal.demo.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.kernal.demo.base.R.color.white))
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivRight, com.kernal.demo.common.R.mipmap.ic_pic)
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
        currentStreet = RealmUtil.instance?.findCurrentStreet()

        exitMethodList.add(ExitMethodBean("2", i18N(com.kernal.demo.base.R.string.收费员不在场欠费驶离)))
        exitMethodList.add(ExitMethodBean("1", i18N(com.kernal.demo.base.R.string.正常缴费驶离)))
        exitMethodList.add(ExitMethodBean("3", i18N(com.kernal.demo.base.R.string.当面拒缴驶离)))
        exitMethodList.add(ExitMethodBean("0", i18N(com.kernal.demo.base.R.string.正常关单)))
        exitMethodList.add(ExitMethodBean("4", i18N(com.kernal.demo.base.R.string.强制关单)))
        exitMethodList.add(ExitMethodBean("5", i18N(com.kernal.demo.base.R.string.线上支付)))
        exitMethodList.add(ExitMethodBean("4", i18N(com.kernal.demo.base.R.string.其它)))

        currentMethod = exitMethodList[3]
        binding.tvExitMethod.text = currentMethod?.name
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
                startArouter(ARouterMap.PIC, data = Bundle().apply {
                    putString(ARouterMap.PIC_ORDER_NO, orderNo)
                })
            }

            R.id.rrl_arrears -> {
                if (parkingSpaceBean?.historyCount != 0) {
                    startArouter(ARouterMap.DEBT_COLLECTION, data = Bundle().apply {
                        putString(ARouterMap.DEBT_CAR_LICENSE, carLicense)
                    })
                } else {
                    ToastUtil.showMiddleToast(i18N(com.kernal.demo.base.R.string.当前车辆没有欠费记录))
                }
            }

            R.id.rrl_exitMethod -> {
                if (exitMethodDialog == null) {
                    exitMethodDialog = ExitMethodDialog(exitMethodList, currentMethod, object : ExitMethodDialog.ExitMethodCallBack {
                        override fun chooseExitMethod(method: ExitMethodBean) {
                            currentMethod = method
                            binding.tvExitMethod.text = currentMethod?.name
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
                            ticketPrintRequest()
                        }
                    }
                } else {
                    ticketPrintRequest()
                }
            }

            R.id.rfl_report -> {
                startArouter(ARouterMap.ABNORMAL_REPORT, data = Bundle().apply {
                    putString(ARouterMap.ABNORMAL_PARKING_NO, parkingSpaceBean?.parkingNo)
                    putString(ARouterMap.ABNORMAL_CARLICENSE, parkingSpaceBean?.carLicense)
                    putString(ARouterMap.ABNORMAL_CAR_COLOR, carColor)
                })
            }

            R.id.rfl_renewal -> {
                startArouter(ARouterMap.PREPAID, data = Bundle().apply {
                    if (parkingSpaceBean != null) {
                        if (BigDecimal(parkingSpaceBean!!.havePayMoney).toDouble() > 0.0) {
                            putDouble(ARouterMap.PREPAID_MIN_AMOUNT, 0.5)
                            putString(ARouterMap.PREPAID_CARLICENSE, parkingSpaceBean!!.carLicense)
                            putString(ARouterMap.PREPAID_PARKING_NO, parkingSpaceBean!!.parkingNo)
                            putString(ARouterMap.PREPAID_ORDER_NO, parkingSpaceBean!!.orderNo)
                            putString(ARouterMap.PREPAID_CAR_COLOR, carColor)
                        } else {
                            putDouble(ARouterMap.PREPAID_MIN_AMOUNT, 1.0)
                            putString(ARouterMap.PREPAID_CARLICENSE, parkingSpaceBean!!.carLicense)
                            putString(ARouterMap.PREPAID_PARKING_NO, parkingSpaceBean!!.parkingNo)
                            putString(ARouterMap.PREPAID_ORDER_NO, parkingSpaceBean!!.orderNo)
                            putString(ARouterMap.PREPAID_CAR_COLOR, carColor)
                        }
                    } else {
                        putDouble(ARouterMap.PREPAID_MIN_AMOUNT, 1.0)
                        putString(ARouterMap.PREPAID_CARLICENSE, "")
                        putString(ARouterMap.PREPAID_PARKING_NO, "")
                        putString(ARouterMap.PREPAID_ORDER_NO, "")
                        putString(ARouterMap.PREPAID_CAR_COLOR, carColor)
                    }
                })
            }

            R.id.rfl_finish -> {
                if (currentMethod == null) {
                    ToastUtil.showMiddleToast(i18N(com.kernal.demo.base.R.string.请选择离场方式))
                    return
                }
                type = currentMethod!!.id
                if (type == "3" && !isUpload) {
                    ToastUtil.showMiddleToast(i18N(com.kernal.demo.base.R.string.请先拍摄在场照片))
                    return
                }
                DialogHelp.Builder().setTitle(i18N(com.kernal.demo.base.R.string.是否确定结束订单))
                    .setLeftMsg(i18N(com.kernal.demo.base.R.string.取消))
                    .setRightMsg(i18N(com.kernal.demo.base.R.string.确定)).setCancelable(true)
                    .setOnButtonClickLinsener(object : DialogHelp.OnButtonClickLinsener {
                        override fun onLeftClickLinsener(msg: String) {
                        }

                        override fun onRightClickLinsener(msg: String) {
                            showProgressDialog(20000)
                            val param = HashMap<String, Any>()
                            val jsonobject = JSONObject()
                            jsonobject["carLicense"] = carLicense
                            jsonobject["orderNo"] = orderNo
                            jsonobject["parkingNo"] = parkingSpaceBean?.parkingNo
                            jsonobject["leftType"] = type
                            jsonobject["simId"] = simId
                            param["attr"] = jsonobject
                            mViewModel.endOrder(param)
                        }

                    }).build(this@ParkingSpaceActivity).showDailog()
            }
        }
    }

    fun takePhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile: File? = createImageFile()
        val photoURI: Uri = FileProvider.getUriForFile(
            this,
            "com.kernal.demo.plateid.fileprovider",
            photoFile!!
        )
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        takePictureIntent.putExtra("android.intent.extra.quickCapture", true)
        takePictureLauncher.launch(takePictureIntent)
    }

    val takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            ImageCompressor.compress(this@ParkingSpaceActivity, imageFile!!, object : ImageCompressor.CompressResult {
                override fun onSuccess(file: File) {
                    val waterContent1: String = currentStreet?.streetName + " " + parkingSpaceBean?.parkingNo
                    val waterContent2: String =
                        parkingSpaceBean?.carLicense + " " + TimeUtils.millis2String(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss")
                    var bitmapCompressed = ImageUtil.getCompressedImage(file.absolutePath, 945f, 1140f)
                    var bitmapWater = ImageUtil.addWaterMark3(
                        bitmapCompressed!!,
                        waterContent1,
                        waterContent2,
                        this@ParkingSpaceActivity
                    )
                    FileUtils.delete(imageFile)
                    val savedFile = FileUtil.FileSaveToInside("${parkingSpaceBean!!.orderNo}_20.png", bitmapWater)
                    picBase64 = FileUtil.fileToBase64(savedFile).toString()
                    uploadImg(parkingSpaceBean!!.orderNo, picBase64, "${parkingSpaceBean!!.orderNo}_20.png")
                }

                override fun onError(e: Throwable) {

                }

            })
        }
    }

    var imageFile: File? = null
    private fun createImageFile(): File? {
        // 创建图像文件名称
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        imageFile = File(storageDir, "PNG_${timeStamp}_${photoType}.png")
        return imageFile
    }

    fun uploadImg(orderNo: String, photo: String, name: String) {
        showProgressDialog(5000)
        val param = HashMap<String, Any>()
        val jsonobject = JSONObject()
        jsonobject["businessId"] = orderNo
        jsonobject["photoName"] = name
        jsonobject["photoType"] = photoType
        jsonobject["photoFormat"] = "png"
        jsonobject["photo"] = photo
        jsonobject["simId"] = simId
        param["attr"] = jsonobject
        mViewModel.picUpload(param)
    }

    fun ticketPrintRequest() {
        showProgressDialog(20000)
        val param = HashMap<String, Any>()
        val jsonobject = JSONObject()
        jsonobject["orderNo"] = orderNo
        jsonobject["simId"] = simId
        param["attr"] = jsonobject
        mViewModel.inquiryTransactionByOrderNo(param)
    }

    @SuppressLint("CheckResult")
    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            parkingSpaceLiveData.observe(this@ParkingSpaceActivity) {
                dismissProgressDialog()
                parkingSpaceBean = it
                binding.layoutToolbar.tvTitle.text = parkingSpaceBean?.parkingNo
                binding.tvPlate.text = parkingSpaceBean?.carLicense

                val strings = arrayOf(i18N(com.kernal.demo.base.R.string.开始时间), parkingSpaceBean?.startTime.toString())
                binding.tvStartTime.text = AppUtil.getSpan(strings, sizes, colors)

                val strings2 =
                    arrayOf(
                        i18N(com.kernal.demo.base.R.string.预付金额),
                        AppUtil.keepNDecimals(parkingSpaceBean?.havePayMoney.toString(), 2) + "元"
                    )
                binding.tvPrepayAmount.text = AppUtil.getSpan(strings2, sizes, colors)

                val strings3 = arrayOf(i18N(com.kernal.demo.base.R.string.超时时长), parkingSpaceBean?.timeOut.toString())
                binding.tvTimeoutDuration.text = AppUtil.getSpan(strings3, sizes, colors)

                val strings4 = arrayOf(
                    i18N(com.kernal.demo.base.R.string.待缴费用),
                    "${AppUtil.keepNDecimals(parkingSpaceBean?.realtimeMoney.toString(), 2)}元"
                )
                binding.tvPendingFee.text = AppUtil.getSpan(strings4, sizes, colors2, styles)

                binding.tvArrearsNum.text = "${parkingSpaceBean?.historyCount}笔"
                binding.tvArrearsAmount.text = "${parkingSpaceBean?.historySum}元"
            }
            endOrderLiveData.observe(this@ParkingSpaceActivity) {
                dismissProgressDialog()
                when (type) {
                    "1", "0" -> {
                        if (parkingSpaceBean?.realtimeMoney!!.toDouble() == 0.0) {
                            onBackPressedSupport()
                        } else {
                            startArouter(ARouterMap.ORDER_INFO, data = Bundle().apply {
                                putString(ARouterMap.ORDER_INFO_ORDER_NO, orderNo)
                            })
                            finish()
                        }
                    }

                    "2", "3", "5" -> {
                        if (parkingSpaceBean?.realtimeMoney!!.toDouble() == 0.0) {
                        } else {
                            val param = HashMap<String, Any>()
                            val jsonobject = JSONObject()
                            jsonobject["orderNoList"] = orderList.joinToString(separator = ",") { it }
                            param["attr"] = jsonobject
                            mViewModel.debtUpload(param)
                        }
                        onBackPressedSupport()
                    }

                    "9", "4" -> {
                        onBackPressedSupport()
                    }
                }
            }
            debtUploadLiveData.observe(this@ParkingSpaceActivity) {
            }
            picUploadLiveData.observe(this@ParkingSpaceActivity) {
                dismissProgressDialog()
                isUpload = true
            }
            inquiryTransactionByOrderNoLiveData.observe(this@ParkingSpaceActivity) {
                dismissProgressDialog()
                if (it.result != null && it.result.size > 0) {
                    performPrintTasks(it.result) {
//                        BluePrint.instance?.disConnect()
                    }
                }
            }
            errMsg.observe(this@ParkingSpaceActivity) {
                dismissProgressDialog()
                ToastUtil.showMiddleToast(it.msg)
            }
            mException.observe(this@ParkingSpaceActivity) {
                dismissProgressDialog()
            }
        }
    }

    fun performPrintTasks(printDataList: List<TicketPrintBean>, onComplete: () -> Unit) {
        val iterator = printDataList.iterator()

        fun printNext() {
            if (iterator.hasNext()) {
                val printData = iterator.next()
                startPrint(printData) {
                    // 打印完成后继续下一个打印
                    printNext()
                }
            } else {
                // 所有打印任务完成时调用 onComplete 回调
                onComplete()
            }
        }
        // 开始第一个打印任务
        printNext()
    }

    fun startPrint(it: TicketPrintBean, onComplete: () -> Unit) {
        val payMoney = it.payMoney
        val printInfo = PrintInfoBean(
            roadId = it.roadName,
            plateId = it.carLicense,
            payMoney = String.format("%.2f", payMoney.toFloat()),
            orderId = it.tradeNo,
            phone = it.phone,
            startTime = it.startTime,
            leftTime = it.endTime,
            remark = it.remark,
            company = it.businessCname,
            oweCount = 0
        )
        val printList = BluePrint.instance?.blueToothDevice!!
        if (printList.size == 1) {
            Thread {
                val device = printList[0]
                var connectResult = BluePrint.instance?.connet(device.address)
                if (connectResult == 0) {
                    BluePrint.instance?.zkblueprint(JSONObject.toJSONString(printInfo))
                }
            }.start()
        }
        GlobalScope.launch {
            delay(3000)
            // 执行打印完成后的回调
            onComplete()
        }

    }

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
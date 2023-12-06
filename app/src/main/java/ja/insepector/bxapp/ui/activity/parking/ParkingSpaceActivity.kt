package ja.insepector.bxapp.ui.activity.parking

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import android.view.View
import android.view.View.OnClickListener
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.alibaba.fastjson.JSONObject
import com.blankj.utilcode.util.EncodeUtils
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.UriUtils
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
import ja.insepector.base.ext.startAct
import ja.insepector.bxapp.dialog.ExitMethodDialog
import ja.insepector.bxapp.dialog.SelectPicDialog
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking

class ParkingSpaceActivity : VbBaseActivity<ParkingSpaceViewModel, ActivityParkingSpaceBinding>(), OnClickListener {
    var job: Job? = null
    val sizes = intArrayOf(19, 19)
    val colors = intArrayOf(ja.insepector.base.R.color.color_ff666666, ja.insepector.base.R.color.color_ff1a1a1a)
    val colors2 = intArrayOf(ja.insepector.base.R.color.color_ff666666, ja.insepector.base.R.color.color_fff70f0f)
    val styles = arrayOf(TextStyle.NORMAL, TextStyle.BOLD)

    var orderNo = ""
    var carLicense = ""
    var carColor = ""
    var parkingNo = ""
    var token = ""

    var selectPicDialog: SelectPicDialog? = null
    var picBase64 = ""

    var exitMethodDialog: ExitMethodDialog? = null
    var exitMethodList: MutableList<String> = ArrayList()
    var currentMethod = ""

    override fun initView() {
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

        showProgressDialog(20000)
        runBlocking {
            token = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.token)
            val param = HashMap<String, Any>()
            val jsonobject = JSONObject()
            jsonobject["token"] = token
            jsonobject["orderNo"] = orderNo
            jsonobject["carLicense"] = carLicense
            jsonobject["carColor"] = carColor
            param["attr"] = jsonobject
            mViewModel.parkingSpaceFee(param)
        }
    }

    @SuppressLint("CheckResult")
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }

            R.id.iv_right -> {
                startAct<PicActivity>()
            }

            R.id.rrl_arrears -> {
//                ARouter.getInstance().build(ARouterMap.DEBT_COLLECTION).withString(ARouterMap.DEBT_CAR_LICENSE, carLicense)
//                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
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
                        selectPicDialog = null
                        if (selectPicDialog == null) {
                            selectPicDialog = SelectPicDialog(object : SelectPicDialog.Callback {
                                override fun onTakePhoto() {
                                    takePhoto()
                                }

                                override fun onPickPhoto() {
                                    selectPhoto()
                                }
                            })
                        }
                        selectPicDialog?.show()
                    }
                }
            }

            R.id.rfl_notification -> {

            }

            R.id.rfl_report -> {
            }

            R.id.rfl_renewal -> {

            }

            R.id.rfl_finish -> {
            }
        }
    }

    fun takePhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureLauncher.launch(takePictureIntent)
    }

    fun selectPhoto() {
        selectImageLauncher.launch("image/*")
    }

    val takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as Bitmap
            val file = ImageUtils.save2Album(imageBitmap, Bitmap.CompressFormat.JPEG)
            val bytes = file?.readBytes()
            picBase64 = EncodeUtils.base64Encode2String(bytes)
        }
    }

    val selectImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val file = UriUtils.uri2File(it)
            val bytes = file?.readBytes()
            picBase64 = EncodeUtils.base64Encode2String(bytes)
        }
    }

    @SuppressLint("CheckResult")
    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            parkingSpaceFeeLiveData.observe(this@ParkingSpaceActivity) {
                dismissProgressDialog()
//                parkingSpaceBean = it
//                binding.tvPlate.text = it.carLicense

                val strings = arrayOf(i18N(ja.insepector.base.R.string.开始时间), "2023-06-30 10:12:24")
                binding.tvStartTime.text = AppUtil.getSpan(strings, sizes, colors)

                val strings2 = arrayOf(i18N(ja.insepector.base.R.string.预付金额), "15.00元")
                binding.tvPrepayAmount.text = AppUtil.getSpan(strings2, sizes, colors)

                val strings3 = arrayOf(i18N(ja.insepector.base.R.string.超时时长), "10小时20分钟04秒")
                binding.tvTimeoutDuration.text = AppUtil.getSpan(strings3, sizes, colors)

                val strings4 = arrayOf(i18N(ja.insepector.base.R.string.待缴费用), "15.00元")
                binding.tvPendingFee.text = AppUtil.getSpan(strings4, sizes, colors2, styles)

                binding.tvArrearsNum.text = "1笔"

                binding.tvArrearsAmount.text = "15.00元"

//                tradeNo = it.tradeNo
//                amountPending = it.amountPending
            }
            errMsg.observe(this@ParkingSpaceActivity) {
                dismissProgressDialog()
                ToastUtil.showToast(it.msg)
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
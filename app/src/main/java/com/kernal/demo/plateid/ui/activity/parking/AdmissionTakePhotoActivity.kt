package com.kernal.demo.plateid.ui.activity.parking

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.KeyEvent
import android.view.View
import android.view.View.OnClickListener
import android.widget.PopupWindow
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.fastjson.JSONObject
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.TimeUtils
import com.hyperai.hyperlpr3.settings.TypeDefine
import com.kernal.demo.base.BaseApplication
import com.kernal.demo.base.arouter.ARouterMap
import com.kernal.demo.base.bean.PlaceOederResultBean
import com.kernal.demo.base.bean.Street
import com.kernal.demo.base.dialog.DialogHelp
import com.kernal.demo.base.ds.PreferencesDataStore
import com.kernal.demo.base.ds.PreferencesKeys
import com.kernal.demo.base.ext.gone
import com.kernal.demo.base.ext.hide
import com.kernal.demo.base.ext.i18N
import com.kernal.demo.base.ext.i18n
import com.kernal.demo.base.ext.show
import com.kernal.demo.base.ext.startArouter
import com.kernal.demo.base.help.ActivityCacheManager
import com.kernal.demo.base.util.ToastUtil
import com.kernal.demo.base.viewbase.VbBaseActivity
import com.kernal.demo.common.realm.RealmUtil
import com.kernal.demo.plateid.R
import com.kernal.demo.plateid.adapter.CollectionPlateColorAdapter
import com.kernal.demo.plateid.databinding.ActivityAdmissionTakePhotoBinding
import com.kernal.demo.plateid.dialog.PromptDialog
import com.kernal.demo.plateid.mvvm.viewmodel.AdmissionTakePhotoViewModel
import com.kernal.demo.plateid.pop.MultipleSeatsPop
import com.kernal.demo.common.util.AppUtil
import com.kernal.demo.common.util.Constant
import com.kernal.demo.common.util.CountDownUtil
import com.kernal.demo.common.util.FileUtil
import com.kernal.demo.common.util.GlideUtils
import com.kernal.demo.common.util.ImageCompressor
import com.kernal.demo.common.util.ImageUtil
import com.kernal.demo.common.view.PlateView
import com.kernal.demo.common.view.keyboard.KeyboardUtil
import com.kernal.demo.plateid.ui.activity.login.LoginActivity
import kotlinx.coroutines.runBlocking
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Route(path = ARouterMap.ADMISSION_TAKE_PHOTO)
class AdmissionTakePhotoActivity : VbBaseActivity<AdmissionTakePhotoViewModel, ActivityAdmissionTakePhotoBinding>(), OnClickListener {
    private lateinit var keyboardUtil: KeyboardUtil
    var collectionPlateColorAdapter: CollectionPlateColorAdapter? = null
    var collectioPlateColorList: MutableList<String> = ArrayList()
    var checkedColor = ""
    val widthType = 2
    var parkingNo = ""
    var parkingAmount = 0

    var multipleSeatsPop: MultipleSeatsPop? = null
    var multipleSeat = ""

    var photoType = 10
    var plateBase64 = ""
    var panoramaBase64 = ""
    var plateImageBitmap: Bitmap? = null
    var panoramaImageBitmap: Bitmap? = null

    var promptDialog1: PromptDialog? = null
    var promptDialog2: PromptDialog? = null
    var simId = ""
    var vehicleType = "1"
    var extParkingNo = ""
    var loginName = ""
    var street: Street? = null
    var countDownUtil: CountDownUtil? = null
    var canGoBack = true

    override fun initView() {
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, com.kernal.demo.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = i18N(com.kernal.demo.base.R.string.入场拍照)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.kernal.demo.base.R.color.white))

        parkingNo = intent.getStringExtra(ARouterMap.ADMISSION_TAKE_PHOTO_PARKING_NO).toString()
        parkingAmount = intent.getIntExtra(ARouterMap.ADMISSION_TAKE_PHOTO_PARKING_AMOUNT, 0)
        collectioPlateColorList.add(Constant.BLUE)
        collectioPlateColorList.add(Constant.GREEN)
        collectioPlateColorList.add(Constant.YELLOW)
        collectioPlateColorList.add(Constant.YELLOW_GREEN)
        collectioPlateColorList.add(Constant.WHITE)
        collectioPlateColorList.add(Constant.BLACK)
        collectioPlateColorList.add(Constant.OTHERS)
        collectioPlateColorList.add(Constant.OTHERS_OLD)

        binding.rvPlateColor.setHasFixedSize(true)
        binding.rvPlateColor.layoutManager = LinearLayoutManager(BaseApplication.instance(), LinearLayoutManager.HORIZONTAL, false)
        collectionPlateColorAdapter = CollectionPlateColorAdapter(widthType, collectioPlateColorList, this)
        binding.rvPlateColor.adapter = collectionPlateColorAdapter

        binding.tvParkingNo.text = parkingNo
        street = RealmUtil.instance?.findCurrentStreet()
        binding.tvStreetName.text = street?.streetName
        binding.pvPlate.setPlateBgAndTxtColor(Constant.BLUE)

        initKeyboard()
    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
        binding.rflMultipleSeats.setOnClickListener(this)
        ClickUtils.applySingleDebouncing(binding.ivRecognize, 1000, this@AdmissionTakePhotoActivity)
        ClickUtils.applySingleDebouncing(binding.rflTakePhoto, 1000, this@AdmissionTakePhotoActivity)
        ClickUtils.applySingleDebouncing(binding.rflTakePhoto2, 1000, this@AdmissionTakePhotoActivity)
        ClickUtils.applySingleDebouncing(binding.rivPlate, 1000, this@AdmissionTakePhotoActivity)
        ClickUtils.applySingleDebouncing(binding.rivPanorama, 1000, this@AdmissionTakePhotoActivity)
        binding.rivPlate.setOnClickListener(this)
        binding.rivPanorama.setOnClickListener(this)
        binding.root.setOnClickListener(this)
        binding.layoutToolbar.toolbar.setOnClickListener(this)
        ClickUtils.applySingleDebouncing(binding.rflStartBilling, 1000, this@AdmissionTakePhotoActivity)
    }

    override fun initData() {
        runBlocking {
            simId = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.simId)
            loginName = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.loginName)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initKeyboard() {
        keyboardUtil = KeyboardUtil(binding.kvKeyBoard) {
            binding.pvPlate.requestFocus()
            keyboardUtil.changeKeyboard(true)
        }

        binding.pvPlate.setNumClickCallback(object : PlateView.NumClickCallback {
            override fun numberClick() {
                binding.pvPlate.requestFocus()
                keyboardUtil.showKeyboard(show = {
                    val location = IntArray(2)
                    binding.pvPlate.getLocationOnScreen(location)
                    val editTextPosY = location[1]

                    val screenHeight = window!!.windowManager.defaultDisplay.height
                    val distanceToBottom: Int = screenHeight - editTextPosY - binding.pvPlate.getHeight()

                    if (binding.kvKeyBoard.height > distanceToBottom) {
                        // 当键盘高度超过输入框到屏幕底部的距离时，向上移动布局
                        binding.flPlate.translationY = (-(binding.kvKeyBoard.height - distanceToBottom)).toFloat()
                    }
                }, hide = {
                    binding.flPlate.translationY = 0f
                    binding.pvPlate.stopAnimation()
                })
                keyboardUtil.changeKeyboard(true)
                keyboardUtil.setCallBack(object : KeyboardUtil.KeyInputCallBack {
                    override fun keyInput(value: String) {
                        binding.pvPlate.setOnePlate(value)
                        changePlateColor(binding.pvPlate.getPvTxt())
                    }

                    override fun keyDelete() {
                        binding.pvPlate.keyDelete()
                        changePlateColor(binding.pvPlate.getPvTxt())
                    }

                    override fun enterKey() {
                    }
                })
            }
        })
    }

    fun changePlateColor(plateId: String) {
        if (plateId.length < 8) {
            checkedColor = Constant.BLUE
        } else {
            checkedColor = Constant.GREEN
        }
        collectionPlateColorAdapter?.updateColor(checkedColor, collectioPlateColorList.indexOf(checkedColor))
        binding.pvPlate.setPlateBgAndTxtColor(checkedColor)
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

    @SuppressLint("NewApi")
    override fun onClick(v: View?) {
        if (keyboardUtil.isShow()) {
            keyboardUtil.hideKeyboard()
        }
        when (v?.id) {
            R.id.fl_back -> {
                canGoBack = true
                onBackPressedSupport()
            }

            R.id.rfl_multipleSeats -> {
                multipleSeatsPop =
                    MultipleSeatsPop(
                        this@AdmissionTakePhotoActivity,
                        parkingNo.substring(parkingNo.length - 3, parkingNo.length),
                        multipleSeat,
                        parkingAmount,
                        object : MultipleSeatsPop.MultipleSeatsCallback {
                            override fun selecctSeats(seat: String) {
                                multipleSeat = seat
                                if (multipleSeat.isNotEmpty()) {
                                    binding.tvMultipleSeats.text = ""
                                    binding.tvMultipleSeats.gone()
                                    binding.tvParkingNo.text = parkingNo + "-" + AppUtil.fillZero2(multipleSeat)
                                    val list = parkingNo.split("-")
                                    extParkingNo = "${list[0]}-${list[1]}-" + AppUtil.fillZero2(multipleSeat)
                                } else {
                                    binding.tvMultipleSeats.text = i18N(com.kernal.demo.base.R.string.多位)
                                    binding.tvMultipleSeats.show()
                                    binding.tvParkingNo.text = parkingNo
                                    extParkingNo = ""
                                }
                            }
                        })
                multipleSeatsPop?.showAsDropDown(v, (binding.rflMultipleSeats.width - SizeUtils.dp2px(92f)) / 2, SizeUtils.dp2px(3f))
                GlideUtils.instance?.loadImage(binding.ivArrow, com.kernal.demo.common.R.mipmap.ic_multiple_seat_arrow_up)
                multipleSeatsPop?.setOnDismissListener(object : PopupWindow.OnDismissListener {
                    override fun onDismiss() {
                        GlideUtils.instance?.loadImage(binding.ivArrow, com.kernal.demo.common.R.mipmap.ic_multiple_seat_arrow_down)
                    }
                })
            }

            R.id.iv_recognize -> {
                ARouter.getInstance().build(ARouterMap.SCAN_PLATE).navigation(this@AdmissionTakePhotoActivity, 1)
            }

            R.id.rfl_takePhoto -> {
                photoType = 10
                takePhoto()
            }

            R.id.rfl_takePhoto2 -> {
                photoType = 11
                takePhoto()
            }

            R.id.rfl_startBilling -> {
                if (binding.pvPlate.getPvTxt().isEmpty()) {
                    ToastUtil.showBottomToast(i18N(com.kernal.demo.base.R.string.请输入车牌号))
                    return
                }
                if(!binding.pvPlate.isCompliant()){
                    ToastUtil.showBottomToast("车牌格式不合规")
                    return
                }
                if (binding.pvPlate.getPvTxt().length != 7 && binding.pvPlate.getPvTxt().length != 8) {
                    ToastUtil.showBottomToast(i18N(com.kernal.demo.base.R.string.车牌长度只能是7位或8位))
                    return
                }
                if ((binding.pvPlate.getPvTxt().length == 8 && checkedColor == Constant.BLUE) || (binding.pvPlate.getPvTxt().length < 8 && checkedColor == Constant.GREEN)) {
                    ToastUtil.showBottomToast("车牌与车牌颜色不匹配")
                    return
                }
                if (checkedColor.isEmpty()) {
                    ToastUtil.showBottomToast(i18n(com.kernal.demo.base.R.string.请选择车牌颜色))
                    return
                }
                if (plateImageBitmap == null) {
                    ToastUtil.showBottomToast(i18n(com.kernal.demo.base.R.string.请上传车牌照))
                    return
                }
                if (panoramaImageBitmap == null) {
                    ToastUtil.showBottomToast(i18n(com.kernal.demo.base.R.string.请上传全景照))
                    return
                }
                DialogHelp.Builder().setTitle(i18N(com.kernal.demo.base.R.string.是否确认下单))
                    .setRightMsg(i18N(com.kernal.demo.base.R.string.确定))
                    .setLeftMsg(i18N(com.kernal.demo.base.R.string.取消)).setCancelable(true)
                    .setOnButtonClickLinsener(object : DialogHelp.OnButtonClickLinsener {
                        override fun onLeftClickLinsener(msg: String) {
                        }

                        override fun onRightClickLinsener(msg: String) {
                            showProgressDialog(20000)
                            binding.rflStartBilling.delegate.setBackgroundColor(
                                ContextCompat.getColor(
                                    BaseApplication.instance(),
                                    com.kernal.demo.base.R.color.black_10_color
                                )
                            )
                            binding.rflStartBilling.delegate.init()
                            binding.rflStartBilling.setOnClickListener(null)
                            countDownUtil = CountDownUtil(20000, 1000, object : CountDownUtil.TimeCallBack {
                                override fun onTimeOut() {
                                    binding.rflStartBilling.delegate.setBackgroundColor(
                                        ContextCompat.getColor(
                                            BaseApplication.instance(),
                                            com.kernal.demo.base.R.color.color_ffea9a00
                                        )
                                    )
                                    binding.tvStartBilling.text = i18N(com.kernal.demo.base.R.string.开始计费)
                                    binding.rflStartBilling.delegate.init()
                                    binding.rflStartBilling.setOnClickListener(this@AdmissionTakePhotoActivity)
                                }

                                override fun onTimeTick(millisUntilFinished: Long) {
                                    binding.tvStartBilling.text = if (millisUntilFinished == 0L) {
                                        i18N(com.kernal.demo.base.R.string.开始计费)
                                    } else {
                                        "${i18N(com.kernal.demo.base.R.string.开始计费)} ${millisUntilFinished / 1000}s"
                                    }
                                }

                            })
                            countDownUtil?.start()
                            val param = HashMap<String, Any>()
                            val jsonobject = JSONObject()
                            jsonobject["carLicense"] = binding.pvPlate.getPvTxt()
                            jsonobject["parkingNo"] = parkingNo
                            jsonobject["inputter"] = loginName
                            jsonobject["plateColor"] = checkedColor
                            jsonobject["vehicleType"] = vehicleType
                            jsonobject["extParkingNo"] = extParkingNo
                            jsonobject["simId"] = simId
                            param["attr"] = jsonobject
                            mViewModel.placeOrder(param)
                        }

                    }).build(this@AdmissionTakePhotoActivity).showDailog()
            }

            R.id.iv_plateDelete -> {
                binding.rflTakePhoto.show()
                binding.rflPlateImg.gone()
                plateImageBitmap = null
                plateBase64 = ""
            }

            R.id.iv_panoramaDelete -> {
                binding.rflTakePhoto2.show()
                binding.rflPanoramaImg.gone()
                panoramaImageBitmap = null
                panoramaBase64 = ""
            }

            R.id.riv_plate -> {
                photoType = 10
                takePhoto()
            }

            R.id.riv_panorama -> {
                photoType = 11
                takePhoto()
            }

            R.id.fl_color -> {
                checkedColor = v.tag as String
                collectionPlateColorAdapter?.updateColor(checkedColor, collectioPlateColorList.indexOf(checkedColor))
                binding.pvPlate.setPlateBgAndTxtColor(checkedColor)
            }

            R.id.toolbar,
            binding.root.id -> {

            }
        }
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            placeOrderLiveData.observe(this@AdmissionTakePhotoActivity) {
                dismissProgressDialog()
                countDownUtil?.onFinish()

                val plateSavedFile = FileUtil.FileSaveToInside("${it.orderNo}_10.png", plateImageBitmap!!)
                plateBase64 = FileUtil.fileToBase64(plateSavedFile).toString()
                uploadImg(it.orderNo, plateBase64, "${it.orderNo}_10.png", 10)

                val panoramaSavedFile = FileUtil.FileSaveToInside("${it.orderNo}_11.png", panoramaImageBitmap!!)
                panoramaBase64 = FileUtil.fileToBase64(panoramaSavedFile).toString()
                uploadImg(it.orderNo, panoramaBase64, "${it.orderNo}_11.png", 11)
                if (it.historyCount > 0) {
                    promptDialog1 = PromptDialog(
                        i18N(com.kernal.demo.base.R.string.下单成功当前车辆有欠费记录是否追缴),
                        i18N(com.kernal.demo.base.R.string.是),
                        i18N(com.kernal.demo.base.R.string.否),
                        object : PromptDialog.PromptCallBack {
                            override fun leftClick() {
                                startArouter(ARouterMap.DEBT_COLLECTION, data = Bundle().apply {
                                    putString(ARouterMap.DEBT_CAR_LICENSE, binding.pvPlate.getPvTxt())
                                })
                                finish()
                            }

                            override fun rightClick() {
                                showPrePayDialog(it)
                            }

                        })
                    promptDialog1?.show()
                    canGoBack = false
                } else {
                    showPrePayDialog(it)
                }
            }
            errMsg.observe(this@AdmissionTakePhotoActivity) {
                try {
                    dismissProgressDialog()
                    ToastUtil.showBottomToast(it.msg)
                    countDownUtil?.onFinish()
                    if (it.code == 2) {
                        DialogHelp.Builder().setTitle(it.msg)
                            .setRightMsg(i18N(com.kernal.demo.base.R.string.确定)).isAloneButton(true)
                            .setCancelable(false)
                            .setOnButtonClickLinsener(object : DialogHelp.OnButtonClickLinsener {
                                override fun onLeftClickLinsener(msg: String) {
                                }

                                override fun onRightClickLinsener(msg: String) {
                                    runBlocking {
                                        PreferencesDataStore(BaseApplication.instance()).putBoolean(PreferencesKeys.isUpdateLocation, false)
                                        PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.simId, "")
                                        PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.phone, "")
                                        PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.name, "")
                                        PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.loginName, "")
                                    }
                                    RealmUtil.instance?.deleteAllStreet()
                                    startArouter(ARouterMap.LOGIN)
                                    for (i in ActivityCacheManager.instance().getAllActivity()) {
                                        if (i !is LoginActivity) {
                                            i.finish()
                                        }
                                    }
                                }

                            }).build(this@AdmissionTakePhotoActivity).showDailog()
                    }
                } catch (_: Exception) {

                }
            }
            mException.observe(this@AdmissionTakePhotoActivity) {
                dismissProgressDialog()
                countDownUtil?.onFinish()
            }
        }
    }

    fun showPrePayDialog(it: PlaceOederResultBean) {
        promptDialog2 = PromptDialog(
            i18N(com.kernal.demo.base.R.string.下单成功是否预支付),
            i18N(com.kernal.demo.base.R.string.取消),
            i18N(com.kernal.demo.base.R.string.确定),
            object : PromptDialog.PromptCallBack {
                override fun leftClick() {
                    canGoBack = true
                    onBackPressedSupport()
                }

                override fun rightClick() {
                    startArouter(ARouterMap.PREPAID, data = Bundle().apply {
                        putDouble(ARouterMap.PREPAID_MIN_AMOUNT, 1.0)
                        putString(ARouterMap.PREPAID_CARLICENSE, binding.pvPlate.getPvTxt())
                        putString(ARouterMap.PREPAID_PARKING_NO, parkingNo)
                        putString(ARouterMap.PREPAID_ORDER_NO, it.orderNo)
                        putString(ARouterMap.PREPAID_CAR_COLOR, checkedColor)
                    })
                    finish()
                }

            })
        promptDialog2?.show()
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
        if (photoType == 10) {
            takePictureLauncher10.launch(takePictureIntent)
        } else {
            takePictureLauncher11.launch(takePictureIntent)
        }
    }

    val takePictureLauncher10 = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val bitmap = ImageUtil.getCompressedImage(imageFile10?.absolutePath, 200f, 300f)
            GlideUtils.instance?.loadImage(binding.rivPlate, bitmap)
            if (panoramaImageBitmap == null) {
                photoType = 11
                takePhoto()
            }
            ImageCompressor.compress(this@AdmissionTakePhotoActivity, imageFile10!!, object : ImageCompressor.CompressResult {
                override fun onSuccess(file: File) {
                    val waterContent1: String = street?.streetName + " " + parkingNo
                    val waterContent2: String =
                        binding.pvPlate.getPvTxt() + " " + TimeUtils.millis2String(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss")
                    val bitmapCompressed = ImageUtil.getCompressedImage(file.absolutePath, 945f, 1140f)
                    var bitmapWater = ImageUtil.addWaterMark3(
                        bitmapCompressed!!,
                        waterContent1,
                        waterContent2,
                        this@AdmissionTakePhotoActivity
                    )
                    FileUtils.delete(imageFile10)
                    binding.rflTakePhoto.hide()
                    binding.rflPlateImg.show()
                    plateImageBitmap = bitmapWater
                }

                override fun onError(e: Throwable) {

                }

            })
        }
    }

    val takePictureLauncher11 = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val bitmap = ImageUtil.getCompressedImage(imageFile11?.absolutePath, 200f, 300f)
            GlideUtils.instance?.loadImage(binding.rivPanorama, bitmap)
            ImageCompressor.compress(this@AdmissionTakePhotoActivity, imageFile11!!, object : ImageCompressor.CompressResult {
                override fun onSuccess(file: File) {
                    val waterContent1: String = street?.streetName + " " + parkingNo
                    val waterContent2: String =
                        binding.pvPlate.getPvTxt() + " " + TimeUtils.millis2String(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss")
                    val bitmapCompressed = ImageUtil.getCompressedImage(file.absolutePath, 945f, 1140f)
                    var bitmapWater = ImageUtil.addWaterMark3(
                        bitmapCompressed!!,
                        waterContent1,
                        waterContent2,
                        this@AdmissionTakePhotoActivity
                    )
                    binding.rflTakePhoto2.hide()
                    binding.rflPanoramaImg.show()
                    panoramaImageBitmap = bitmapWater
                    FileUtils.delete(imageFile11)
                }

                override fun onError(e: Throwable) {

                }

            })
        }
    }

    var imageFile10: File? = null
    var imageFile11: File? = null
    private fun createImageFile(): File? {
        // 创建图像文件名称
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (photoType == 10) {
            imageFile10 = File(storageDir, "PNG_${timeStamp}_${photoType}.png")
            return imageFile10
        } else {
            imageFile11 = File(storageDir, "PNG_${timeStamp}_${photoType}.png")
            return imageFile11
        }
    }

    fun uploadImg(orderNo: String, photo: String, name: String, type: Int) {
        val param = HashMap<String, Any>()
        val jsonobject = JSONObject()
        jsonobject["businessId"] = orderNo
        jsonobject["photoName"] = name
        jsonobject["photoType"] = type
        jsonobject["photoFormat"] = "png"
        jsonobject["photo"] = photo
        jsonobject["simId"] = simId
        param["attr"] = jsonobject
        mViewModel.picUpload(param)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                val plate = data?.getStringExtra("plate")
                val plateColor = data?.getIntExtra("plateColor", TypeDefine.PLATE_TYPE_BLUE)
                if (!plate.isNullOrEmpty()) {
                    binding.pvPlate.setAllPlate(plate)
                    when (plateColor) {
                        TypeDefine.PLATE_TYPE_UNKNOWN -> {
                            checkedColor = Constant.OTHERS
                            collectionPlateColorAdapter?.updateColor(checkedColor, 6)
                            binding.pvPlate.setPlateBgAndTxtColor(checkedColor)
                            if (plate.length == 8) {
                                changePlateColor(plate)
                            }
                        }

                        TypeDefine.PLATE_TYPE_BLUE -> {
                            checkedColor = Constant.BLUE
                            collectionPlateColorAdapter?.updateColor(checkedColor, 0)
                            binding.pvPlate.setPlateBgAndTxtColor(checkedColor)
                            if (plate.length == 8) {
                                changePlateColor(plate)
                            }
                        }

                        TypeDefine.PLATE_TYPE_YELLOW_SINGLE,
                        TypeDefine.PLATE_TYPE_YELLOW_DOUBLE -> {
                            checkedColor = Constant.YELLOW
                            collectionPlateColorAdapter?.updateColor(checkedColor, 2)
                            binding.pvPlate.setPlateBgAndTxtColor(checkedColor)
                            if (plate.length == 8) {
                                changePlateColor(plate)
                            }
                        }

                        TypeDefine.PLATE_TYPE_WHILE_SINGLE -> {
                            checkedColor = Constant.WHITE
                            collectionPlateColorAdapter?.updateColor(checkedColor, 4)
                            binding.pvPlate.setPlateBgAndTxtColor(checkedColor)
                            if (plate.length == 8) {
                                changePlateColor(plate)
                            }
                        }

                        TypeDefine.PLATE_TYPE_GREEN -> {
                            checkedColor = Constant.GREEN
                            collectionPlateColorAdapter?.updateColor(checkedColor, 1)
                            binding.pvPlate.setPlateBgAndTxtColor(checkedColor)
                            if (plate.length < 8) {
                                changePlateColor(plate)
                            }
                        }

                        TypeDefine.PLATE_TYPE_BLACK_HK_MACAO -> {
                            checkedColor = Constant.BLACK
                            collectionPlateColorAdapter?.updateColor(Constant.BLACK, 5)
                            binding.pvPlate.setPlateBgAndTxtColor(Constant.BLACK)
                            if (plate.length == 8) {
                                changePlateColor(plate)
                            }
                        }

                        TypeDefine.PLATE_TYPE_HK_SINGLE,
                        TypeDefine.PLATE_TYPE_HK_DOUBLE,
                        TypeDefine.PLATE_TYPE_MACAO_SINGLE,
                        TypeDefine.PLATE_TYPE_MACAO_DOUBLE -> {
                            checkedColor = Constant.WHITE
                            collectionPlateColorAdapter?.updateColor(checkedColor, 4)
                            binding.pvPlate.setPlateBgAndTxtColor(checkedColor)
                            if (plate.length == 8) {
                                changePlateColor(plate)
                            }
                        }
                    }
                    binding.rflTakePhoto.show()
                    binding.rflPlateImg.gone()
                    plateImageBitmap = null
                    plateBase64 = ""
                    binding.rflTakePhoto2.show()
                    binding.rflPanoramaImg.gone()
                    panoramaImageBitmap = null
                    panoramaBase64 = ""
                }
            }
        }
    }

    override fun providerVMClass(): Class<AdmissionTakePhotoViewModel>? {
        return AdmissionTakePhotoViewModel::class.java
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityAdmissionTakePhotoBinding.inflate(layoutInflater)
    }

    override val isFullScreen: Boolean
        get() = true

    override fun marginStatusBarView(): View {
        return binding.layoutToolbar.ablToolbar
    }

    override fun onBackPressedSupport() {
        if (canGoBack) {
            super.onBackPressedSupport()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (countDownUtil != null) {
            countDownUtil?.onFinish()
            countDownUtil = null
        }
        plateImageBitmap?.recycle()
        plateImageBitmap = null
        panoramaImageBitmap?.recycle()
        panoramaImageBitmap = null
        System.gc()
    }
}
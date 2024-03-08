package com.kernal.demo.plateid.ui.activity.abnormal

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.ArrayMap
import android.view.KeyEvent
import android.view.View
import android.view.View.OnClickListener
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.fastjson.JSONObject
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.TimeUtils
import com.kernal.demo.base.BaseApplication
import com.kernal.demo.base.arouter.ARouterMap
import com.kernal.demo.base.bean.Street
import com.kernal.demo.base.ds.PreferencesDataStore
import com.kernal.demo.base.ds.PreferencesKeys
import com.kernal.demo.base.ext.gone
import com.kernal.demo.base.ext.hide
import com.kernal.demo.base.ext.i18n
import com.kernal.demo.base.ext.show
import com.kernal.demo.base.ext.startArouter
import com.kernal.demo.base.util.ToastUtil
import com.kernal.demo.base.viewbase.VbBaseActivity
import com.kernal.demo.common.realm.RealmUtil
import com.kernal.demo.common.util.AppUtil
import com.kernal.demo.common.util.GlideUtils
import com.kernal.demo.common.view.keyboard.KeyboardUtil
import com.kernal.demo.common.view.keyboard.MyTextWatcher
import com.kernal.demo.plateid.R
import com.kernal.demo.plateid.adapter.CollectionPlateColorAdapter
import com.kernal.demo.plateid.databinding.ActivityAbnormalReportBinding
import com.kernal.demo.plateid.dialog.AbnormalClassificationDialog
import com.kernal.demo.plateid.dialog.AbnormalStreetListDialog
import com.kernal.demo.plateid.mvvm.viewmodel.AbnormalReportViewModel
import com.kernal.demo.common.event.AbnormalReportEvent
import com.kernal.demo.common.util.Constant
import com.kernal.demo.common.util.FileUtil
import com.kernal.demo.common.util.ImageCompressor
import com.kernal.demo.common.util.ImageUtil
import kotlinx.coroutines.runBlocking
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Route(path = ARouterMap.ABNORMAL_REPORT)
class AbnormalReportActivity : VbBaseActivity<AbnormalReportViewModel, ActivityAbnormalReportBinding>(), OnClickListener {
    var collectionPlateColorAdapter: CollectionPlateColorAdapter? = null
    var collectioPlateColorList: MutableList<String> = ArrayList()
    var checkedColor = ""
    private lateinit var keyboardUtil: KeyboardUtil
    val widthType = 1

    var abnormalStreetListDialog: AbnormalStreetListDialog? = null
    var streetList: MutableList<Street> = ArrayList()

    var abnormalClassificationDialog: AbnormalClassificationDialog? = null
    var classificationList: MutableList<String> = ArrayList()
    var currentStreet: Street? = null

    var parkingNo = ""
    var orderNo = ""
    var carColor = ""
    var carLicense = ""
    var type = ""
    var simId = ""
    var loginName = ""

    var photoType = 10
    var plateBase64 = ""
    var panoramaBase64 = ""
    var plateImageBitmap: Bitmap? = null
    var plateFileName = ""
    var panoramaFileName = ""
    var panoramaImageBitmap: Bitmap? = null
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

    override fun initView() {
        binding.layoutToolbar.tvTitle.text = i18n(com.kernal.demo.base.R.string.泊位异常上报)
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivRight, com.kernal.demo.common.R.mipmap.ic_help)
        binding.layoutToolbar.ivRight.show()

        if (intent.getStringExtra(ARouterMap.ABNORMAL_CARLICENSE) != null) {
            parkingNo = intent.getStringExtra(ARouterMap.ABNORMAL_PARKING_NO)!!
            carLicense = intent.getStringExtra(ARouterMap.ABNORMAL_CARLICENSE)!!
            checkedColor = intent.getStringExtra(ARouterMap.ABNORMAL_CAR_COLOR)!!
        }
        binding.etPlate.setText(carLicense)

        collectioPlateColorList.add(Constant.BLUE)
        collectioPlateColorList.add(Constant.GREEN)
        collectioPlateColorList.add(Constant.YELLOW)
        collectioPlateColorList.add(Constant.YELLOW_GREEN)
        collectioPlateColorList.add(Constant.WHITE)
        collectioPlateColorList.add(Constant.BLACK)
        collectioPlateColorList.add(Constant.OTHERS)
        binding.rvPlateColor.isNestedScrollingEnabled = false
        binding.rvPlateColor.setHasFixedSize(true)
        binding.rvPlateColor.layoutManager = LinearLayoutManager(BaseApplication.instance(), LinearLayoutManager.HORIZONTAL, false)
        collectionPlateColorAdapter = CollectionPlateColorAdapter(widthType, collectioPlateColorList, this)
        binding.rvPlateColor.adapter = collectionPlateColorAdapter

        if (checkedColor == Constant.BLUE) {
            collectionPlateColorAdapter?.updateColor(checkedColor, 0)
        } else if (checkedColor == Constant.GREEN) {
            collectionPlateColorAdapter?.updateColor(checkedColor, 1)
        } else if (checkedColor == Constant.YELLOW) {
            collectionPlateColorAdapter?.updateColor(checkedColor, 2)
        } else if (checkedColor == Constant.YELLOW_GREEN) {
            collectionPlateColorAdapter?.updateColor(checkedColor, 3)
        } else if (checkedColor == Constant.WHITE) {
            collectionPlateColorAdapter?.updateColor(checkedColor, 4)
        } else if (checkedColor == Constant.BLACK) {
            collectionPlateColorAdapter?.updateColor(checkedColor, 5)
        } else {
            collectionPlateColorAdapter?.updateColor(checkedColor, 6)
        }
        updateColor()
        initKeyboard()
    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
        binding.layoutToolbar.ivRight.setOnClickListener(this)
        binding.cbLotName.setOnClickListener(this)
        binding.cbAbnormalClassification.setOnClickListener(this)
        binding.rflAbnormalClassification.setOnClickListener(this)
        binding.rflRecognize.setOnClickListener(this)
        binding.rflTakePhoto.setOnClickListener(this)
        binding.rflTakePhoto2.setOnClickListener(this)
        binding.ivPlateDelete.setOnClickListener(this)
        binding.ivPanoramaDelete.setOnClickListener(this)
        binding.rivPlate.setOnClickListener(this)
        binding.rivPanorama.setOnClickListener(this)
        binding.rflReport.setOnClickListener(this)
        binding.root.setOnClickListener(this)
        binding.llBerthAbnormal2.setOnClickListener(this)
    }

    override fun initData() {
        RealmUtil.instance?.findCheckedStreetList()?.let { streetList.addAll(it) }
        runBlocking {
            simId = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.simId)
            loginName = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.loginName)
        }
        currentStreet = RealmUtil.instance?.findCurrentStreet()
        if (streetList.size == 1) {
            binding.cbLotName.hide()
            binding.rflLotName.setOnClickListener(null)
        } else {
            binding.cbLotName.show()
            binding.rflLotName.setOnClickListener(this)
        }
        binding.tvLotName.text = currentStreet?.streetName
        if (currentStreet?.streetNo!!.contains("_")) {
            val index = currentStreet?.streetNo!!.indexOf("_")
            val newStreetNo = currentStreet?.streetNo!!.substring(0, index)
            binding.rtvStreetNo.text = newStreetNo
            binding.retParkingNo.setText(parkingNo.replaceFirst(newStreetNo, "").replace("-", ""))
        } else {
            binding.rtvStreetNo.text = currentStreet?.streetNo
            binding.retParkingNo.setText(parkingNo.replaceFirst(currentStreet?.streetNo.toString(), "").replace("-", ""))
        }

        classificationList.add(i18n(com.kernal.demo.base.R.string.无法关单))
        classificationList.add(i18n(com.kernal.demo.base.R.string.订单丢失))
        classificationList.add(i18n(com.kernal.demo.base.R.string.车牌录入错误))
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initKeyboard() {
        keyboardUtil = KeyboardUtil(binding.kvKeyBoard) {
            binding.etPlate.requestFocus()
            keyboardUtil.changeKeyboard(true)
            keyboardUtil.setEditText(binding.etPlate)
        }

        binding.etPlate.addTextChangedListener(MyTextWatcher(null, null, true, keyboardUtil))

        binding.etPlate.setOnTouchListener { v, p1 ->
            (v as EditText).requestFocus()
            keyboardUtil.changeKeyboard(true)
            val clickPosition = v.getOffsetForPosition(p1!!.x, p1.y)
            keyboardUtil.setEditText(v, clickPosition)
            keyboardUtil.showKeyboard(show = {
                val location = IntArray(2)
                v.getLocationOnScreen(location)
                val editTextPosY = location[1]

                val screenHeight = window!!.windowManager.defaultDisplay.height
                val distanceToBottom: Int = screenHeight - editTextPosY - v.getHeight()

                if (binding.kvKeyBoard.height > distanceToBottom) {
                    // 当键盘高度超过输入框到屏幕底部的距离时，向上移动布局
                    binding.llBerthAbnormal.translationY = (-(binding.kvKeyBoard.height - distanceToBottom)).toFloat()
                }
            }, hide = {
                binding.llBerthAbnormal.translationY = 0f
            })
            true
        }
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

    override fun onClick(v: View?) {
        if (keyboardUtil.isShow()) {
            keyboardUtil.hideKeyboard()
        }
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }

            R.id.iv_right -> {
                startArouter(ARouterMap.ABNORMAL_HELP)
            }

            R.id.cb_lotName -> {
                showAbnormalStreetListDialog()
            }

            R.id.rfl_lotName -> {
                binding.cbLotName.isChecked = true
                showAbnormalStreetListDialog()
            }

            R.id.cb_abnormalClassification -> {
                showAbnormalClassificationDialog()
            }

            R.id.rfl_abnormalClassification -> {
                binding.cbAbnormalClassification.isChecked = true
                showAbnormalClassificationDialog()
            }

            R.id.rfl_recognize -> {
                ARouter.getInstance().build(ARouterMap.SCAN_PLATE).navigation(this@AbnormalReportActivity, 1)
            }

            R.id.rfl_takePhoto -> {
                photoType = 10
                takePhoto()
            }

            R.id.rfl_takePhoto2 -> {
                photoType = 11
                takePhoto()
            }

            R.id.iv_plateDelete -> {
                binding.rflTakePhoto.show()
                binding.rflPlateImg.gone()
                plateImageBitmap = null
                plateBase64 = ""
                plateFileName = ""
            }

            R.id.iv_panoramaDelete -> {
                binding.rflTakePhoto2.show()
                binding.rflPanoramaImg.gone()
                panoramaImageBitmap = null
                panoramaBase64 = ""
                panoramaFileName = ""
            }

            R.id.riv_plate -> {
                photoType = 10
                takePhoto()
            }

            R.id.riv_panorama -> {
                photoType = 11
                takePhoto()
            }

            R.id.rfl_report -> {
                type = AppUtil.fillZero((classificationList.indexOf(binding.tvAbnormalClassification.text.toString()) + 1).toString())
                if (binding.retParkingNo.text.toString().isEmpty()) {
                    ToastUtil.showMiddleToast(i18n(com.kernal.demo.base.R.string.请填写泊位号))
                    return
                }
                if (type == "00") {
                    ToastUtil.showMiddleToast(i18n(com.kernal.demo.base.R.string.请选择异常分类))
                    return
                }
                if (type == "03" && binding.etPlate.text.toString().isEmpty()) {
                    ToastUtil.showMiddleToast(i18n(com.kernal.demo.base.R.string.请填写车牌))
                    return
                }
                if (type == "03") {
                    if (binding.etPlate.text.toString().length != 7 && binding.etPlate.text.toString().length != 8) {
                        ToastUtil.showMiddleToast(i18n(com.kernal.demo.base.R.string.车牌长度只能是7位或8位))
                        return
                    }
                }
                if (type == "03" && checkedColor.isEmpty()) {
                    ToastUtil.showMiddleToast(i18n(com.kernal.demo.base.R.string.请选择车牌颜色))
                    return
                }
                if (type == "03" && plateImageBitmap == null) {
                    ToastUtil.showMiddleToast(i18n(com.kernal.demo.base.R.string.请上传车牌照))
                    return
                }
                if (type == "03" && panoramaImageBitmap == null) {
                    ToastUtil.showMiddleToast(i18n(com.kernal.demo.base.R.string.请上传全景照))
                    return
                }
                val param = HashMap<String, Any>()
                val jsonobject = JSONObject()
                jsonobject["parkingNo"] = currentStreet?.streetNo + "-" + fillZero(binding.retParkingNo.text.toString())
                param["attr"] = jsonobject
                showProgressDialog(20000)
                mViewModel.inquiryOrderNoByParkingNo(param)
            }

            R.id.ll_berthAbnormal2, binding.root.id -> {

            }

            R.id.fl_color -> {
                checkedColor = v.tag as String
                collectionPlateColorAdapter?.updateColor(checkedColor, collectioPlateColorList.indexOf(checkedColor))
                updateColor()
            }
        }
    }

    fun fillZero(value: String): String {
        if (value.length == 2) {
            return "0" + value
        } else if (value.length == 1) {
            return "00" + value
        } else {
            return value
        }
    }

    fun showAbnormalStreetListDialog() {
        abnormalStreetListDialog =
            AbnormalStreetListDialog(streetList, currentStreet!!, object : AbnormalStreetListDialog.AbnormalStreetCallBack {
                override fun chooseStreet(street: Street) {
                    currentStreet = street
                    binding.tvLotName.text = currentStreet?.streetName
                    binding.rtvStreetNo.text = currentStreet?.streetNo
                }
            })
        abnormalStreetListDialog?.show()
        abnormalStreetListDialog?.setOnDismissListener {
            binding.cbLotName.isChecked = false
        }
    }

    fun showAbnormalClassificationDialog() {
        abnormalClassificationDialog = AbnormalClassificationDialog(classificationList,
            binding.tvAbnormalClassification.text.toString(),
            object : AbnormalClassificationDialog.AbnormalClassificationCallBack {
                override fun chooseClassification(classification: String) {
                    binding.tvAbnormalClassification.text = classification
                    if (classification == i18n(com.kernal.demo.base.R.string.车牌录入错误)) {
                        binding.llPlate.show()
                        binding.rvPlateColor.show()
                        binding.rlTakePhoto.show()
                    } else {
                        binding.llPlate.gone()
                        binding.rvPlateColor.gone()
                        binding.rlTakePhoto.gone()
                    }
                }
            })
        abnormalClassificationDialog?.show()
        abnormalClassificationDialog?.setOnDismissListener {
            binding.cbAbnormalClassification.isChecked = false
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
        if (photoType == 10) {
            takePictureLauncher10.launch(takePictureIntent)
        } else {
            takePictureLauncher11.launch(takePictureIntent)
        }
    }

    val takePictureLauncher10 = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            ImageCompressor.compress(this@AbnormalReportActivity, imageFile10!!, object : ImageCompressor.CompressResult {
                override fun onSuccess(file: File) {
                    val waterContent1: String = currentStreet?.streetName + " " + parkingNo
                    val waterContent2: String =
                        binding.etPlate.text.toString() + " " + TimeUtils.millis2String(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss")
                    val bitmapCompressed = ImageUtil.getCompressedImage(file.absolutePath, 945f, 1140f)
                    var bitmapWater = ImageUtil.addWaterMark3(
                        bitmapCompressed!!,
                        waterContent1,
                        waterContent2,
                        this@AbnormalReportActivity
                    )
                    FileUtils.delete(imageFile10)
                    GlideUtils.instance?.loadImage(binding.rivPlate, bitmapWater)
                    binding.rflTakePhoto.hide()
                    binding.rflPlateImg.show()
                    plateImageBitmap = bitmapWater
                    if (panoramaImageBitmap == null) {
                        photoType = 11
                        takePhoto()
                    }
                }

                override fun onError(e: Throwable) {

                }

            })
        }
    }

    val takePictureLauncher11 = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            ImageCompressor.compress(this@AbnormalReportActivity, imageFile11!!, object : ImageCompressor.CompressResult {
                override fun onSuccess(file: File) {
                    val waterContent1: String = currentStreet?.streetName + " " + parkingNo
                    val waterContent2: String =
                        binding.etPlate.text.toString() + " " + TimeUtils.millis2String(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss")
                    val bitmapCompressed = ImageUtil.getCompressedImage(file.absolutePath, 945f, 1140f)
                    var bitmapWater = ImageUtil.addWaterMark3(
                        bitmapCompressed!!,
                        waterContent1,
                        waterContent2,
                        this@AbnormalReportActivity
                    )
                    GlideUtils.instance?.loadImage(binding.rivPanorama, bitmapWater)
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
                if (!plate.isNullOrEmpty()) {
                    val plateId = if (plate.contains("新能源")) {
                        plate.substring(plate.length - 8, plate.length)
                    } else {
                        plate.substring(plate.length.minus(7) ?: 0, plate.length)
                    }
                    binding.etPlate.setText(plateId)
                    binding.etPlate.setSelection(plateId.length)
                    if (plate.startsWith("蓝")) {
                        checkedColor = Constant.BLUE
                        collectionPlateColorAdapter?.updateColor(checkedColor, 0)
                    } else if (plate.startsWith("绿")) {
                        checkedColor = Constant.GREEN
                        collectionPlateColorAdapter?.updateColor(checkedColor, 1)
                    } else if (plate.startsWith("黄")) {
                        checkedColor = Constant.YELLOW
                        collectionPlateColorAdapter?.updateColor(checkedColor, 2)
                    } else if (plate.startsWith("黄绿")) {
                        checkedColor = Constant.YELLOW_GREEN
                        collectionPlateColorAdapter?.updateColor(checkedColor, 3)
                    } else if (plate.startsWith("白")) {
                        checkedColor = Constant.WHITE
                        collectionPlateColorAdapter?.updateColor(checkedColor, 4)
                    } else if (plate.startsWith("黑")) {
                        checkedColor = Constant.BLACK
                        collectionPlateColorAdapter?.updateColor(checkedColor, 5)
                    } else {
                        checkedColor = Constant.OTHERS
                        collectionPlateColorAdapter?.updateColor(checkedColor, 6)
                    }
                    updateColor()
                    binding.rflTakePhoto.show()
                    binding.rflPlateImg.gone()
                    plateImageBitmap = null
                    plateBase64 = ""
                    plateFileName = ""
                    binding.rflTakePhoto2.show()
                    binding.rflPanoramaImg.gone()
                    panoramaImageBitmap = null
                    panoramaBase64 = ""
                    panoramaFileName = ""
                }
            }
        }
    }

    fun updateColor() {
        if (checkedColor == Constant.YELLOW_GREEN) {
            binding.llCarColor.show()
            binding.flCarColor.show()
            binding.rtvCarColor.delegate.setBackgroundColor(
                ContextCompat.getColor(
                    BaseApplication.instance(),
                    com.kernal.demo.base.R.color.transparent
                )
            )
            binding.rtvCarColor.delegate.init()
        } else {
            if (checkedColor.isNotEmpty()) {
                binding.flCarColor.show()
                binding.llCarColor.hide()
                binding.rtvCarColor.delegate.setStrokeWidth(0)
                binding.rtvCarColor.delegate.setBackgroundColor(
                    ContextCompat.getColor(
                        BaseApplication.instance(),
                        plateLogoColorMap[checkedColor]!!
                    )
                )
                if (plateLogoColorMap[checkedColor]!! == com.kernal.demo.base.R.color.white) {
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
            } else {
                binding.flCarColor.hide()
            }
        }
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            inquiryOrderNoByParkingNoLiveData.observe(this@AbnormalReportActivity) {
                orderNo = it.orderNo
                val param = HashMap<String, Any>()
                val jsonobject = JSONObject()
                jsonobject["parkingNo"] = currentStreet?.streetNo + "-" + fillZero(binding.retParkingNo.text.toString())
                jsonobject["state"] = type
                jsonobject["remark"] = binding.retRemarks.text.toString()
                if (type == "03") {
                    jsonobject["carLicenseNew"] = binding.etPlate.text.toString()
                    jsonobject["carColor"] = checkedColor
                } else {
                    jsonobject["carLicenseNew"] = carLicense
                    jsonobject["carColor"] = carColor
                }
                jsonobject["loginName"] = loginName
                jsonobject["orderNo"] = orderNo
                param["attr"] = jsonobject
                mViewModel.abnormalReport(param)
            }
            abnormalReportLiveData.observe(this@AbnormalReportActivity) {
                dismissProgressDialog()
                if (type == "03") {
                    val plateSavedFile = FileUtil.FileSaveToInside("${orderNo}_10.png", plateImageBitmap!!)
                    plateBase64 = FileUtil.fileToBase64(plateSavedFile).toString()
                    uploadImg(orderNo, plateBase64, "${orderNo}_10.png", 10)

                    val panoramaSavedFile = FileUtil.FileSaveToInside("${orderNo}_11.png", panoramaImageBitmap!!)
                    panoramaBase64 = FileUtil.fileToBase64(panoramaSavedFile).toString()
                    uploadImg(orderNo, panoramaBase64, "${orderNo}_11.png", 11)
                }
                ToastUtil.showMiddleToast(i18n(com.kernal.demo.base.R.string.上报成功))
                EventBus.getDefault().post(AbnormalReportEvent())
                onBackPressedSupport()
            }
            errMsg.observe(this@AbnormalReportActivity) {
                dismissProgressDialog()
                ToastUtil.showMiddleToast(it.msg)
            }
            mException.observe(this@AbnormalReportActivity) {
                dismissProgressDialog()
            }
        }
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityAbnormalReportBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override val isFullScreen: Boolean
        get() = true

    override fun marginStatusBarView(): View {
        return binding.layoutToolbar.ablToolbar
    }

    override fun providerVMClass(): Class<AbnormalReportViewModel> {
        return AbnormalReportViewModel::class.java
    }

    override fun onDestroy() {
        super.onDestroy()
        plateImageBitmap?.recycle()
        plateImageBitmap = null
        panoramaImageBitmap?.recycle()
        panoramaImageBitmap = null
        System.gc()
    }
}
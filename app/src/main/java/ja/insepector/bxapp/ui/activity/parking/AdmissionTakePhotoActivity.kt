package ja.insepector.bxapp.ui.activity.parking

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import android.view.KeyEvent
import android.view.View
import android.view.View.OnClickListener
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.EncodeUtils
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.SizeUtils
import ja.insepector.base.BaseApplication
import ja.insepector.base.arouter.ARouterMap
import ja.insepector.base.ext.gone
import ja.insepector.base.ext.hide
import ja.insepector.base.ext.i18N
import ja.insepector.base.ext.show
import ja.insepector.base.ext.startArouter
import ja.insepector.base.viewbase.VbBaseActivity
import ja.insepector.bxapp.R
import ja.insepector.bxapp.adapter.CollectionPlateColorAdapter
import ja.insepector.bxapp.databinding.ActivityAdmissionTakePhotoBinding
import ja.insepector.bxapp.dialog.PromptDialog
import ja.insepector.bxapp.mvvm.viewmodel.AdmissionTakePhotoViewModel
import ja.insepector.bxapp.pop.MultipleSeatsPop
import ja.insepector.common.util.AppUtil
import ja.insepector.common.util.Constant
import ja.insepector.common.util.GlideUtils
import ja.insepector.common.view.keyboard.KeyboardUtil

@Route(path = ARouterMap.ADMISSION_TAKE_PHOTO)
class AdmissionTakePhotoActivity : VbBaseActivity<AdmissionTakePhotoViewModel, ActivityAdmissionTakePhotoBinding>(), OnClickListener {
    private lateinit var keyboardUtil: KeyboardUtil
    var collectionPlateColorAdapter: CollectionPlateColorAdapter? = null
    var collectioPlateColorList: MutableList<String> = ArrayList()
    var checkedColor = ""
    val widthType = 2
    var parkingNo = "JAZ02109"

    var multipleSeatsPop: MultipleSeatsPop? = null
    var multipleSeat = ""

    var takePhotoType = 0

    var promptDialog: PromptDialog? = null

    override fun initView() {
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, ja.insepector.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = i18N(ja.insepector.base.R.string.入场拍照)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), ja.insepector.base.R.color.white))

        collectioPlateColorList.add(Constant.BLUE)
        collectioPlateColorList.add(Constant.GREEN)
        collectioPlateColorList.add(Constant.YELLOW)
        collectioPlateColorList.add(Constant.YELLOW_GREEN)
        collectioPlateColorList.add(Constant.WHITE)
        collectioPlateColorList.add(Constant.BLACK)
        collectioPlateColorList.add(Constant.OTHERS)

        binding.rvPlateColor.setHasFixedSize(true)
        binding.rvPlateColor.layoutManager = LinearLayoutManager(BaseApplication.instance(), LinearLayoutManager.HORIZONTAL, false)
        collectionPlateColorAdapter = CollectionPlateColorAdapter(widthType, collectioPlateColorList, this)
        binding.rvPlateColor.adapter = collectionPlateColorAdapter

        binding.tvParkingNo.text = parkingNo
        binding.tvStreetName.text = "昌平路(西康路-常德路)"
        initKeyboard()
        binding.pvPlate.setPlateBgAndTxtColor(Constant.BLUE)

    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
        binding.rflMultipleSeats.setOnClickListener(this)
        binding.ivRecognize.setOnClickListener(this)
        binding.rflTakePhoto.setOnClickListener(this)
        binding.rflTakePhoto2.setOnClickListener(this)
        binding.ivPlateDelete.setOnClickListener(this)
        binding.ivPanoramaDelete.setOnClickListener(this)
        binding.rivPlate.setOnClickListener(this)
        binding.rivPanorama.setOnClickListener(this)
        binding.rflStartBilling.setOnClickListener(this)
        binding.root.setOnClickListener(this)
        binding.layoutToolbar.toolbar.setOnClickListener(this)
    }

    override fun initData() {
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initKeyboard() {
        keyboardUtil = KeyboardUtil(binding.kvKeyBoard) {
            binding.pvPlate.requestFocus()
            keyboardUtil.changeKeyboard(true)
        }

        binding.pvPlate.setOnTouchListener { v, p1 ->
            v.requestFocus()
            keyboardUtil.showKeyboard(show = {
                val location = IntArray(2)
                v.getLocationOnScreen(location)
                val editTextPosY = location[1]

                val screenHeight = window!!.windowManager.defaultDisplay.height
                val distanceToBottom: Int = screenHeight - editTextPosY - v.getHeight()

                if (binding.kvKeyBoard.height > distanceToBottom) {
                    // 当键盘高度超过输入框到屏幕底部的距离时，向上移动布局
                    binding.flPlate.translationY = (-(binding.kvKeyBoard.height - distanceToBottom)).toFloat()
                }
            }, hide = {
                binding.flPlate.translationY = 0f
            })
            keyboardUtil.changeKeyboard(true)
            keyboardUtil.setCallBack(object : KeyboardUtil.KeyInputCallBack {
                override fun keyInput(value: String) {
                    binding.pvPlate.setOnePlate(value)
                }

                override fun keyDelete() {
                    binding.pvPlate.keyDelete()
                }
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
            R.id.rfl_multipleSeats -> {
                multipleSeatsPop =
                    MultipleSeatsPop(this@AdmissionTakePhotoActivity, "09", multipleSeat, object : MultipleSeatsPop.MultipleSeatsCallback {
                        override fun selecctSeats(seat: String) {
                            multipleSeat = seat
                            binding.tvMultipleSeats.text = ""
                            binding.tvParkingNo.text = parkingNo + "-" + AppUtil.fillZero(multipleSeat)
                        }
                    })
                multipleSeatsPop?.showAsDropDown(v, (binding.rflMultipleSeats.width - SizeUtils.dp2px(92f)) / 2, SizeUtils.dp2px(3f))
            }

            R.id.iv_recognize -> {
                ARouter.getInstance().build(ARouterMap.SCAN_PLATE).navigation(this@AdmissionTakePhotoActivity, 1)
            }

            R.id.rfl_takePhoto -> {
                takePhotoType = 0
                takePhoto()
            }

            R.id.rfl_takePhoto2 -> {
                takePhotoType = 1
                takePhoto()
            }

            R.id.rfl_startBilling -> {
                promptDialog = PromptDialog(
                    i18N(ja.insepector.base.R.string.下单成功当前车辆有欠费记录是否追缴),
                    i18N(ja.insepector.base.R.string.是),
                    i18N(ja.insepector.base.R.string.否),
                    object : PromptDialog.PromptCallBack {
                        override fun leftClick() {

                        }

                        override fun rightClick() {
                            promptDialog = PromptDialog(
                                i18N(ja.insepector.base.R.string.下单成功是否预支付),
                                i18N(ja.insepector.base.R.string.取消),
                                i18N(ja.insepector.base.R.string.确定),
                                object : PromptDialog.PromptCallBack {
                                    override fun leftClick() {
                                        onBackPressedSupport()
                                    }

                                    override fun rightClick() {
                                        startArouter(ARouterMap.PREPAID)
                                    }

                                })
                            promptDialog?.show()
                        }

                    })
                promptDialog?.show()
            }

            R.id.iv_plateDelete -> {
                binding.rflTakePhoto.show()
                binding.rflPlateImg.hide()
            }

            R.id.iv_panoramaDelete -> {
                binding.rflTakePhoto2.show()
                binding.rflPanoramaImg.hide()
            }

            R.id.riv_plate -> {
                takePhotoType = 0
                takePhoto()
            }

            R.id.riv_panorama -> {
                takePhotoType = 1
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

    fun takePhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureLauncher.launch(takePictureIntent)
    }

    val takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as Bitmap
            if (takePhotoType == 0) {
                GlideUtils.instance?.loadImage(binding.rivPlate, imageBitmap)
                binding.rflTakePhoto.hide()
                binding.rflPlateImg.show()
            } else {
                GlideUtils.instance?.loadImage(binding.rivPanorama, imageBitmap)
                binding.rflTakePhoto2.hide()
                binding.rflPanoramaImg.show()
            }
            val file = ImageUtils.save2Album(imageBitmap, Bitmap.CompressFormat.JPEG)
            val bytes = file?.readBytes()
//            picBase64 = EncodeUtils.base64Encode2String(bytes)
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
                    binding.pvPlate.setAllPlate(plateId)
                    if (plate.startsWith("蓝")) {
                        collectionPlateColorAdapter?.updateColor(Constant.BLUE, 0)
                    } else if (plate.startsWith("绿")) {
                        collectionPlateColorAdapter?.updateColor(Constant.GREEN, 1)
                    } else if (plate.startsWith("黄")) {
                        collectionPlateColorAdapter?.updateColor(Constant.YELLOW, 2)
                    } else if (plate.startsWith("黄绿")) {
                        collectionPlateColorAdapter?.updateColor(Constant.YELLOW_GREEN, 3)
                    } else if (plate.startsWith("白")) {
                        collectionPlateColorAdapter?.updateColor(Constant.WHITE, 4)
                    } else if (plate.startsWith("黑")) {
                        collectionPlateColorAdapter?.updateColor(Constant.BLACK, 5)
                    } else {
                        collectionPlateColorAdapter?.updateColor(Constant.OTHERS, 6)
                    }
                }
            }
        }
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityAdmissionTakePhotoBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override val isFullScreen: Boolean
        get() = true

    override fun marginStatusBarView(): View {
        return binding.layoutToolbar.ablToolbar
    }
}
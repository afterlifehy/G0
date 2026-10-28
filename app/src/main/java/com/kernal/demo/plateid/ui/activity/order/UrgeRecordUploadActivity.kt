package com.kernal.demo.plateid.ui.activity.order

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.view.View.OnClickListener
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSONObject
import com.blankj.utilcode.util.EncodeUtils
import com.kernal.demo.base.BaseApplication
import com.kernal.demo.base.arouter.ARouterMap
import com.kernal.demo.base.bean.Street
import com.kernal.demo.base.bean.UrgeBean
import com.kernal.demo.base.ds.PreferencesDataStore
import com.kernal.demo.base.ds.PreferencesKeys
import com.kernal.demo.base.ext.gone
import com.kernal.demo.base.ext.show
import com.kernal.demo.base.util.ToastUtil
import com.kernal.demo.base.viewbase.VbBaseActivity
import com.kernal.demo.common.realm.RealmUtil
import com.kernal.demo.common.util.CompressUtil
import com.kernal.demo.common.util.Constant
import com.kernal.demo.common.util.FileUtil
import com.kernal.demo.common.util.GlideUtils
import com.kernal.demo.plateid.R
import com.kernal.demo.plateid.databinding.ActivityUrgeRecordUploadBinding
import com.kernal.demo.plateid.mvvm.viewmodel.UrgeRecordUploadViewModel
import com.tbruyelle.rxpermissions3.RxPermissions
import kotlinx.coroutines.runBlocking

@Route(path = ARouterMap.URGE_RECORD_UPLOAD)
class UrgeRecordUploadActivity : VbBaseActivity<UrgeRecordUploadViewModel, ActivityUrgeRecordUploadBinding>(),
    OnClickListener {
    var streetNo = ""
    var currentStreet: Street? = null
    var currentPic = 1
    var pic1Base64 = ""
    var pic2Base64 = ""
    var pic1Url = ""
    var pic2Url = ""
    var imageType = 0
    var loginName = ""
    var token = ""
    lateinit var urgeBean: UrgeBean
    val plateMap = mutableMapOf(
        Constant.BLUE to com.kernal.demo.common.R.mipmap.ic_plate_blue,
        Constant.GREEN to com.kernal.demo.common.R.mipmap.ic_plate_green,
        Constant.YELLOW to com.kernal.demo.common.R.mipmap.ic_plate_yellow,
        Constant.YELLOW_GREEN to com.kernal.demo.common.R.mipmap.ic_plate_yellow_green,
        Constant.WHITE to com.kernal.demo.common.R.mipmap.ic_plate_white,
        Constant.BLACK to com.kernal.demo.common.R.mipmap.ic_plate_black
    )

    override fun initView() {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, com.kernal.demo.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = "催缴记录上传"
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.kernal.demo.base.R.color.white))

        urgeBean = intent.getParcelableExtra(ARouterMap.URGE)!!
        binding.tvPlate.setText(urgeBean.plateId)
        if (urgeBean.plateColor == 33) {
            binding.flLin.show()
        } else {
            binding.flLin.gone()
            GlideUtils.instance?.loadImage(binding.ivPlateColor, plateMap[urgeBean.plateColor.toString()]!!)
        }
    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
        binding.rflSubmit.setOnClickListener(this)
        binding.tvPic1.setOnClickListener(this)
        binding.tvPic2.setOnClickListener(this)
        binding.rivPic1.setOnClickListener(this)
        binding.rivPic2.setOnClickListener(this)
    }

    override fun initData() {
        runBlocking {
            token = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.token)
            loginName = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.loginName)
        }
        currentStreet = RealmUtil.instance?.findCurrentStreet()
        streetNo = currentStreet!!.streetNo
        binding.tvStreetName.text = currentStreet?.streetName
    }

    @SuppressLint("CheckResult")
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }

            R.id.rfl_submit -> {
                submit()
            }

            R.id.toolbar,
            binding.root.id -> {
            }

            R.id.tv_pic1,
            R.id.riv_pic1 -> {
                var rxPermissions = RxPermissions(this@UrgeRecordUploadActivity)
                rxPermissions.request(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).subscribe {
                    if (it) {
                        currentPic = 1
                        takePhoto()
                    }
                }
            }

            R.id.tv_pic2,
            R.id.riv_pic2 -> {
                var rxPermissions = RxPermissions(this@UrgeRecordUploadActivity)
                rxPermissions.request(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).subscribe {
                    if (it) {
                        currentPic = 2
                        takePhoto()
                    }
                }
            }
        }
    }

    private var photoUri: Uri? = null

    fun takePhoto() {
        // 创建存放原图的文件
        val photoFile = FileUtil.createImageFile(this@UrgeRecordUploadActivity)
        photoUri = FileProvider.getUriForFile(
            this@UrgeRecordUploadActivity,
            // 这里替换成你AndroidManifest里配置的FileProvider authority
            "com.kernal.demo.plateid.fileprovider",
            photoFile
        )
        if (currentPic == 1) {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
            takePictureLauncher1.launch(takePictureIntent)
        } else {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
            takePictureLauncher2.launch(takePictureIntent)
        }
    }

    val takePictureLauncher1 = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            photoUri?.let { uri ->
                // 从uri读取原图bitmap（这里才是高清原图）
                val sourceBitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri))
                // 压缩到目标尺寸1440*1920
                val compressedBytes = CompressUtil.compressBitmapToTargetSize(sourceBitmap, 1440, 1920)
                val previewBmp = BitmapFactory.decodeByteArray(compressedBytes, 0, compressedBytes!!.size)
                binding.rivPic1.show()
                GlideUtils.instance?.loadImage(binding.rivPic1, previewBmp)
                val file = FileUtil.FileSaveToInside(
                    this@UrgeRecordUploadActivity,
                    "${urgeBean.urgePayId}_${System.currentTimeMillis()}_1.jpg",
                    compressedBytes
                )
                val bytes = file?.readBytes()
                pic1Base64 = EncodeUtils.base64Encode2String(bytes)
            }
        }
    }

    val takePictureLauncher2 = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            photoUri?.let { uri ->
                // 从uri读取原图bitmap（这里才是高清原图）
                val sourceBitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri))
                // 压缩到目标尺寸1440*1920
                val compressedBytes = CompressUtil.compressBitmapToTargetSize(sourceBitmap, 1440, 1920)
                val previewBmp = BitmapFactory.decodeByteArray(compressedBytes, 0, compressedBytes!!.size)
                binding.rivPic2.show()
                GlideUtils.instance?.loadImage(binding.rivPic2, previewBmp)
                val file = FileUtil.FileSaveToInside(
                    this@UrgeRecordUploadActivity,
                    "${urgeBean.urgePayId}_${System.currentTimeMillis()}_2.jpg",
                    compressedBytes
                )
                val bytes = file?.readBytes()
                pic2Base64 = EncodeUtils.base64Encode2String(bytes)
            }
        }
    }

    fun submit() {
        showProgressDialog(60000)
        val param = HashMap<String, Any>()
        param["token"] = token
        param["urgePayId"] = urgeBean.urgePayId
        param["roadId"] = streetNo
        param["photo1"] = pic1Base64
        param["photo2"] = pic2Base64
        param["photoType"] = "jpg"
        param["dataTime"] = System.currentTimeMillis()
        mViewModel.urgepay(param)
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            urgepayLiveData.observe(this@UrgeRecordUploadActivity) {
                imageType = 1
                val params = HashMap<String, Any>()
                params["fileName"] = "${urgeBean.urgePayId}_${System.currentTimeMillis()}_1.jpg"
                params["file"] = pic1Base64
                mViewModel.uploadFileForApp(params)
            }
            uploadFileForAppLiveData.observe(this@UrgeRecordUploadActivity) {
                if (imageType == 1) {
                    imageType = 2
                    pic1Url = it.URL
                    val params = HashMap<String, Any>()
                    params["fileName"] = "${urgeBean.urgePayId}_${System.currentTimeMillis()}_2.jpg"
                    params["file"] = pic2Base64
                    mViewModel.uploadFileForApp(params)
                } else {
                    pic2Url = it.URL
                    urgeNotice()
                }
            }
            urgeNoticeLiveData.observe(this@UrgeRecordUploadActivity) {
                dismissProgressDialog()
                ToastUtil.showBottomToast("催缴成功")
                onBackPressedSupport()
            }
            errMsg.observe(this@UrgeRecordUploadActivity) {
                dismissProgressDialog()
                ToastUtil.showBottomToast(it.msg)
            }
            mException.observe(this@UrgeRecordUploadActivity) {
                dismissProgressDialog()
            }
        }
    }

    fun urgeNotice() {
        val param = HashMap<String, Any>()
        val jsonObject = JSONObject()
        jsonObject["urgePayId"] = urgeBean.urgePayId
        jsonObject["roadId"] = streetNo
        jsonObject["photo1"] = pic1Url
        jsonObject["photo2"] = pic2Url
        jsonObject["photoType"] = "jpg"
        jsonObject["dataTime"] = System.currentTimeMillis()
        jsonObject["inputter"] = loginName
        param["attr"] = jsonObject
        mViewModel.urgeNotice(param)
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityUrgeRecordUploadBinding.inflate(layoutInflater)
    }

    override val isFullScreen: Boolean
        get() = true

    override fun marginStatusBarView(): View {
        return binding.layoutToolbar.ablToolbar
    }

    override fun providerVMClass(): Class<UrgeRecordUploadViewModel> {
        return UrgeRecordUploadViewModel::class.java
    }
}
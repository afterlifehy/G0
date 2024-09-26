package com.kernal.demo.plateid.ui.activity

import android.os.Environment
import android.view.View
import android.view.View.OnClickListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.kernal.demo.base.arouter.ARouterMap
import com.kernal.demo.base.util.LogFileUtil
import com.kernal.demo.base.viewbase.VbBaseActivity
import com.kernal.demo.plateid.R
import com.kernal.demo.plateid.adapter.LogFileListAdapter
import com.kernal.demo.plateid.databinding.ActivityLogListBinding
import com.kernal.demo.plateid.mvvm.viewmodel.LogFileListViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@Route(path = ARouterMap.LOG_FILE)
class LogFileListActivity : VbBaseActivity<LogFileListViewModel, ActivityLogListBinding>(), OnClickListener {
    var logFileListAdapter: LogFileListAdapter? = null
    var logFileList: MutableList<File> = ArrayList()

    override fun initView() {
        val logDir = File(Environment.getExternalStorageDirectory().absolutePath, LogFileUtil.LOG_DIR_NAME)
        if (logDir.exists() && logDir.isDirectory) {
            val files = logDir.listFiles()
            if (files != null && files.isNotEmpty()) {
                logFileList.addAll(files)
            } else {
            }
        } else {
        }
        logFileListAdapter = LogFileListAdapter(logFileList) {
            val param = HashMap<String, File>()
            param["file"] = it
            mViewModel.logFileUpload(prepareFilePart("file", it))
        }
        binding.rvFileList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@LogFileListActivity)
            adapter = logFileListAdapter
        }
    }

    fun prepareFilePart(partName: String, file: File): MultipartBody.Part {
        // 创建 RequestBody 实例，指定文件类型
        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())

        // 使用 MultipartBody.Part 封装文件
        return MultipartBody.Part.createFormData(partName, file.name, requestBody)
    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
    }

    override fun initData() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }
        }
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            logFileUploadLiveData.observe(this@LogFileListActivity) {

            }
        }
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityLogListBinding.inflate(layoutInflater)
    }

    override fun providerVMClass(): Class<LogFileListViewModel> {
        return LogFileListViewModel::class.java
    }

    override val isFullScreen: Boolean
        get() = true

}
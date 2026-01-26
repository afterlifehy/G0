package com.kernal.demo.base.viewbase

import android.content.Context
import android.view.View
import com.kernal.demo.base.R
import com.kernal.demo.base.widget.NoDataView

class BaseViewAddFactoryImpl : BaseViewAddFactory {


    override fun getRootView(context: Context): View {//写在这里好统一布局
        val mRootView = View.inflate(context, R.layout.base_no_title_layout, null)
        return mRootView
    }
}
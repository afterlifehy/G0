package com.kernal.demo.base.base.mvvm

import java.lang.Exception

interface OnNetWorkCallLinsener {
    fun onNewWorkErrorCall(tag: String, ext: Exception?)
}
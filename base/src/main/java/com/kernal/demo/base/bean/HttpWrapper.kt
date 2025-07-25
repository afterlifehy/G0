package com.kernal.demo.base.bean

data class HttpWrapper<out T>(val msg: String, val status: Int, var attr: @UnsafeVariance T)
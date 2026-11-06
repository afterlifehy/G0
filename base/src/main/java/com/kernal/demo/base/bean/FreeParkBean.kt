package com.kernal.demo.base.bean

data class FreeParkBean(
    val date: Long,
    val freeCount: Int,
    val plateColor: Int,
    val plateId: String,
    val remain: Int
)
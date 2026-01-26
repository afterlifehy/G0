package com.kernal.demo.base.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

class PagerStatesView : FrameLayout {
    constructor(conext: Context) : super(conext)
    constructor(conext: Context, attrs: AttributeSet) : super(conext, attrs)
    constructor(conext: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        conext,
        attrs,
        defStyleAttr
    )

    init {
        initView()
    }

    private fun initView() {

    }
}
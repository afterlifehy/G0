package com.kernal.demo.base.ext

import com.kernal.demo.base.BaseApplication


fun Any.i18n(res: Int): String {
    return BaseApplication.instance().resources.getString(res)
}

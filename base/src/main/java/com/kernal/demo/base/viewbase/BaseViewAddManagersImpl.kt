package com.kernal.demo.base.viewbase

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.kernal.demo.base.R
import com.kernal.demo.base.widget.PagerStatesView

class BaseViewAddManagersImpl : BaseViewAddManagers {
    //数据加载匡
    private var loadData: View? = null

    //加载动画
    private var mLadAnimation: AnimationDrawable? = null
    private var loading_iv: ImageView? = null//动画加载img

    //是否加载子布局
    private var isLoadNotData = false

    private var mRootView: View? = null
    protected var mViewAddManager: BaseViewAddFactory? = null
    private var mContext: Context? = null
    private var fl_other_content: PagerStatesView? = null

    init {
        mViewAddManager = BaseViewAddFactory.getInstance()
    }

    override fun getRootView(context: Context, mView: View): View {
        mContext = context
        mRootView = mViewAddManager?.getRootView(context)!!
        fl_other_content = mRootView?.findViewById(R.id.fl_other_content)
        val fl_conent = mRootView!!.findViewById<FrameLayout>(R.id.fl_conent)
        fl_conent.addView(mView)
        return mRootView!!
    }

    override fun getRootViewId(context: Context, contextId: Int): View {
        mContext = context
        mRootView = mViewAddManager?.getRootView(context)!!
        fl_other_content = mRootView?.findViewById(R.id.fl_other_content)
        addContentView(mRootView, contextId)
        return mRootView!!
    }

    /**
     * 把子布局添加进来
     */
    private fun addContentView(view: View?, contentId: Int) {
        val fl_conent = view!!.findViewById<FrameLayout>(R.id.fl_conent)
        val mContetxView = View.inflate(view.context, contentId, null)
        fl_conent.addView(mContetxView)
    }
}
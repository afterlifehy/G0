package ja.insepector.bxapp.ui.activity.order

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import ja.insepector.base.BaseApplication
import ja.insepector.base.arouter.ARouterMap
import ja.insepector.base.bean.OrderBean
import ja.insepector.base.ext.i18N
import ja.insepector.base.ext.i18n
import ja.insepector.base.ext.show
import ja.insepector.base.viewbase.VbBaseActivity
import ja.insepector.common.util.AppUtil
import ja.insepector.common.util.BigDecimalManager
import ja.insepector.common.util.GlideUtils
import ja.insepector.bxapp.R
import ja.insepector.bxapp.databinding.ActivityOrderDetailBinding
import ja.insepector.bxapp.mvvm.viewmodel.OrderDetailViewModel
import com.zrq.spanbuilder.TextStyle
import ja.insepector.base.dialog.DialogHelp
import ja.insepector.base.ext.gone
import ja.insepector.base.ext.startArouter
import ja.insepector.base.help.ActivityCacheManager

@Route(path = ARouterMap.ORDER_DETAIL)
class OrderDetailActivity : VbBaseActivity<OrderDetailViewModel, ActivityOrderDetailBinding>(), OnClickListener {
    val colors = intArrayOf(
        ja.insepector.base.R.color.color_ff04a091,
        ja.insepector.base.R.color.color_ff04a091,
        ja.insepector.base.R.color.color_ff04a091
    )
    val colors1 = intArrayOf(
        ja.insepector.base.R.color.color_ffe92404,
        ja.insepector.base.R.color.color_ffe92404,
        ja.insepector.base.R.color.color_ffe92404
    )
    val colors2 = intArrayOf(ja.insepector.base.R.color.color_ff666666, ja.insepector.base.R.color.color_ff1a1a1a)
    val sizes = intArrayOf(16, 24, 16)
    val sizes2 = intArrayOf(19, 19)
    var order: OrderBean? = null
    val styles = arrayOf(TextStyle.NORMAL, TextStyle.BOLD, TextStyle.NORMAL)
    val picList: MutableList<String> = ArrayList()

    @SuppressLint("NewApi")
    override fun initView() {
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, ja.insepector.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = i18N(ja.insepector.base.R.string.订单详细信息)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), ja.insepector.base.R.color.white))

        order = intent.getParcelableExtra(ARouterMap.ORDER) as? OrderBean
        binding.tvPlate.text = order?.carLicense
        if (order?.paidAmount?.toDouble()!! > 0.0) {
            val strings =
                arrayOf(i18n(ja.insepector.base.R.string.已付), order?.paidAmount.toString(), i18n(ja.insepector.base.R.string.元))
            binding.tvPayment.text = AppUtil.getSpan(strings, sizes, colors, styles)
            binding.rtvUpload.gone()
            binding.rtvTransactionRecord.delegate.setBackgroundColor(
                ContextCompat.getColor(
                    BaseApplication.instance(), ja.insepector.base.R.color.color_ff04a091
                )
            )
            binding.rtvTransactionRecord.setOnClickListener(this)
        } else if (order!!.paidAmount.toDouble() == 0.0 && order!!.amount.toDouble() == 0.0) {
            val strings = arrayOf(
                i18n(ja.insepector.base.R.string.已付),
                AppUtil.keepNDecimals(order?.paidAmount.toString(), 2),
                i18n(ja.insepector.base.R.string.元)
            )
            binding.tvPayment.text = AppUtil.getSpan(strings, sizes, colors, styles)
            binding.rtvUpload.gone()
            binding.rtvTransactionRecord.delegate.setBackgroundColor(
                ContextCompat.getColor(
                    BaseApplication.instance(), ja.insepector.base.R.color.color_ffc5dddb
                )
            )
            binding.rtvTransactionRecord.setOnClickListener(null)
        } else {
            val strings = arrayOf(
                i18n(ja.insepector.base.R.string.欠),
                AppUtil.keepNDecimals(
                    BigDecimalManager.subtractionDoubleToString(
                        order?.amount!!.toDouble(),
                        order?.paidAmount!!.toDouble()
                    ), 2
                ),
                i18n(ja.insepector.base.R.string.元)
            )
            binding.tvPayment.text = AppUtil.getSpan(strings, sizes, colors1, styles)
            binding.rtvUpload.show()
            binding.rtvTransactionRecord.delegate.setBackgroundColor(
                ContextCompat.getColor(
                    BaseApplication.instance(), ja.insepector.base.R.color.color_ffc5dddb
                )
            )
            binding.rtvTransactionRecord.setOnClickListener(null)
        }
        binding.rtvTransactionRecord.delegate.init()

        val strings1 = arrayOf(i18N(ja.insepector.base.R.string.订单) + "：", order?.orderNo.toString())
        binding.tvOrderNo.text = AppUtil.getSpan(strings1, sizes2, colors2)
        val strings2 = arrayOf(i18N(ja.insepector.base.R.string.泊位) + "：", order?.parkingNo.toString())
        binding.tvBerth.text = AppUtil.getSpan(strings2, sizes2, colors2)
        val strings3 = arrayOf(i18N(ja.insepector.base.R.string.路段) + "：", order?.streetName.toString())
        binding.tvStreet.text = AppUtil.getSpan(strings3, sizes2, colors2)
        val strings4 = arrayOf(i18N(ja.insepector.base.R.string.入场) + "：", order?.startTime.toString())
        binding.tvStartTime.text = AppUtil.getSpan(strings4, sizes2, colors2)
        val strings5 = arrayOf(i18N(ja.insepector.base.R.string.出场) + "：", order?.endTime.toString())
        binding.tvEndTime.text = AppUtil.getSpan(strings5, sizes2, colors2)
        val strings6 = arrayOf(i18N(ja.insepector.base.R.string.时长) + "：", AppUtil.dayHourMin(order?.duration!!.toInt()))
        binding.tvTotalTime.text = AppUtil.getSpan(strings6, sizes2, colors2)
        val strings7 = arrayOf(i18N(ja.insepector.base.R.string.总额) + "：", order?.amount.toString() + "元")
        binding.tvAmount.text = AppUtil.getSpan(strings7, sizes2, colors2)
        picList.add("https://n.sinaimg.cn/sinacn10112/384/w2048h1536/20190218/bd7a-htacqww5359098.jpg")
        picList.add("https://p4.itc.cn/q_70/images03/20200723/76f7fd2511a048abbb2e58939b1f9bde.jpeg")
        picList.add("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fsafe-img.xhscdn.com%2Fbw1%2F8ccb3755-b76e-49f1-85ab-60f6c6b161ae%3FimageView2%2F2%2Fw%2F1080%2Fformat%2Fjpg&refer=http%3A%2F%2Fsafe-img.xhscdn.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1704444015&t=45651f5b799fd231a323879ff7abca31")
        GlideUtils.instance?.loadImage(binding.rivPic1, picList[0], ja.insepector.common.R.mipmap.ic_placeholder)
        GlideUtils.instance?.loadImage(binding.rivPic2, picList[1], ja.insepector.common.R.mipmap.ic_placeholder)
        GlideUtils.instance?.loadImage(binding.rivPic3, picList[2], ja.insepector.common.R.mipmap.ic_placeholder)
    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
        binding.layoutToolbar.ivRight.setOnClickListener(this)
        binding.rtvDebtCollection.setOnClickListener(this)
        binding.rtvUpload.setOnClickListener(this)
        binding.rivPic1.setOnClickListener(this)
        binding.rivPic1.setOnClickListener(this)
        binding.rivPic3.setOnClickListener(this)
    }

    override fun initData() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }

            R.id.rtv_debtCollection -> {
                ARouter.getInstance().build(ARouterMap.DEBT_COLLECTION).withString(ARouterMap.DEBT_CAR_LICENSE, order?.carLicense)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
            }

            R.id.rtv_transactionRecord -> {
                startArouter(ARouterMap.TRANSACTION_RECORD, data = Bundle().apply {
                    putString(ARouterMap.TRANSACTION_RECORD_ORDER_NO, order?.orderNo)
                })
            }

            R.id.rtv_upload -> {
                DialogHelp.Builder().setTitle(i18N(ja.insepector.base.R.string.是否立即上传))
                    .setLeftMsg(i18N(ja.insepector.base.R.string.取消))
                    .setRightMsg(i18N(ja.insepector.base.R.string.确定)).setCancelable(true)
                    .setOnButtonClickLinsener(object : DialogHelp.OnButtonClickLinsener {
                        override fun onLeftClickLinsener(msg: String) {
                        }

                        override fun onRightClickLinsener(msg: String) {

                        }

                    }).build(ActivityCacheManager.instance().getCurrentActivity()).showDailog()
            }

            R.id.riv_pic1 -> {
                startArouter(ARouterMap.PREVIEW_IMAGE, data = Bundle().apply {
                    putInt(ARouterMap.IMG_INDEX, 0)
                    putStringArrayList(ARouterMap.IMG_LIST, picList as ArrayList<String>)
                })
            }

            R.id.riv_pic2 -> {
                startArouter(ARouterMap.PREVIEW_IMAGE, data = Bundle().apply {
                    putInt(ARouterMap.IMG_INDEX, 1)
                    putStringArrayList(ARouterMap.IMG_LIST, picList as ArrayList<String>)
                })
            }

            R.id.riv_pic3 -> {
                startArouter(ARouterMap.PREVIEW_IMAGE, data = Bundle().apply {
                    putInt(ARouterMap.IMG_INDEX, 2)
                    putStringArrayList(ARouterMap.IMG_LIST, picList as ArrayList<String>)
                })
            }
        }
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityOrderDetailBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override val isFullScreen: Boolean
        get() = true

    override fun marginStatusBarView(): View? {
        return binding.layoutToolbar.ablToolbar
    }

    override fun providerVMClass(): Class<OrderDetailViewModel> {
        return OrderDetailViewModel::class.java
    }
}
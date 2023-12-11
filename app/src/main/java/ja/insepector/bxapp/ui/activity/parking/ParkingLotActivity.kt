package ja.insepector.bxapp.ui.activity.parking

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.View.OnClickListener
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewbinding.ViewBinding
import com.alibaba.fastjson.JSONObject
import ja.insepector.base.BaseApplication
import ja.insepector.base.arouter.ARouterMap
import ja.insepector.base.bean.ParkingLotBean
import ja.insepector.base.bean.Street
import ja.insepector.base.ext.i18N
import ja.insepector.base.ext.startAct
import ja.insepector.base.ext.startArouter
import ja.insepector.base.util.ToastUtil
import ja.insepector.base.viewbase.VbBaseActivity
import ja.insepector.common.realm.RealmUtil
import ja.insepector.common.util.GlideUtils
import ja.insepector.bxapp.R
import ja.insepector.bxapp.adapter.ParkingLotAdapter
import ja.insepector.bxapp.databinding.ActivityParkingLotBinding
import ja.insepector.bxapp.mvvm.viewmodel.ParkingLotViewModel

class ParkingLotActivity : VbBaseActivity<ParkingLotViewModel, ActivityParkingLotBinding>(), OnClickListener {
    var parkingLotAdapter: ParkingLotAdapter? = null
    var parkingLotList: MutableList<ParkingLotBean> = ArrayList()
    var currentStreet: Street? = null
    var count = 0
    var handler = Handler(Looper.getMainLooper())

    override fun initView() {
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, ja.insepector.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = i18N(ja.insepector.base.R.string.停车场)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), ja.insepector.base.R.color.white))

        binding.rvParkingLot.setHasFixedSize(true)
        binding.rvParkingLot.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        parkingLotAdapter = ParkingLotAdapter(parkingLotList, this)
        binding.rvParkingLot.adapter = parkingLotAdapter
    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
    }

    override fun initData() {
        currentStreet = RealmUtil.instance?.findCurrentStreet()
    }

    val runnable = object : Runnable {
        override fun run() {
            if (count < 600) {
                getParkingLotList()
                count++
                handler.postDelayed(this, 10000)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }

    override fun onResume() {
        super.onResume()
        handler.post(runnable)
    }

    fun getParkingLotList() {
        showProgressDialog(20000)
        val param = HashMap<String, Any>()
        val jsonobject = JSONObject()
        jsonobject["streetNo"] = currentStreet!!.streetNo
        param["attr"] = jsonobject
        mViewModel.getParkingLotList(param)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }

            R.id.rfl_parking -> {
                val parkingLotBean = v.tag as ParkingLotBean
                if (parkingLotBean.state == "01") {
//                    ARouter.getInstance().build(ARouterMap.BERTH_ABNORMAL)
//                        .withString(ARouterMap.ABNORMAL_STREET_NO, currentStreet!!.streetNo)
//                        .withString(ARouterMap.ABNORMAL_PARKING_NO, parkingLotBean.parkingNo)
//                        .withString(ARouterMap.ABNORMAL_ORDER_NO, "")
//                        .withString(ARouterMap.ABNORMAL_CARLICENSE, "")
//                        .withString(ARouterMap.ABNORMAL_CAR_COLOR, "")
//                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
                } else {
                    startArouter(ARouterMap.PARKING_SPACE,data = Bundle().apply {
                        putString(ARouterMap.CAR_LICENSE, parkingLotBean.carLicense)
                        putString(ARouterMap.PARKING_NO, parkingLotBean.parkingNo)
                        putString(ARouterMap.CAR_COLOR,parkingLotBean.carColor)
                    })
                }
            }
        }
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            parkingLotListLiveData.observe(this@ParkingLotActivity) {
                dismissProgressDialog()
                parkingLotList.clear()
                parkingLotList.addAll(it.result)
                parkingLotAdapter?.setList(parkingLotList)
            }
            errMsg.observe(this@ParkingLotActivity) {
                dismissProgressDialog()
                ToastUtil.showMiddleToast(it.msg)
            }
        }
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityParkingLotBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override val isFullScreen: Boolean
        get() = true

    override fun marginStatusBarView(): View {
        return binding.layoutToolbar.ablToolbar
    }

    override fun providerVMClass(): Class<ParkingLotViewModel> {
        return ParkingLotViewModel::class.java
    }

    override fun onDestroy() {
        super.onDestroy()
        if (handler != null) {
            handler.removeCallbacks(runnable)
        }
    }
}
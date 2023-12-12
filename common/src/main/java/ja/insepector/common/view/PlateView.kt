package ja.insepector.common.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import ja.insepector.base.BaseApplication
import ja.insepector.common.R
import ja.insepector.common.databinding.ViewPlateBinding
import ja.insepector.common.util.Constant

class PlateView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {
    var binding: ViewPlateBinding? = null
    var emptyPosition = 0

    init {
        initView()
    }

    private fun initView() {
        binding = ViewPlateBinding.inflate(LayoutInflater.from(context))
    }

    fun setOnePlate(value: String) {
        when (emptyPosition) {
            0 -> {
                binding!!.tvPlate1.text = value
                emptyPosition = 1
            }

            1 -> {
                binding!!.tvPlate2.text = value
                emptyPosition = 2
            }

            2 -> {
                binding!!.tvPlate3.text = value
                emptyPosition = 3
            }

            3 -> {
                binding!!.tvPlate4.text = value
                emptyPosition = 4
            }

            4 -> {
                binding!!.tvPlate5.text = value
                emptyPosition = 5
            }

            5 -> {
                binding!!.tvPlate6.text = value
                emptyPosition = 6
            }

            6 -> {
                binding!!.tvPlate7.text = value
                emptyPosition = 7
            }

            7 -> {
                binding!!.tvPlate8.text = value
                emptyPosition = -1
            }

            -1 -> {

            }
        }
    }

    fun setAllPlate(value: String) {
        val plateArray = value.toCharArray()
        binding!!.tvPlate1.text = plateArray[0].toString()
        binding!!.tvPlate2.text = plateArray[1].toString()
        binding!!.tvPlate3.text = plateArray[2].toString()
        binding!!.tvPlate4.text = plateArray[3].toString()
        binding!!.tvPlate5.text = plateArray[4].toString()
        binding!!.tvPlate6.text = plateArray[5].toString()
        binding!!.tvPlate7.text = plateArray[6].toString()
        if (plateArray.size == 8) {
            binding!!.tvPlate8.text = plateArray[7].toString()
        }
    }

    fun setPlateBg(color: String) {
        when (color) {
            Constant.BLUE -> {
                setPlateImgBg(R.mipmap.ic_plate_blue_bg_start, R.mipmap.ic_plate_blue_bg_middle, R.mipmap.ic_plate_blue_bg_end)
            }

            Constant.GREEN -> {
                setPlateImgBg(R.mipmap.ic_plate_green_bg_start, R.mipmap.ic_plate_green_bg_middle, R.mipmap.ic_plate_green_bg_end)
            }

            Constant.YELLOW -> {
                setPlateImgBg(R.mipmap.ic_plate_yellow_bg_start, R.mipmap.ic_plate_yellow_bg_middle, R.mipmap.ic_plate_yellow_bg_end)
            }

            Constant.YELLOW_GREEN -> {
                setPlateImgBg2(
                    R.mipmap.ic_plate_yellow_bg_start,
                    R.mipmap.ic_plate_yellow_bg_middle,
                    R.mipmap.ic_plate_yellow_green_bg_middle,
                    R.mipmap.ic_plate_yellow_green_bg_end
                )
            }

            Constant.WHITE -> {
                setPlateImgBg(R.mipmap.ic_plate_white_bg_start, R.mipmap.ic_plate_white_bg_middle, R.mipmap.ic_plate_white_bg_end)
            }

            Constant.BLACK -> {
                setPlateImgBg(R.mipmap.ic_plate_black_bg_start, R.mipmap.ic_plate_black_bg_middle, R.mipmap.ic_plate_black_bg_end)
            }

            Constant.OTHERS -> {

            }
        }
    }

    fun setPlateImgBg(startImg: Int, middleImg: Int, endImg: Int) {
        binding!!.tvPlate1.background = ContextCompat.getDrawable(BaseApplication.instance(), startImg)
        binding!!.tvPlate2.background = ContextCompat.getDrawable(BaseApplication.instance(), middleImg)
        binding!!.tvPlate3.background = ContextCompat.getDrawable(BaseApplication.instance(), middleImg)
        binding!!.tvPlate4.background = ContextCompat.getDrawable(BaseApplication.instance(), middleImg)
        binding!!.tvPlate5.background = ContextCompat.getDrawable(BaseApplication.instance(), middleImg)
        binding!!.tvPlate6.background = ContextCompat.getDrawable(BaseApplication.instance(), middleImg)
        binding!!.tvPlate7.background = ContextCompat.getDrawable(BaseApplication.instance(), middleImg)
        binding!!.tvPlate8.background = ContextCompat.getDrawable(BaseApplication.instance(), endImg)
    }

    fun setPlateImgBg2(startImg: Int, middleImg: Int, middleImg2: Int, endImg: Int) {
        binding!!.tvPlate1.background = ContextCompat.getDrawable(BaseApplication.instance(), startImg)
        binding!!.tvPlate2.background = ContextCompat.getDrawable(BaseApplication.instance(), middleImg)
        binding!!.tvPlate3.background = ContextCompat.getDrawable(BaseApplication.instance(), middleImg2)
        binding!!.tvPlate4.background = ContextCompat.getDrawable(BaseApplication.instance(), middleImg2)
        binding!!.tvPlate5.background = ContextCompat.getDrawable(BaseApplication.instance(), middleImg2)
        binding!!.tvPlate6.background = ContextCompat.getDrawable(BaseApplication.instance(), middleImg2)
        binding!!.tvPlate7.background = ContextCompat.getDrawable(BaseApplication.instance(), middleImg2)
        binding!!.tvPlate8.background = ContextCompat.getDrawable(BaseApplication.instance(), endImg)
    }
}
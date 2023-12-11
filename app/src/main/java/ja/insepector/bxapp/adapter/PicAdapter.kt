package ja.insepector.bxapp.adapter

import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.aries.ui.view.radius.RadiusFrameLayout
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.SizeUtils
import ja.insepector.base.BaseApplication
import ja.insepector.base.adapter.BaseBindingAdapter
import ja.insepector.base.adapter.VBViewHolder
import ja.insepector.bxapp.databinding.ItemPicBinding
import ja.insepector.common.util.GlideUtils

class PicAdapter(data: MutableList<String>? = null, val onClickListener: OnClickListener) :
    BaseBindingAdapter<String, ItemPicBinding>(data) {
    override fun convert(holder: VBViewHolder<ItemPicBinding>, item: String) {
        val lp = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        lp.bottomMargin = SizeUtils.dp2px(20f)
        holder.vb.rivPic.layoutParams = lp

        GlideUtils.instance?.loadLongImage(holder.vb.rivPic, item)
        holder.vb.rivPic.tag = item
        holder.vb.rivPic.setOnClickListener(onClickListener)
    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemPicBinding {
        return ItemPicBinding.inflate(inflater)
    }
}
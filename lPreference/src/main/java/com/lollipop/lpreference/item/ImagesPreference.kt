package com.lollipop.lpreference.item

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.lollipop.base.findInSelf
import com.lollipop.lpreference.R
import com.lollipop.lpreference.dialog.ImagesPanelDialogFragment
import com.lollipop.lpreference.info.ImagesPreferenceInfo

/**
 * @author lollipop
 * @date 2020-01-18 20:44
 * 一个用于颜色的偏好设置项
 */
class ImagesPreference(group: ViewGroup): BasePreferenceItem<ImagesPreferenceInfo>(group) {
    override val widgetId: Int = R.layout.preference_plugin_image

    private var selectedIndex = 0

    private val imageView: ImageView? by findInSelf()

    override fun onItemClick(view: View) {
        super.onItemClick(view)
        preferenceInfo?.let { info ->
            ImagesPanelDialogFragment.show(context, info.values.getList(), info.maxSize) {
                info.onImageChange(it)
                info.save(context)
                notifyInfoChange()
            }
        }
    }

    override fun onPreviewClick(view: View): Boolean {
        super.onPreviewClick(view)
        preferenceInfo?.let { info ->
            val values = info.getValue(context)
            if (values.size > 1) {
                selectedIndex++
                selectedIndex %= values.size
                imageView?.let {
                    Glide.with(it).load(values[selectedIndex]).into(it)
                }
                return true
            }
        }
        return false
    }

    override fun onBind(info: ImagesPreferenceInfo) {
        super.onBind(info)
        selectedIndex = 0
        val values = info.getValue(context)
        imageView?.let {
            if (values.size > 0) {
                Glide.with(it).load(values[selectedIndex]).into(it)
            } else {
                it.setImageDrawable(null)
            }
        }
    }

}
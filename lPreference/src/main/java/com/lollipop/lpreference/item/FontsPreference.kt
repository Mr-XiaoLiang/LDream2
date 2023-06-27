package com.lollipop.lpreference.item

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.lollipop.base.findInSelf
import com.lollipop.fonts.FontsHelper
import com.lollipop.fonts.LFont
import com.lollipop.lpreference.R
import com.lollipop.lpreference.dialog.FontsPanelDialogFragment
import com.lollipop.lpreference.dialog.ImagesPanelDialogFragment
import com.lollipop.lpreference.info.FontsPreferenceInfo
import com.lollipop.lpreference.info.ImagesPreferenceInfo

/**
 * @author lollipop
 * @date 2020-01-18 20:44
 * 一个用于颜色的偏好设置项
 */
class FontsPreference(group: ViewGroup): BasePreferenceItem<FontsPreferenceInfo>(group) {
    override val widgetId: Int = R.layout.preference_plugin_font

    private val textView: TextView? by findInSelf()

    override fun onItemClick(view: View) {
        super.onItemClick(view)
        preferenceInfo?.let { info ->
            FontsPanelDialogFragment.show(context, info.getValue(context)) {
                info.putValue(context, it?.assetsName?:"")
                notifyInfoChange()
            }
        }
    }

    override fun onBind(info: FontsPreferenceInfo) {
        super.onBind(info)
        textView?.let {
            val values = info.getValue(context).ifEmpty { info.default }
            val lFont = FontsHelper.valueOf(values)
            lFont.bind(it)
        }
    }

}
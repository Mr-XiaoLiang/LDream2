package com.lollipop.ldream.preference.item

import android.view.ViewGroup
import android.widget.TextView
import com.lollipop.ldream.R
import com.lollipop.ldream.preference.info.BasePreferenceInfo
import com.lollipop.ldream.preference.info.NumberPreferenceInfo
import com.lollipop.ldream.preference.item.BasePreferenceItem

/**
 * @author lollipop
 * @date 2020-01-18 19:50
 * 数字设置的偏好设置项
 */
class NumberPreference(viewGroup: ViewGroup): BasePreferenceItem<NumberPreferenceInfo>(viewGroup) {

    override val widgetId: Int
        get() = R.layout.preference_plugin_number

    private val numberView: TextView by lazy {
        itemView.findViewById<TextView>(R.id.numberView)
    }

    override fun onBind(info: NumberPreferenceInfo) {
        super.onBind(info)

    }

}
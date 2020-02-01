package com.lollipop.ldream.preference.item

import android.view.View
import android.view.ViewGroup
import com.lollipop.ldream.preference.info.ActionPreferenceInfo
import com.lollipop.ldream.preference.info.BasePreferenceInfo

/**
 * @author lollipop
 * @date 2020-01-18 20:44
 * 一个用于跳转的偏好设置项
 */
class ActionPreference(group: ViewGroup): BasePreferenceItem<ActionPreferenceInfo>(group) {
    override val widgetId: Int = 0

    override fun onItemClick(view: View) {
        super.onItemClick(view)
        preferenceInfo?.let {
            context.startActivity(it.action)
        }
    }

}
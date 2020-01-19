package com.lollipop.ldream.preference.item

import android.view.ViewGroup

/**
 * @author lollipop
 * @date 2020-01-18 20:44
 * 一个空的偏好设置项
 */
class EmptyPreferenceItem(group: ViewGroup): BasePreferenceItem(group) {
    override val widgetId: Int = 0
}
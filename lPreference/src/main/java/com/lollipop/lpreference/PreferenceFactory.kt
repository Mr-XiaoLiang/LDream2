package com.lollipop.lpreference

import android.view.ViewGroup
import android.widget.Switch
import com.lollipop.lpreference.info.ActionPreferenceInfo
import com.lollipop.lpreference.info.BasePreferenceInfo
import com.lollipop.lpreference.info.NumberPreferenceInfo
import com.lollipop.lpreference.info.SwitchPreferenceInfo
import com.lollipop.lpreference.item.*

/**
 * @author lollipop
 * @date 2020-01-18 20:35
 * 偏好设置工厂
 */
object PreferenceFactory {

    private const val Empty = 0
    private const val Number = 1
    private const val Action = 2
    private const val Switch = 3

    fun getInfoType(info: BasePreferenceInfo<*>): Int {
        return when (info) {
            is NumberPreferenceInfo -> Number
            is ActionPreferenceInfo -> Action
            is SwitchPreferenceInfo -> Switch
            else -> Empty
        }
    }

    fun createItem(group: ViewGroup, type: Int): BasePreferenceItem<*> {
        return when(type) {
            Number -> NumberPreference(group)
            Action -> ActionPreference(group)
            Switch -> SwitchPreference(group)
            else -> EmptyPreferenceItem(group)
        }
    }

    fun bindItem(item: BasePreferenceItem<*>, info: BasePreferenceInfo<*>) {
        when (item) {
            is NumberPreference -> if (info is NumberPreferenceInfo) {
                item.bind(info)
            }
            is ActionPreference -> if (info is ActionPreferenceInfo) {
                item.bind(info)
            }
            is SwitchPreference -> if (info is SwitchPreferenceInfo) {
                item.bind(info)
            }
            is EmptyPreferenceItem -> {
                item.bind(info)
            }
        }
    }

}
package com.lollipop.lpreference

import android.view.ViewGroup
import com.lollipop.lpreference.info.*
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
    private const val Colors = 4

    fun getInfoType(info: BasePreferenceInfo<*>): Int {
        return when (info) {
            is NumberPreferenceInfo -> Number
            is ActionPreferenceInfo -> Action
            is SwitchPreferenceInfo -> Switch
            is ColorsPreferenceInfo -> Colors
            else -> Empty
        }
    }

    fun createItem(group: ViewGroup, type: Int): BasePreferenceItem<*> {
        return when(type) {
            Number -> NumberPreference(group)
            Action -> ActionPreference(group)
            Switch -> SwitchPreference(group)
            Colors -> ColorsPreference(group)
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
            is ColorsPreference -> if (info is ColorsPreferenceInfo) {
                item.bind(info)
            }
            is EmptyPreferenceItem -> {
                item.bind(info)
            }
        }
    }

}
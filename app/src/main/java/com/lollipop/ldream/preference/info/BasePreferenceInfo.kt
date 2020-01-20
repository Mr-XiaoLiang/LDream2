package com.lollipop.ldream.preference.info

import android.content.Context
import com.lollipop.ldream.preference.PreferenceHelper
import com.lollipop.ldream.preference.value.PreferenceValue

/**
 * @author lollipop
 * @date 2020-01-16 23:43
 * 基础的偏好设置信息
 */
open class BasePreferenceInfo<T: Any>(private val key: String,
                                 private val default: T) {
    /**
     * 标题
     */
    var title: CharSequence = ""

    /**
     * 描述
     */
    var summary: CharSequence = ""

    /**
     * icon的图标ID
     */
    var iconId: Int = 0

    @Suppress("UNCHECKED_CAST")
    fun getValue(context: Context): T {
        when (default) {
            is PreferenceValue -> {
                default.parse(PreferenceHelper.get(
                    context, key, default.serialization()))
            }
            is String -> {
                PreferenceHelper.get(context, key, default)
            }
            is Int -> {
                PreferenceHelper.get(context, key, default)
            }
            is Boolean -> {
                PreferenceHelper.get(context, key, default)
            }
            is Float -> {
                PreferenceHelper.get(context, key, default)
            }
            is Double -> {
                PreferenceHelper.get(context, key, default)
            }
            is Long -> {
                PreferenceHelper.get(context, key, default)
            }
            else -> {
                throw RuntimeException("Unknown value type, " +
                        "please Implementing the PreferenceValue interface")
            }
        }
        return default
    }

    fun putValue(context: Context, value: T) {
        when (value) {
            is PreferenceValue -> {
                PreferenceHelper.put(
                    context, key, value.serialization())
            }
            is String -> {
                PreferenceHelper.put(context, key, value)
            }
            is Int -> {
                PreferenceHelper.put(context, key, value)
            }
            is Boolean -> {
                PreferenceHelper.put(context, key, value)
            }
            is Float -> {
                PreferenceHelper.put(context, key, value)
            }
            is Double -> {
                PreferenceHelper.put(context, key, value)
            }
            is Long -> {
                PreferenceHelper.put(context, key, value)
            }
            else -> {
                throw RuntimeException("Unknown value type, " +
                        "please Implementing the PreferenceValue interface")
            }
        }
    }

}
package com.lollipop.ldream.preference

import android.content.Context
import java.lang.Exception

/**
 * @author lollipop
 * @date 2020-01-16 23:43
 * 基础的偏好设置信息
 */
open class BasePreferenceInfo {
    /**
     * 标题
     */
    var title: CharSequence = ""

    /**
     * 描述
     */
    var summary: CharSequence = ""

    /**
     * 偏好设置的键
     */
    var key: String = ""

    /**
     * 偏好设置的缺省值
     */
    var default: Any = ""

    @Suppress("UNCHECKED_CAST")
    fun <T> getValue(context: Context): T {
        val value = PreferenceHelper.get(context, key, default)
        try {
            return value as T
        } catch (e: Exception) {}
        return default as T
    }

}
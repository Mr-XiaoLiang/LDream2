package com.lollipop.ldream.util

import android.graphics.Color
import kotlin.math.max
import kotlin.math.min

/**
 * @author lollipop
 * @date 2020-01-18 20:11
 * 常见的工具方法
 */

fun Int.changeAlpha(alpha: Int = 0x16): Int {
    val a = max(min(alpha, 255), 0) shl 24
    return this and 0xFFFFFF or a
}

fun Int.changeAlpha(weight: Float): Int {
    return changeAlpha((this.alpha * weight).toInt())
}

inline val Int.alpha get() = Color.alpha(this)

fun Int.range(min: Int, max: Int): Int {
    if(this < min) {
        return min
    }
    if (this > max) {
        return max
    }
    return this
}
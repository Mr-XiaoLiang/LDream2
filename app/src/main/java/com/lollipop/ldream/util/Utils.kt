package com.lollipop.ldream.util

/**
 * @author lollipop
 * @date 2020-01-18 20:11
 * 常见的工具方法
 */

fun Float.range(min: Float, max: Float): Float {
    if (this < min) {
        return min
    }
    if (this > max) {
        return max
    }
    return this
}

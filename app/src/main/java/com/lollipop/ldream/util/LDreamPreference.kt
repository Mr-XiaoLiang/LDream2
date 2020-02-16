package com.lollipop.ldream.util

import android.content.Context
import android.graphics.Color
import com.lollipop.ldream.R
import com.lollipop.lpreference.PreferenceHelper
import com.lollipop.lpreference.info.BasePreferenceInfo
import com.lollipop.lpreference.info.NumberPreferenceInfo
import com.lollipop.lpreference.value.ColorArray

/**
 * @author lollipop
 * @date 2020-02-16 18:50
 * 屏保的偏好设置
 */
object LDreamPreference {

    const val KEY_KEY_WORD = "KEY_KEY_WORD"
    const val KEY_PRIMARY_COLOR = "KEY_PRIMARY_COLOR"
    const val KEY_SECONDARY_COLOR = "KEY_SECONDARY_COLOR"
    const val KEY_FLASH_COLOR = "KEY_FLASH_COLOR"
    const val KEY_FLASH_ENABLE = "KEY_FLASH_ENABLE"
    const val KEY_TINT_ENABLE = "KEY_TINT_ENABLE"
    const val KEY_TINT_COLOR = "KEY_TINT_COLOR"

    fun getPreferenceList(context: Context): Array<BasePreferenceInfo<*>> {
        return arrayOf(
            NumberPreferenceInfo(KEY_KEY_WORD).apply { 
                title = context.getString(R.string.title_keyword)
                summary = context.getString(R.string.summary_keyword)
//                iconId = R.drawable.ic_
            }
        )
    }

}

fun Context.timerPrimaryColor(): Int {
    return PreferenceHelper.get(this, LDreamPreference.KEY_PRIMARY_COLOR, Color.RED)
}

fun Context.timerSecondaryColor(): Int {
    return PreferenceHelper.get(this, LDreamPreference.KEY_SECONDARY_COLOR, Color.WHITE)
}

fun Context.timerKeyWord(): Int {
    return PreferenceHelper.get(this, LDreamPreference.KEY_KEY_WORD, 1)
}

fun Context.timerFlashColor(): ColorArray {
    return PreferenceHelper.get(this, LDreamPreference.KEY_FLASH_COLOR, ColorArray())
}

fun Context.timerFlashEnable(): Boolean {
    return PreferenceHelper.get(this, LDreamPreference.KEY_FLASH_ENABLE, true)
}

fun Context.timerTintEnable(): Boolean {
    return PreferenceHelper.get(this, LDreamPreference.KEY_TINT_ENABLE, true)
}

fun Context.timerTintColor(): Int {
    val colors = PreferenceHelper.get(this, LDreamPreference.KEY_TINT_COLOR, ColorArray())
    if (colors.size < 1) {
        return Color.WHITE
    }
    return colors[0]
}

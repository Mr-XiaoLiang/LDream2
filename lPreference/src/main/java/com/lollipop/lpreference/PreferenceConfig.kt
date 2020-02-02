package com.lollipop.lpreference

import android.graphics.Color
import com.lollipop.lpreference.util.changeAlpha


/**
 * @author lollipop
 * @date 2020-01-18 20:08
 * 偏好设置中的配置文件
 */
object PreferenceConfig {

    const val titleColor = Color.WHITE

    val summaryColor = titleColor.changeAlpha(0.8F)

    const val iconColor = titleColor

}
package com.lollipop.ldream.preference.info

import android.content.Intent

/**
 * @author lollipop
 * @date 2020-01-18 20:38
 * 跳转意图偏好设置
 */
class ActionPreferenceInfo(key: String, val action: Intent): BasePreferenceInfo<Intent>(key, Intent())
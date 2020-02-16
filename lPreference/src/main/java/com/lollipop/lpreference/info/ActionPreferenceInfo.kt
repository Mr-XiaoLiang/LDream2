package com.lollipop.lpreference.info

import android.content.Intent

/**
 * @author lollipop
 * @date 2020-01-18 20:38
 * 跳转意图偏好设置
 */
class ActionPreferenceInfo(val action: Intent): BasePreferenceInfo<Intent>(
    System.currentTimeMillis().toString(16), Intent()) {

    var actionType = ActionType.Activity

    enum class ActionType {
        Activity,
        Service,
        Broadcast
    }

}
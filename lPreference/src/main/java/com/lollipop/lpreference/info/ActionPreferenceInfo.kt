package com.lollipop.lpreference.info

import android.content.Intent

/**
 * @author lollipop
 * @date 2020-01-18 20:38
 * 跳转意图偏好设置
 */
class ActionPreferenceInfo(val action: Intent): BasePreferenceInfo<Intent>(myKey(), Intent()) {
    companion object {
        private fun Any.myKey(): String {
            return System.identityHashCode(this).toString(16)
        }
    }

    var actionType = ActionType.Activity

    enum class ActionType {
        Activity,
        Service,
        Broadcast
    }

}
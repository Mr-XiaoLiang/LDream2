package com.lollipop.lpreference.info

/**
 * @author lollipop
 * @date 2020-01-18 20:38
 * 开关的偏好设置
 */
class SwitchPreferenceInfo(key: String, def: Boolean = false): BasePreferenceInfo<Boolean>(key, def) {
    /**
     * 开启状态下的描述内容
     */
    var summaryTrue = ""
    /**
     * 关闭状态下的描述信息
     */
    var summaryFalse = ""
}
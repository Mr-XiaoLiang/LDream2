package com.lollipop.lpreference.item

import android.view.ViewGroup
import android.widget.CompoundButton
import com.lollipop.lpreference.info.SwitchPreferenceInfo

/**
 * @author lollipop
 * @date 2020-01-18 20:44
 * 一个用于跳转的偏好设置项
 */
class SwitchPreference(group: ViewGroup): BasePreferenceItem<SwitchPreferenceInfo>(group),
    CompoundButton.OnCheckedChangeListener {

    override val widgetId: Int = 0

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
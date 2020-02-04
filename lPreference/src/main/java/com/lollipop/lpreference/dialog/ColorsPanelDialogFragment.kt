package com.lollipop.lpreference.dialog


import android.content.Context
import com.lollipop.lpreference.R

/**
 * 颜色选择的面板
 * @author Lollipop
 */
class ColorsPanelDialogFragment private constructor(): BaseDialog() {

    companion object {
        fun show(context: Context) {
            ColorsPanelDialogFragment().apply {

            }.show(context, "ColorsPanelDialogFragment")
        }
    }

    override val contextId: Int
        get() = R.layout.fragment_colors_panel_dialog

}

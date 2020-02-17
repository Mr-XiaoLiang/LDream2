package com.lollipop.ldream.util

import android.content.Context
import android.view.View
import android.widget.ImageView
import com.lollipop.ldream.drawer.FlashDrawable

/**
 * @author lollipop
 * @date 2020-02-17 23:50
 * 闪光的辅助类
 */
class FlashHelper(private val flashDrawable: FlashDrawable = FlashDrawable()) {

    private var bindView: View? = null

    var flashEnable = true

    fun unbind() {
        bindView?.let {
            if (it.background == flashDrawable) {
                it.background = null
            }
            if (it is ImageView && it.drawable == flashDrawable) {
                it.setImageDrawable(null)
            }
        }
    }

    fun bindToBackground(view: View) {
        unbind()
        view.background = flashDrawable
        bindView = view
    }

    fun bindToImage(view: ImageView) {
        unbind()
        view.setImageDrawable(flashDrawable)
        bindView = view
    }

    fun init(context: Context) {
        flashEnable = context.timerFlashEnable()
        updateInfo(context)
    }

    fun updateInfo(context: Context) {
        if (flashEnable) {
            val colorArray = context.timerFlashColor()
            flashDrawable.setColor(colorArray.values())
        } else {

        }
        flashDrawable.notifyDataChange()
    }

}
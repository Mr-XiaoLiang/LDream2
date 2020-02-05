package com.lollipop.lpreference.view

import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

/**
 * @author lollipop
 * @date 2020-02-05 20:16
 * 方形的容器
 */
class SquareLayout(context: Context, attrs: AttributeSet?, defStyleAttr:Int )
    : FrameLayout(context,attrs,defStyleAttr) {

    constructor(context: Context, attrs: AttributeSet?):this(context,attrs,0)
    constructor(context: Context):this(context,null)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

    }

}
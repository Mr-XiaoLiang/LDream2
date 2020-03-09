package com.lollipop.ldream.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.lollipop.ldream.drawer.BlackHoleDrawable

/**
 * @author lollipop
 * @date 2020/3/10 00:56
 * 背景图的ImageView
 */
class BackgroundView(context: Context, attrs: AttributeSet?, defStyleAttr: Int):
    AppCompatImageView(context, attrs, defStyleAttr) {

    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)
    constructor(context: Context): this(context, null)

    private val blackHoleDrawable = BlackHoleDrawable()

    init {
        blackHoleDrawable.callback = this
    }

    override fun invalidateDrawable(dr: Drawable) {
        if (dr == blackHoleDrawable) {
            invalidate()
            return
        }
        super.invalidateDrawable(dr)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        blackHoleDrawable.setBounds(paddingLeft, paddingTop,
            width - paddingRight, height -paddingBottom)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?:return
        blackHoleDrawable.draw(canvas)
    }

}
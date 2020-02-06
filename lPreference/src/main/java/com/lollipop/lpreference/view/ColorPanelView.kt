package com.lollipop.lpreference.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import com.lollipop.lpreference.R
import com.lollipop.lpreference.util.lifecycleBinding
import com.lollipop.lpreference.util.onCancel
import com.lollipop.lpreference.util.onEnd
import com.lollipop.lpreference.util.onStart
import kotlin.math.min

/**
 * @author lollipop
 * @date 2020-02-05 21:46
 * 颜色面板的View
 */
class ColorPanelView(context: Context, attrs: AttributeSet?, defStyleAttr:Int):
    SquareLayout(context,attrs,defStyleAttr) {

    constructor(context: Context, attrs: AttributeSet?):this(context,attrs,0)
    constructor(context: Context):this(context,null)

    private var contextWeight = 0.8F

    private val log = { value: String ->
        Log.d("ColorPanelView", "${System.identityHashCode(this)}:$value")
    }

    private val colorView = CirclePointView(context).apply {
        setTextColor(Color.WHITE)
        setShadowLayer(2F, 1F, 1F, Color.BLACK)
        gravity = Gravity.CENTER
    }

    private val borderView = View(context).apply {
        setBackgroundResource(R.drawable.checked_item_colors_panel)
        visibility = View.INVISIBLE
    }

    init {
        addView(colorView, LayoutParams(LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT).apply {
            val margin = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 5F, resources.displayMetrics).toInt()
            setMargins(margin, margin, margin, margin)
        })
        addView(borderView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        log("onMeasure")
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val length = min(measuredWidth, measuredHeight)
        val padding = (length * (1 - width) * 0.5F).toInt()
        if (padding != paddingLeft ||
            padding != paddingTop ||
            padding != paddingRight ||
            padding != paddingBottom) {
            setPadding(padding, padding, padding, padding)
            log("setPadding: $padding")
        }
    }

    fun setColor(color: Int) {
        colorView.setStatusColor(color)
    }

    fun setChecked(isChecked: Boolean, number: String = "", animation: Boolean = true) {
        if (animation) {
            val start = if (isChecked) { 0F } else { 1F }
            borderView.scaleX = start
            borderView.scaleY = start
            val end = if (isChecked) { 1F } else {0F }
            borderView.animate().let { animator ->
                animator.cancel()
                animator.scaleX(end).scaleY(end)
                animator.lifecycleBinding {
                    onStart {
                        if (isChecked) {
                            borderView.visibility = View.VISIBLE
                            removeThis(it)
                        }
                    }
                    onEnd {
                        if (!isChecked) {
                            borderView.visibility = View.INVISIBLE
                        }
                        removeThis(it)
                    }
                    onCancel {
                        removeThis(it)
                    }
                }
                animator.start()
            }
        } else {
            borderView.scaleX = 1F
            borderView.scaleY = 1F
            borderView.visibility = if (isChecked) { View.VISIBLE } else { View.INVISIBLE }
        }
        colorView.setAutoValue(if (isChecked) { number } else { "" })
    }

}
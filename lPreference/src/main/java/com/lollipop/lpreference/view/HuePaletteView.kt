package com.lollipop.lpreference.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView

/**
 * 色相显示的View
 * @author Lollipop
 * @date 2019/09/05
 */
class HuePaletteView(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int
) : AppCompatImageView(context, attrs, defStyleAttr) {

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    var hueCallback: HueCallback? = null
    private val hueDrawable = HuePaletteDrawable()

    init {
        setImageDrawable(hueDrawable)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event ?: return super.onTouchEvent(event)
        return when (event.action) {

            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP -> {
                if (event.action == MotionEvent.ACTION_DOWN) {
                    if (parent is ViewGroup) {
                        parent.requestDisallowInterceptTouchEvent(true)
                    }
                }
                val hue = hueDrawable.selectTo(event.y)
                hueCallback?.onHueSelect(hue)
                true
            }
            else -> {
                super.onTouchEvent(event)
            }
        }
    }

    fun parser(hue: Float) {
        hueDrawable.parser(hue)
        hueCallback?.onHueSelect(hue.toInt())
    }

    interface HueCallback {
        fun onHueSelect(hue: Int)
    }

    class HuePaletteDrawable : Drawable() {

        private val paint = Paint().apply {
            isDither = true
            isAntiAlias = true
        }
        private val hueColor = IntArray(361)
        private val linePaint = Paint().apply {
            isDither = true
            isAntiAlias = true
            color = Color.WHITE
            strokeWidth = 1F
        }
        private var selectHue = 0

        private val arrowPath = Path()

        init {
            for ((count, i) in ((hueColor.size - 1) downTo 0).withIndex()) {
                hueColor[count] = Color.HSVToColor(floatArrayOf(i.toFloat(), 1f, 1f))
            }
        }

        override fun draw(canvas: Canvas) {
            canvas.drawRect(bounds, paint)
            val selectY = getSelectY()
            canvas.drawLine(
                bounds.left.toFloat(), selectY,
                bounds.right.toFloat(), selectY, linePaint
            )
            canvas.save()
            canvas.translate(0F, selectY)
            canvas.drawPath(arrowPath, linePaint)
            canvas.restore()
        }

        override fun onBoundsChange(bounds: Rect?) {
            super.onBoundsChange(bounds)
            bounds ?: return

            val hueShader = LinearGradient(
                bounds.left.toFloat(), bounds.top.toFloat(),
                bounds.left.toFloat(), bounds.bottom.toFloat(),
                hueColor, null, Shader.TileMode.CLAMP
            )

            paint.shader = hueShader

            arrowPath.reset()
            val width = bounds.width() * 1F
            val h = width / 10
            arrowPath.moveTo(0F, -h)
            arrowPath.lineTo(0F, h)
            arrowPath.lineTo(h * 2, 0F)
            arrowPath.lineTo(0F, -h)

            arrowPath.moveTo(width, -h)
            arrowPath.lineTo(width, h)
            arrowPath.lineTo(width - h * 2, 0F)
            arrowPath.lineTo(width, -h)

            invalidateSelf()
        }

        fun selectTo(y: Float): Int {
            //计算高度对应的色相值序号
            val values = y / bounds.height() * 361 + 0.5
            //得到色相值
            selectHue = hueColor.size - values.toInt()
            if (selectHue < 0) {
                selectHue = 0
            }
            if (selectHue > 360) {
                selectHue = 360
            }
            //发起重绘
            invalidateSelf()
            return selectHue
        }

        fun parser(hue: Float) {
            this.selectHue = (hue + 0.5F).toInt()
            invalidateSelf()
        }

        private fun getSelectY(): Float {
            return (1 - 1.0F * selectHue / hueColor.size) * bounds.height()
        }

        override fun setAlpha(alpha: Int) {
            paint.alpha = alpha
        }

        override fun getOpacity(): Int {
            return PixelFormat.TRANSLUCENT
        }

        override fun setColorFilter(filter: ColorFilter?) {
            paint.colorFilter = filter
        }
    }

}
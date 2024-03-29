package com.lollipop.lpreference.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.max
import kotlin.math.min

/**
 * 透明度的调色面板
 * @author Lollipop
 * @date 2019/09/05
 */
class TransparencyPaletteView(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int
) : AppCompatImageView(context, attrs, defStyleAttr) {

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    private val transparencyPaletteDrawable = TransparencyPaletteDrawable()

    var transparencyCallback: TransparencyCallback? = null

    init {
        setImageDrawable(transparencyPaletteDrawable)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event ?: return super.onTouchEvent(event)
        return when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP -> {
                if (event.action == MotionEvent.ACTION_DOWN) {
                    if (parent is ViewGroup) {
                        parent.requestDisallowInterceptTouchEvent(true)
                    }
                }
                val alpha = transparencyPaletteDrawable.selectTo(event.x)
                transparencyCallback?.onTransparencySelect(alpha, (alpha * 255).toInt())
                true
            }
            else -> {
                super.onTouchEvent(event)
            }
        }
    }

    fun parser(alpha: Int) {
        val a = min(255, max(0, alpha))
        transparencyPaletteDrawable.parser(a / 255F)
    }


    interface TransparencyCallback {
        fun onTransparencySelect(alphaF: Float, alphaI: Int)
    }

    private class TransparencyPaletteDrawable : Drawable() {

        companion object {
            private const val spanY = 4
        }

        private var selectedAlpha = 0F

        private var alphaShader: Shader? = null

        private val paint = Paint().apply {
            isDither = true
            isAntiAlias = true
            color = Color.BLACK
        }

        private val arrowPath = Path()

        override fun draw(canvas: Canvas) {
            val gridWidth = bounds.height() * 1F / spanY
            val spanX = (bounds.width() / gridWidth).toInt()

            paint.color = Color.BLACK
            paint.shader = null
            var left: Float
            var top: Float
            for (x in 0..spanX) {
                for (y in 0..spanY) {
                    if ((x + y) % 2 != 0) {
                        continue
                    }
                    left = x * gridWidth + bounds.left
                    top = y * gridWidth + bounds.top
                    canvas.drawRect(left, top, left + gridWidth, top + gridWidth, paint)
                }
            }

            paint.shader = alphaShader
            canvas.drawRect(
                bounds.left.toFloat(), bounds.top.toFloat(),
                bounds.right.toFloat(), bounds.bottom.toFloat(), paint
            )

            paint.shader = null
            paint.color = Color.WHITE
            left = selectedAlpha * bounds.width() + bounds.left
            canvas.drawLine(left, bounds.top.toFloat(), left, bounds.bottom.toFloat(), paint)

            canvas.save()
            canvas.translate(left, 0F)
            canvas.drawPath(arrowPath, paint)
            canvas.restore()
        }

        fun parser(alphaF: Float) {
            selectedAlpha = alphaF
            invalidateSelf()
        }

        fun selectTo(x: Float): Float {
            selectedAlpha = when {
                x < bounds.left -> 0F
                x > bounds.right -> 1F
                else -> {
                    (x - bounds.left) / bounds.width()
                }
            }
            invalidateSelf()
            return selectedAlpha
        }

        override fun onBoundsChange(bounds: Rect?) {
            super.onBoundsChange(bounds)
            bounds ?: return
            alphaShader = LinearGradient(
                bounds.left.toFloat(), bounds.top.toFloat(),
                bounds.right.toFloat(), bounds.top.toFloat(),
                Color.TRANSPARENT, Color.BLACK, Shader.TileMode.CLAMP
            )

            arrowPath.reset()
            val height = bounds.height() * 1F
            val w = height / 10
            arrowPath.moveTo(-w, 0F)
            arrowPath.lineTo(w, 0F)
            arrowPath.lineTo(0F, w * 2)
            arrowPath.lineTo(-w, 0F)

            arrowPath.moveTo(-w, height)
            arrowPath.lineTo(w, height)
            arrowPath.lineTo(0F, height - w * 2)
            arrowPath.lineTo(-w, height)

            invalidateSelf()
        }

        override fun setAlpha(alpha: Int) {
            paint.alpha = alpha
        }

        override fun getOpacity(): Int {
            return PixelFormat.TRANSPARENT
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
            paint.colorFilter = colorFilter
        }

    }

}
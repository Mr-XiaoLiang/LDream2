package com.lollipop.ldream.drawer

import android.graphics.*
import android.graphics.drawable.Drawable
import com.lollipop.ldream.util.range
import kotlin.math.roundToInt

/**
 * @author lollipop
 * @date 2020-02-17 21:46
 * 闪光用的Drawable
 */
class FlashDrawable : Drawable() {

    companion object {

        private const val PRECISION = 1000
        private const val VALUE = 0xFFFF
        private const val SYMBOL = 0x1 shl 17
        private const val POSITIVE = 0x1 shl 18

        fun makeLocation(offset: Float, isHorizontal: Boolean, isPositive: Boolean): Int {
            var value = (offset.range(0F, 1F) * PRECISION).roundToInt() and VALUE
            if (isHorizontal) {
                value = value or SYMBOL
            }
            if (isPositive) {
                value = value or POSITIVE
            }
            return value
        }

        fun getOffset(location: Int): Float {
            return (location and VALUE).toFloat() / PRECISION
        }

        fun isHorizontal(location: Int): Boolean {
            return (location and SYMBOL) > 0
        }

        fun isPositive(location: Int): Boolean {
            return (location and POSITIVE) > 0
        }

    }

    private val paint = Paint().apply {
        isAntiAlias = true
        isDither = true
        strokeCap = Paint.Cap.ROUND
    }

    private var horizontalShader: Shader? = null
    private var verticalShader: Shader? = null

    var progress: Float = 0F
        set(value) {
            field = value
            invalidateSelf()
        }

    private val flashLocations = ArrayList<Int>()

    private val colors = ArrayList<Int>()

    var strokeWidth: Float
        get() {
            return paint.strokeWidth
        }
        set(value) {
            paint.strokeWidth = value
        }

    private var width = 0F
    private var height = 0F

    fun setColor(array: ArrayList<Int>) {
        colors.clear()
        colors.addAll(array)
    }

    fun clear() {
        flashLocations.clear()
    }

    fun setLocations(array: IntArray) {
        clear()
        array.forEach {
            flashLocations.add(it)
        }
    }

    override fun draw(canvas: Canvas) {
        for (location in flashLocations) {
            val offset = getOffset(location)
            var pro = progress
            if (isPositive(location)) {
                pro *= -1
            }
            if (isHorizontal(location)) {
                drawHorizontal(canvas, offset, pro)
            } else {
                drawVertical(canvas, offset, pro)
            }
        }
    }

    private fun drawHorizontal(canvas: Canvas, offset: Float, pro: Float) {
        paint.shader = horizontalShader
        val x = width * pro
        val y = height * offset + bounds.top
        canvas.save()
        canvas.translate(x, 0F)
        canvas.drawLine(bounds.left.toFloat(), y, bounds.right.toFloat(), y, paint)
        canvas.restore()
    }

    private fun drawVertical(canvas: Canvas, offset: Float, pro: Float) {
        paint.shader = verticalShader
        val x = width * offset + bounds.left
        val y = height * pro
        canvas.save()
        canvas.translate(0F, y)
        canvas.drawLine(x, bounds.top.toFloat(), x, bounds.bottom.toFloat(), paint)
        canvas.restore()
    }

    private fun bindDefaultColor() {
        paint.color = if (colors.isEmpty()) {
            Color.WHITE
        } else {
            colors[0]
        }
    }

    override fun onBoundsChange(bounds: Rect?) {
        super.onBoundsChange(bounds)
        notifyDataChange()
    }

    fun notifyDataChange() {
        width = bounds.width().toFloat()
        height = bounds.height().toFloat()
        bindDefaultColor()
        if (bounds.isEmpty || colors.isEmpty() || colors.size < 2) {
            horizontalShader = null
            verticalShader = null
            return
        }
        val colorArray = IntArray(colors.size) { colors[it] }
        horizontalShader = LinearGradient(
            bounds.left.toFloat(),
            bounds.top.toFloat(), bounds.right.toFloat(), bounds.top.toFloat(),
            colorArray, null, Shader.TileMode.CLAMP
        )
        verticalShader = LinearGradient(
            bounds.left.toFloat(),
            bounds.top.toFloat(), bounds.left.toFloat(), bounds.bottom.toFloat(),
            colorArray, null, Shader.TileMode.CLAMP
        )
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
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
class FlashDrawable: Drawable() {

    companion object {

        private const val PRECISION = 10000
        private const val VALUE = 0x1 shl 16
        private const val SYMBOL = VALUE shl 1

        fun makeLocation(offset: Float, isHorizontal: Boolean): Int {
            val value = (offset.range(0F, 1F) * PRECISION).roundToInt() and VALUE
            if (isHorizontal) {
                return value or SYMBOL
            }
            return value
        }

        fun getOffset(location: Int): Float {
            return (location and VALUE).toFloat() / PRECISION
        }

        fun isHorizontal(location: Int): Boolean {
            return (location and SYMBOL) > 0
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
            if (isHorizontal(location)) {
                drawHorizontal(canvas, offset)
            } else {
                drawVertical(canvas, offset)
            }
        }
    }

    private fun drawHorizontal(canvas: Canvas, offset: Float) {
        horizontalShader?:return
        paint.shader = horizontalShader
        val x = width * progress
        val y = height * offset + bounds.top - (strokeWidth * 0.5F)
        canvas.save()
        canvas.translate(x, 0F)
        canvas.drawLine(bounds.left.toFloat(), y, bounds.right.toFloat(), y, paint)
        canvas.restore()
    }

    private fun drawVertical(canvas: Canvas, offset: Float) {
        verticalShader?:return
        paint.shader = verticalShader
        val x = width * offset + bounds.left - (strokeWidth * 0.5F)
        val y = height * progress
        canvas.save()
        canvas.translate(0F, y)
        canvas.drawLine(x, bounds.top.toFloat(), x, bounds.bottom.toFloat(), paint)
    }

    override fun onBoundsChange(bounds: Rect?) {
        super.onBoundsChange(bounds)
        notifyDataChange()
    }

    fun notifyDataChange() {
        width = bounds.width().toFloat()
        height = bounds.height().toFloat()
        if (bounds.isEmpty || colors.isEmpty()) {
            horizontalShader = null
            verticalShader = null
            return
        }
        val colorArray = IntArray(colors.size) { colors[it] }
        horizontalShader = LinearGradient(bounds.left.toFloat(),
            bounds.top.toFloat(), bounds.right.toFloat(), bounds.top.toFloat(),
            colorArray, null, Shader.TileMode.CLAMP)
        verticalShader = LinearGradient(bounds.left.toFloat(),
            bounds.top.toFloat(), bounds.left.toFloat(), bounds.bottom.toFloat(),
            colorArray, null, Shader.TileMode.CLAMP)
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
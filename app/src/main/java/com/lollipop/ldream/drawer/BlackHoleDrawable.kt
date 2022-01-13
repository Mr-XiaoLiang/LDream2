package com.lollipop.ldream.drawer

import android.graphics.*
import android.graphics.drawable.Drawable
import com.lollipop.lpreference.util.changeAlpha
import kotlin.math.min

/**
 * @author lollipop
 * @date 2020-02-16 17:38
 * 黑洞的绘制器
 */
class BlackHoleDrawable: Drawable() {

    private val paint: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            isDither = true
        }
    }

    private val matrix = Matrix()

    private val squareBounds = RectF()

    private val drawingBounds = RectF()

    override fun onBoundsChange(bounds: Rect?) {
        super.onBoundsChange(bounds)
        resetShader()
    }

    private fun resetShader() {
        if (bounds.isEmpty) {
            paint.shader = null
            invalidateSelf()
            return
        }
        val radius = min(bounds.width(), bounds.height()) * 0.5F
        val gradient = createBlackGradient(radius, 0F, 0F, 0F, 1F)
        squareBounds.set(0F, 0F, radius * 2, radius * 2)
        drawingBounds.set(bounds)
        matrix.reset()
        matrix.postScale(drawingBounds.width() / squareBounds.width(),
            drawingBounds.height() / squareBounds.height())
        gradient.setLocalMatrix(matrix)
        paint.shader = gradient
    }

    private fun createBlackGradient(radius: Float, vararg alpha: Float): RadialGradient {
        val colors = IntArray(alpha.size) { Color.BLACK.changeAlpha(alpha[it]) }
        return RadialGradient(radius, radius, radius, colors,  null, Shader.TileMode.CLAMP)
    }

    override fun draw(canvas: Canvas) {
        paint.shader?:return
        canvas.drawRect(bounds, paint)
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
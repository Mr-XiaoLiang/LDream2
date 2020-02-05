package com.lollipop.lpreference.dialog


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.animation.BounceInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ViewAnimator
import com.lollipop.lpreference.R
import com.lollipop.lpreference.util.*
import com.lollipop.lpreference.view.HuePaletteView
import com.lollipop.lpreference.view.SatValPaletteView
import com.lollipop.lpreference.view.TransparencyPaletteView
import kotlinx.android.synthetic.main.fragment_colors_panel_dialog.*

/**
 * 颜色选择的面板
 * @author Lollipop
 */
class ColorsPanelDialogFragment private constructor(): BaseDialog(),
    HuePaletteView.HueCallback, SatValPaletteView.HSVCallback,
    TransparencyPaletteView.TransparencyCallback,
    View.OnClickListener {

    companion object {
        fun show(context: Context) {
            ColorsPanelDialogFragment().apply {

            }.show(context, "ColorsPanelDialogFragment")
        }
    }

    override val contextId: Int
        get() = R.layout.fragment_colors_panel_dialog

    private val hsvTemp = FloatArray(3)

    private var colorRGB = Color.RED
    private var colorAlpha = 255

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        huePalette.hueCallback = this
        satValPalette.hsvCallback = this
        transparencyPalette.transparencyCallback = this
        definiteBtn.setOnClickListener(this)
        selectedColor(colorRGB)

    }

    private fun selectedColor(color: Int) {
        Color.colorToHSV(color, hsvTemp)
        huePalette.parser(hsvTemp[0])
        satValPalette.parser(hsvTemp[1], hsvTemp[2])
        transparencyPalette.parser(Color.alpha(color))
    }

    override fun onHueSelect(hue: Int) {
        satValPalette.onHueChange(hue.toFloat())
    }

    override fun onHSVSelect(hsv: FloatArray, rgb: Int) {
        colorRGB = rgb
        onColorChange()
    }

    override fun onTransparencySelect(alphaF: Float, alphaI: Int) {
        colorAlpha = alphaI
        onColorChange()
    }

    private fun merge(alpha: Int = colorAlpha, rgb: Int = colorRGB): Int {
        val a = alpha.range(0, 255) shl 24
        return rgb and 0xFFFFFF or a
    }

    private fun onColorChange() {
        val color = merge()
        previewColorView.setStatusColor(color)
        colorValueView.text = color.colorValue()
    }

    private fun openPalette() {
        if (palettePanel.translationX < 1) {
            palettePanel.translationX = palettePanel.width * 1F
        }
        palettePanel.animate().let { animator ->
            animator.cancel()
            changeInterpolator(animator)
            animator.translationX(0F)

            animator.lifecycleBinding {
                onStart {
                    palettePanel.visibility = View.VISIBLE
                    removeThis(it)
                }
            }
            animator.start()
        }

        colorPoolView.animate().let { animator ->
            animator.cancel()
            changeInterpolator(animator)
            animator.translationX(colorPoolView.width * -1F)
            animator.lifecycleBinding {
                onStart {
                    colorPoolView.visibility = View.INVISIBLE
                    removeThis(it)
                }
            }
            animator.start()
        }
    }

    private fun closePalette() {
        palettePanel.animate().let { animator ->
            animator.cancel()
            changeInterpolator(animator)
            animator.translationX(palettePanel.width * 1F)
            animator.lifecycleBinding {
                onEnd {
                    palettePanel.visibility = View.INVISIBLE
                    removeThis(it)
                }
            }
            animator.start()
        }

        colorPoolView.animate().let { animator ->
            animator.cancel()
            changeInterpolator(animator)
            animator.translationX(0F)
            animator.lifecycleBinding {
                onStart {
                    colorPoolView.visibility = View.VISIBLE
                    removeThis(it)
                }
            }
            animator.start()
        }
    }

    private fun changeInterpolator(animator: ViewPropertyAnimator) {
        if (animator.interpolator !is DecelerateInterpolator) {
            animator.interpolator = DecelerateInterpolator()
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            definiteBtn -> {
                closePalette()
            }
        }
    }

}
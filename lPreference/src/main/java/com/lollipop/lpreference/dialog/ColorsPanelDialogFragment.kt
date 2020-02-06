package com.lollipop.lpreference.dialog


import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import android.view.animation.DecelerateInterpolator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.lpreference.R
import com.lollipop.lpreference.util.*
import com.lollipop.lpreference.view.ColorPanelView
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

    private val shownData = ArrayList<Int>()

    private val selectedData = ArrayList<Int>()

    private val shownActions = ArrayList<Int>()

    private var isEditMode = false

    private val itemAdapter: ItemAdapter by lazy {
        ItemAdapter(shownData, {
            itemIsChecked(it)
        }, {
            onItemClick(it)
        }, shownActions)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        huePalette.hueCallback = this
        satValPalette.hsvCallback = this
        transparencyPalette.transparencyCallback = this
        definiteBtn.setOnClickListener(this)
        selectedColor(colorRGB)

        colorPoolView.layoutManager = GridLayoutManager(colorPoolView.context,
            4, RecyclerView.VERTICAL, false)

        colorPoolView.adapter = itemAdapter
        itemAdapter.notifyDataSetChanged()
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
        if (!palettePanel.isPortrait()) {
            return
        }
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
        if (!palettePanel.isPortrait()) {
            return
        }
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

    private fun itemIsChecked(position: Int): Int {
        if (isEditMode) {
            return 0
        }
        if (position < 0 || position >= shownData.size) {
            return 0
        }
        val color = shownData[position]
        for (index in selectedData.indices) {
            if (selectedData[index] == color) {
                return index + 1
            }
        }
        return 0
    }

    private fun onItemClick(holder: RecyclerView.ViewHolder): Boolean {
        if (ItemAdapter.isAction(holder)) {
            onActionSelected(ItemAdapter.getAction(holder))
            return true
        }
        if (isEditMode) {
            itemAdapter.removeItem(shownData[holder.adapterPosition])
            return false
        }
        val position = holder.adapterPosition
        val color = shownData[position]
        for (index in selectedData.indices) {
            if (selectedData[index] == color) {
                selectedData.removeAt(index)
                return true
            }
        }
        selectedData.add(color)
        return true
    }

    private fun onActionSelected(actionId: Int) {
        // TODO
    }

    private fun onModeChange() {
        // TODO
    }

    private class ItemAdapter(private val data: ArrayList<Int>,
                              private val isChecked: (Int) -> Int,
                              private val onClick: (RecyclerView.ViewHolder) -> Boolean,
                              private val actionList: ArrayList<Int>):
        RecyclerView.Adapter<ItemHolder>() {

        companion object {

            private const val TYPE_ITEM = 0
            private const val TYPE_ACTION = 1

            fun getAction(holder: RecyclerView.ViewHolder): Int {
                if (holder is ActionHolder) {
                    return holder.iconId
                }
                return 0
            }

            fun isAction(holder: RecyclerView.ViewHolder): Boolean {
                return holder is ActionHolder
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
            return when (viewType) {
                TYPE_ACTION -> {
                    ActionHolder.create(parent, onClick)
                }
                else -> {
                    ItemHolder.create(parent, isChecked, onClick)
                }
            }
        }

        override fun getItemCount(): Int {
            return data.size + actionList.size
        }

        override fun getItemViewType(position: Int): Int {
            return if (position < data.size) {
                TYPE_ITEM
            } else {
                TYPE_ACTION
            }
        }

        override fun onBindViewHolder(holder: ItemHolder, position: Int) {
            when (getItemViewType(position)) {
                TYPE_ITEM -> {
                    holder.onBind(data[position])
                }
                TYPE_ACTION -> {
                    holder.onBind(actionList[position - data.size])
                }
            }
        }

        fun addItem(color: Int) {
            data.add(color)
            notifyItemInserted(data.size - 1)
        }

        fun removeItem(color: Int) {
            var index = -1
            for (i in data.indices) {
                if (color == data[i]) {
                    index = i
                    break
                }
            }
            if (index >= 0) {
                data.removeAt(index)
                notifyItemRemoved(index)
            }
        }
    }

    private open class ItemHolder (protected val colorView: ColorPanelView,
                            private val isChecked: (Int) -> Int,
                            private val onClick: (RecyclerView.ViewHolder) -> Boolean):
        RecyclerView.ViewHolder(colorView) {

        companion object {

            fun createView(viewGroup: ViewGroup): ColorPanelView {
                return ColorPanelView(viewGroup.context)
            }

            fun create(viewGroup: ViewGroup,
                       isChecked: (Int) -> Int,
                       onClick: (RecyclerView.ViewHolder) -> Boolean): ItemHolder {
                return ItemHolder(createView(viewGroup), isChecked, onClick)
            }
        }

        init {
            itemView.setOnClickListener {
                if (onClick(this)) {
                    updateCheckedStatus()
                }
            }
        }

        open fun onBind(value: Int) {
            colorView.setColor(value)
            updateCheckedStatus(false)
        }

        private fun updateCheckedStatus(isAnim: Boolean = true) {
            val status = isChecked(this.adapterPosition)
            val checked = status != 0
            val value = if (status > 0) { status.toString() } else { "" }
            colorView.setChecked(checked, value, isAnim)
        }

    }

    private class ActionHolder private constructor(colorView: ColorPanelView,
                                                  onClick: (RecyclerView.ViewHolder) -> Boolean):
        ItemHolder(colorView, { 0 }, onClick) {

        companion object {
            fun create(viewGroup: ViewGroup,
                       onClick: (RecyclerView.ViewHolder) -> Boolean): ActionHolder {
                return ActionHolder(createView(viewGroup), onClick)
            }
        }

        init {
            itemView.setOnClickListener {
                onClick(this)
            }
        }

        var iconId = 0
            private set

        override fun onBind(value: Int) {
            iconId = value
            colorView.setBackgroundResource(value)
            colorView.setColor(Color.TRANSPARENT)
            colorView.setChecked(false)
        }
    }



}
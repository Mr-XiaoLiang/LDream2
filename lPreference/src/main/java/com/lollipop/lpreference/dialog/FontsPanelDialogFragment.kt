package com.lollipop.lpreference.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.base.findInSelf
import com.lollipop.fonts.FontsHelper
import com.lollipop.fonts.LFont
import com.lollipop.lpreference.R
import com.lollipop.lpreference.util.*

/**
 * @author lollipop
 * @date 2020-02-14 22:02
 * 图片选择的对话框
 */
class FontsPanelDialogFragment : BaseDialog() {

    companion object {

        private const val ARG_PRESET = "ARG_PRESET"

        fun show(
            context: Context,
            selected: String = "",
            callback: (LFont?) -> Unit
        ) {
            FontsPanelDialogFragment().apply {
                val bundle = arguments?:Bundle()
                bundle.putString(ARG_PRESET, selected)
                arguments = bundle
                selectedFontCallback = callback
            }.show(context, "FontsPanelDialogFragment")
        }
    }

    override val contextId: Int
        get() = R.layout.fragment_fonts_panel

    private var selectedFontCallback: ((LFont?) -> Unit)? = null

    private val fontsGroup: RecyclerView? by findInSelf()

    private var presetFont = ""
    private var selectedFont: LFont? = null

    private val data = ArrayList<LFont>()

    private val adapter by lazy {
        FontsAdapter(data, ::isItemChecked, ::onItemClick)
    }

    private fun isItemChecked(lFont: LFont): Boolean {
        return lFont.assetsName == selectedFont?.assetsName
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun onItemClick(position: Int) {
        selectedFont = if (position in data.indices) {
            data[position]
        } else {
            null
        }
        selectedFontCallback?.invoke(selectedFont)
        adapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fontsGroup?.let { group ->
            group.layoutManager = LinearLayoutManager(group.context)
            group.adapter = adapter
        }
        data.clear()
        data.addAll(FontsHelper.findListByAssets(view.context))
        presetFont = arguments?.getString(ARG_PRESET) ?: ""
        selectedFont = data.find { it.assetsName == presetFont }
        adapter.notifyDataSetChanged()
    }

    private class FontsAdapter(
        private val data: List<LFont>,
        private val isChecked: (LFont) -> Boolean,
        private val onClickListener: (Int) -> Unit
    ) : RecyclerView.Adapter<FontHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FontHolder {
            return FontHolder.create(
                parent,
                onClickListener
            )
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: FontHolder, position: Int) {
            val info = data[position]
            holder.bind(info, isChecked(info))
        }

    }

    private class FontHolder private constructor(
        view: View,
        private val onClickListener: (Int) -> Unit
    ) : RecyclerView.ViewHolder(view) {
        companion object {
            fun create(
                group: ViewGroup,
                onClick: (Int) -> Unit
            ): FontHolder {
                return FontHolder(
                    LayoutInflater.from(group.context)
                        .inflate(R.layout.item_font_panel, group, false),
                    onClick
                )
            }

        }

        init {
            itemView.setOnClickListener {
                onItemClick()
            }
        }

        private val fontPreviewView: TextView? by findInSelf()
        private val fontNameView: TextView? by findInSelf()
        private val checkedIconView: ImageView? by findInSelf()

        fun bind(info: LFont, isSelected: Boolean) {
            checkedIconView?.isInvisible = !isSelected
            fontPreviewView?.let {
                info.bind(it)
            }
            fontNameView?.let {
                it.text = info.displayName
            }
        }

        private fun onItemClick() {
            onClickListener(adapterPosition)
        }

    }
}
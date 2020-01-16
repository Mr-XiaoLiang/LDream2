package com.lollipop.ldream.preference

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.ldream.R

/**
 * @author lollipop
 * @date 2020-01-16 23:08
 * 基础的偏好设置项
 */
open class BasePreferenceItem private constructor(view: View) :
    RecyclerView.ViewHolder(view),
    View.OnClickListener{

    constructor(group: ViewGroup): this(createView(group))

    companion object {
        private fun createView(group: ViewGroup): View {
            return LayoutInflater.from(group.context).inflate(R.layout.item_base_preference, group)
        }
    }

    private val titleView: TextView by lazy {
        itemView.setOnClickListener(this)
        itemView.findViewById<TextView>(R.id.titleView)
    }
    private val summaryView: TextView by lazy {
        itemView.findViewById<TextView>(R.id.summaryView)
    }
    private val iconView: ImageView by lazy {
        itemView.findViewById<ImageView>(R.id.iconView).apply {
            outlineProvider = ViewOutlineProvider.BACKGROUND
        }
    }
    private val previewBody: FrameLayout by lazy {
        itemView.findViewById<FrameLayout>(R.id.previewBody).apply {
            setOnClickListener(this@BasePreferenceItem)
        }
    }

    protected fun bindPreview(view: View) {
        if (previewBody.childCount > 0) {
            previewBody.removeAllViews()
        }
        if (previewBody.visibility != View.VISIBLE) {
            previewBody.visibility = View.VISIBLE
        }
        previewBody.addView(view)
    }

    protected fun bindPreview(layoutId: Int): View {
        val inflater = LayoutInflater.from(previewBody.context)
        val view = inflater.inflate(layoutId, previewBody, false)
        bindPreview(view)
        return view
    }

    protected fun setIcon(resId: Int) {
        if (iconView.visibility != View.VISIBLE) {
            iconView.visibility = View.VISIBLE
        }
        iconView.setImageResource(resId)
    }

    protected open fun onPreviewClick(view: View) { }

    protected open fun onItemClick(view: View) { }

    protected var title: CharSequence
        get() {
            return titleView.text
        }
        set(value) {
            titleView.text = value
        }

    protected var summary: CharSequence
        get() {
            return summaryView.text
        }
        set(value) {
            summaryView.text = value
        }

    fun bind(info: BasePreferenceInfo) {
        title = info.title
        summary = info.summary
        onBind(info)
    }

    protected open fun onBind(info: BasePreferenceInfo) { }

    override fun onClick(v: View?) {
        when(v) {
            itemView -> {
                onItemClick(v)
            }
            previewBody -> {
                onPreviewClick(v)
            }
        }
    }

}
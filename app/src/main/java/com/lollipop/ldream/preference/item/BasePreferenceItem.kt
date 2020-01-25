package com.lollipop.ldream.preference.item

import android.content.Context
import android.content.res.ColorStateList
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.ldream.R
import com.lollipop.ldream.preference.PreferenceConfig
import com.lollipop.ldream.preference.info.BasePreferenceInfo

/**
 * @author lollipop
 * @date 2020-01-16 23:08
 * 基础的偏好设置项
 */
abstract class BasePreferenceItem <T : BasePreferenceInfo<*>> private constructor(view: View) :
    RecyclerView.ViewHolder(view),
    View.OnClickListener{

    abstract val widgetId: Int

    protected val preferenceKey: String
        get() {
            return preferenceInfo?.key?:""
        }

    protected var preferenceInfo: T? = null
        private set

    protected val context: Context
        get() {
            return itemView.context
        }

    constructor(group: ViewGroup): this(
        createView(
            group
        )
    )

    companion object {
        private fun createView(group: ViewGroup): View {
            return LayoutInflater.from(group.context).inflate(R.layout.item_base_preference, group)
        }
    }

    private val titleView: TextView by lazy {
        itemView.setOnClickListener(this)
        itemView.findViewById<TextView>(R.id.titleView).apply {
            setTextSize(TypedValue.COMPLEX_UNIT_SP,
                PreferenceConfig.titleSize
            )
            setTextColor(PreferenceConfig.titleColor)
        }
    }
    private val summaryView: TextView by lazy {
        itemView.findViewById<TextView>(R.id.summaryView).apply {
            setTextSize(TypedValue.COMPLEX_UNIT_SP,
                PreferenceConfig.summarySize
            )
            setTextColor(PreferenceConfig.summaryColor)
        }
    }
    private val iconView: ImageView by lazy {
        itemView.findViewById<ImageView>(R.id.iconView).apply {
            outlineProvider = ViewOutlineProvider.BACKGROUND
            imageTintList = ColorStateList.valueOf(PreferenceConfig.iconColor)
        }
    }
    private val previewBody: FrameLayout by lazy {
        itemView.findViewById<FrameLayout>(R.id.previewBody).apply {
            setOnClickListener(this@BasePreferenceItem)
        }
    }

    fun init() {
        if (widgetId != 0) {
            bindPreview(widgetId)
        }
        onInit(itemView)
    }

    protected open fun onInit(view: View) {}

    private fun bindPreview(view: View) {
        if (previewBody.childCount > 0) {
            previewBody.removeAllViews()
        }
        if (previewBody.visibility != View.VISIBLE) {
            previewBody.visibility = View.VISIBLE
        }
        previewBody.addView(view)
    }

    private fun bindPreview(layoutId: Int): View {
        val inflater = LayoutInflater.from(previewBody.context)
        val view = inflater.inflate(layoutId, previewBody, false)
        bindPreview(view)
        return view
    }

    protected fun setIcon(resId: Int) {
        if (resId != 0) {
            if (iconView.visibility != View.VISIBLE) {
                iconView.visibility = View.VISIBLE
            }
            iconView.setImageResource(resId)
        } else {
            if (iconView.visibility != View.GONE) {
                iconView.visibility = View.GONE
            }
        }
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

    fun bind(info: T) {
        preferenceInfo = info
        title = info.title
        summary = info.summary
        setIcon(info.iconId)
        onBind(info)
    }

    protected fun notifyInfoChange() {
        preferenceInfo?.let { bind(it) }
    }

    protected open fun onBind(info: T) { }

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
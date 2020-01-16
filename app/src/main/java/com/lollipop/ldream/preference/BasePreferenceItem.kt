package com.lollipop.ldream.preference

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.ldream.R

/**
 * @author lollipop
 * @date 2020-01-16 23:08
 * 基础的偏好设置项
 */
open class BasePreferenceItem(group: ViewGroup) :
    RecyclerView.ViewHolder(createView(group)),
    View.OnClickListener{

    companion object {
        private fun createView(group: ViewGroup): View {
            return LayoutInflater.from(group.context).inflate(R.layout.item_base_preference, group)
        }
    }

    private val titleView: TextView by lazy {
        itemView.findViewById<TextView>(R.id.titleView)
    }
    private val summaryView: TextView by lazy {
        itemView.findViewById<TextView>(R.id.summaryView)
    }
    private val previewBody: FrameLayout by lazy {
        itemView.findViewById<FrameLayout>(R.id.previewBody)
    }

    protected fun bindPrevier(view: View) {
        previewBody.addView(view)
    }

    override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
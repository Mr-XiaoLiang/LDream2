package com.lollipop.ldream.preference

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.ldream.preference.info.BasePreferenceInfo
import com.lollipop.ldream.preference.item.BasePreferenceItem

/**
 * @author lollipop
 * @date 2020-01-18 20:32
 * 偏好设置的适配器
 */
class PreferenceAdapter(private val data: ArrayList<BasePreferenceInfo<*>>):
    RecyclerView.Adapter<BasePreferenceItem<*>>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasePreferenceItem<*> {
        return PreferenceFactory.createItem(parent,viewType)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: BasePreferenceItem<*>, position: Int) {
        PreferenceFactory.bindItem(holder, data[position])
    }

    override fun getItemViewType(position: Int): Int {
        return PreferenceFactory.getInfoType(data[position])
    }
}
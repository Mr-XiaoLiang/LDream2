package com.lollipop.ldream.preference

import android.text.TextUtils
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.ldream.preference.info.BasePreferenceInfo
import com.lollipop.ldream.preference.item.BasePreferenceItem
import com.lollipop.ldream.preference.item.StatusProvider

/**
 * @author lollipop
 * @date 2020-01-18 20:32
 * 偏好设置的适配器
 */
class PreferenceAdapter(private val data: ArrayList<BasePreferenceInfo<*>>):
    RecyclerView.Adapter<BasePreferenceItem<*>>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasePreferenceItem<*> {
        val item = PreferenceFactory.createItem(parent,viewType)
        item.init(
            { getStatus(data[it]) },
            { checkItemStatus(data[it]) }
        )
        return item
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: BasePreferenceItem<*>, position: Int) {
        PreferenceFactory.bindItem(holder, data[position])
    }

    private fun getStatus(info: BasePreferenceInfo<*>): Boolean {
        if (TextUtils.isEmpty(info.relevantKey)) {
            return info.enable
        }
        val relevantInfo = PreferenceHelper.findItemByKey(info.relevantKey, data) ?: return info.enable
        return if (relevantInfo is StatusProvider) {
            relevantInfo.statusValue == info.relevantEnable
        } else {
            info.enable
        }
    }

    private fun checkItemStatus(info: BasePreferenceInfo<*>) {
        if (TextUtils.isEmpty(info.key)) {
            return
        }
        if (info !is StatusProvider) {
            return
        }
        val statusValue = info.statusValue
        for (index in data.indices) {
            val item = data[index]
            if (item == info) {
                continue
            }
            if (item.relevantKey == info.key) {
                item.enable = item.relevantEnable == statusValue
            }
            notifyItemChanged(index)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return PreferenceFactory.getInfoType(data[position])
    }
}
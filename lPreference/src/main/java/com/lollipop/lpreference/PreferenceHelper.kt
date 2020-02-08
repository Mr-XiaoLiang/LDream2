package com.lollipop.lpreference

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.ArraySet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.lpreference.info.*

/**
 * @author lollipop
 * @date 2020-01-17 00:06
 * 偏好设置的辅助器
 */
class PreferenceHelper(private val group: RecyclerView) {

    companion object {

        inline fun <reified T> put(context: Context, key: String, value: T) {
            if (TextUtils.isEmpty(key)) {
                return
            }
            val preference = context.getSharedPreferences(key, Context.MODE_PRIVATE).edit()
            when (value) {
                is String -> {
                    preference.putString(key, value)
                }
                is Int -> {
                    preference.putInt(key, value)
                }
                is Boolean -> {
                    preference.putBoolean(key, value)
                }
                is Float -> {
                    preference.putFloat(key, value)
                }
                is Double -> {
                    preference.putFloat(key, value.toFloat())
                }
                is Long -> {
                    preference.putLong(key, value)
                }
                is Set<*> -> {
                    if (value.isEmpty()) {
                        preference.putStringSet(key, ArraySet<String>())
                    } else {
                        val set = ArraySet<String>()
                        for (v in value) {
                            when (v) {
                                null -> {
                                    set.add("")
                                }
                                is String -> {
                                    set.add(v)
                                }
                                else -> {
                                    set.add(v.toString())
                                }
                            }
                        }
                    }
                }
                else -> {
                    preference.putString(key, value.toString())
                }
            }
            preference.apply()
        }

        @Suppress("UNCHECKED_CAST")
        inline fun <reified T> get(context: Context, key: String, def: T): T {
            if (TextUtils.isEmpty(key)) {
                return def
            }
            val preference = context.getSharedPreferences(key, Context.MODE_PRIVATE)
            val value = when (def) {
                is String -> {
                    preference.getString(key, def)
                }
                is Int -> {
                    preference.getInt(key, def)
                }
                is Boolean -> {
                    preference.getBoolean(key, def)
                }
                is Float -> {
                    preference.getFloat(key, def)
                }
                is Long -> {
                    preference.getLong(key, def)
                }
                is Set<*> -> {
                    preference.getStringSet(key, def as Set<String>)
                }
                else -> def
            }?:def
            return value as T
        }

        fun findItemByKey(key: String, data: ArrayList<BasePreferenceInfo<*>>): BasePreferenceInfo<*>? {
            for (info in data) {
                if (info.key == key) {
                    return info
                }
            }
            return null
        }

    }

    private val context: Context
        get() = group.context

    private val data = ArrayList<BasePreferenceInfo<*>>()

    private val adapter: PreferenceAdapter by lazy {
        PreferenceAdapter(data)
    }

    private fun init() {
        if (group.layoutManager == null) {
            group.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }
        if (group.adapter == null) {
            group.adapter = adapter
        }
        adapter.notifyDataSetChanged()
    }

    fun addItem(vararg info: BasePreferenceInfo<*>) {
        data.addAll(info)
    }

    fun removeItem(key: String) {
        findItemByKey(key)?.let {
            data.remove(it)
        }
    }

    fun build(run: PreferenceHelper.() -> Unit) {
        this.apply(run)
        init()
    }

    fun number(key: String, run: NumberPreferenceInfo.() -> Unit): NumberPreferenceInfo {
        checkItem(key)
        return NumberPreferenceInfo(key).apply(run)
    }

    fun action(action: Intent,
               run: ActionPreferenceInfo.() -> Unit): ActionPreferenceInfo {
        return ActionPreferenceInfo(action).apply(run)
    }

    fun switch(key: String, run: SwitchPreferenceInfo.() -> Unit): SwitchPreferenceInfo {
        checkItem(key)
        return SwitchPreferenceInfo(key).apply(run)
    }

    fun colors(key: String, run: ColorsPreferenceInfo.() -> Unit): ColorsPreferenceInfo {
        checkItem(key)
        return ColorsPreferenceInfo(key).apply(run)
    }

    private fun checkItem(key: String) {
        if (findItemByKey(key) != null) {
            throw RuntimeException("Already contains an item with the same key")
        }
    }

    private fun findItemByKey(key: String): BasePreferenceInfo<*>? {
        return findItemByKey(key, data)
    }

}
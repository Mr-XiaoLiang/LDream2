package com.lollipop.ldream.preference

import android.content.Context
import android.util.ArraySet
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView

/**
 * @author lollipop
 * @date 2020-01-17 00:06
 * 偏好设置的辅助器
 */
class PreferenceHelper(private val group: RecyclerView,
                       private val key: String = "user") {

    companion object {

        inline fun <reified T> put(context: Context, key: String, value: T) {
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
            }
            preference.apply()
        }

        @Suppress("UNCHECKED_CAST")
        inline fun <reified T> get(context: Context, key: String, def: T): T {
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

    }

    private class PreferenceAdapter(private val data: ArrayList<BasePreferenceInfo>,
                                    private val layoutInflater: LayoutInflater) {

    }

}
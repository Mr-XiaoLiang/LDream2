package com.lollipop.fonts

import android.content.Context
import android.graphics.Typeface
import android.widget.TextView

class LFont(
    val displayName: String,
    val assetsName: String
) {

    companion object {

        val EMPTY = LFont("", "")

        fun valueOf(assetsName: String): LFont {
            var name = assetsName
            val lastIndexOf = assetsName.lastIndexOf("/")
            if (lastIndexOf >= 0 && lastIndexOf < assetsName.length - 1) {
                name = assetsName.substring(lastIndexOf + 1)
            }
            return LFont(displayName = name, assetsName = assetsName)
        }
    }

    fun load(context: Context): Typeface? {
        if (assetsName.isEmpty()) {
            return null
        }
        return FontsHelper.load(context, this)
    }

    fun bind(textView: TextView) {
        textView.typeface = load(textView.context)
    }

}
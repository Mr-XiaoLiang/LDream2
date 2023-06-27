package com.lollipop.fonts

import android.content.Context
import android.graphics.Typeface

object FontsHelper {

    private val typefaceMap = HashMap<String, Typeface>()

    private val fontList = ArrayList<LFont>()

    private val fontCache = HashMap<String, LFont>()

    fun load(context: Context, font: LFont): Typeface? {
        return load(context, font.assetsName)
    }

    fun valueOf(assetsName: String): LFont {
        val lFont = fontCache[assetsName]
        if (lFont != null) {
            return lFont
        }
        val newFont = LFont.valueOf(assetsName)
        fontCache[assetsName] = newFont
        return newFont
    }

    private fun load(context: Context, font: String): Typeface? {
        val typeface = typefaceMap[font]
        if (typeface != null) {
            return typeface
        }
        try {
            val newTypeface = Typeface.createFromAsset(context.assets, font)
            typefaceMap[font] = newTypeface
            return newTypeface
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return null
    }

    fun findListByAssets(context: Context): List<LFont> {
        if (fontList.isNotEmpty()) {
            return ArrayList(fontList)
        }
        val assets = context.assets
        val list = assets.list("fonts") ?: return emptyList()
        val lFonts = list.map { LFont(displayName = it, assetsName = "fonts/$it") }
        fontList.clear()
        fontList.addAll(lFonts)
        lFonts.forEach {
            fontCache[it.assetsName] = it
        }
        return lFonts
    }

}
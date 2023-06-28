package com.lollipop.ldream.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.provider.Settings
import com.lollipop.ldream.R
import com.lollipop.ldream.activity.AboutActivity
import com.lollipop.lpreference.PreferenceHelper
import com.lollipop.lpreference.info.ActionPreferenceInfo
import com.lollipop.lpreference.util.changeAlpha
import com.lollipop.lpreference.value.ColorArray
import com.lollipop.lpreference.value.ImageArray
import com.lollipop.lpreference.view.ItemBuilder

/**
 * @author lollipop
 * @date 2020-02-16 18:50
 * 屏保的偏好设置
 */
object LDreamPreference {

    const val KEY_KEYWORD = "KEY_KEY_WORD"
    const val KEY_PRIMARY_COLOR = "KEY_PRIMARY_COLOR"
    const val KEY_SECONDARY_COLOR = "KEY_SECONDARY_COLOR"
    const val KEY_BACKGROUND = "KEY_BACKGROUND"

    const val KEY_FLASH_COLOR = "KEY_FLASH_COLOR"
    const val KEY_FLASH_ENABLE = "KEY_FLASH_ENABLE"
    const val KEY_FLASH_FEATURES = "KEY_FLASH_FEATURES"

    const val KEY_TINT_ENABLE = "KEY_TINT_ENABLE"
    const val KEY_TINT_COLOR = "KEY_TINT_COLOR"

    const val KEY_TIME_FONT = "KEY_TIME_FONT"
    const val KEY_POWER_FONT = "KEY_POWER_FONT"

    const val KEY_AGREE_PRIVACY_AGREEMENT = "KEY_AGREE_PRIVACY_AGREEMENT"

    const val DEF_KEYWORD = 1

    const val DEF_TIME_FONT = "fonts/RobotoMono-ThinItalic.ttf"

    const val DEF_POWER_FONT = "fonts/time_font.otf"

    val DEF_PRIMARY_COLOR = Color.RED.changeAlpha(0.8F)

    val DEF_SECONDARY_COLOR = Color.WHITE.changeAlpha(0.8F)

    const val DEF_FLASH_ENABLER = true

    const val DEF_TINT_ENABLE = true

    val DEF_TINT_COLOR = DEF_SECONDARY_COLOR

    private const val ACTION_NOTIFICATION_LISTENER_SETTINGS =
        "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"

    private const val ACTION_TEST_FLASH = "ACTION_TEST_FLASH"

    private val testFlashFilter: IntentFilter by lazy {

        IntentFilter().apply {
            addAction(ACTION_TEST_FLASH)
        }
    }

    val DEF_FLASH_COLOR = ColorArray().apply {
        addAll(
            Color.YELLOW,
            Color.RED,
            Color.GREEN
        )
    }

    fun registerFlashTest(context: Context, run: () -> Unit): BroadcastReceiver {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                run.invoke()
            }
        }
        context.registerReceiver(receiver, testFlashFilter)
        return receiver
    }

    fun getPreferenceList(context: Context): ItemBuilder.() -> Unit {
        return {
            add(
                group(context.getString(R.string.group_display)) {
                    add(
                        number(KEY_KEYWORD, DEF_KEYWORD) {
                            title = context.getString(R.string.title_keyword)
                            summary = context.getString(R.string.summary_keyword)
                            iconId = R.drawable.ic_text_fields_black_24dp
                        },
                        fonts(KEY_TIME_FONT, DEF_TIME_FONT) {
                            title = context.getString(R.string.title_time_font)
                            summary = context.getString(R.string.summary_time_font)
                            iconId = R.drawable.ic_font_download_24
                        },
                        fonts(KEY_POWER_FONT, DEF_POWER_FONT) {
                            title = context.getString(R.string.title_power_font)
                            summary = context.getString(R.string.summary_power_font)
                            iconId = R.drawable.ic_font_download_24
                        },
                        colors(KEY_PRIMARY_COLOR) {
                            title = context.getString(R.string.title_primary_color)
                            summary = context.getString(R.string.summary_primary_color)
                            maxSize = 1
                            iconId = R.drawable.ic_color_lens_black_24dp
                            defaultColors = intArrayOf(DEF_PRIMARY_COLOR)
                        },
                        colors(KEY_SECONDARY_COLOR) {
                            title = context.getString(R.string.title_secondary_color)
                            summary = context.getString(R.string.summary_secondary_color)
                            iconId = R.drawable.ic_color_lens_black_24dp
                            maxSize = 1
                            defaultColors = intArrayOf(DEF_SECONDARY_COLOR)
                        },
                        images(KEY_BACKGROUND) {
                            title = context.getString(R.string.title_background)
                            summary = context.getString(R.string.summary_background)
                            maxSize = 1
                            iconId = R.drawable.ic_image_black_24dp
                        }
                    )
                },
                group(context.getString(R.string.group_flash)) {
                    add(
                        switch(KEY_FLASH_ENABLE, DEF_FLASH_ENABLER) {
                            title = context.getString(R.string.title_flash_enable)
                            summaryTrue = context.getString(R.string.summary_flash_enable_true)
                            summaryFalse = context.getString(R.string.summary_flash_enable_false)
                            iconId = R.drawable.ic_flash_on_black_24dp
                        },
                        colors(KEY_FLASH_COLOR) {
                            relevantKey = KEY_FLASH_ENABLE
                            relevantEnable = true
                            title = context.getString(R.string.title_flash_color)
                            summary = context.getString(R.string.summary_flash_color)
                            defaultColors = IntArray(DEF_FLASH_COLOR.size) { DEF_FLASH_COLOR[it] }
                            iconId = R.drawable.ic_color_lens_black_24dp
                        },
                        action(Intent(ACTION_TEST_FLASH)) {
                            relevantKey = KEY_FLASH_ENABLE
                            relevantEnable = true
                            title = context.getString(R.string.title_test_flash)
                            summary = context.getString(R.string.summary_test_flash)
                            iconId = R.drawable.ic_notifications_none_black_24dp
                            actionType = ActionPreferenceInfo.ActionType.Broadcast
                        }
                    )
                },
                group(context.getString(R.string.group_notification)) {
                    add(
                        action(Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS)) {
                            title = context.getString(R.string.title_notification_listener)
                            summary = context.getString(R.string.summary_notification_listener)
                            iconId = R.drawable.ic_notifications_none_black_24dp
                        },
                        switch(KEY_TINT_ENABLE, DEF_TINT_ENABLE) {
                            title = context.getString(R.string.title_tint_enable)
                            summaryTrue = context.getString(R.string.summary_tint_enable_true)
                            summaryFalse = context.getString(R.string.summary_tint_enable_false)
                            iconId = R.drawable.ic_format_color_fill_black_24dp
                        },
                        colors(KEY_TINT_COLOR) {
                            relevantKey = KEY_TINT_ENABLE
                            relevantEnable = true
                            title = context.getString(R.string.title_tint_color)
                            summary = context.getString(R.string.summary_tint_color)
                            defaultColors = intArrayOf(DEF_TINT_COLOR)
                            maxSize = 1
                            iconId = R.drawable.ic_color_lens_black_24dp
                        }
                    )
                },
                action(Intent(Settings.ACTION_DREAM_SETTINGS)) {
                    title = context.getString(R.string.title_dream_settings)
                    summary = context.getString(R.string.summary_dream_settings)
                    iconId = R.drawable.ic_settings_system_daydream_black_24dp
                },
                action(Intent(context, AboutActivity::class.java)) {
                    title = context.getString(R.string.title_about)
                    summary = context.getString(R.string.summary_about)
                    iconId = R.drawable.ic_info_outline_black_24dp
                }
            )
        }
    }

}

fun Context.timerPrimaryColor(): Int {
    val colors = PreferenceHelper.getColor(this, LDreamPreference.KEY_PRIMARY_COLOR, ColorArray())
    if (colors.size < 1) {
        return LDreamPreference.DEF_PRIMARY_COLOR
    }
    return colors[0]
}

fun Context.timerSecondaryColor(): Int {
    val colors = PreferenceHelper.getColor(this, LDreamPreference.KEY_SECONDARY_COLOR, ColorArray())
    if (colors.size < 1) {
        return LDreamPreference.DEF_SECONDARY_COLOR
    }
    return colors[0]
}

fun Context.timerKeyWord(): String {
    return PreferenceHelper.get(
        this, LDreamPreference.KEY_KEYWORD,
        LDreamPreference.DEF_KEYWORD
    ).toString()
}

fun Context.timeFont(): String {
    return PreferenceHelper.get(
        this,
        LDreamPreference.KEY_TIME_FONT,
        LDreamPreference.DEF_TIME_FONT
    ).toString()
}

fun Context.powerFont(): String {
    return PreferenceHelper.get(
        this,
        LDreamPreference.KEY_POWER_FONT,
        LDreamPreference.DEF_POWER_FONT
    ).toString()
}

fun Context.timerFlashColor(value: ColorArray? = null): ColorArray {
    return PreferenceHelper.getColor(
        this, LDreamPreference.KEY_FLASH_COLOR,
        value ?: LDreamPreference.DEF_FLASH_COLOR.newInstance()
    )
}

var Context.isAgreePrivacyAgreement: Boolean
    get() {
        return PreferenceHelper.get(
            this,
            LDreamPreference.KEY_AGREE_PRIVACY_AGREEMENT,
            false
        )
    }
    set(value) {
        PreferenceHelper.put(
            this,
            LDreamPreference.KEY_AGREE_PRIVACY_AGREEMENT,
            value
        )
    }

fun Context.timerFlashEnable(): Boolean {
    return PreferenceHelper.get(
        this, LDreamPreference.KEY_FLASH_ENABLE,
        LDreamPreference.DEF_FLASH_ENABLER
    )
}

fun Context.timerTintEnable(): Boolean {
    return PreferenceHelper.get(
        this, LDreamPreference.KEY_TINT_ENABLE,
        LDreamPreference.DEF_TINT_ENABLE
    )
}

fun Context.timerTintColor(): Int {
    val colors = PreferenceHelper.getColor(this, LDreamPreference.KEY_TINT_COLOR, ColorArray())
    if (colors.size < 1) {
        return LDreamPreference.DEF_TINT_COLOR
    }
    return colors[0]
}

fun Context.timerBackgroundUri(): Uri? {
    val images = PreferenceHelper.getImage(this, LDreamPreference.KEY_BACKGROUND, ImageArray())
    if (images.size < 1) {
        return null
    }
    return images[0]
}

fun Context.isFlashFeatures(): Boolean {
    return PreferenceHelper.get(this, LDreamPreference.KEY_FLASH_FEATURES, false)
}

fun Context.openFlashFeatures() {
    PreferenceHelper.put(this, LDreamPreference.KEY_FLASH_FEATURES, true)
}

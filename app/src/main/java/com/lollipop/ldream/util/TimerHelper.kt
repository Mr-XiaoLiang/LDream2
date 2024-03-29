package com.lollipop.ldream.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.BatteryManager
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.IconCompat
import com.bumptech.glide.Glide
import com.google.android.flexbox.FlexboxLayout
import com.lollipop.fonts.FontsHelper
import com.lollipop.fonts.LFont
import com.lollipop.ldream.R
import com.lollipop.ldream.service.NotificationService
import java.util.*
import kotlin.collections.ArrayList

/**
 * @author lollipop
 * @date 2020-01-12 19:02
 * 时间块的辅助器
 */
class TimerHelper(
    private val context: Context,
    private val timeView: () -> TextView?,
    private val notificationGroup: () -> FlexboxLayout?,
    private val powerView: () -> TextView?,
    private val backgroundView: () -> ImageView?
) : BroadcastReceiver() {

    companion object {
        private const val UPDATE_DELAYED = 400L
        private const val ACTION_NOTIFICATION_POSTED =
            NotificationService.ACTION_NOTIFICATION_POSTED
        private const val ACTION_NOTIFICATION_REMOVED =
            NotificationService.ACTION_NOTIFICATION_REMOVED
    }

    var specialKeyword = "1"
    var specialKeywordColor = Color.RED
    var backgroundUri: Uri? = null
    var secondaryTextColor = Color.WHITE
    var isTintIcon = false
    var iconTintColor = Color.WHITE

    private var lastBattery = 0

    private var isRunning = false

    val notificationSize: Int
        get() {
            return notificationList.size
        }

    private val inflater: LayoutInflater by lazy {
        LayoutInflater.from(context)
    }
    private val handler = Handler(Looper.getMainLooper())
    private val tempBuilder = StringBuilder()
    private val calender = Calendar.getInstance()
    private val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    private val intentFilter: IntentFilter by lazy {
        IntentFilter().apply {
            addAction(ACTION_NOTIFICATION_POSTED)
            addAction(ACTION_NOTIFICATION_REMOVED)
        }
    }
    private val notificationList = ArrayList<NotificationService.Info>()
    private val notificationViewList = ArrayList<IconHolder>()
    private val recycleViewList = LinkedList<IconHolder>()

    private var onBatteryChangeListener: (() -> Unit)? = null

    private var onNotificationChangeListener: ((Int) -> Unit)? = null

    init {
        specialKeyword = context.timerKeyWord()
        specialKeywordColor = context.timerPrimaryColor()
        backgroundUri = context.timerBackgroundUri()
        secondaryTextColor = context.timerSecondaryColor()
        isTintIcon = context.timerTintEnable()
        iconTintColor = context.timerTintColor()

        notifyTimeFontChanged()
        notifyPowerFontChanged()

    }

    private val updateTask = Runnable {
        notifyUpdateText()
    }

    private fun TextView.setTypefaceForName(name: String) {
        typeface = Typeface.createFromAsset(context.assets, name)
    }

    fun notifyTimeFontChanged() {
        timeView()?.let {
            it.post {
                FontsHelper.valueOf(context.timeFont()).bind(it)
            }
        }
    }

    fun notifyPowerFontChanged() {
        powerView()?.let {
            it.post {
                FontsHelper.valueOf(context.powerFont()).bind(it)
            }
        }
    }

    fun onBatteryChange(lis: () -> Unit) {
        onBatteryChangeListener = lis
    }

    fun onNotificationChange(lis: (Int) -> Unit) {
        onNotificationChangeListener = lis
    }

    private fun TextView.setValue(str: String) {
        setTextColor(secondaryTextColor)
        if (str.contains(specialKeyword)) {
            val spannableString = SpannableString(str)
            var index: Int = str.indexOf(specialKeyword)
            while (index >= 0) {
                spannableString.setSpan(
                    ForegroundColorSpan(specialKeywordColor),
                    index,
                    index + 1,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                index = str.indexOf(specialKeyword, index + 1)
            }
            text = spannableString
        } else {
            text = str
        }
    }

    private fun Int.format(size: Int = 2): String {
        tempBuilder.clear()
        tempBuilder.append(this)
        while (tempBuilder.length < size) {
            tempBuilder.insert(0, "0")
        }
        return tempBuilder.toString()
    }

    private fun updateTimer() {
        calender.timeInMillis = System.currentTimeMillis()
        val hour = calender.get(Calendar.HOUR_OF_DAY).format()
        val minute = calender.get(Calendar.MINUTE).format()
        timeView()?.setValue("$hour:$minute")
        if (isRunning) {
            handler.removeCallbacks(updateTask)
            handler.postDelayed(updateTask, UPDATE_DELAYED)
        }
    }

    private fun updateBattery() {
        val batteryLevel = getBatteryLevel()
        if (batteryLevel != lastBattery) {
            onBatteryChangeListener?.invoke()
            lastBattery = batteryLevel
        }
        val power = if (batteryLevel < 0) {
            ""
        } else {
            "${batteryLevel.format()}%"
        }
        powerView()?.setValue(power)
    }

    private fun updateBackground() {
        backgroundView()?.let {
            if (backgroundUri != null) {
                Glide.with(it).load(backgroundUri).into(it)
            } else {
                it.setImageDrawable(null)
            }
        }
    }

    private fun updateNotificationIcon() {
        for (i in notificationViewList.indices) {
            val holder = notificationViewList[i]
            holder.tintColor = iconTintColor
            holder.tintEnable = isTintIcon
            holder.onBind(notificationList[i])
        }
    }

    fun notifyUpdateText() {
        updateTimer()
        updateBattery()
    }

    fun notifyUpdateBackground() {
        updateBackground()
    }

    fun notifyUpdateIcon() {
        updateNotificationIcon()
    }

    private fun notifyUpdateAll() {
        notifyUpdateText()
        notifyUpdateBackground()
        notifyUpdateIcon()
    }

    fun onStart() {
        if (isRunning) {
            return
        }
        isRunning = true
        lastBattery = getBatteryLevel()
        notifyUpdateAll()
        initNotifications()
        context.registerReceiver(this, intentFilter)
    }

    fun onStop() {
        if (!isRunning) {
            return
        }
        isRunning = false
        context.unregisterReceiver(this)
        handler.removeCallbacks(updateTask)
    }

    private fun initNotifications() {
        val infoList = NotificationService.getAllNotification()
        notificationList.clear()
        notificationList.addAll(infoList)
        if (notificationList.isEmpty()) {
            for (holder in notificationViewList) {
                val view = holder.view
                val parent = view.parent
                if (parent != null && parent is ViewGroup) {
                    parent.removeView(view)
                }
            }
            recycleViewList.addAll(notificationViewList)
            notificationViewList.clear()
        }
        while (notificationViewList.size < notificationList.size) {
            val holder = getIconHolder()
            notificationGroup()?.addView(holder.view)
            notificationViewList.add(holder)
        }
        while (notificationViewList.size > notificationList.size) {
            recycleViewList.add(notificationViewList.removeAt(0))
        }
        updateNotificationIcon()
    }

    private fun onNotificationPosted(pkg: String, icon: IconCompat) {
        if (TextUtils.isEmpty(pkg)) {
            return
        }
        for (info in notificationList) {
            if (pkg == info.pkg) {
                return
            }
        }
        val newInfo = NotificationService.Info(pkg, icon)
        notificationList.add(newInfo)
        val holder = getIconHolder()
        notificationGroup()?.addView(holder.view)
        notificationViewList.add(holder)
        holder.onBind(newInfo)
    }

    private fun onNotificationRemoved(pkg: String) {
        if (TextUtils.isEmpty(pkg)) {
            return
        }
        for (info in notificationList) {
            if (pkg == info.pkg) {
                notificationList.remove(info)
                break
            }
        }
        for (holder in notificationViewList) {
            if (pkg == holder.pkg) {
                notificationViewList.remove(holder)
                recycleViewList.add(holder)
                notificationGroup()?.removeView(holder.view)
                break
            }
        }
    }

    private fun getIconHolder(): IconHolder {
        return if (recycleViewList.isNotEmpty()) {
            recycleViewList.removeFirst()
        } else {
            IconHolder.getInstance(inflater, notificationGroup())
        }
    }

    fun onDestroy() {
        notificationViewList.clear()
        recycleViewList.clear()
        notificationList.clear()
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: return
        when (intent?.action) {
            ACTION_NOTIFICATION_POSTED -> {
                val icon = intent.getNotificationIcon() ?: return
                val pkg = intent.getNotificationPkg() ?: return
                onNotificationPosted(pkg, icon)
                onNotificationChangeListener?.invoke(notificationList.size)
            }
            ACTION_NOTIFICATION_REMOVED -> {
                val pkg = intent.getNotificationPkg() ?: return
                onNotificationRemoved(pkg)
                onNotificationChangeListener?.invoke(notificationList.size)
            }
        }
    }

    private fun Intent.getNotificationIcon(): IconCompat? {
        return NotificationService.getIcon(this)
    }

    private fun Intent.getNotificationPkg(): String? {
        return NotificationService.getPkg(this)
    }

    private class IconHolder private constructor(val view: View) {
        companion object {
            fun getInstance(inflater: LayoutInflater, viewGroup: ViewGroup?): IconHolder {
                return IconHolder(inflater.inflate(R.layout.notification_icon, viewGroup, false))
            }
        }

        private val iconView = view.findViewById<ImageView>(R.id.iconView)

        var pkg: String = ""

        var tintColor = Color.WHITE
        var tintEnable = true

        fun onBind(info: NotificationService.Info) {
            pkg = info.pkg
            iconView.setImageDrawable(info.icon.apply {
                if (tintEnable) {
                    setTint(tintColor)
                } else {
                    setTintList(null)
                }
            }.loadDrawable(view.context))
        }

    }

    private fun getBatteryLevel(): Int {
        return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    }

}
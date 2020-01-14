package com.lollipop.ldream.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.Typeface
import android.os.BatteryManager
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.core.graphics.drawable.IconCompat
import com.google.android.flexbox.FlexboxLayout
import com.lollipop.ldream.NotificationService
import java.util.*
import kotlin.collections.ArrayList

/**
 * @author lollipop
 * @date 2020-01-12 19:02
 * 时间块的辅助器
 */
class TimerHelper(private val timeView: TextView,
                  private val notificationGroup: FlexboxLayout,
                  private val powerView: TextView): BroadcastReceiver() {

    companion object {
        private const val UPDATE_DELAYED = 400L
        private const val ACTION_BATTERY_CHANGED = Intent.ACTION_BATTERY_CHANGED
        private const val ACTION_NOTIFICATION_POSTED = NotificationService.ACTION_NOTIFICATION_POSTED
        private const val ACTION_NOTIFICATION_REMOVED = NotificationService.ACTION_NOTIFICATION_REMOVED
    }

    var specialKeyword = "1"
    var specialKeywordColor = Color.RED
    var isRunning = false

    private val notificationList = ArrayList<NotificationService.Info>()
    private val context: Context
        get() = timeView.context
    private val handler = Handler(Looper.getMainLooper())
    private val tempBuilder = StringBuilder()
    private val calender = Calendar.getInstance()
    private val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    private val intentFilter: IntentFilter by lazy {
        IntentFilter().apply {
            addAction(ACTION_BATTERY_CHANGED)
            addAction(ACTION_NOTIFICATION_POSTED)
            addAction(ACTION_NOTIFICATION_REMOVED)
        }
    }
    private val packageNames = ArrayList<String>()

    init {
        timeView.setTypefaceForName("fonts/Roboto-ThinItalic.ttf")
        powerView.setTypefaceForName("fonts/time_font.otf")
    }

    private val updateTask = Runnable {
        updateTimer()
    }

    private fun TextView.setTypefaceForName(name: String) {
        typeface = Typeface.createFromAsset(context.assets, name)
    }

    private fun TextView.setValue(str: String) {
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
        timeView.setValue("$hour:$minute")
        if (isRunning) {
            handler.postDelayed(updateTask, UPDATE_DELAYED)
        }
    }

    private fun updateBattery() {
        val value = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        val power = if (value < 0) {
            ""
        } else {
            "${value.format()}%"
        }
        powerView.setValue(power)
    }

    fun onStart() {
        if (isRunning) {
            return
        }
        isRunning = true
        updateTimer()
        updateBattery()
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
        val infos = NotificationService.getAllNotification()
        notificationList.clear()
        notificationList.addAll(infos)

    }

    fun onDestroy() {

    }

    override fun onReceive(context: Context?, intent: Intent?) {
        context?:return
        when (intent?.action) {
            ACTION_BATTERY_CHANGED -> {
                updateBattery()
            }
            ACTION_NOTIFICATION_POSTED -> {
                val icon = intent.getNotificationIcon()?:return
                val pkg = intent.getNotificationPkg()?:return
            }
            ACTION_NOTIFICATION_REMOVED -> {
                val pkg = intent.getNotificationPkg()?:return
            }
        }
    }

    private fun Intent.getNotificationIcon(): IconCompat? {
        return NotificationService.getIcon(this)
    }

    private fun Intent.getNotificationPkg(): String? {
        return NotificationService.getPkg(this)
    }

}
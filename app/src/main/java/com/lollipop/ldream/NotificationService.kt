package com.lollipop.ldream

import android.app.Notification
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.text.TextUtils
import androidx.core.graphics.drawable.IconCompat

/**
 * 消息通知的服务
 * @author Lollipop
 */
class NotificationService : NotificationListenerService() {

    companion object {
        /**
         * 广播，用于监听通知内容
         * 消息增加的action
         */
        const val ACTION_NOTIFICATION_POSTED = "ACTION_LOLLIPOP_NOTIFICATION_POSTED"
        /**
         * 广播，用于监听通知内容
         * 消息删除的action
         */
        const val ACTION_NOTIFICATION_REMOVED = "ACTION_LOLLIPOP_NOTIFICATION_REMOVED"

        /**
         * 应用图标
         */
        private const val ARG_ICON = "ARG_ICON"
        /**
         * 应用包名
         */
        private const val ARG_PKG = "ARG_PKG"

        fun getIcon(intent: Intent): IconCompat? {
            val bundle = intent.getBundleExtra(ARG_ICON)?:return null
            return IconCompat.createFromBundle(bundle)
        }

        fun getPkg(intent: Intent): String? {
            return intent.getStringExtra(ARG_PKG)
        }

        /**
         * 全局的消息缓存中心
         */
        private val notificationList = ArrayList<NotificationInfo>()

        /**
         * 获取当前已知的所有消息
         */
        fun getAllNotification(): ArrayList<Info> {
            synchronized(this) {
                val resultList = ArrayList<Info>()
                for (info in notificationList) {
                    val icon = IconCompat.createFromBundle(info.icon)?:continue
                    resultList.add(Info(info.pkg, icon))
                }
                return resultList
            }
        }

        private fun putNotification(pkg: String, icon: IconCompat) {
            // 有相同就不加了
            synchronized(this) {
                for (info in notificationList) {
                    if (info.pkg == pkg) {
                        return
                    }
                }
                notificationList.add(NotificationInfo(pkg, icon.toBundle()))
            }
        }

        private fun removeNotification(pkg: String) {
            // 有相同就移除
            synchronized(this) {
                val iterator = notificationList.iterator()
                while (iterator.hasNext()) {
                    val info = iterator.next()
                    if (info.pkg == pkg) {
                        iterator.remove()
                    }
                }
            }
        }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        val notification = sbn?.notification ?: return
        if ((notification.flags and Notification.FLAG_ONGOING_EVENT) == Notification.FLAG_ONGOING_EVENT) {
            return
        }
        val pkg = sbn.packageName
        if (TextUtils.isEmpty(pkg)) {
            return
        }
        val notifyInfo = notification.extras ?: return
        val icon = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            notification.smallIcon != null) {
            IconCompat.createFromIcon(notification.smallIcon)
        } else {
            IconCompat.createWithResource(
                createPackageContext(pkg, CONTEXT_IGNORE_SECURITY),
                notifyInfo.getInt(Notification.EXTRA_SMALL_ICON))
        }?:return
        putNotification(pkg, icon)
        sendBroadcast(Intent(ACTION_NOTIFICATION_POSTED).apply {
            putExtra(ARG_PKG, pkg)
            putExtra(ARG_ICON, icon.toBundle())
        })
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        val pkg = sbn?.packageName ?: return
        removeNotification(pkg)
        sendBroadcast(Intent(ACTION_NOTIFICATION_REMOVED).apply {
            putExtra(ARG_PKG, pkg)
        })
    }

    private data class NotificationInfo(val pkg: String, val icon: Bundle)

    data class Info(val pkg: String, val icon: IconCompat)

}

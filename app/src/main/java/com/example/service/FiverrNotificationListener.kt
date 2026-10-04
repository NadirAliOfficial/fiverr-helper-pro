package com.example.service

import android.app.Notification
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.example.data.AppDatabase
import com.example.domain.FiverrSyncService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class FiverrNotificationListener : NotificationListenerService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        private const val TAG = "FiverrNotifListener"
        private val _autoCapturedOrders = MutableSharedFlow<String>(extraBufferCapacity = 10)
        val autoCapturedOrders = _autoCapturedOrders.asSharedFlow()

        var isServiceRunning = false
            private set
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        isServiceRunning = true
        Log.d(TAG, "Fiverr Real-Time Notification Listener Connected & Listening")
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        isServiceRunning = false
        Log.d(TAG, "Fiverr Real-Time Notification Listener Disconnected")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        if (sbn == null) return

        val packageName = sbn.packageName?.lowercase() ?: ""
        // Listen to Fiverr official app or test broadcaster
        val isFiverr = packageName.contains("fiverr") || packageName.contains("example")

        val extras = sbn.notification?.extras ?: return
        val title = extras.getString(Notification.EXTRA_TITLE) ?: ""
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""
        val bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString() ?: ""

        val combinedContent = "$title\n$text\n$bigText"

        // Only process if it mentions an order, purchase, revision, or buyer
        val isOrderRelated = combinedContent.contains("order", ignoreCase = true) ||
                combinedContent.contains("purchased", ignoreCase = true) ||
                combinedContent.contains("tip", ignoreCase = true) ||
                combinedContent.contains("revision", ignoreCase = true) ||
                combinedContent.contains("$")

        if (isFiverr && isOrderRelated) {
            Log.d(TAG, "Fiverr Order Notification Captured: $title -> $text")
            val parsedOrder = FiverrSyncService.parseFiverrOrderText(combinedContent)
            if (parsedOrder != null) {
                serviceScope.launch {
                    try {
                        val db = AppDatabase.getDatabase(applicationContext)
                        db.orderDao().insertOrder(parsedOrder)
                        _autoCapturedOrders.emit("Captured order from @${parsedOrder.buyerUsername} ($${parsedOrder.orderAmount.toInt()})")
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to save auto-captured order", e)
                    }
                }
            }
        }
    }
}

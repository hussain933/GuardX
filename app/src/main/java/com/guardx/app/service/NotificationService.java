package com.guardx.app.service;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

public class NotificationService extends NotificationListenerService {

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        // Monitor notifications if needed
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        // Monitor notification removal
    }
}

package com.guardx.app.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.guardx.app.R;
import com.guardx.app.service.GuardXService;
import com.guardx.app.ui.DashboardActivity;

public class NotifHelper {

    private Context context;
    private NotificationManager manager;

    public NotifHelper(Context context) {
        this.context = context;
        this.manager = (NotificationManager)
            context.getSystemService(Context.NOTIFICATION_SERVICE);
        createChannel();
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                Constants.NOTIF_CHANNEL_ID,
                Constants.NOTIF_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("GuardX Protection");
            channel.enableVibration(true);
            channel.setShowBadge(true);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }

    // Main sticky notification
    public Notification buildMainNotif(String content) {
        Intent dashIntent = new Intent(context, DashboardActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 0, dashIntent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        return new NotificationCompat.Builder(context, Constants.NOTIF_CHANNEL_ID)
            .setContentTitle("🛡️ GuardX V2.5 ⚡ Active")
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_shield)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build();
    }

    // Warning notification
    public void showWarningNotif(int threatCount) {
        Intent fixIntent = new Intent(context, GuardXService.class);
        fixIntent.setAction(Constants.ACTION_FIX_ALL);
        PendingIntent fixPending = PendingIntent.getService(
            context, 1, fixIntent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Intent exitIntent = new Intent(context, GuardXService.class);
        exitIntent.setAction(Constants.ACTION_CANCEL);
        PendingIntent exitPending = PendingIntent.getService(
            context, 2, exitIntent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Notification notif = new NotificationCompat.Builder(
            context, Constants.NOTIF_CHANNEL_ID)
            .setContentTitle("🚨 GuardX V2.5 — ALERT!")
            .setContentText(threatCount + " Threats Detected! Game FROZEN")
            .setSmallIcon(R.drawable.ic_shield)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setOngoing(true)
            .addAction(R.drawable.ic_shield, "🔧 FIX & RESUME", fixPending)
            .addAction(R.drawable.ic_shield, "❌ EXIT", exitPending)
            .build();

        if (manager != null) {
            manager.notify(Constants.NOTIF_ID_WARNING, notif);
        }
    }

    // All clear notification
    public void showClearNotif() {
        Notification notif = new NotificationCompat.Builder(
            context, Constants.NOTIF_CHANNEL_ID)
            .setContentTitle("🛡️ GuardX V2.5 ⚡")
            .setContentText("✅ All Clear! Safe to Play!")
            .setSmallIcon(R.drawable.ic_shield)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build();

        if (manager != null) {
            manager.notify(Constants.NOTIF_ID_CLEAR, notif);
        }
    }

    public void cancelWarning() {
        if (manager != null) {
            manager.cancel(Constants.NOTIF_ID_WARNING);
        }
    }
}

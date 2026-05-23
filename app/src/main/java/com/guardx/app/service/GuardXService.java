package com.guardx.app.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import androidx.core.app.NotificationCompat;
import com.guardx.app.R;
import com.guardx.app.scanner.AppScanner;
import com.guardx.app.scanner.FileScanner;
import com.guardx.app.scanner.EnvScanner;
import com.guardx.app.scanner.ProcessScanner;
import com.guardx.app.scanner.AIFilter;
import com.guardx.app.database.HistoryDAO;
import com.guardx.app.ui.WarningPopupActivity;
import com.guardx.app.utils.Constants;
import com.guardx.app.utils.PrefsManager;
import java.util.ArrayList;
import java.util.List;

public class GuardXService extends Service {

    private Handler handler;
    private Runnable scanRunnable;
    private AppScanner appScanner;
    private FileScanner fileScanner;
    private EnvScanner envScanner;
    private ProcessScanner processScanner;
    private AIFilter aiFilter;
    private HistoryDAO historyDAO;
    private PrefsManager prefs;
    private boolean isScanning = false;

    @Override
    public void onCreate() {
        super.onCreate();
        initComponents();
        createNotificationChannel();
        startForeground(Constants.NOTIF_ID_MAIN, buildNotification("Protection Active", "GuardX is protecting your games"));
    }

    private void initComponents() {
        appScanner = new AppScanner(this);
        fileScanner = new FileScanner();
        envScanner = new EnvScanner(this);
        processScanner = new ProcessScanner();
        aiFilter = new AIFilter(this);
        historyDAO = new HistoryDAO(this);
        prefs = new PrefsManager(this);
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && Constants.ACTION_FIX_ALL.equals(intent.getAction())) {
            resumeGame();
        }
        startScanning();
        return START_STICKY;
    }

    private void startScanning() {
        scanRunnable = new Runnable() {
            @Override
            public void run() {
                if (prefs.isProtectionOn()) {
                    performScan();
                }
                handler.postDelayed(this, Constants.SCAN_GAME_ACTIVE);
            }
        };
        handler.post(scanRunnable);
    }

    private void performScan() {
        if (isScanning) return;
        isScanning = true;

        new Thread(() -> {
            List<String> allThreats = new ArrayList<>();

            // Layer 1: App Scan
            allThreats.addAll(appScanner.scan());

            // Layer 2: File Scan
            allThreats.addAll(fileScanner.scan());

            // Layer 3: Environment Scan
            allThreats.addAll(envScanner.scan());

            // Layer 4: Process Scan
            allThreats.addAll(processScanner.scan());

            // Layer 5: AI Filter
            allThreats.addAll(aiFilter.scan());

            // Check if game running
            boolean gameRunning = appScanner.isGameRunning(Constants.GAMES);

            if (!allThreats.isEmpty() && gameRunning) {
                // Save to history
                String threats = String.join(", ", allThreats);
                historyDAO.addHistory(threats, "BLOCKED", getRiskLevel(allThreats));

                // Update notification
                updateNotification("⚠️ " + allThreats.size() + " Threats Detected!", true);

                // Show warning popup
                showWarningPopup(allThreats);

            } else {
                updateNotification("✅ All Clear — Safe to Play!", false);
                prefs.setLastScan(System.currentTimeMillis());
            }

            isScanning = false;
        }).start();
    }

    private void showWarningPopup(List<String> threats) {
        Intent intent = new Intent(this, WarningPopupActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putStringArrayListExtra("threats", new ArrayList<>(threats));
        startActivity(intent);
    }

    private void resumeGame() {
        updateNotification("✅ Fixed! Game Resuming...", false);
    }

    private String getRiskLevel(List<String> threats) {
        if (threats.size() >= 4) return Constants.RISK_CRITICAL;
        if (threats.size() >= 2) return Constants.RISK_HIGH;
        return Constants.RISK_MEDIUM;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                Constants.NOTIF_CHANNEL_ID,
                Constants.NOTIF_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("GuardX Protection Service");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }

    private Notification buildNotification(String title, String content) {
        return new NotificationCompat.Builder(this, Constants.NOTIF_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_shield)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .build();
    }

    private void updateNotification(String content, boolean warning) {
        NotificationManager manager = (NotificationManager)
            getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(Constants.NOTIF_ID_MAIN,
                buildNotification("🛡️ GuardX V2.5", content));
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null && scanRunnable != null) {
            handler.removeCallbacks(scanRunnable);
        }
        // Restart service if killed
        Intent restartIntent = new Intent(this, GuardXBootReceiver.class);
        sendBroadcast(restartIntent);
    }
}

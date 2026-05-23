package com.guardx.app.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
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
    private List<String> lastThreats = new ArrayList<>();
    private boolean warningShown = false;

    @Override
    public void onCreate() {
        super.onCreate();
        initComponents();
        createNotificationChannel();
        // Sirf ek sticky notification — update nahi hogi bar bar
        startForeground(Constants.NOTIF_ID_MAIN, buildMainNotif());
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
            warningShown = false;
            lastThreats.clear();
        }
        startScanning();
        return START_STICKY;
    }

    private void startScanning() {
        if (scanRunnable != null) return;
        scanRunnable = new Runnable() {
            @Override
            public void run() {
                if (prefs.isProtectionOn()) {
                    performScan();
                }
                // Scan har 10 second me — notification nahi aayegi bar bar
                handler.postDelayed(this, 10000);
            }
        };
        handler.post(scanRunnable);
    }

    private void performScan() {
        if (isScanning) return;
        isScanning = true;

        new Thread(() -> {
            List<String> allThreats = new ArrayList<>();

            // 5 Layer Scan — silently
            allThreats.addAll(appScanner.scan());
            allThreats.addAll(fileScanner.scan());
            allThreats.addAll(envScanner.scan());
            allThreats.addAll(processScanner.scan());
            allThreats.addAll(aiFilter.scan());

            boolean gameRunning = appScanner.isGameRunning(Constants.GAMES);

            if (!allThreats.isEmpty() && gameRunning) {
                // Sirf tab notification aaye jab NEW threat mile
                boolean newThreat = !allThreats.equals(lastThreats);

                if (newThreat && !warningShown) {
                    lastThreats = new ArrayList<>(allThreats);
                    warningShown = true;

                    // History save karo
                    String threats = String.join(", ", allThreats);
                    historyDAO.addHistory(threats, "BLOCKED", getRiskLevel(allThreats));

                    // Sirf ek baar warning dikhao
                    showWarningPopup(allThreats);
                    showWarningNotif(allThreats.size());
                }
            } else {
                // Threats clear ho gayi — reset karo
                if (warningShown) {
                    warningShown = false;
                    lastThreats.clear();
                    cancelWarningNotif();
                }
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

    private void showWarningNotif(int count) {
        NotificationManager manager = (NotificationManager)
            getSystemService(NOTIFICATION_SERVICE);
        if (manager == null) return;

        Notification notif = new NotificationCompat.Builder(this, Constants.NOTIF_CHANNEL_ID)
            .setContentTitle("🚨 GuardX — " + count + " Threats!")
            .setContentText("Tap to fix before getting banned")
            .setSmallIcon(R.drawable.ic_shield)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true) // Sirf ek baar dikhega
            .build();

        manager.notify(Constants.NOTIF_ID_WARNING, notif);
    }

    private void cancelWarningNotif() {
        NotificationManager manager = (NotificationManager)
            getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.cancel(Constants.NOTIF_ID_WARNING);
        }
    }

    private Notification buildMainNotif() {
        return new NotificationCompat.Builder(this, Constants.NOTIF_CHANNEL_ID)
            .setContentTitle("🛡️ GuardX Active")
            .setContentText("Protecting your games silently")
            .setSmallIcon(R.drawable.ic_shield)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setSilent(true) // Koi sound nahi!
            .build();
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
                NotificationManager.IMPORTANCE_LOW // LOW — koi sound nahi
            );
            channel.setDescription("GuardX Silent Protection");
            channel.setSound(null, null); // Sound band
            channel.enableVibration(false); // Vibration band
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null && scanRunnable != null) {
            handler.removeCallbacks(scanRunnable);
        }
    }
}

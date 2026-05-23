package com.guardx.app.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.os.Build;
import android.view.accessibility.AccessibilityEvent;
import com.guardx.app.utils.Constants;
import com.guardx.app.utils.PrefsManager;

public class AccessibilityWatcher extends AccessibilityService {

    private PrefsManager prefs;
    private String lastPackage = "";

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        prefs = new PrefsManager(this);

        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        info.notificationTimeout = 100;
        setServiceInfo(info);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            String pkg = event.getPackageName() != null ?
                event.getPackageName().toString() : "";

            if (!pkg.equals(lastPackage)) {
                lastPackage = pkg;
                checkIfGame(pkg);
            }
        }
    }

    private void checkIfGame(String packageName) {
        if (!prefs.isProtectionOn()) return;

        for (String game : Constants.GAMES) {
            if (game.equals(packageName)) {
                // Game detected — start service
                Intent serviceIntent = new Intent(this, GuardXService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(serviceIntent);
                } else {
                    startService(serviceIntent);
                }
                break;
            }
        }
    }

    @Override
    public void onInterrupt() {}
}

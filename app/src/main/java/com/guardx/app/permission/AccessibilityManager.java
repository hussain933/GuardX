package com.guardx.app.permission;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;
import com.guardx.app.service.AccessibilityWatcher;

public class AccessibilityManager {

    public static boolean isAccessibilityEnabled(Context context) {
        String service = context.getPackageName() + "/" +
            AccessibilityWatcher.class.getCanonicalName();
        try {
            int enabled = Settings.Secure.getInt(
                context.getContentResolver(),
                Settings.Secure.ACCESSIBILITY_ENABLED
            );
            if (enabled != 1) return false;

            String services = Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            );
            if (services != null) {
                TextUtils.SimpleStringSplitter splitter =
                    new TextUtils.SimpleStringSplitter(':');
                splitter.setString(services);
                while (splitter.hasNext()) {
                    if (splitter.next().equalsIgnoreCase(service)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void openAccessibilitySettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}

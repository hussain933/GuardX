package com.guardx.app.permission;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

public class PermissionChecker {

    // Check Usage Stats Permission
    public static boolean hasUsageStats(Context context) {
        try {
            AppOpsManager appOps = (AppOpsManager)
                context.getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context.getPackageName()
            );
            return mode == AppOpsManager.MODE_ALLOWED;
        } catch (Exception e) {
            return false;
        }
    }

    // Check Overlay Permission
    public static boolean hasOverlayPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context);
        }
        return true;
    }

    // Check Accessibility
    public static boolean hasAccessibility(Context context) {
        return AccessibilityManager.isAccessibilityEnabled(context);
    }

    // Check Device Admin
    public static boolean hasDeviceAdmin(Context context) {
        return DeviceOwnerManager.isAdminActive(context);
    }

    // Check Device Owner
    public static boolean hasDeviceOwner(Context context) {
        return DeviceOwnerManager.isDeviceOwner(context);
    }

    // Check All Permissions
    public static boolean hasAllPermissions(Context context) {
        return hasUsageStats(context) &&
               hasOverlayPermission(context) &&
               hasAccessibility(context);
    }

    // Open Usage Stats Settings
    public static void openUsageStats(Context context) {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    // Open Overlay Settings
    public static void openOverlaySettings(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + context.getPackageName())
            );
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}

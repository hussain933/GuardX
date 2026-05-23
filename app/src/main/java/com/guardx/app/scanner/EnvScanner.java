package com.guardx.app.scanner;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EnvScanner {

    private Context context;

    public EnvScanner(Context context) {
        this.context = context;
    }

    public List<String> scan() {
        List<String> threats = new ArrayList<>();

        if (isRooted()) threats.add("Root Access Detected");
        if (isMagisk()) threats.add("Magisk Detected");
        if (isXposed()) threats.add("Xposed Framework Detected");
        if (isEmulator()) threats.add("Emulator Detected");
        if (isADBEnabled()) threats.add("ADB Debug Mode On");
        if (isDeveloperMode()) threats.add("Developer Mode On");

        return threats;
    }

    private boolean isRooted() {
        String[] paths = {
            "/system/app/Superuser.apk",
            "/sbin/su", "/system/bin/su",
            "/system/xbin/su", "/data/local/xbin/su",
            "/data/local/bin/su", "/system/sd/xbin/su"
        };
        for (String path : paths) {
            if (new File(path).exists()) return true;
        }
        return false;
    }

    private boolean isMagisk() {
        String[] paths = {
            "/sbin/.magisk", "/cache/.disable_magisk",
            "/data/adb/magisk", "/data/adb/magisk.img"
        };
        for (String path : paths) {
            if (new File(path).exists()) return true;
        }
        try {
            context.getPackageManager().getPackageInfo(
                "com.topjohnwu.magisk", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private boolean isXposed() {
        try {
            context.getPackageManager().getPackageInfo(
                "de.robv.android.xposed.installer", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {}
        try {
            context.getPackageManager().getPackageInfo(
                "org.lsposed.manager", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {}
        return false;
    }

    private boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
            || Build.FINGERPRINT.startsWith("unknown")
            || Build.MODEL.contains("google_sdk")
            || Build.MODEL.contains("Emulator")
            || Build.MODEL.contains("Android SDK")
            || Build.MANUFACTURER.contains("Genymotion")
            || Build.BRAND.startsWith("generic")
            || Build.DEVICE.startsWith("generic");
    }

    private boolean isADBEnabled() {
        return Settings.Global.getInt(
            context.getContentResolver(),
            Settings.Global.ADB_ENABLED, 0) == 1;
    }

    private boolean isDeveloperMode() {
        return Settings.Global.getInt(
            context.getContentResolver(),
            Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0) == 1;
    }
}

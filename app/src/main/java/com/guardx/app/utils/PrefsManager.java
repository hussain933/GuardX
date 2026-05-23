package com.guardx.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsManager {

    private static final String PREF_NAME = "guardx_prefs";
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public PrefsManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void setProtectionOn(boolean on) {
        editor.putBoolean(Constants.PREF_PROTECTION_ON, on).apply();
    }

    public boolean isProtectionOn() {
        return prefs.getBoolean(Constants.PREF_PROTECTION_ON, true);
    }

    public void setBootStart(boolean on) {
        editor.putBoolean(Constants.PREF_BOOT_START, on).apply();
    }

    public boolean isBootStart() {
        return prefs.getBoolean(Constants.PREF_BOOT_START, true);
    }

    public void setAutoFix(boolean on) {
        editor.putBoolean(Constants.PREF_AUTO_FIX, on).apply();
    }

    public boolean isAutoFix() {
        return prefs.getBoolean(Constants.PREF_AUTO_FIX, false);
    }

    public void setScanSensitivity(int level) {
        editor.putInt(Constants.PREF_SCAN_SENSITIVITY, level).apply();
    }

    public int getScanSensitivity() {
        return prefs.getInt(Constants.PREF_SCAN_SENSITIVITY, Constants.SENSITIVITY_MEDIUM);
    }

    public void setNotifStyle(String style) {
        editor.putString(Constants.PREF_NOTIF_STYLE, style).apply();
    }

    public String getNotifStyle() {
        return prefs.getString(Constants.PREF_NOTIF_STYLE, "NORMAL");
    }

    public void setLastScan(long time) {
        editor.putLong(Constants.PREF_LAST_SCAN, time).apply();
    }

    public long getLastScan() {
        return prefs.getLong(Constants.PREF_LAST_SCAN, 0);
    }
}

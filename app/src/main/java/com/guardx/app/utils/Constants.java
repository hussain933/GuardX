package com.guardx.app.utils;

public class Constants {

    // App Info
    public static final String APP_NAME = "GuardX";
    public static final String VERSION = "2.5.0";

    // Notification
    public static final String NOTIF_CHANNEL_ID = "guardx_channel";
    public static final String NOTIF_CHANNEL_NAME = "GuardX Protection";
    public static final int NOTIF_ID_MAIN = 1001;
    public static final int NOTIF_ID_WARNING = 1002;
    public static final int NOTIF_ID_CLEAR = 1003;

    // Supported Games
    public static final String[] GAMES = {
        "com.pubg.imobile",          // BGMI
        "com.dts.freefireth",        // Free Fire
        "com.activision.callofduty.shooter", // COD Mobile
        "com.tencent.ig",            // PUBG
        "com.epicgames.fortnite"     // Fortnite
    };

    // Scan Intervals (ms)
    public static final int SCAN_GAME_ACTIVE = 5000;    // 5 sec
    public static final int SCAN_FILE_SYSTEM = 30000;   // 30 sec
    public static final int SCAN_ENVIRONMENT = 60000;   // 1 min
    public static final int SCAN_IDLE = 300000;         // 5 min

    // Risk Levels
    public static final String RISK_LOW = "LOW";
    public static final String RISK_MEDIUM = "MEDIUM";
    public static final String RISK_HIGH = "HIGH";
    public static final String RISK_CRITICAL = "CRITICAL";

    // Prefs Keys
    public static final String PREF_PROTECTION_ON = "protection_on";
    public static final String PREF_BOOT_START = "boot_start";
    public static final String PREF_AUTO_FIX = "auto_fix";
    public static final String PREF_SCAN_SENSITIVITY = "scan_sensitivity";
    public static final String PREF_NOTIF_STYLE = "notif_style";
    public static final String PREF_LAST_SCAN = "last_scan";

    // Intent Actions
    public static final String ACTION_THREAT_FOUND = "com.guardx.THREAT_FOUND";
    public static final String ACTION_ALL_CLEAR = "com.guardx.ALL_CLEAR";
    public static final String ACTION_FIX_ALL = "com.guardx.FIX_ALL";
    public static final String ACTION_CANCEL = "com.guardx.CANCEL";

    // Sensitivity
    public static final int SENSITIVITY_LOW = 0;
    public static final int SENSITIVITY_MEDIUM = 1;
    public static final int SENSITIVITY_HIGH = 2;

    // DB
    public static final String DB_NAME = "guardx.db";
    public static final int DB_VERSION = 1;
}

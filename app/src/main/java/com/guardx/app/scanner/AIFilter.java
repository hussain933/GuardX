package com.guardx.app.scanner;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import java.util.ArrayList;
import java.util.List;

public class AIFilter {

    private Context context;
    private PackageManager packageManager;
    private ActivityManager activityManager;

    public AIFilter(Context context) {
        this.context = context;
        this.packageManager = context.getPackageManager();
        this.activityManager = (ActivityManager)
            context.getSystemService(Context.ACTIVITY_SERVICE);
    }

    public List<String> scan() {
        List<String> threats = new ArrayList<>();
        threats.addAll(checkSuspiciousPermissions());
        threats.addAll(checkMemorySpike());
        return threats;
    }

    private List<String> checkSuspiciousPermissions() {
        List<String> threats = new ArrayList<>();
        List<ApplicationInfo> apps = packageManager.getInstalledApplications(
            PackageManager.GET_META_DATA);

        for (ApplicationInfo app : apps) {
            try {
                String[] permissions = packageManager.getPackageInfo(
                    app.packageName,
                    PackageManager.GET_PERMISSIONS
                ).requestedPermissions;

                if (permissions == null) continue;

                int suspiciousCount = 0;
                for (String perm : permissions) {
                    if (perm.contains("OVERLAY") ||
                        perm.contains("ACCESSIBILITY") ||
                        perm.contains("SYSTEM_ALERT")) {
                        suspiciousCount++;
                    }
                }

                if (suspiciousCount >= 2) {
                    String name = packageManager.getApplicationLabel(app).toString();
                    threats.add("Suspicious permissions: " + name);
                }
            } catch (Exception e) {
                // Skip
            }
        }
        return threats;
    }

    private List<String> checkMemorySpike() {
        List<String> threats = new ArrayList<>();
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memInfo);

        long usedMem = memInfo.totalMem - memInfo.availMem;
        long usedPercent = (usedMem * 100) / memInfo.totalMem;

        if (usedPercent > 90) {
            threats.add("High memory usage: " + usedPercent + "% (possible cheat tool)");
        }
        return threats;
    }
}

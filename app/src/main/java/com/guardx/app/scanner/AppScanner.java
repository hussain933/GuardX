package com.guardx.app.scanner;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import com.guardx.app.database.BlacklistDAO;
import java.util.ArrayList;
import java.util.List;

public class AppScanner {

    private Context context;
    private BlacklistDAO blacklistDAO;
    private PackageManager packageManager;
    private ActivityManager activityManager;

    public AppScanner(Context context) {
        this.context = context;
        this.blacklistDAO = new BlacklistDAO(context);
        this.packageManager = context.getPackageManager();
        this.activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    }

    public List<String> scan() {
        List<String> threats = new ArrayList<>();
        List<String> blacklist = blacklistDAO.getEnabledPackages();
        List<String> running = getRunningApps();

        for (String pkg : running) {
            if (blacklist.contains(pkg)) {
                try {
                    ApplicationInfo info = packageManager.getApplicationInfo(pkg, 0);
                    String appName = packageManager.getApplicationLabel(info).toString();
                    threats.add(appName + " (" + pkg + ")");
                } catch (PackageManager.NameNotFoundException e) {
                    threats.add(pkg);
                }
            }
        }
        return threats;
    }

    private List<String> getRunningApps() {
        List<String> running = new ArrayList<>();
        List<ActivityManager.RunningAppProcessInfo> processes =
            activityManager.getRunningAppProcesses();

        if (processes != null) {
            for (ActivityManager.RunningAppProcessInfo process : processes) {
                if (process.pkgList != null) {
                    for (String pkg : process.pkgList) {
                        running.add(pkg);
                    }
                }
            }
        }
        return running;
    }

    public boolean isGameRunning(String[] gamePackages) {
        List<String> running = getRunningApps();
        for (String game : gamePackages) {
            if (running.contains(game)) {
                return true;
            }
        }
        return false;
    }

    public String getActiveGame(String[] gamePackages) {
        List<String> running = getRunningApps();
        for (String game : gamePackages) {
            if (running.contains(game)) {
                return game;
            }
        }
        return null;
    }
}

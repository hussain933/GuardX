package com.guardx.app.permission;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class DeviceOwnerManager extends DeviceAdminReceiver {

    public static ComponentName getComponentName(Context context) {
        return new ComponentName(context, DeviceOwnerManager.class);
    }

    public static boolean isDeviceOwner(Context context) {
        DevicePolicyManager dpm = (DevicePolicyManager)
            context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        return dpm != null && dpm.isDeviceOwnerApp(context.getPackageName());
    }

    public static boolean isAdminActive(Context context) {
        DevicePolicyManager dpm = (DevicePolicyManager)
            context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        return dpm != null && dpm.isAdminActive(getComponentName(context));
    }

    public static void lockApp(Context context, String packageName) {
        if (!isDeviceOwner(context)) return;
        DevicePolicyManager dpm = (DevicePolicyManager)
            context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        if (dpm != null) {
            try {
                dpm.setPackagesSuspended(
                    getComponentName(context),
                    new String[]{packageName},
                    true
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void unlockApp(Context context, String packageName) {
        if (!isDeviceOwner(context)) return;
        DevicePolicyManager dpm = (DevicePolicyManager)
            context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        if (dpm != null) {
            try {
                dpm.setPackagesSuspended(
                    getComponentName(context),
                    new String[]{packageName},
                    false
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void freezeGame(Context context, String gamePackage) {
        lockApp(context, gamePackage);
    }

    public static void resumeGame(Context context, String gamePackage) {
        unlockApp(context, gamePackage);
    }

    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);
    }
}

package com.inforoeste.mocklocationdetector;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import java.util.List;

/**
 * Created by smarques84 on 15/05/16.
 */
public class MockLocationDetector {

    private static final String TAG = MockLocationDetector.class.getSimpleName();

    //Always return false on Marshmallow because the settings have been updated and its not possible to check if an app has been allowed
    // to perform mock locations. Please use isLocationFromMockProvider instead
    @Deprecated
    public static boolean isAllowMockLocationsOn(Context context) {
        // returns true if mock location enabled, false if not enabled.
        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            if (Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ALLOW_MOCK_LOCATION).equals("0"))
                return false;
            else {
                return true;
            }
        } else {
            return false;
        }
    }

    public static boolean checkForAllowMockLocationsApps(Context context) {

        int count = 0;

        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages =
                pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo applicationInfo : packages) {
            try {
                PackageInfo packageInfo = pm.getPackageInfo(applicationInfo.packageName,
                        PackageManager.GET_PERMISSIONS);

                // Get Permissions
                String[] requestedPermissions = packageInfo.requestedPermissions;

                if (requestedPermissions != null) {
                    for (int i = 0; i < requestedPermissions.length; i++) {
                        if (requestedPermissions[i]
                                .equals("android.permission.ACCESS_MOCK_LOCATION")
                                && !applicationInfo.packageName.equals(context.getPackageName())) {
                            count++;
                        }
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "Got exception " + e.getMessage());
            }
        }

        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    //http://stackoverflow.com/questions/6880232/disable-check-for-mock-location-prevent-gps-spoofing
    public static boolean isLocationFromMockProvider(Context context, Location location) {
        boolean isMock = false;
        if (android.os.Build.VERSION.SDK_INT >= 18) {
            isMock = location.isFromMockProvider();
        } else {
            //isMock = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0");
            if (Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ALLOW_MOCK_LOCATION).equals("0"))
                return false;
            else {
                return true;
            }
        }
        return isMock;
    }
}

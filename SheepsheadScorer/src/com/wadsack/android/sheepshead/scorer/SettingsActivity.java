package com.wadsack.android.sheepshead.scorer;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import roboguice.activity.GuicePreferenceActivity;

/**
 * Author: Jeremy Wadsack
 */
public class SettingsActivity extends GuicePreferenceActivity {
    private static final String TAG = SettingsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        findPreference("about_version").setSummary(getVersionName());
    }




    private String getVersionName() {
        String version = "";
        try {
            PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            version = "Unknown";
            Log.e(TAG, "Could not get package for version name", e);
        }
        return version;
    }
}

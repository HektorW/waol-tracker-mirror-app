package com.waol.trackermirror.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

public class Settings {

    private static final String sharedPrefKey = "com.waol.trackermirror.settingsKey";

    public static void store(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPrefKey, Activity.MODE_PRIVATE);
        sharedPreferences.edit().putString(key, value).apply();
    }

    public static String get(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPrefKey, Activity.MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }
    public static String get(Context context, String key, @Nullable String defValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPrefKey, Activity.MODE_PRIVATE);
        return sharedPreferences.getString(key, defValue);
    }
}

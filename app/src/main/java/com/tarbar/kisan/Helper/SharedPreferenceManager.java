package com.tarbar.kisan.Helper;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceManager {
    private Context context;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private static final String PREFS_NAME = "Cattle";

    public SharedPreferenceManager(Context context) {
        this.context = context;
        connectDB();
    }

    public void connectDB() {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void closeDB() {
        editor.commit();
    }

    public void clear() {
        editor.clear().apply();
    }

    public void setBoolean(String key, boolean value) {
        editor.putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key) {
        return prefs.getBoolean(key, false);
    }

    public void setString(String key, String value) {
        editor.putString(key, value).apply();
    }

    public String getString(String key) {
        return prefs.getString(key, null);
    }
}

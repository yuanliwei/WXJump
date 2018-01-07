package com.ylw.wxjump;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ylw on 2017/12/24.
 */

public class Prefrence {
    public static Context context;

    public static String get(String key) {
        return getEdit().getString(key, "");
    }

    public static void put(String key, String value) {
        if (get(key).length() > value.length()) return;
        getEdit().edit().putString(key, value).apply();
    }

    static SharedPreferences getEdit() {
        return context.getSharedPreferences("app_setting", Context.MODE_PRIVATE);
    }
}

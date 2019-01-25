package com.lotterental.generalrental.util.preferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by macpro on 2018. 7. 11..
 */

/**
 * ODS preferences class
 */
public class LPreferences {
    private static final String UUID = "UUID"; //앱 처음시작
    private static final String APP_FIRST_LAUNCH = "APP_FIRST_LAUNCH";
    private static final String TOKEN = "TOKEN";
    private static final String IS_CONNECTED = "IS_CONNECTED";

    public static final String PREF_NAME = "ibkc_ods.pref";

    private static void setString(Context context, String key, String value) {
        assert context != null;
        assert key != null;

        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private static String getString(Context context, String key, String defValue) {
        assert context != null;
        assert key != null;

        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, 0);
        return pref.getString(key, defValue);
    }

    private static void setBoolean(Context context, String key, boolean value) {
        assert context != null;
        assert key != null;

        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    private static boolean getBoolean(Context context, String key, boolean defValue) {
        assert context != null;
        assert key != null;

        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, 0);
        return pref.getBoolean(key, defValue);
    }

    public static void setToken(Context context, String token) {
        assert context != null;
        setString(context, TOKEN, token);
    }

    public static String getToken(Context context) {
        assert context != null;
        return getString(context, TOKEN, "");
    }
    public static String getUUID(Context context) {
        assert context != null;
        return getString(context, UUID, null);
    }

    public static void setUUID(Context context, String data) {
        assert context != null;
        setString(context, UUID, data);
    }

    public static void setIsConnected(Context context, boolean isConnected) {
        assert context != null;
        setBoolean(context, IS_CONNECTED, isConnected);
    }

    public static boolean getIsConnected(Context context) {
        assert context != null;
        return getBoolean(context, IS_CONNECTED, false);
    }
}

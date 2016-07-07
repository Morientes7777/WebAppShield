package com.crgglobal.SecondFactorSecurity;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;


public class SessionManager {
    // User name (make variable public to access from outside)
    public static final String KEY_USER = "user";

    public static final String KEY_OFFLINE_CODE = "offLineCode";
    public static final String KEY_CODE_EXPIRATION = "codeExpiration";

    public static final String KEY_DEVICE_ID = "deviceId";
    public static final String KEY_REQUIRE_PIN = "requirePin";

    // Sharedpref file name
    private static final String PREF_NAME = "UserPrefs";


    // Shared Preferences
    SharedPreferences pref;
    // Editor for Shared preferences
    Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;


    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    public void createPreferencesSession(String user, String offLineCode, String codeExpiration) {

        editor.putString(KEY_USER, user);
        editor.putString(KEY_OFFLINE_CODE, offLineCode);
        editor.putString(KEY_CODE_EXPIRATION, codeExpiration);

        editor.commit();
    }

    public String getUserFromPrefs() {
        return pref.getString(KEY_USER, "");
    }

    public String getOfflineFromPrefs() {
        return pref.getString(KEY_OFFLINE_CODE, "");
    }

    public String getCodeExpiration() {
        return pref.getString(KEY_CODE_EXPIRATION, "");
    }

    /**
     * Get stored session data
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<>();

        user.put(KEY_USER, pref.getString(KEY_USER, null));
        user.put(KEY_OFFLINE_CODE, pref.getString(KEY_OFFLINE_CODE, null));

        return user;
    }

    public void clearSession() {
        /**
         *  Clearing all data from Shared Preferences
         */

        editor.clear();
        editor.commit();
    }


    public void setRequirePin(boolean requirePin) {
        editor.putBoolean(KEY_REQUIRE_PIN, requirePin);
        editor.commit();
    }

    public boolean isRequiredPin() {
        return pref.getBoolean(KEY_REQUIRE_PIN, false);
    }

    public void setDeviceId(String deviceId) {
        editor.putString(KEY_DEVICE_ID, deviceId);
        editor.commit();
    }

    public String getDeviceId() {
        return pref.getString(KEY_DEVICE_ID, null);
    }

}

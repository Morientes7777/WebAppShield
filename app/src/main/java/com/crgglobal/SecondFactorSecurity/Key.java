package com.crgglobal.SecondFactorSecurity;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by oaldea on 4/22/2016.
 */
public class Key {

    String onlineCode;
    String offlineCode;
    String onlineCodeExpiration;
    String offlineCodeExpiration;


    public String getOnlineCode() {
        return onlineCode;
    }

    public void setOnlineCode(String onlineCode) {
        this.onlineCode = onlineCode;
    }

    public String getOfflineCode() {
        return offlineCode;
    }

    public void setOfflineCode(String offlineCode) {
        this.offlineCode = offlineCode;
    }

    public String getOnlineCodeExpiration() {
        return onlineCodeExpiration;
    }

    public void setOnlineCodeExpiration(String onlineCodeExpiration) {
        this.onlineCodeExpiration = onlineCodeExpiration;
    }

    public String getOfflineCodeExpiration() {
        return offlineCodeExpiration;
    }

    public void setOfflineCodeExpiration(String offlineCodeExpiration) {
        this.offlineCodeExpiration = offlineCodeExpiration;
    }

    @Override
    public String toString() {
        return "onlineCode " + onlineCode + " offlineCode " + offlineCode + " setOnlineCodeExpiration " + onlineCodeExpiration;
    }

    public Key() {
    }


    /**
     * Parse JSON and create Key Object
     */
    public Key(String content) {
        try {

            JSONObject obj = new JSONObject(content);
            this.setOnlineCode(obj.getString("OnlineCode"));
            this.setOfflineCode(obj.getString("OfflineCode"));
            this.setOnlineCodeExpiration(obj.getString("OnlineCodeExpiration"));
            this.setOfflineCodeExpiration(obj.getString("OfflineCodeExpiration"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

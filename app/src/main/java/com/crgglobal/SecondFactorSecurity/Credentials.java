package com.crgglobal.SecondFactorSecurity;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Created by oaldea on 7/5/2016.
 */
public class Credentials {

    @SerializedName("Username")
    private String Username;

    @SerializedName("Password")
    private String Password;

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}

package com.crgglobal.SecondFactorSecurity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by oaldea on 7/4/2016.
 */
public class UserDevice {

    @SerializedName("UserDataId")
    private int userDataId;

    @SerializedName("UserDeviceId")
    private int userDeviceId;

    @SerializedName("DeviceId")
    private String deviceId;

    @SerializedName("RequirePIN")
    private boolean requirePIN;

    @SerializedName("PIN")
    private String PIN;

    @SerializedName("RegistrationExpired")
    private boolean RegistrationExpired;



    public int getUserDataId() {
        return userDataId;
    }

    public void setUserDataId(int userDataId) {
        this.userDataId = userDataId;
    }

    public int getUserDeviceId() {
        return userDeviceId;
    }

    public void setUserDeviceId(int userDeviceId) {
        this.userDeviceId = userDeviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public boolean isRequirePIN() {
        return requirePIN;
    }

    public void setRequirePIN(boolean requirePIN) {
        this.requirePIN = requirePIN;
    }

    public String getPIN() {
        return PIN;
    }

    public void setPIN(String PIN) {
        this.PIN = PIN;
    }

    public boolean isRegistrationExpired() {
        return RegistrationExpired;
    }

    public void setRegistrationExpired(boolean registrationExpired) {
        RegistrationExpired = registrationExpired;
    }

    @Override
    public String toString() {
        return "UserDevice{" +
                "userDataId=" + userDataId +
                ", userDeviceId=" + userDeviceId +
                ", deviceId='" + deviceId + '\'' +
                ", requirePIN=" + requirePIN +
                ", PIN='" + PIN + '\'' +
                ", RegistrationExpired=" + RegistrationExpired +
                '}';
    }
}

package com.crgglobal.SecondFactorSecurity;


import android.os.Parcel;
import android.os.Parcelable;

public class KeyValuePair implements Parcelable {

    public static final Creator<KeyValuePair> CREATOR = new Creator<KeyValuePair>() {

        @Override
        public KeyValuePair createFromParcel(Parcel in) {

            return new KeyValuePair(in);

        }

        @Override
        public KeyValuePair[] newArray(int i) {
            return new KeyValuePair[i];
        }

    };
    private int key;
    private String value;

    public KeyValuePair() {

    }

    public KeyValuePair(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public KeyValuePair(Parcel input) {
        key = input.readInt();
        value = input.readString();
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(key);
        dest.writeString(value);
    }

    public String toString() {
        return key + " " + value;
    }
}

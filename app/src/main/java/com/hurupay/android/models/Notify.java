package com.hurupay.android.models;

public class Notify {

    String token;

    public Notify() {
    }

    public Notify(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

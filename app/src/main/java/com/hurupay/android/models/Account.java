package com.hurupay.android.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Account implements Serializable {

    private String phone;
    private String pic;
    private String username;
    private String address;
    private String token;
    private ArrayList<String> tags;
    private boolean verified;

    public Account() {
    }

    public Account(String phone, String pic, String username, String address, String token, ArrayList<String> tags, boolean verified) {
        this.phone = phone;
        this.pic = pic;
        this.username = username;
        this.address = address;
        this.token = token;
        this.tags = tags;
        this.verified = verified;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    @Override
    public boolean equals(@androidx.annotation.Nullable Object obj){
        Account account = (Account) obj;
        return address.matches(account.getAddress());
    }
}

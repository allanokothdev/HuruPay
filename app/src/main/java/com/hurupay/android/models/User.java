package com.hurupay.android.models;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {

    private String id; //ID
    private String email;
    private String phone;
    private String pic;
    private String username;
    private String address;
    private String privateKey;
    private String balance;
    private String country;
    private String token;
    private boolean verified;
    private String currency;
    private ArrayList<String> tags;
    private long date;

    public User() {
    }

    public User(String id, String email, String phone, String pic, String username, String address, String privateKey, String balance, String country, String token, boolean verified, String currency, ArrayList<String> tags, long date) {
        this.id = id;
        this.email = email;
        this.phone = phone;
        this.pic = pic;
        this.username = username;
        this.address = address;
        this.privateKey = privateKey;
        this.balance = balance;
        this.country = country;
        this.token = token;
        this.verified = verified;
        this.currency = currency;
        this.tags = tags;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Override
    public boolean equals(@androidx.annotation.Nullable Object obj){
        User user = (User) obj;
        return id.matches(user.getId());
    }
}

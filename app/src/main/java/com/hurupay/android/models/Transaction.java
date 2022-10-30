package com.hurupay.android.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Transaction implements Serializable {

    private String hash;
    private String type;
    private String timestamp;
    private String tokenAddress;
    private String sender;
    private String recipient;
    private String value;
    private String currency;
    private ArrayList<String> tags;
    private long date;
    private String token;

    public Transaction() {
    }

    public Transaction(String hash, String type, String timestamp, String tokenAddress, String sender, String recipient, String value, String currency, ArrayList<String> tags, long date, String token) {
        this.hash = hash;
        this.type = type;
        this.timestamp = timestamp;
        this.tokenAddress = tokenAddress;
        this.sender = sender;
        this.recipient = recipient;
        this.value = value;
        this.currency = currency;
        this.tags = tags;
        this.date = date;
        this.token = token;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTokenAddress() {
        return tokenAddress;
    }

    public void setTokenAddress(String tokenAddress) {
        this.tokenAddress = tokenAddress;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public boolean equals(@androidx.annotation.Nullable Object obj){
        Transaction transaction = (Transaction) obj;
        return hash.matches(transaction.getHash());
    }
}

package com.hurupay.android.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Token implements Serializable {

    private String tokenAddress;
    private String pic;
    private String title;
    private String summary;
    private int decimals;
    private String tokenType;
    private String symbol;
    private int type;
    private ArrayList<String> tags;

    public Token() {
    }

    public Token(String tokenAddress, String pic, String title, String summary, int decimals, String tokenType, String symbol, int type, ArrayList<String> tags) {
        this.tokenAddress = tokenAddress;
        this.pic = pic;
        this.title = title;
        this.summary = summary;
        this.decimals = decimals;
        this.tokenType = tokenType;
        this.symbol = symbol;
        this.type = type;
        this.tags = tags;
    }

    public String getTokenAddress() {
        return tokenAddress;
    }

    public void setTokenAddress(String tokenAddress) {
        this.tokenAddress = tokenAddress;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public int getDecimals() {
        return decimals;
    }

    public void setDecimals(int decimals) {
        this.decimals = decimals;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    @Override
    public boolean equals(@androidx.annotation.Nullable Object obj){
        Token token = (Token) obj;
        return tokenAddress.matches(token.getTokenAddress());
    }
}

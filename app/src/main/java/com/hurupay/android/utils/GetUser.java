package com.hurupay.android.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.hurupay.android.constants.Constants;
import com.hurupay.android.models.User;

public class GetUser extends Application {

    public static User getUser(Context context, String currentUserID) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.USERS, Context.MODE_PRIVATE);
        return new User(currentUserID, prefs.getString(Constants.EMAIL, Constants.EMAIL), prefs.getString(Constants.PHONE_NUMBER, Constants.PHONE_NUMBER),prefs.getString(Constants.PIC, Constants.PIC), prefs.getString(Constants.USERNAME, Constants.USERNAME), prefs.getString(Constants.WALLET_ADDRESS, Constants.WALLET_ADDRESS), prefs.getString(Constants.PRIVATE_KEY,Constants.PRIVATE_KEY), prefs.getString(Constants.BALANCE,"0"), prefs.getString(Constants.COUNTRY,Constants.COUNTRY),prefs.getString(Constants.TOKEN,Constants.TOKEN),prefs.getBoolean(Constants.VERIFIED,false),prefs.getString(Constants.CURRENCY,Constants.CURRENCY),null,prefs.getLong(Constants.JOININGdate,System.currentTimeMillis()));
    }

    public static void saveUser(Context context, User user) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Constants.USERS,Context.MODE_PRIVATE).edit();
        editor.putString(Constants.PIC, user.getPic());
        editor.putString(Constants.USERNAME, user.getUsername());
        editor.putString(Constants.WALLET_ADDRESS, user.getAddress());
        editor.putString(Constants.PRIVATE_KEY, user.getPrivateKey());
        editor.putString(Constants.EMAIL, user.getEmail());
        editor.putString(Constants.BALANCE, user.getBalance());
        editor.putString(Constants.PHONE_NUMBER, user.getPhone());
        editor.putString(Constants.TOKEN, user.getToken());
        editor.putBoolean(Constants.VERIFIED, user.isVerified());
        editor.putString(Constants.CURRENCY, user.getCurrency());
        editor.apply();
    }

    public static void saveObject(Context context, String key, String value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Constants.USERS,Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String fetchObject(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.USERS, Context.MODE_PRIVATE);
        return prefs.getString(key, "0");
    }
}

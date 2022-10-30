package com.hurupay.android.utils;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.hurupay.android.models.Account;
import com.hurupay.android.models.User;
import com.hurupay.android.models.Wallet;

import java.util.HashMap;
import java.util.Map;

public class FetchFirebaseObjects extends Application {

    public static String fetchBalance(String privateKey) {
        FirebaseFunctions firebaseFunctions = FirebaseFunctions.getInstance();
        Map<String, Object> data = new HashMap<>();
        data.put("key", privateKey);
        return firebaseFunctions.getHttpsCallable("fetchBalance").call(data).continueWith(task -> (String) task.getResult().getData()).toString();

    }
}

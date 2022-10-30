package com.hurupay.android.listeners;

import android.widget.ImageView;

import com.hurupay.android.models.Account;
import com.hurupay.android.models.User;

public interface AccountItemClickListener {
    void onAccountItemClick(Account account, ImageView imageView, String objectID);
}

package com.hurupay.android.listeners;

import android.widget.ImageView;

import com.hurupay.android.models.Token;

public interface TokenItemClickListener {

    void onTokenItemClick(Token token, ImageView imageView);
}

package com.hurupay.android.listeners;

import android.widget.ImageView;

import com.hurupay.android.models.Transaction;

public interface TransactionItemClickListener {

    void onTransactionItemClick(Transaction transaction, ImageView imageView, String objectID);
}

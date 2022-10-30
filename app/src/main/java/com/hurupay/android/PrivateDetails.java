package com.hurupay.android;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.hurupay.android.models.User;
import com.hurupay.android.utils.GetUser;

import java.util.Objects;

public class PrivateDetails extends AppCompatActivity {

    private final Context mContext = PrivateDetails.this;
    private final String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        User user = GetUser.getUser(mContext, currentUserID);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Private Key");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_blue_24dp);
        toolbar.setNavigationOnClickListener(v -> finishAfterTransition());
        TextView textView = findViewById(R.id.textView);
        textView.setText(user.getPrivateKey());
    }
}
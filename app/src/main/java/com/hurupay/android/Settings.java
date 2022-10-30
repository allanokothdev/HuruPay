package com.hurupay.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.FirebaseFunctions;
import com.hbb20.CountryCodePicker;
import com.hurupay.android.constants.Constants;
import com.hurupay.android.models.User;
import com.hurupay.android.utils.GetUser;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Settings extends AppCompatActivity implements View.OnClickListener {

    private final FirebaseFunctions firebaseFunctions = FirebaseFunctions.getInstance();
    private final Context mContext = Settings.this;
    private RelativeLayout container;
    private final String TAG = this.getClass().getSimpleName();
    private final String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        User user = GetUser.getUser(mContext, currentUserID);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_yellow_24dp);
        toolbar.setNavigationOnClickListener(v -> finishAfterTransition());

        container = findViewById(R.id.container);
        ImageView countryImageView = findViewById(R.id.countryImageView);
        CountryCodePicker countryCodePicker = findViewById(R.id.countryCodePicker);
        countryCodePicker.detectLocaleCountry(true);
        countryCodePicker.detectSIMCountry(true);
        countryCodePicker.detectNetworkCountry(true);
        countryCodePicker.setImageViewFlag(countryImageView);
        ImageView imageView = findViewById(R.id.imageView);
        TextView textView = findViewById(R.id.textView);
        TextView subTextView = findViewById(R.id.subTextView);
        TextView addressTextView = findViewById(R.id.addressTextView);
        TextView phoneTextView = findViewById(R.id.phoneTextView);
        RelativeLayout privateKeyLayout = findViewById(R.id.privateKeyLayout);
        RelativeLayout privacyLayout = findViewById(R.id.privacyLayout);
        RelativeLayout feedbackLayout = findViewById(R.id.feedbackLayout);
        RelativeLayout aboutLayout = findViewById(R.id.aboutLayout);
        RelativeLayout signOutLayout = findViewById(R.id.signOutLayout);
        RelativeLayout tokenLayout = findViewById(R.id.tokenLayout);
        privacyLayout.setOnClickListener(this);
        tokenLayout.setOnClickListener(this);
        privateKeyLayout.setOnClickListener(this);
        feedbackLayout.setOnClickListener(this);
        aboutLayout.setOnClickListener(this);
        signOutLayout.setOnClickListener(this);
        Glide.with(mContext.getApplicationContext()).load(user.getPic()).placeholder(R.drawable.placeholder).into(imageView);
        textView.setText(user.getUsername());
        subTextView.setText(getString(R.string.price_with_currency_string, user.getBalance()));
        addressTextView.setText(user.getAddress());
        phoneTextView.setText(user.getPhone());
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.privacyLayout:
                Intent indie = new Intent(Intent.ACTION_VIEW);
                indie.setData(Uri.parse(Constants.PRIVACY_POLICY));
                startActivity(indie);
                break;
            case R.id.privateKeyLayout:

                break;
            case R.id.tokenLayout:
                startActivity(new Intent(mContext, TokenList.class));
                break;
            case R.id.feedbackLayout:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:hello@hurupay.com"));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, " HuruPay User Feedback");
                startActivity(emailIntent);
                break;
            case R.id.aboutLayout:
                startActivity(new Intent(mContext, About.class));
                break;
            case R.id.signOutLayout:
                signOut();
                break;
        }
    }

    private void signOut(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sign Out");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", (dialog, which) -> { firebaseAuth.signOut();finishAndRemoveTask(); });
        builder.setNegativeButton("Hell No", (dialog, which) -> { });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
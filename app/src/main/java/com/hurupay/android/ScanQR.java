package com.hurupay.android;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hurupay.android.constants.Constants;
import com.hurupay.android.models.Account;

public class ScanQR extends AppCompatActivity implements View.OnClickListener {

    private CodeScanner mCodeScanner;
    private final Context mContext = ScanQR.this;
    private FrameLayout container;
    private CodeScannerView scannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);
        try {

            ImageView closeImageView = findViewById(R.id.closeImageView);
            container = findViewById(R.id.container);
            closeImageView.setOnClickListener(this);
            scannerView = findViewById(R.id.scanner_view);
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ScanQR.this, new String[]{Manifest.permission.CAMERA}, 1);
            }
            initialize();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initialize(){
        try {
            mCodeScanner = new CodeScanner(this, scannerView);
            mCodeScanner.startPreview();
            mCodeScanner.setDecodeCallback(result -> runOnUiThread(() -> sendMoney(result.getText())));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (mCodeScanner != null){
                mCodeScanner.startPreview();
            } else {
                initialize();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        if (mCodeScanner != null){
            mCodeScanner.startPreview();
        } else {
            initialize();
        }
    }

    @Override
    protected void onPause() {
        if (mCodeScanner != null){
            mCodeScanner.releaseResources();
        } else {
            initialize();
        }
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.closeImageView){
            finishAfterTransition();
        }
    }

    private void sendMoney(String objectID){
        FirebaseFirestore.getInstance().collection(Constants.ACCOUNTS).document(objectID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()){
                Account account = documentSnapshot.toObject(Account.class);
                assert account != null;
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.OBJECT,account);
                startActivity(new Intent(mContext, Send.class).putExtras(bundle));
                finishAfterTransition();
            } else {
                Snackbar snackbar = Snackbar.make(container,"This wallet address does not exist in our database", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        });


    }




}
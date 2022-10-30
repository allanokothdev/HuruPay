package com.hurupay.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class Recovery extends AppCompatActivity implements View.OnClickListener {

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private RelativeLayout container;
    private final Context mContext = Recovery.this;
    private ProgressBar progressBar;
    private TextInputEditText emailTextInputEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Account Recovery");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_yellow_24dp);
        toolbar.setNavigationOnClickListener(v -> finishAfterTransition());

        Button button = findViewById(R.id.button);
        progressBar = findViewById(R.id.progressBar);
        container = findViewById(R.id.container);
        emailTextInputEditText = findViewById(R.id.emailTextInputEditText);
        button.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.button){
            String email = emailTextInputEditText.getText().toString();

            if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                emailTextInputEditText.setError("Enter Correct Email");
                progressBar.setVisibility(View.GONE);
                emailTextInputEditText.requestFocus();
            } else {
                firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){

                        Snackbar snackbar = Snackbar.make(container, "Check your Email", Snackbar.LENGTH_LONG);
                        snackbar.setAction("Open Email App", new RecoveryEmailListener());
                        snackbar.show();
                    }else {
                        Snackbar snackbar = Snackbar.make(container, Objects.requireNonNull(task.getException()).toString(), Snackbar.LENGTH_LONG);
                        snackbar.show();


                    }

                });
            }
        }
    }

    private class RecoveryEmailListener implements View.OnClickListener{
        @Override
        public void onClick(View view){
            try {
                Intent emailIntent = new Intent(Intent.ACTION_MAIN);
                emailIntent.addCategory(Intent.CATEGORY_APP_EMAIL);
                startActivity(emailIntent);
                startActivity(Intent.createChooser(emailIntent, "Open Email Client"));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
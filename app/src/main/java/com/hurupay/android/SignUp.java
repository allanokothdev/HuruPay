package com.hurupay.android;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;
import com.hurupay.android.constants.Constants;
import com.hurupay.android.models.Wallet;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class SignUp extends AppCompatActivity implements View.OnClickListener {

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final Context mContext = SignUp.this;
    private ProgressBar progressBar;
    private RelativeLayout container;
    private TextInputEditText emailTextInputEditText;
    private TextInputEditText passwordTextInputEditText;
    private TextInputEditText repeatPasswordTextInputEditText;
    private TextInputLayout passwordTextInputLayout;
    private TextInputLayout repeatPasswordTextInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        try {
            TextView privacyTextView = findViewById(R.id.privacyTextView);
            passwordTextInputLayout = findViewById(R.id.passwordTextInputLayout);
            repeatPasswordTextInputLayout = findViewById(R.id.repeatPasswordTextInputLayout);
            privacyTextView.setOnClickListener(this);
            TextView loginTextView = findViewById(R.id.loginTextView);
            Button button = findViewById(R.id.button);
            progressBar = findViewById(R.id.progressBar);
            container = findViewById(R.id.container);
            emailTextInputEditText = findViewById(R.id.emailTextInputEditText);
            passwordTextInputEditText = findViewById(R.id.passwordTextInputEditText);
            repeatPasswordTextInputEditText = findViewById(R.id.repeatPasswordTextInputEditText);
            loginTextView.setOnClickListener(this);
            button.setOnClickListener(this);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.privacyTextView){
            Intent indie = new Intent(Intent.ACTION_VIEW);
            indie.setData(Uri.parse(Constants.PRIVACY_POLICY));
            startActivity(indie);
        } else if (view.getId()==R.id.loginTextView){
            startActivity(new Intent(mContext, com.hurupay.android.Login.class));
        } else if (view.getId()==R.id.button){
            String email = emailTextInputEditText.getText().toString();
            String password = passwordTextInputEditText.getText().toString();
            String repeatPassword = repeatPasswordTextInputEditText.getText().toString();

            if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                emailTextInputEditText.setError("Enter Correct Email");
                progressBar.setVisibility(View.GONE);
                emailTextInputEditText.requestFocus();
            } else if (TextUtils.isEmpty(password) || password.length()<6) {
                passwordTextInputLayout.setError("Enter Password");
                 progressBar.setVisibility(View.GONE);
                 passwordTextInputEditText.requestFocus();
            }else if (TextUtils.isEmpty(repeatPassword) || repeatPassword.length()<6) {
                repeatPasswordTextInputLayout.setError("Repeat Password");
                progressBar.setVisibility(View.GONE);
                repeatPasswordTextInputEditText.requestFocus();
            } else if (!password.toLowerCase().equals(repeatPassword.toLowerCase())){
                repeatPasswordTextInputLayout.setError("Enter Matching Password");
                progressBar.setVisibility(View.GONE);
                repeatPasswordTextInputEditText.requestFocus();
            } else {
                progressBar.setVisibility(View.VISIBLE);
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);
                                sendVerificationEmail();
                            } else {
                                String message = Objects.requireNonNull(task.getException()).getMessage();
                                assert message != null;
                                Snackbar snackbar = Snackbar.make(container, message, Snackbar.LENGTH_LONG);
                                snackbar.show();
                                progressBar.setVisibility(View.GONE);
                            }
                        });
            }
        }
    }


    private void sendVerificationEmail(){
        try {
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null){
                user.sendEmailVerification().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        progressBar.setVisibility(View.GONE);
                        new Handler().postDelayed(() -> {
                            Intent intent = new Intent(mContext, CreateUserProfile.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }, 1000);
                    }else {
                        Snackbar snackbar = Snackbar.make(container, Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()), Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
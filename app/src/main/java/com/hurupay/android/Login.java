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
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener authStateListener;
    private final Context mContext = Login.this;
    private ProgressBar progressBar;
    private RelativeLayout container;
    private TextInputEditText emailTextInputEditText;
    private TextInputEditText passwordTextInputEditText;
    private TextInputLayout passwordTextInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        try {
            TextView signupTextView = findViewById(R.id.signupTextView);
            TextView recoveryTextView = findViewById(R.id.forgotTextView);
            passwordTextInputLayout = findViewById(R.id.passwordTextInputLayout);
            Button button = findViewById(R.id.button);
            container = findViewById(R.id.container);
            progressBar = findViewById(R.id.progressBar);
            emailTextInputEditText = findViewById(R.id.emailTextInputEditText);
            passwordTextInputEditText = findViewById(R.id.passwordTextInputEditText);
            signupTextView.setOnClickListener(this);
            button.setOnClickListener(this);
            recoveryTextView.setOnClickListener(this);
            setupFirebaseAuth();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.signupTextView) {
            startActivity(new Intent(mContext, SignUp.class));
        } else if (view.getId() == R.id.forgotTextView){
            startActivity(new Intent(mContext, com.hurupay.android.Recovery.class));
        } else if (view.getId()==R.id.button){
            String email = emailTextInputEditText.getText().toString();
            String password = passwordTextInputEditText.getText().toString();

            if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                emailTextInputEditText.setError("Enter Correct Email");
                progressBar.setVisibility(View.GONE);
                emailTextInputEditText.requestFocus();
            } else if (TextUtils.isEmpty(password) || password.length()<6) {
                passwordTextInputLayout.setError("Enter Password");
                progressBar.setVisibility(View.GONE);
                passwordTextInputEditText.requestFocus();
            } else {
                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()){

                                final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                assert firebaseUser != null;
                                if (firebaseUser.isEmailVerified()){
                                    Intent ent = new Intent(mContext, MainActivity.class);
                                    ent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(ent);

                                }else {

                                    String message = "Check your Email to verify your Account";
                                    Snackbar snackbar = Snackbar.make(container, message, Snackbar.LENGTH_SHORT);
                                    snackbar.show();

                                    Intent ent = new Intent(mContext, MainActivity.class);
                                    ent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(ent);

                                }
                                finish();
                            }else {

                                String message = Objects.requireNonNull(task.getException()).getMessage();
                                assert message != null;
                                Snackbar snackbar = Snackbar.make(container, message, Snackbar.LENGTH_SHORT);
                                snackbar.show();

                            }
                            progressBar.setVisibility(View.GONE);
                        });

            }
        }
    }


    private void setupFirebaseAuth(){
        authStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null){
                startActivity(new Intent(mContext, MainActivity.class));
                finish();
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}
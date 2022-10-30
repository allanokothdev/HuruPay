package com.hurupay.android;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;
import com.hurupay.android.constants.Constants;
import com.hurupay.android.models.Account;
import com.hurupay.android.models.User;
import com.hurupay.android.models.Wallet;
import com.hurupay.android.utils.GetUser;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class UpdateUserProfile extends AppCompatActivity implements View.OnClickListener {

    public final static int PICK_IMAGE = 1;
    private final String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private final Context mContext = UpdateUserProfile.this;
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private Uri mainImageUri = null;
    private String downloadUrlString = null;
    private final FirebaseFunctions firebaseFunctions = FirebaseFunctions.getInstance();
    private Spinner spinner;
    private Spinner genderSpinner;

    private RelativeLayout container;
    private ImageView imageView;
    private TextInputEditText usernameTextInputEditText;
    private TextInputEditText walletTextInputEditText;
    private AVLoadingIndicatorView progressBar;
    private Button button;
    private Wallet wallet;
    private CountryCodePicker countryCodePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user_profile);

        spinner = findViewById(R.id.spinner);
        genderSpinner = findViewById(R.id.genderSpinner);
        TextView privacyTextView = findViewById(R.id.privacyTextView);
        countryCodePicker = findViewById(R.id.countryCodePicker);
        EditText editText = findViewById(R.id.editText);
        container = findViewById(R.id.container);
        button = findViewById(R.id.button);
        Button uploadButton = findViewById(R.id.uploadButton);
        imageView = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progressBar);
        usernameTextInputEditText = findViewById(R.id.usernameTextInputEditText);
        walletTextInputEditText = findViewById(R.id.walletTextInputEditText);
        usernameTextInputEditText.setText(firebaseUser.getDisplayName());
        if (firebaseUser.getPhotoUrl() != null){
            downloadUrlString = firebaseUser.getPhotoUrl().toString();
            Glide.with(mContext.getApplicationContext()).load(firebaseUser.getPhotoUrl()).into(imageView);
        }
        countryCodePicker.detectLocaleCountry(true);
        countryCodePicker.detectSIMCountry(true);
        countryCodePicker.detectNetworkCountry(true);
        countryCodePicker.registerCarrierNumberEditText(editText);
        createWallet();
        button.setOnClickListener(this);
        privacyTextView.setOnClickListener(this);
        uploadButton.setOnClickListener(this);
    }

    private void createWallet() {
        firebaseFunctions.getHttpsCallable("createWallet").call().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Map<String, Object> result = (Map<String, Object>) task.getResult().getData();
                assert result != null;
                wallet = new Wallet(Objects.requireNonNull(result.get("address")).toString(), Objects.requireNonNull(result.get("privateKey")).toString());
                walletTextInputEditText.setText(wallet.getAddress());
                Toast.makeText(mContext, result.toString(),Toast.LENGTH_SHORT).show();
            } else {
                Snackbar snackbar = Snackbar.make(container, Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()), Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button:

                String title = Objects.requireNonNull(usernameTextInputEditText.getText()).toString().trim();
                String phone = countryCodePicker.getFullNumberWithPlus().trim();
                String country = countryCodePicker.getSelectedCountryName();
                String profile = spinner.getSelectedItem().toString();
                String gender = genderSpinner.getSelectedItem().toString();

                if (TextUtils.isEmpty(title)) {
                    usernameTextInputEditText.setError("Enter your Name");
                    progressBar.setVisibility(View.GONE);
                    usernameTextInputEditText.requestFocus();
                    break;
                }else if (TextUtils.isEmpty(phone)){
                    Snackbar snackbar = Snackbar.make(container,"Enter phone Number", Snackbar.LENGTH_SHORT);snackbar.show();
                    progressBar.setVisibility(View.GONE);
                    countryCodePicker.requestFocus();
                    break;
                }else if (TextUtils.isEmpty(phone)){
                    Snackbar snackbar = Snackbar.make(container,"Select Profile Type", Snackbar.LENGTH_SHORT);snackbar.show();
                    progressBar.setVisibility(View.GONE);
                    spinner.requestFocus();
                    break;

                }else if (mainImageUri == null){
                    Snackbar snackbar = Snackbar.make(container,R.string.upload_profile_pic, Snackbar.LENGTH_SHORT);snackbar.show();
                    progressBar.setVisibility(View.GONE);
                    imageView.requestFocus();
                    break;

                }else if (TextUtils.isEmpty(phone)){
                    Snackbar snackbar = Snackbar.make(container,R.string.select_gender, Snackbar.LENGTH_SHORT);snackbar.show();
                    progressBar.setVisibility(View.GONE);
                    genderSpinner.requestFocus();
                    break;

                }else {
                    button.setEnabled(false);
                    createUser(title, firebaseUser.getEmail() ,phone, country, profile, gender);
                    progressBar.setVisibility(View.VISIBLE);
                    break;
                }
            case R.id.privacyTextView:
                Intent indie = new Intent(Intent.ACTION_VIEW);
                indie.setData(Uri.parse(Constants.PRIVACY_POLICY));
                startActivity(indie);
                break;
            case R.id.uploadButton:
                try {
                    if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(UpdateUserProfile.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else { BringImagePicker();break; }
                }catch (Exception e){ break; }


        }
    }

    private void createUser(String username, String email, String phone, String country, String profile, String gender){
        try {
            ArrayList<String> tags = new ArrayList<>();
            tags.add(country);
            tags.add(profile);
            tags.add(gender);
            tags.add(currentUserID);
            if (downloadUrlString != null) {
                User user = new User(currentUserID,email,phone,downloadUrlString,username,wallet.getAddress(),wallet.getPrivateKey(),"0",country,"",false,"USD",tags,System.currentTimeMillis());
                firebaseFirestore.collection(Constants.USERS).document(currentUserID).set(user).addOnSuccessListener(aVoid -> {
                    Account account = new Account(phone,downloadUrlString,username,wallet.getAddress(),"",tags, false);
                    firebaseFirestore.collection(Constants.ACCOUNTS).document(wallet.getAddress()).set(account);
                    GetUser.saveUser(mContext,user);
                    progressBar.setVisibility(View.GONE);
                    finishAfterTransition();
                }).addOnFailureListener(e -> {
                    Snackbar snackbar = Snackbar.make(container, Objects.requireNonNull(e.getMessage()), Snackbar.LENGTH_SHORT);snackbar.show();
                    progressBar.setVisibility(View.GONE);
                });
            } else {
                final StorageReference ref = FirebaseStorage.getInstance().getReference().child(Constants.USERS).child(System.currentTimeMillis() + ".png");
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),mainImageUri);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
                byte[] data = byteArrayOutputStream.toByteArray();
                UploadTask uploadTask = ref.putBytes(data);
                uploadTask.addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();

                        User user = new User(currentUserID,email,phone,downloadUri.toString(),username,wallet.getAddress(),wallet.getPrivateKey(),"0",country,"",false,"USD",tags,System.currentTimeMillis());
                        firebaseFirestore.collection(Constants.USERS).document(currentUserID).set(user).addOnSuccessListener(aVoid -> {

                            Account account = new Account(phone,downloadUri.toString(),username,wallet.getAddress(),"", tags, false);
                            firebaseFirestore.collection(Constants.ACCOUNTS).document(wallet.getAddress()).set(account);
                            GetUser.saveUser(mContext,user);
                            progressBar.setVisibility(View.GONE);
                            finishAfterTransition();
                        }).addOnFailureListener(e -> {
                            Snackbar snackbar = Snackbar.make(container, Objects.requireNonNull(e.getMessage()), Snackbar.LENGTH_SHORT);snackbar.show();
                            progressBar.setVisibility(View.GONE);
                        });
                    }
                })).addOnFailureListener(e -> {
                    Snackbar snackbar = Snackbar.make(container, Objects.requireNonNull(e.getMessage()), Snackbar.LENGTH_SHORT);snackbar.show();
                    progressBar.setVisibility(View.INVISIBLE);
                });
            }
        } catch (IOException e){e.printStackTrace(); }
    }

    private void BringImagePicker() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent, "Select Profile Pic"),PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode ==PICK_IMAGE && resultCode == RESULT_OK && data != null){
                imageView.setImageURI(data.getData());
                mainImageUri = data.getData();
                postImage(data.getData());
            }
        }catch (Exception e){e.printStackTrace(); }
    }

    private void postImage(Uri mainImageUri){
        try {
            final StorageReference ref = FirebaseStorage.getInstance().getReference().child(Constants.USERS).child(System.currentTimeMillis() + ".png");
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),mainImageUri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
            byte[] data = byteArrayOutputStream.toByteArray();
            UploadTask uploadTask = ref.putBytes(data);
            uploadTask.addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    Uri downloadUri = task.getResult();
                    downloadUrlString = downloadUri.toString();
                }
            })).addOnFailureListener(e -> {
                Snackbar snackbar = Snackbar.make(container, Objects.requireNonNull(e.getMessage()), Snackbar.LENGTH_SHORT);snackbar.show();
                progressBar.setVisibility(View.INVISIBLE);
                button.setEnabled(true);
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}

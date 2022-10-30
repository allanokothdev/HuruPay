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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hurupay.android.constants.Constants;
import com.hurupay.android.models.Token;
import com.hurupay.android.models.User;
import com.hurupay.android.utils.GetUser;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class CreateToken extends AppCompatActivity implements View.OnClickListener {

    public final static int PICK_IMAGE = 1;
    private final String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private final Context mContext = CreateToken.this;
    private Uri mainImageUri = null;
    private String downloadUrlString = null;
    private RelativeLayout container;
    private ImageView imageView;
    private TextInputEditText titleTextInputEditText;
    private TextInputEditText symbolTextInputEditText;
    private TextInputEditText summaryTextInputEditText;
    private TextInputEditText addressTextInputEditText;
    private TextInputEditText decimalsTextInputEditText;
    private Spinner spinner;
    private Spinner categorySpinner;
    private AVLoadingIndicatorView progressBar;
    private Button button;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_token);

        user = GetUser.getUser(mContext,currentUserID);
        spinner = findViewById(R.id.spinner);
        categorySpinner = findViewById(R.id.categorySpinner);
        container = findViewById(R.id.container);
        button = findViewById(R.id.button);
        Button uploadButton = findViewById(R.id.uploadButton);
        imageView = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progressBar);
        titleTextInputEditText = findViewById(R.id.titleTextInputEditText);
        symbolTextInputEditText = findViewById(R.id.symbolTextInputEditText);
        summaryTextInputEditText = findViewById(R.id.summaryTextInputEditText);
        addressTextInputEditText = findViewById(R.id.addressTextInputEditText);
        decimalsTextInputEditText = findViewById(R.id.decimalsTextInputEditText);
        button.setOnClickListener(this);
        uploadButton.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button:

                String title = Objects.requireNonNull(titleTextInputEditText.getText()).toString().trim();
                String symbol = Objects.requireNonNull(symbolTextInputEditText.getText()).toString().trim();
                String address = Objects.requireNonNull(addressTextInputEditText.getText()).toString().trim();
                String summary = Objects.requireNonNull(summaryTextInputEditText.getText()).toString().trim();
                String decimals = Objects.requireNonNull(decimalsTextInputEditText.getText()).toString().trim();

                String type = spinner.getSelectedItem().toString();
                String category = categorySpinner.getSelectedItem().toString();

                if (TextUtils.isEmpty(title)) {
                    titleTextInputEditText.setError("Please Enter Token Title");
                    progressBar.setVisibility(View.GONE);
                    titleTextInputEditText.requestFocus();
                    break;
                }else if (TextUtils.isEmpty(symbol)){
                    symbolTextInputEditText.setError("Please Enter Token Symbol");
                    progressBar.setVisibility(View.GONE);
                    symbolTextInputEditText.requestFocus();
                    break;
                }else if (TextUtils.isEmpty(address)){
                    addressTextInputEditText.setError("Please Enter Token Address");
                    progressBar.setVisibility(View.GONE);
                    addressTextInputEditText.requestFocus();
                    break;
                }else if (TextUtils.isEmpty(summary)){
                    summaryTextInputEditText.setError("Please Enter Token Summary");
                    progressBar.setVisibility(View.GONE);
                    summaryTextInputEditText.requestFocus();
                    break;
                }else if (TextUtils.isEmpty(decimals)){
                    decimalsTextInputEditText.setError("Please Enter Token Decimals");
                    progressBar.setVisibility(View.GONE);
                    decimalsTextInputEditText.requestFocus();
                    break;
                }else if (TextUtils.isEmpty(type)){
                    Snackbar snackbar = Snackbar.make(container,R.string.select_token_type, Snackbar.LENGTH_SHORT);snackbar.show();
                    progressBar.setVisibility(View.GONE);
                    spinner.requestFocus();
                    break;

                }else if (TextUtils.isEmpty(category)){
                    Snackbar snackbar = Snackbar.make(container,R.string.specify_token_category, Snackbar.LENGTH_SHORT);snackbar.show();
                    progressBar.setVisibility(View.GONE);
                    categorySpinner.requestFocus();
                    break;

                }else if (mainImageUri == null){
                    Snackbar snackbar = Snackbar.make(container,R.string.upload_profile_pic, Snackbar.LENGTH_SHORT);snackbar.show();
                    progressBar.setVisibility(View.GONE);
                    imageView.requestFocus();
                    break;
                }else {
                    button.setEnabled(false);
                    createToken(title, symbol, summary, address, type, category, decimals);
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
                        ActivityCompat.requestPermissions(CreateToken.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else { BringImagePicker();break; }
                }catch (Exception e){ break; }


        }
    }

    private void createToken(String title, String symbol, String summary, String address, String type, String category, String decimals) {
        try {
            ArrayList<String> tags = new ArrayList<>();
            tags.add(user.getAddress());
            tags.add(type);
            tags.add(category);
            if (downloadUrlString != null) {
                Token token = new Token(address,downloadUrlString,title,summary,Integer.parseInt(decimals),type,symbol,4,tags);
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.OBJECT,token);
                progressBar.setVisibility(View.GONE);
                startActivity(new Intent(mContext, SelectRegion.class).putExtras(bundle));
                finishAfterTransition();
            } else {
                final StorageReference ref = FirebaseStorage.getInstance().getReference().child(Constants.TOKENS).child(System.currentTimeMillis() + ".png");
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),mainImageUri);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
                byte[] data = byteArrayOutputStream.toByteArray();
                UploadTask uploadTask = ref.putBytes(data);
                uploadTask.addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        Token token = new Token(address,downloadUri.toString(),title,summary,Integer.parseInt(decimals),type,symbol,4,tags);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Constants.OBJECT,token);
                        progressBar.setVisibility(View.GONE);
                        startActivity(new Intent(mContext, SelectRegion.class).putExtras(bundle));
                        finishAfterTransition();
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
            final StorageReference ref = FirebaseStorage.getInstance().getReference().child(Constants.TOKENS).child(System.currentTimeMillis() + ".png");
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

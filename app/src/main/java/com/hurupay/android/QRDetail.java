package com.hurupay.android;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.print.PrintHelper;

import com.bumptech.glide.Glide;
import com.hurupay.android.models.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.hurupay.android.utils.GetUser;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Objects;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import de.hdodenhof.circleimageview.CircleImageView;

public class QRDetail extends AppCompatActivity implements View.OnClickListener {

    private final String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private final Context mContext = QRDetail.this;
    private RelativeLayout container;
    private ImageView imageView;
    private ImageView saveImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrdetail);

        User user = GetUser.getUser(mContext,currentUserID);

        container = findViewById(R.id.container);
        saveImageView = findViewById(R.id.saveImageView);
        ImageView closeImageView = findViewById(R.id.closeImageView);
        TextView textView = findViewById(R.id.textView);
        TextView subTextView = findViewById(R.id.subTextView);
        imageView = findViewById(R.id.imageView);
        CircleImageView circleImageView = findViewById(R.id.circleImageView);
        saveImageView.setOnClickListener(this);
        closeImageView.setOnClickListener(this);
        Glide.with(mContext.getApplicationContext()).load(user.getPic()).placeholder(R.drawable.placeholder).into(circleImageView);
        textView.setText(user.getUsername());
        subTextView.setText(user.getAddress());
        getQR(user.getAddress());
    }

    private void getQR(String inviteCode){
        try {
            WindowManager windowManager = (WindowManager)getSystemService(WINDOW_SERVICE);
            Display display = windowManager.getDefaultDisplay();
            Point point = new Point();
            display.getSize(point);
            int width = point.x;
            int height = point.y;
            int smallerDimension = Math.min(width, height);
            smallerDimension=smallerDimension*3/4;
            QRGEncoder qrgEncoder = new QRGEncoder(inviteCode, null, QRGContents.Type.TEXT, smallerDimension);
            qrgEncoder.setColorBlack(ContextCompat.getColor(mContext,R.color.colorPrimary));
            qrgEncoder.setColorWhite(Color.WHITE);
            imageView.setImageBitmap(qrgEncoder.getBitmap());
        }catch (Exception e){ e.printStackTrace(); }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.saveImageView) {
            PopupMenu popupMen = new PopupMenu(mContext, saveImageView);
            popupMen.getMenuInflater().inflate(R.menu.menu_qr_detail, popupMen.getMenu());
            popupMen.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.action_print:
                        doPhotoPrint();
                        break;
                    case R.id.action_save:
                        try {
                            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(QRDetail.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                            } else {
                                flatEarth();
                            }
                            break;
                        } catch (Exception e) {
                            break;
                        }
                    case R.id.action_share:
                        shareTextandImage(viewToBitmap(container));
                        break;
                }
                return true;
            });
            popupMen.show();
        } else if (v.getId()==R.id.closeImageView){
            finishAfterTransition();
        }
    }


    public Bitmap viewToBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }


    private void shareTextandImage(Bitmap bitmap) {
        final  String appPackageName = getPackageName();
        String link = "https://play.google.com/store/apps/details?id=" + appPackageName;
        Uri uri = saveImageToShare(bitmap);
        Intent imageIntent = new Intent(Intent.ACTION_SEND);
        imageIntent.putExtra(imageIntent.EXTRA_STREAM, uri);
        imageIntent.putExtra(imageIntent.EXTRA_TEXT, link);
        imageIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name);
        imageIntent.setType("image/png");
        startActivity(Intent.createChooser(imageIntent, "Share Via"));
    }

    private void doPhotoPrint(){

        try {
            PrintHelper printHelper = new PrintHelper(mContext);
            printHelper.setScaleMode(PrintHelper.SCALE_MODE_FIT);
            printHelper.setOrientation(PrintHelper.ORIENTATION_PORTRAIT);
            printHelper.printBitmap("Wallet Address",viewToBitmap(container));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void flatEarth(){

        File storageLocation = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File file = new File(storageLocation, System.currentTimeMillis()+".jpg");
        try {

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            viewToBitmap(container).compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
            scanFile(mContext, Uri.fromFile(file));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void scanFile(Context context, Uri imageUri){

        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(imageUri);
        context.sendBroadcast(scanIntent);
        Snackbar snackbar = Snackbar.make(container, "Saved Successfully", Snackbar.LENGTH_SHORT);snackbar.show();
    }


    private Uri saveImageToShare(Bitmap bitmap) {
        File imageFolder = new File(this.getCacheDir(), "images");
        Uri uri = null;
        try {
            imageFolder.mkdirs();
            File file = new File(imageFolder, "shared_image.png");
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(mContext, "com.hurupay.android.fileprovider", file);
        }catch (Exception e){
            Toast.makeText(mContext, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

        }
        return uri;
    }
}

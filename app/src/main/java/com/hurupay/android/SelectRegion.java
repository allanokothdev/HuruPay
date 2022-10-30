package com.hurupay.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hurupay.android.constants.Constants;
import com.hurupay.android.models.Token;
import com.wang.avi.AVLoadingIndicatorView;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;

public class SelectRegion extends AppCompatActivity implements View.OnClickListener{

    private final Context mContext = SelectRegion.this;
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private RelativeLayout container;
    private TagFlowLayout tagFlowLayout;
    private Button button;
    private AVLoadingIndicatorView progressBar;
    private String[] identities;
    private final ArrayList<String> stringArrayList = new ArrayList<>();
    private Token token;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_region);

        try {
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            token = (Token) bundle.getSerializable("object");
            identities = getResources().getStringArray(R.array.countries);

            final LayoutInflater mInflater = LayoutInflater.from(mContext);
            container = findViewById(R.id.container);
            tagFlowLayout = findViewById(R.id.tagFlowLayout);
            progressBar = findViewById(R.id.progressBar);
            button = findViewById(R.id.button);
            button.setOnClickListener(this);
            tagFlowLayout.setAdapter(new TagAdapter<String>(identities)
            {
                @Override
                public View getView(FlowLayout parent, int position, String s) {
                    TextView textView = (TextView) mInflater.inflate(R.layout.values_listitem, tagFlowLayout, false);
                    textView.setText(s);
                    return textView;
                }

                @Override public void onSelected(int position, View view) {
                    super.onSelected(position, view);
                    view.setSelected(true);
                    stringArrayList.add(identities[position]);
                    if (stringArrayList.size()>0){
                        button.setVisibility(View.VISIBLE);
                    }else {
                        button.setVisibility(View.GONE);
                        Snackbar snackbar = Snackbar.make(container, "Select at least 1 County", Snackbar.LENGTH_SHORT);snackbar.show();
                    }
                }

                @Override public void unSelected(int position, View view) {
                    super.unSelected(position, view);
                    view.setSelected(false);

                    stringArrayList.remove(identities[position]);
                    if (stringArrayList.size()>0){
                        button.setVisibility(View.VISIBLE);
                    }else {
                        button.setVisibility(View.GONE);
                        Snackbar snackbar = Snackbar.make(container, "Select at least 1 County", Snackbar.LENGTH_SHORT);snackbar.show();
                    }
                }

                @Override public int getCount() { return super.getCount(); }
                @Override public String getItem(int position) {
                    return super.getItem(position);
                }
                @NonNull @Override public String toString() {
                    return super.toString();
                }
            });
        }catch (Exception e){ e.printStackTrace();}
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button) {
            progressBar.setVisibility(View.VISIBLE);
            token.getTags().addAll(stringArrayList);
            firebaseFirestore.collection(Constants.TOKENS).document(token.getTokenAddress()).set(token).addOnSuccessListener(unused -> {
                progressBar.setVisibility(View.GONE);
                finishAfterTransition();
            });
        }
    }


}

